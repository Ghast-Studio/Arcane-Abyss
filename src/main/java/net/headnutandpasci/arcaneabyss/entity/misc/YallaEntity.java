package net.headnutandpasci.arcaneabyss.entity.misc;


import com.google.common.collect.ImmutableList;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.ai.brain.YallaBrain;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class YallaEntity extends HostileEntity implements Vibrations {
    protected static final ImmutableList<SensorType<? extends Sensor<? super YallaEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;
    private static final Logger LOGGER = ArcaneAbyss.LOGGER;
    private static final TrackedData<Boolean> DANCING;

    static {
        DANCING = DataTracker.registerData(YallaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SENSORS = ImmutableList.of(
                SensorType.NEAREST_LIVING_ENTITIES,
                SensorType.NEAREST_PLAYERS,
                SensorType.HURT_BY);

        MEMORY_MODULES = ImmutableList.of(
                MemoryModuleType.PATH,
                MemoryModuleType.LOOK_TARGET,
                MemoryModuleType.VISIBLE_MOBS,
                MemoryModuleType.WALK_TARGET,
                MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                MemoryModuleType.HURT_BY,
                MemoryModuleType.LIKED_PLAYER,
                MemoryModuleType.LIKED_NOTEBLOCK,
                MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
                MemoryModuleType.ATTACK_TARGET,
                MemoryModuleType.ATTACK_COOLING_DOWN,
                MemoryModuleType.HURT_BY_ENTITY,
                MemoryModuleType.NEAREST_HOSTILE);
    }

    private final EntityGameEventHandler<Vibrations.VibrationListener> gameEventHandler;
    private final Vibrations.Callback vibrationCallback;
    private final EntityGameEventHandler<YallaEntity.JukeboxEventListener> jukeboxEventHandler;
    private Vibrations.ListenerData vibrationListenerData;
    private @Nullable BlockPos jukeboxPos;
    private float animationCycleProgress;
    private float currentAnimationState;
    private float prevAnimationState;

    public YallaEntity(EntityType<? extends YallaEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.vibrationCallback = new YallaEntity.VibrationCallback();
        this.vibrationListenerData = new Vibrations.ListenerData();
        this.gameEventHandler = new EntityGameEventHandler<>(new Vibrations.VibrationListener(this));
        this.jukeboxEventHandler = new EntityGameEventHandler<>(new YallaEntity.JukeboxEventListener(this.vibrationCallback.getPositionSource(), GameEvent.JUKEBOX_PLAY.getRange()));
    }

    public static DefaultAttributeContainer.Builder setAttributesYalla() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0);
    }

    protected Brain.Profile<YallaEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return YallaBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<YallaEntity> getBrain() {
        return (Brain<YallaEntity>) super.getBrain();
    }

    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DANCING, false);
    }

    public void travel(Vec3d movementInput) {
        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8));
            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
            } else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.91));
            }
        }

        this.updateLimbs(false);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.6F;
    }

    public boolean damage(DamageSource source, float amount) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof PlayerEntity playerEntity) {
            Optional<UUID> likedPlayer = this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LIKED_PLAYER);
            if (likedPlayer.isPresent() && playerEntity.getUuid().equals(likedPlayer.get())) {
                return false;
            }
        }

        this.getBrain().remember(MemoryModuleType.ATTACK_TARGET, (LivingEntity) attacker);
        return super.damage(source, amount);
    }

    @Override
    protected void pushAway(Entity entity) {
        if (!(entity instanceof PlayerEntity))
            super.pushAway(entity);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!(entity instanceof PlayerEntity))
            super.pushAwayFrom(entity);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    protected SoundEvent getAmbientSound() {
        return this.hasStackEquipped(EquipmentSlot.MAINHAND) ? SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ALLAY_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }

    protected void mobTick() {
        this.getWorld().getProfiler().push("yallaBrain");
        this.getBrain().tick((ServerWorld) this.getWorld(), this);
        this.getWorld().getProfiler().pop();
        this.getWorld().getProfiler().push("yallaActivityUpdate");
        YallaBrain.updateActivities(this);
        this.getWorld().getProfiler().pop();
        super.mobTick();
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient && this.isAlive() && this.age % 10 == 0) {
            this.heal(1.0F);
        }

        if (this.isDancing() && this.shouldStopDancing() && this.age % 20 == 0) {
            this.setDancing(false);
            this.jukeboxPos = null;
        }
    }

    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.isDancing()) {
                ++this.animationCycleProgress;
                this.prevAnimationState = this.currentAnimationState;
                if (this.animationCycleProgress()) {
                    ++this.currentAnimationState;
                } else {
                    --this.currentAnimationState;
                }

                this.currentAnimationState = MathHelper.clamp(this.currentAnimationState, 0.0F, 15.0F);
            } else {
                this.animationCycleProgress = 0.0F;
                this.currentAnimationState = 0.0F;
                this.prevAnimationState = 0.0F;
            }
        } else {
            Ticker.tick(this.getWorld(), this.vibrationListenerData, this.vibrationCallback);
            if (this.isPanicking()) {
                this.setDancing(false);
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        this.getWorld().playSoundFromEntity(player, this, SoundEvents.ENTITY_ALLAY_ITEM_GIVEN, SoundCategory.NEUTRAL, 2.0F, 1.0F);
        this.getBrain().remember(MemoryModuleType.LIKED_PLAYER, player.getUuid());
        return super.interactMob(player, hand);
    }

    public void updateJukeboxPos(BlockPos jukeboxPos, boolean playing) {
        if (playing) {
            if (!this.isDancing()) {
                this.jukeboxPos = jukeboxPos;
                this.setDancing(true);
            }
        } else if (jukeboxPos.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public boolean isFlappingWings() {
        return !this.isOnGround();
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        World world = this.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            callback.accept(this.gameEventHandler, serverWorld);
            callback.accept(this.jukeboxEventHandler, serverWorld);
        }
    }

    public boolean isDancing() {
        return this.dataTracker.get(DANCING);
    }

    public void setDancing(boolean dancing) {
        if (!this.getWorld().isClient && this.canMoveVoluntarily() && (!dancing || !this.isPanicking())) {
            this.dataTracker.set(DANCING, dancing);
        }
    }

    public boolean isPanicking() {
        return false;
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null || !this.jukeboxPos.isWithinDistance(this.getPos(), GameEvent.JUKEBOX_PLAY.getRange()) || !this.getWorld().getBlockState(this.jukeboxPos).isOf(Blocks.JUKEBOX);
    }

    public boolean animationCycleProgress() {
        float f = this.animationCycleProgress % 55.0F;
        return f < 15.0F;
    }

    public float getAnimationProgress(float f) {
        return MathHelper.lerp(f, this.prevAnimationState, this.currentAnimationState) / 15.0F;
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        DataResult<NbtElement> result = ListenerData.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationListenerData);
        Logger logger = LOGGER;
        Objects.requireNonNull(logger);
        result.resultOrPartial(logger::error).ifPresent((nbtElement) -> {
            nbt.put("listener", nbtElement);
        });
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("listener", 10)) {
            DataResult<ListenerData> listener = ListenerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("listener")));
            Logger logger = LOGGER;
            Objects.requireNonNull(logger);
            listener.resultOrPartial(logger::error).ifPresent((listenerData) -> {
                this.vibrationListenerData = listenerData;
            });
        }
    }

    @Override
    protected boolean shouldFollowLeash() {
        return false;
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, (double) this.getStandingEyeHeight() * 0.6, (double) this.getWidth() * 0.1);
    }

    @Override
    public double getHeightOffset() {
        return 0.4;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 18) {
            for (int i = 0; i < 3; ++i) {
                this.addHeartParticle();
            }
        } else {
            super.handleStatus(status);
        }
    }

    private void addHeartParticle() {
        double d = this.random.nextGaussian() * 0.02;
        double e = this.random.nextGaussian() * 0.02;
        double f = this.random.nextGaussian() * 0.02;
        this.getWorld().addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
    }

    public Vibrations.ListenerData getVibrationListenerData() {
        return this.vibrationListenerData;
    }

    public Vibrations.Callback getVibrationCallback() {
        return this.vibrationCallback;
    }

    private class VibrationCallback implements Vibrations.Callback {
        private static final int RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(YallaEntity.this, YallaEntity.this.getStandingEyeHeight());

        VibrationCallback() {
        }

        public int getRange() {
            return 16;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public boolean accepts(ServerWorld world, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
            if (YallaEntity.this.isAiDisabled()) {
                return false;
            } else {
                Optional<GlobalPos> optional = YallaEntity.this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LIKED_NOTEBLOCK);
                if (optional.isEmpty()) {
                    return true;
                } else {
                    GlobalPos globalPos = optional.get();
                    return globalPos.getDimension().equals(world.getRegistryKey()) && globalPos.getPos().equals(pos);
                }
            }
        }

        public void accept(ServerWorld world, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {

        }

        public TagKey<GameEvent> getTag() {
            return GameEventTags.ALLAY_CAN_LISTEN;
        }
    }

    private class JukeboxEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public JukeboxEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public int getRange() {
            return this.range;
        }

        public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
            if (event == GameEvent.JUKEBOX_PLAY) {
                YallaEntity.this.updateJukeboxPos(BlockPos.ofFloored(emitterPos), true);
                return true;
            } else if (event == GameEvent.JUKEBOX_STOP_PLAY) {
                YallaEntity.this.updateJukeboxPos(BlockPos.ofFloored(emitterPos), false);
                return true;
            } else {
                return false;
            }
        }
    }
}
