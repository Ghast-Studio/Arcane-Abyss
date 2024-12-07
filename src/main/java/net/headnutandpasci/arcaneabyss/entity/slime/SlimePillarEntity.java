package net.headnutandpasci.arcaneabyss.entity.slime;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class SlimePillarEntity extends ArcaneSlimeEntity {

    private SlimePillarEntity parent; // The slime below this one
    private SlimePillarEntity child;

    public SlimePillarEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);


        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1.0D);
    }

    public static DefaultAttributeContainer.Builder setAttributesSlimePillar() {
        return ArcaneSlimeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void initGoals() {

        this.goalSelector.add(1, new SwimmingGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;
    }

    @Override
    public void travel(Vec3d movementInput) {

    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.hasChild()) {
            if (source.isOf(DamageTypes.GENERIC) || source.isOf(DamageTypes.GENERIC_KILL))
                return super.damage(source, amount);
            return false;
        }

        // Allow damage otherwise
        boolean result = super.damage(source, amount);

        // Notify parent slime if this one dies
        if (!this.isAlive() && this.parent != null) {
            this.parent.setChild(null);
        }

        return result;
    }

    @Override
    protected void jump() {

    }

    public void setParent(SlimePillarEntity parent) {
        this.parent = parent;
    }

    public void setChild(SlimePillarEntity child) {
        this.child = child;
    }

    public boolean hasChild() {
        return this.child != null && this.child.isAlive();
    }
}
