package net.headnutandpasci.arcaneabyss.entity.slime;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
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

public abstract class ArcaneBossSlime extends ArcaneRangedSlime implements SkinOverlayOwner {
    protected static final int DEFAULT_INVUL_TIMER = 200;
    protected static final int DEFAULT_AWAKENING_TIMER = 200;

    private static final TrackedData<Integer> INVUL_TIMER;
    private static final TrackedData<Integer> AWAKENING_TIMER;
    private static final TrackedData<Integer> PHASE;
    private static final TrackedData<Integer> DATA_STATE;
    private static final TrackedData<Integer> ATTACK_TIMER;

    static {
        DATA_STATE = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        INVUL_TIMER = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        AWAKENING_TIMER = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
        ATTACK_TIMER = DataTracker.registerData(ArcaneBossSlime.class, TrackedDataHandlerRegistry.INTEGER);
    }

    protected final List<ArcaneBossSlime.Ability> abilityPool;
    private final ServerBossBar bossBar;
    protected int playerUpdateTimer;
    protected ArcaneBossSlime.State lastState;
    private List<ServerPlayerEntity> playerNearby;
    @Nullable
    private Predicate<Entity> showBossBarPredicate;

    public ArcaneBossSlime(EntityType<? extends HostileEntity> entityType, World world, int size) {
        super(entityType, world, size);
        this.moveControl = new DisabledMoveControl(this);
        this.jumpControl = new DisabledJumpControl(this);
        this.abilityPool = new ArrayList<>();
        this.playerNearby = new ArrayList<>();
        this.experiencePoints = 500;

        this.setPersistent();

        this.bossBar = (ServerBossBar) (new ServerBossBar(Text.translatable("entity.arcaneabyss.black_slime"), BossBar.Color.GREEN, BossBar.Style.PROGRESS))
                .setDragonMusic(true)
                .setThickenFog(true);

        this.bossBar.setPercent(0.0F);

        this.initAbilities();
    }

    public ArcaneBossSlime(EntityType<? extends HostileEntity> entityType, World world) {
        this(entityType, world, 16);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setInvulTimer(DEFAULT_INVUL_TIMER);
        this.showBossBarPredicate = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), 50)).and(Util.visibleTo(this));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_STATE, State.SPAWNING.getValue());
        this.dataTracker.startTracking(PHASE, 0);
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(AWAKENING_TIMER, 0);
        this.dataTracker.startTracking(ATTACK_TIMER, 0);
    }

    protected void updatePlayers() {
        if (this.showBossBarPredicate == null) {
            this.showBossBarPredicate = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), 50)).and(Util.visibleTo(this));
        }

        this.bossBar.clearPlayers();

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            this.playerNearby = serverWorld.getPlayers(this.showBossBarPredicate);
            for (ServerPlayerEntity serverPlayerEntity : this.playerNearby) {
                this.bossBar.addPlayer(serverPlayerEntity);
            }

            this.playerNearby.removeIf(player -> player.isSpectator() || player.isCreative());
        }

        if (this.isInState(State.SPAWNING) && !this.playerNearby.isEmpty()) {
            this.startBossFight();
        }
    }

    protected void registerAbility(ArcaneBossSlime.State state, int weight) {
        this.abilityPool.add(new Ability(state, weight, (slime) -> true));
    }

    protected void registerAbility(ArcaneBossSlime.State state, int weight, Predicate<ArcaneBossSlime> condition) {
        this.abilityPool.add(new Ability(state, weight, condition));
    }

    private void abilitySelectionTick() {
        if (this.getAttackTimer() <= 0) {
            WeightedRandomBag<State> attackPool = new WeightedRandomBag<>();

            this.abilityPool.forEach(ability -> {
                if (ability.condition.test(this)) {
                    attackPool.addEntry(ability.state, ability.weight);
                }
            });

            ArcaneBossSlime.State selectedState;
            int maxTries = 10;
            do {
                selectedState = attackPool.getRandom();
                if (maxTries-- <= 0) {
                    selectedState = State.IDLE;
                    ArcaneAbyss.LOGGER.warn("[ArcaneBossSlime] Failed to select ability, defaulting to IDLE");
                    break;
                }
            } while (selectedState == this.lastState && !isDistanceBasedAbility(selectedState));

            this.setState(selectedState);
            lastState = selectedState;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (--this.playerUpdateTimer < 1) {
            this.playerUpdateTimer = 20 * 2;
            this.updatePlayers();
        }

        if (this.getAwakeningTimer() > 0 && this.isInState(State.AWAKENING)) {
            int timer = this.getAwakeningTimer();
            this.setAwakeningTimer(timer - 1);
            this.setInvulTimer(timer);
            this.getBossBar().setPercent(1.0F - (float) timer / DEFAULT_AWAKENING_TIMER);

            if (this.getAwakeningTimer() <= 0) {
                this.setState(State.IDLE);
            }
        } else if (this.isAlive()) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            if (this.hasTarget()) {
                this.phaseUpdateTick();

                if (this.getAttackTimer() > 0) {
                    this.setAttackTimer(this.getAttackTimer() - 1);
                } else if (this.isInState(State.IDLE)) {
                    this.abilitySelectionTick();
                }
            }
        }

        if (this.isInState(State.SPAWNING)) {
            this.setInvulTimer(200);
        }

        if (this.getInvulnerableTimer() > 0) {
            int timer = this.getInvulnerableTimer();
            this.setInvulTimer(timer - 1);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (this.getInvulnerableTimer() > 0) {
            if (source.isOf(DamageTypes.GENERIC) || source.isOf(DamageTypes.GENERIC_KILL))
                return super.damage(source, amount);
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
        nbt.putInt("AttackTicks", this.getAttackTimer());
        nbt.putInt("InvulTimer", this.getInvulnerableTimer());
        nbt.putInt("AwakeningTicks", this.getAwakeningTimer());
        nbt.putInt("Phase", this.getPhase());
        nbt.putInt("State", this.getState().getValue());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAttackTimer(nbt.getInt("AttackTicks"));
        this.setInvulTimer(nbt.getInt("InvulTimer"));
        this.setAwakeningTimer(nbt.getInt("AwakeningTicks"));
        this.setPhase(nbt.getInt("Phase"));
        this.dataTracker.set(DATA_STATE, nbt.getInt("State"));

        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
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

    @Override
    public void checkDespawn() {

    }

    public boolean hasTarget() {
        return this.getTarget() != null;
    }

    public int getAttackTimer() {
        return this.dataTracker.get(ATTACK_TIMER);
    }

    public void setAttackTimer(int i) {
        this.dataTracker.set(ATTACK_TIMER, i);
    }

    public List<ServerPlayerEntity> getPlayerNearby() {
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

    public int getAwakeningTimer() {
        return this.dataTracker.get(AWAKENING_TIMER);
    }

    public void setAwakeningTimer(int ticks) {
        this.dataTracker.set(AWAKENING_TIMER, ticks);
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

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        if (this.getInvulnerableTimer() > 0 || this.inAttackState()) return;

        super.attack(target, pullProgress);
    }

    protected abstract void recalculateAttributes();

    protected abstract void phaseUpdateTick();

    protected abstract void startBossFight();

    protected abstract boolean inAttackState();

    protected abstract boolean isDistanceBasedAbility(State state);

    protected abstract void initAbilities();

    public abstract void reset();

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

    protected record Ability(ArcaneBossSlime.State state, int weight, Predicate<ArcaneBossSlime> condition) {

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
