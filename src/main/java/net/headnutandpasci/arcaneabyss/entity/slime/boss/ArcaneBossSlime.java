package net.headnutandpasci.arcaneabyss.entity.slime.boss;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ArcaneBossSlime<E extends Enum<E> & BossStateEnum> extends ArcaneSlimeEntity implements SkinOverlayOwner {

    private static final TrackedData<Integer> PHASE = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> STATE = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> SPAWN_POINT = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> AWAKENING_TICKS = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);

    private final ServerBossBar bossBar;

    private final Class<E> statesEnum;

    private static final int DEFAULT_INVUL_TIMER = 200;

    protected int attackTimer;
    protected int playerUpdateTimer;

    public ArcaneBossSlime(EntityType<? extends HostileEntity> entityType, World world, Class<E> statesEnum) {
        super(entityType, world);
        this.setPersistent();

        this.statesEnum = statesEnum;
        this.bossBar = (ServerBossBar) (new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS))
                .setDragonMusic(true)
                .setThickenFog(true)
                .setDarkenSky(true);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setInvulTimer(DEFAULT_INVUL_TIMER);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(STATE, BlackSlimeEntity.State.SPAWNING.getValue());
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(AWAKENING_TICKS, 0);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.getInvulnerableTimer() > 0) {
            return false;
        } else {
            return super.damage(source, amount);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AttackTicks", this.attackTimer);
        nbt.putInt("InvulTimer", this.getInvulnerableTimer());
        nbt.putInt("AwakeningTicks", this.dataTracker.get(AWAKENING_TICKS));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.attackTimer = nbt.getInt("AttackTicks");
        this.setInvulTimer(nbt.getInt("InvulTimer"));
        this.dataTracker.set(AWAKENING_TICKS, nbt.getInt("AwakeningTicks"));

        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    public void kill() {
        this.dataTracker.set(STATE, BlackSlimeEntity.State.DEATH.getValue());
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.kill();
    }

    @Override
    protected void updatePostDeath() {
        this.dataTracker.set(STATE, BlackSlimeEntity.State.DEATH.getValue());
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.updatePostDeath();
    }

    public void tick() {
        super.tick();

        if (this.getInvulnerableTimer() > 0) {
            this.setInvulTimer(this.getInvulnerableTimer() - 1);
            int i = this.getInvulnerableTimer();

            if (i > 0) {
                if (this.isInState(this.getStateFromInt(0))) {
                    this.bossBar.setPercent(1.0F - (float) i / DEFAULT_INVUL_TIMER);
                    this.setInvulTimer(i);
                    if (this.age % 10 == 0) {
                        this.heal(10.0f);
                    }
                }
            } else {
                this.dataTracker.set(STATE, BlackSlimeEntity.State.IDLE.getValue());
                this.setAttackTimer(40);
                this.setInvulTimer(0);
            }
        } else if (this.isAlive()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);

        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.getInvulnerableTimer() > 0;
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    public void setAttackTimer(int tick) {
        this.attackTimer = tick;
    }

    public boolean isInState(E state) {
        return this.getState().equals(state);
    }

    public E getStateFromInt(int state) {
        return this.statesEnum.getEnumConstants()[state];
    }

    public boolean isInPhase(int phase) {
        return this.getPhase() == phase;
    }

    public double getAttackDamage() {
        return this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public double getFollowDistance() {
        return this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    public int getInvulnerableTimer() {
        return this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    public void setAttackTick(int tick) {
        this.attackTimer = tick;
    }

    public int getAwakeningTick() {
        return this.dataTracker.get(AWAKENING_TICKS);
    }

    public void setAwakeningTick(int tick) {
        this.dataTracker.set(AWAKENING_TICKS, tick);
    }

    public ServerBossBar getBossBar() {
        return bossBar;
    }

    public BlockPos getSpawnPointPos() {
        return this.dataTracker.get(SPAWN_POINT);
    }

    public E getState() {
        return this.statesEnum.getEnumConstants()[this.dataTracker.get(STATE)];
    }

    public int getPhase() {
        return this.dataTracker.get(PHASE);
    }

    public void setPhase(int phase) {
        this.dataTracker.set(PHASE, phase);
    }
}
