package net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.ai.*;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlimeviathanEntity extends ArcaneSlimeEntity implements SkinOverlayOwner {
    private static final TrackedData<Integer> PHASE = DataTracker.registerData(SlimeviathanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DATA_STATE = DataTracker.registerData(SlimeviathanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(SlimeviathanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> SPAWN_POINT = DataTracker.registerData(SlimeviathanEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> AWAKENING_TICKS = DataTracker.registerData(SlimeviathanEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int DEFAULT_INVUL_TIMER = 200;

    private final ServerBossBar bossBar;
    private final CopyOnWriteArrayList<Integer> summonedMobIds;
    private final CopyOnWriteArrayList<Integer> summonedPillarIds;
    protected int attackTimer;
    protected int playerUpdateTimer;
    private int x = 0;
    private List<PlayerEntity> playerNearby;
    private EntityDimensions customDimensions;

    /*private List<PlayerEntity> pushTargets;*/


    public SlimeviathanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
        this.bossBar = (ServerBossBar) (new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS))
                .setDragonMusic(true)
                .setThickenFog(true)
                .setDarkenSky(true);
        this.summonedMobIds = new CopyOnWriteArrayList<>();
        this.summonedPillarIds = new CopyOnWriteArrayList<>();
        this.bossBar.setPercent(0.0F);
        this.experiencePoints = 500;
        this.dataTracker.startTracking(PHASE, 1);
        this.attackTimer = 20 * 2;
        this.customDimensions = EntityDimensions.fixed(1.0F, 2.0F); // Default 1x2 hitbox
        this.updateBoundingBox();
    }


    public static DefaultAttributeContainer.Builder setAttributesGreenSlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 800.0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f)
                .add(EntityAttributes.GENERIC_ARMOR, 30)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D);


    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setInvulTimer(DEFAULT_INVUL_TIMER);
        this.startBossFight();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_STATE, SlimeviathanEntity.State.SPAWNING.getValue());
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(AWAKENING_TICKS, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SlimeviathanResetGoal(this, getFollowDistance()));
        this.goalSelector.add(1, new SlimeviathanBlastGoal(this));
        this.goalSelector.add(1, new SlimeviathanStrikeGoal(this));
        this.goalSelector.add(1, new SlimeviathanSummonPillarGoal(this));
        this.goalSelector.add(1, new SlimeviathanGrandSummonGoal(this));
        this.goalSelector.add(1, new SlimeviathanSuperPushGoal(this));
        this.goalSelector.add(3, new ArcaneSlimeEntity.SwimmingGoal(this));
        this.goalSelector.add(4, new ArcaneSlimeEntity.FaceTowardTargetGoal(this));
        this.goalSelector.add(5, new ArcaneSlimeEntity.RandomLookGoal(this));
        this.goalSelector.add(6, new ArcaneSlimeEntity.MoveGoal(this, 1.0));
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

    public void startBossFight() {
        if (this.isAlive() && this.dataTracker.get(PHASE) < 1 && this.getState() != SlimeviathanEntity.State.AWAKENING) {
            Box bossArena = new Box(this.getBlockPos()).expand(getFollowDistance());

            /*List<ServerPlayer> players = this.level().getEntitiesOfClass(ServerPlayer.class, bossArena);
            for (ServerPlayer p : players) {
                playerUUIDs.add(p.getUUID());
            }
            int playerCount = players.size();
            EntityScale.scaleBossHealth(this, playerCount);
            EntityScale.scaleBossAttack(this, playerCount);
            this.dataTracker.set(PLAYER_COUNT, playerCount);*/

            this.dataTracker.set(AWAKENING_TICKS, 160);
            this.dataTracker.set(DATA_STATE, SlimeviathanEntity.State.AWAKENING.getValue());

            if (this.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
                moveControl.setDisabled(false);
            }
        }
    }

    @Override
    public void checkDespawn() {
    }

    public boolean isAttacking(SlimeviathanEntity.State attackState) {
        return this.dataTracker.get(DATA_STATE) == attackState.getValue();
    }

    public void stopAttacking(int cooldown) {
        this.dataTracker.set(DATA_STATE, SlimeviathanEntity.State.IDLE.getValue());
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
        if (this.getState().equals(SlimeviathanEntity.State.SPAWNING))
            this.startBossFight();
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    private void abilitySelectionTick() {
        if (this.getTarget() == null)
            return;

        if (attackTimer <= 0) {
            WeightedRandomBag<State> attackPool = new WeightedRandomBag<>();

            if (!this.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.getBlockPos()).expand(5), (player) -> !player.isInvulnerable()).isEmpty())
                attackPool.addEntry(State.PUSH, 1);

            attackPool.addEntry(State.SUMMON, 1);
            attackPool.addEntry(State.PILLAR_SUMMON, 1);
            attackPool.addEntry(State.SHOOT_SLIME_BULLET, 1);
            attackPool.addEntry(State.STRIKE_SUMMON, 1);

            this.dataTracker.set(DATA_STATE, attackPool.getRandom().getValue());
        } else {
            --this.attackTimer;
        }
    }

    @Override
    public void kill() {
        ArcaneAbyss.LOGGER.info("Slimeviathan Killed");
        this.dataTracker.set(DATA_STATE, SlimeviathanEntity.State.DEATH.getValue());
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.kill();
    }

    @Override
    protected void updatePostDeath() {
        this.dataTracker.set(DATA_STATE, SlimeviathanEntity.State.DEATH.getValue());
        this.bossBar.clearPlayers();
        this.bossBar.setPercent(0.0F);
        super.updatePostDeath();
    }

    public void tick() {
        super.tick();
        this.setDimensions(1F, 1F);

        if (!this.summonedPillarIds.isEmpty())
            this.summonedPillarIds.removeIf(id -> this.getWorld().getEntityById(id) == null);
        if (!this.summonedMobIds.isEmpty())
            this.summonedMobIds.removeIf(id -> this.getWorld().getEntityById(id) == null);
        if (!this.summonedMobIds.isEmpty() || !this.summonedPillarIds.isEmpty()) this.setInvulTimer(40);

        if (this.isInState(State.PILLAR_SUMMON)) {
            x++;

            if (!this.summonedPillarIds.isEmpty() && x >= 1800) { //1800 == ~1:30min
                for (PlayerEntity player : playerNearby) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 99999, 20));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 99999, 10));
                }
                x = 0;
            }
        }

        if (--this.playerUpdateTimer < 1) {
            this.playerUpdateTimer = 20 * 2;
            playerNearby = this.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.getBlockPos()).expand(this.getFollowDistance()), (player) -> true);


            if (this.isInState(SlimeviathanEntity.State.SPAWNING)) {
                if (!playerNearby.isEmpty()) {
                    this.startBossFight();

                    if (!this.getWorld().isClient()) {
                        playerNearby.forEach(player -> {
                            ServerPlayerEntity serverPlayer = this.getServer().getPlayerManager().getPlayer(player.getUuid());
                            this.getBossBar().addPlayer(serverPlayer);
                        });
                    }
                }
            }
        }

        if (this.getInvulnerableTimer() > 0) {
            this.setInvulTimer(this.getInvulnerableTimer() - 1);
            int i = this.getInvulnerableTimer();

            if (i > 0) {
                if (this.getState().equals(SlimeviathanEntity.State.SPAWNING)) {
                    this.bossBar.setPercent(1.0F - (float) i / DEFAULT_INVUL_TIMER);
                    this.setInvulTimer(i);
                    if (this.age % 10 == 0) {
                        this.heal(10.0f);
                    }
                }
            } else {
                this.dataTracker.set(DATA_STATE, SlimeviathanEntity.State.IDLE.getValue());
                this.setAttackTimer(40);
                this.setInvulTimer(0);
            }
        } else if (this.isAlive()) {
            this.abilitySelectionTick();
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
            this.phaseUpdateTick();
        }
    }

    public boolean isInState(SlimeviathanEntity.State state) {
        return this.getState().equals(state);
    }

    public double getAttackDamage() {
        return this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public void triggerRangeAttackAnimation() {
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

    public void disableBossBar() {
        this.bossBar.setVisible(false);
    }

    public void setAttackTick(int tick) {
        this.attackTimer = tick;
    }

    public boolean isAwakening() {
        return this.dataTracker.get(DATA_STATE) == SlimeviathanEntity.State.AWAKENING.getValue();
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

    @Override
    public boolean shouldRenderOverlay() {
        return this.getInvulnerableTimer() > 0;
    }

    public CopyOnWriteArrayList<Integer> getSummonedMobIds() {
        return summonedMobIds;
    }

    public CopyOnWriteArrayList<Integer> getSummonedPillarIds() {
        return summonedPillarIds;
    }

    public SlimeviathanEntity.State getState() {
        return SlimeviathanEntity.State.values()[this.dataTracker.get(DATA_STATE)];
    }

    public void setState(SlimeviathanEntity.State state) {
        this.dataTracker.set(DATA_STATE, state.getValue());
    }

    private void phaseUpdateTick() {
        if (this.getHealth() < (this.getMaxHealth() * 0.66)) {
            this.setPhase(2);
        }
        if (this.getHealth() < (this.getMaxHealth() * 0.33)) {
            this.setPhase(3);
        }
    }

    public int getPhase() {
        return this.dataTracker.get(PHASE);
    }

    public void setPhase(int phase) {
        this.dataTracker.set(PHASE, phase);
    }

    public void setDimensions(float width, float height) {
        this.customDimensions = EntityDimensions.fixed(width, height); // Set new dimensions
        this.updateBoundingBox();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        // Return the custom dimensions
        return this.customDimensions;
    }

    private void updateBoundingBox() {
        // Calculate the new bounding box based on current position and dimensions
        float halfWidth = this.customDimensions.width / 2.0F;
        this.setBoundingBox(new Box(
                this.getX() - halfWidth,
                this.getY(),
                this.getZ() - halfWidth,
                this.getX() + halfWidth,
                this.getY() + this.customDimensions.height,
                this.getZ() + halfWidth
        ));
    }

    public enum State {
        SPAWNING(0),
        AWAKENING(1),
        IDLE(2),
        SHOOT_SLIME_BULLET(3),
        SUMMON(4),
        PUSH(5),
        DEATH(6),
        PILLAR_SUMMON(7),
        STRIKE_SUMMON(8);


        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
