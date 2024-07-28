package net.headnutandpasci.arcaneabyss.entity.slime.boss.black;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.ai.SlimePushGoal;
import net.headnutandpasci.arcaneabyss.entity.ai.SlimeShootGoal;
import net.headnutandpasci.arcaneabyss.entity.ai.SlimeSummonGoal;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CopyOnWriteArrayList;

public class BlackSlimeEntity extends ArcaneSlimeEntity implements SkinOverlayOwner {

    private static final TrackedData<Integer> DATA_STATE = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int DEFAULT_INVUL_TIMER = 200;

    private final ServerBossBar bossBar;
    private final CopyOnWriteArrayList<Integer> summonedMobIds;

    protected int attackTimer;
    /*private List<PlayerEntity> pushTargets;*/

    public BlackSlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
        this.bossBar = (ServerBossBar) (new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS))
                .setDragonMusic(true)
                .setThickenFog(true)
                .setDarkenSky(true);
        this.summonedMobIds = new CopyOnWriteArrayList<>();

        this.bossBar.setPercent(0.0F);
        this.experiencePoints = 500;
    }

    public static DefaultAttributeContainer.Builder setAttributesGreenSlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 220.0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
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
        this.dataTracker.startTracking(DATA_STATE, State.SPAWNING.getValue());
        this.dataTracker.startTracking(INVUL_TIMER, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SlimeSummonGoal(this));
        this.goalSelector.add(1, new SlimePushGoal(this));
        this.goalSelector.add(1, new SlimeShootGoal(this));
        this.goalSelector.add(2, new SwimmingGoal(this));
        this.goalSelector.add(3, new FaceTowardTargetGoal(this));
        this.goalSelector.add(4, new RandomLookGoal(this));
        this.goalSelector.add(5, new MoveGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
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
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.attackTimer = nbt.getInt("AttackTicks");
        this.setInvulTimer(nbt.getInt("InvulTimer"));
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
    public void checkDespawn() {
    }

    public boolean isAttacking(State attackState) {
        return this.dataTracker.get(DATA_STATE) == attackState.getValue();
    }

    public void stopAttacking(int cooldown) {
        this.dataTracker.set(DATA_STATE, State.IDLE.getValue());
        this.setAttackTimer(cooldown);
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    public void setAttackTimer(int tick) {
        this.attackTimer = tick;
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

    private void abilitySelectionTick() {
        if (this.getTarget() != null) {
            if (attackTimer > 0) {
                --this.attackTimer;
            } else {
                WeightedRandomBag<State> attackPool = new WeightedRandomBag<>();
                /*Box aabb = (new Box(this.getBlockPos())).expand(10);
                pushTargets = this.getWorld().getEntitiesByType(EntityType.PLAYER, aabb, (player) -> !player.isCreative());*/
                /*if (!pushTargets.isEmpty()) {
                    attackPool.addEntry(State.PUSH, 3);
                    attackPool.addEntry(State.SHOOT_GHOST_BULLET_SINGLE, 3);
                    attackPool.addEntry(State.SHOOT_GHOST_BULLET_BURST, 2);
                    attackPool.addEntry(State.SUMMON_MOB, 1);
                } else {*/
                attackPool.addEntry(State.SUMMON, 1);
                attackPool.addEntry(State.PUSH, 1);
                attackPool.addEntry(State.SHOOT_SLIME_BULLET, 1);
                this.dataTracker.set(DATA_STATE, attackPool.getRandom().getValue());
            }
        }
    }

    @Override
    public void kill() {
        ArcaneAbyss.LOGGER.info("Black Slime Killed");
        this.dataTracker.set(DATA_STATE, State.DEATH.getValue());
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.kill();
    }

    @Override
    protected void updatePostDeath() {
        this.dataTracker.set(DATA_STATE, State.DEATH.getValue());
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.updatePostDeath();
    }

    public void tick() {
        super.tick();
        this.summonedMobIds.removeIf(id -> this.getWorld().getEntityById(id) == null);

        if (!this.summonedMobIds.isEmpty()) {
            ArcaneAbyss.LOGGER.info("applying invul timer to black slime");
            this.setInvulTimer(40);
        }

        if (this.getInvulnerableTimer() > 0) {
            ArcaneAbyss.LOGGER.info("Invul Timer: " + this.getInvulnerableTimer());
            this.setInvulTimer(this.getInvulnerableTimer() - 1);
            int i = this.getInvulnerableTimer();

            if (i > 0) {
                if (this.getState().equals(State.SPAWNING)) {
                    this.bossBar.setPercent(1.0F - (float) i / DEFAULT_INVUL_TIMER);
                    this.setInvulTimer(i);
                    if (this.age % 10 == 0) {
                        this.heal(10.0f);
                    }
                }
            } else {
                this.dataTracker.set(DATA_STATE, State.IDLE.getValue());
                this.setAttackTimer(40);
                this.setInvulTimer(0);
            }
        } else if (this.isAlive()) {
            this.abilitySelectionTick();
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    public double getAttackDamage() {
        return this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public void triggerRangeAttackAnimation() {
    }

    public int getInvulnerableTimer() {
        return this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    @Override
    public boolean shouldRenderOverlay() {
        return this.getInvulnerableTimer() > 0;
    }

    public CopyOnWriteArrayList<Integer> getSummonedMobIds() {
        return summonedMobIds;
    }

    public State getState() {
        return State.values()[this.dataTracker.get(DATA_STATE)];
    }

    public enum State {
        SPAWNING(0),
        IDLE(1),
        SUMMON(2),
        PUSH(3),
        SHOOT_SLIME_BULLET(4),
        DEATH(5);


        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
