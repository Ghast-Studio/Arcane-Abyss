package net.headnutandpasci.arcaneabyss.entity.slime.grey;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.particle.SlimeParticleEffect;
import net.headnutandpasci.arcaneabyss.util.ParticleUtil;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class GreySlimeEntity extends ArcaneSlimeEntity {

    public static final StatusEffectInstance EFFECT_INSTANCE;
    private static final int EFFECT_RADIUS = 10;
    private static final int EFFECT_DURATION = 5;
    private static final float PULSE_DAMAGE = 6.0f;
    private static final int EFFECT_PARTICLES = 50;
    private static final TrackedData<Integer> PULSE_COOLDOWN;

    static {
        EFFECT_INSTANCE = new StatusEffectInstance(StatusEffects.RESISTANCE, EFFECT_DURATION, 4, true, true);
        PULSE_COOLDOWN = DataTracker.registerData(GreySlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    public GreySlimeEntity(EntityType<? extends ArcaneSlimeEntity> entityType, World world) {
        super(entityType, world, 3);
    }

    public static DefaultAttributeContainer.Builder setAttributesGreySlime() {
        return ArcaneSlimeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FaceTowardTargetGoal(this));
        this.goalSelector.add(2, new RandomLookGoal(this));
        this.goalSelector.add(3, new MoveGoal(this, 0.7));
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, ArcaneSlimeEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PULSE_COOLDOWN, 60);
    }

    @Override
    public ParticleEffect getParticles() {
        return new SlimeParticleEffect(0x8B8B8B);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world.isClient) {
            ParticleUtil.particleSphere(this.getWorld(), this.getPos(), ParticleTypes.AMBIENT_ENTITY_EFFECT, EFFECT_PARTICLES, EFFECT_RADIUS);
        } else if (this.isAlive()) {
            this.energyPulseTick();
        }
    }

    private void energyPulseTick() {
        if (this.getPulseCooldown() > 0) {
            this.setPulseCooldown(this.getPulseCooldown() - 1);
            return;
        }

        this.setPulseCooldown(60);
        this.applyResistanceEffect();

        if (this.world instanceof ServerWorld serverWorld) {
            Box searchBox = new Box(
                    this.getX() - EFFECT_RADIUS, this.getY() - EFFECT_RADIUS, this.getZ() - EFFECT_RADIUS,
                    this.getX() + EFFECT_RADIUS, this.getY() + EFFECT_RADIUS, this.getZ() + EFFECT_RADIUS
            );

            List<PlayerEntity> playersInBox = serverWorld.getPlayers(ServerPlayerEntity::isAlive)
                    .stream()
                    .filter(player -> !player.isCreative() && !player.isSpectator())
                    .filter(player -> searchBox.contains(player.getPos()))
                    .collect(Collectors.toList());

            if (playersInBox.isEmpty()) {
                return;
            }

            playersInBox.forEach(player -> {
                player.damage(this.getDamageSources().magic(), PULSE_DAMAGE);
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 30.0F, 1.0F);

                Vec3d pos = player.getPos().add(0, 1, 0);
                Util.damageParticles(serverWorld, pos, 20);
            });
        }
    }


    private void applyResistanceEffect() {
        List<ArcaneSlimeEntity> nearbySlimes = this.world.getEntitiesByClass(
                ArcaneSlimeEntity.class,
                this.getBoundingBox().expand(EFFECT_RADIUS),
                entity -> entity != this && entity.isAlive()
        );

        nearbySlimes.forEach(slime -> slime.addStatusEffect(EFFECT_INSTANCE));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("pulseCooldown", this.getPulseCooldown());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setPulseCooldown(nbt.getInt("pulseCooldown"));
    }

    public int getPulseCooldown() {
        return this.dataTracker.get(PULSE_COOLDOWN);
    }

    private void setPulseCooldown(int cooldown) {
        this.dataTracker.set(PULSE_COOLDOWN, cooldown);
    }
}
