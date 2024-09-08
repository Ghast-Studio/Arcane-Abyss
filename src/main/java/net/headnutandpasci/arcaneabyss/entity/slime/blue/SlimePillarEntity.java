package net.headnutandpasci.arcaneabyss.entity.slime.blue;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class SlimePillarEntity extends ArcaneSlimeEntity {

    public SlimePillarEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        // Set knockback resistance to maximum (1.0 means 100% resistance)
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1.0D);
    }

    public static DefaultAttributeContainer.Builder setAttributesSlimePillar() {
        return ArcaneSlimeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D); // Ensure knockback resistance is set here too
    }

    @Override
    protected void initGoals() {
        // Keeping basic swimming goal, but no movement-based goals
        this.goalSelector.add(1, new SwimmingGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        // Set velocity to zero to ensure no movement every tick
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;  // Force Minecraft to treat the velocity as updated
    }

    @Override
    public void travel(Vec3d movementInput) {
        // Override travel method to prevent any movement from AI or input
        // Do nothing to prevent movement
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);

        // After taking damage, ensure no knockback is applied by resetting velocity to zero
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;

        return result;
    }

    @Override
    protected void jump() {
        // Override the jump method to prevent jumping
    }
}
