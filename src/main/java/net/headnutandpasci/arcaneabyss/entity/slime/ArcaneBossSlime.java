package net.headnutandpasci.arcaneabyss.entity.slime;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ArcaneBossSlime extends ArcaneSlimeEntity implements SkinOverlayOwner {
    protected static final int DEFAULT_INVUL_TIMER = 200;
    private static final TrackedData<Integer> INVUL_TIMER;
    private static final TrackedData<Integer> AWAKENING_TICKS;
    private static final TrackedData<Integer> PHASE;
    private static final TrackedData<Integer> DATA_STATE;

    static {
        DATA_STATE = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        INVUL_TIMER = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        AWAKENING_TICKS = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
    }

    private final Predicate<Entity> showBossBarPredicate;
    private final ServerBossBar bossBar;
    private final List<PlayerEntity> playerNearby;
    protected int attackTimer;
    protected int playerUpdateTimer;

    public ArcaneBossSlime(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new DisabledMoveControl(this);
        this.jumpControl = new DisabledJumpControl(this);
        this.playerNearby = new ArrayList<>();
        this.experiencePoints = 500;

        this.setAttackTimer(100);
        this.setPersistent();

        this.showBossBarPredicate = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), 50));
        this.bossBar = (ServerBossBar) (new ServerBossBar(Text.translatable("entity.minecraft.ender_dragon"), BossBar.Color.PINK, BossBar.Style.PROGRESS))
                .setDragonMusic(true)
                .setThickenFog(true);

        this.bossBar.setPercent(0.0F);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setInvulTimer(DEFAULT_INVUL_TIMER);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_STATE, BlackSlimeEntity.State.SPAWNING.getValue());
        this.dataTracker.startTracking(PHASE, 1);
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(AWAKENING_TICKS, 0);
    }

    protected void updatePlayers() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers(this.showBossBarPredicate)) {
                this.bossBar.addPlayer(serverPlayerEntity);
                playerNearby.add(serverPlayerEntity);
            }
        }
    }

    public void tick() {
        super.tick();

        if (--this.playerUpdateTimer < 1) {
            this.playerUpdateTimer = 20 * 2;
            this.updatePlayers();
        }

        if (this.getInvulnerableTimer() > 0) {
            this.setInvulTimer(this.getInvulnerableTimer() - 1);
        } else if (this.isAlive()) {
            this.abilitySelectionTick();
            this.phaseUpdateTick();
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
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
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    public void kill() {
        ArcaneAbyss.LOGGER.info("Black Slime Killed");
        this.setState(State.DEATH);
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.kill();
    }

    @Override
    protected void updatePostDeath() {
        this.setState(State.DEATH);
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.updatePostDeath();
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
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);

        this.bossBar.addPlayer(player);
        if (this.isInState(State.SPAWNING))
            this.startBossFight();
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

    @Override
    public void checkDespawn() {

    }

    public boolean hasTarget() {
        return this.getTarget() != null;
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    public void setAttackTimer(int ticks) {
        this.attackTimer = ticks;
    }

    public List<PlayerEntity> getPlayerNearby() {
        return playerNearby;
    }

    public ServerBossBar getBossBar() {
        return bossBar;
    }

    public double getAttackDamage() {
        return this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public int getInvulnerableTimer() {
        return this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    public void setAwakeningTicks(int ticks) {
        this.dataTracker.set(AWAKENING_TICKS, ticks);
    }

    public void stopAttacking(int cooldown) {
        this.setState(State.IDLE);
        this.setAttackTimer(cooldown);
    }

    public State getState() {
        return State.values()[this.dataTracker.get(DATA_STATE)];
    }

    public void setState(State state) {
        this.dataTracker.set(DATA_STATE, state.getValue());
    }

    public boolean isInState(State state) {
        return this.getState().equals(state);
    }

    public int getPhase() {
        return this.dataTracker.get(PHASE);
    }

    public void setPhase(int phase) {
        this.dataTracker.set(PHASE, phase);
    }

    public boolean isInPhase(int phase) {
        return this.getPhase() == phase;
    }

    @Override
    public JumpControl getJumpControl() {
        return super.getJumpControl();
    }

    @Override
    protected void jump() {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected void playSecondaryStepSound(BlockState state) {

    }

    @Override
    protected SoundEvent getJumpSound() {
        return null;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    protected abstract void recalculateAttributes();

    protected abstract void abilitySelectionTick();

    protected abstract void phaseUpdateTick();

    protected abstract void startBossFight();

    public enum State {
        SPAWNING(0),
        AWAKENING(1),
        IDLE(2),
        SHOOT_SLIME_BULLET(3),
        SUMMON(4),
        PUSH(5),
        CURSE(6),
        PILLAR_SUMMON(7),
        STRIKE_SUMMON(8),
        DEATH(9);

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public static class DisabledMoveControl extends MoveControl {
        private float targetYaw;

        public DisabledMoveControl(MobEntity entity) {
            super(entity);
        }

        public void look(float targetYaw) {
            this.targetYaw = targetYaw;
        }

        @Override
        public void tick() {
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0F));
            this.entity.headYaw = this.entity.getYaw();
            this.entity.bodyYaw = this.entity.getYaw();
        }
    }

    public static class DisabledJumpControl extends JumpControl {
        public DisabledJumpControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
        }
    }
}
