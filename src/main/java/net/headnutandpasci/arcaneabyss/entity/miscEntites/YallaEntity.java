package net.headnutandpasci.arcaneabyss.entity.miscEntites;


import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;

import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;


import java.util.List;

import static net.minecraft.entity.ai.brain.MemoryModuleType.DANCING;

public class YallaEntity extends TameableEntity {
    private final double ATTACK_RANGE = 3.0D;
    private static TrackedData<Boolean> DANCING = null;
    private float field_38935;
    private float field_38936;
    private float field_39472;
    private float field_39473;
    private float field_39474;


    public YallaEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }


    @Override
    public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }

    public static DefaultAttributeContainer.Builder setAttributesYalla() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f)
                .add(EntityAttributes.GENERIC_ARMOR, 2)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }



    @Override
    protected void initGoals() {
        // Add taming-friendly goals
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, true));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.isTamed()) {
            if (itemStack.isEmpty() && !this.getWorld().isClient) {
                this.setSitting(!this.isSitting());
                return ActionResult.SUCCESS;
            }
        } else if (itemStack.getItem() == Items.DIAMOND) {
            if (!this.getWorld().isClient) {
                if (this.random.nextInt(3) == 0) {
                    this.setOwner(player);
                    this.setTamed(true);
                    this.getWorld().sendEntityStatus(this, (byte) 7);
                } else {
                    this.getWorld().sendEntityStatus(this, (byte) 6);
                }
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();


        if (this.isTamed() && !this.getWorld().isClient) {
            collectNearbyItems();
        }
        if (this.getWorld().isClient) {
            this.field_38936 = this.field_38935;
            if (this.isHoldingItem()) {
                this.field_38935 = MathHelper.clamp(this.field_38935 + 1.0F, 0.0F, 5.0F);
            } else {
                this.field_38935 = MathHelper.clamp(this.field_38935 - 1.0F, 0.0F, 5.0F);
            }

            if (this.isDancing()) {
                ++this.field_39472;
                this.field_39474 = this.field_39473;
                if (this.method_44360()) {
                    ++this.field_39473;
                } else {
                    --this.field_39473;
                }

                this.field_39473 = MathHelper.clamp(this.field_39473, 0.0F, 15.0F);
            } else {
                this.field_39472 = 0.0F;
                this.field_39473 = 0.0F;
                this.field_39474 = 0.0F;
            }
        }
    }
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DANCING, false);

    }

    private void collectNearbyItems() {
        List<ItemEntity> nearbyItems = this.getWorld().getEntitiesByClass(
                ItemEntity.class,
                new Box(this.getPos().add(-5, -5, -5), this.getPos().add(5, 5, 5)),
                item -> item.isAlive() && !item.getStack().isEmpty()
        );

        for (ItemEntity itemEntity : nearbyItems) {
            Vec3d itemPos = itemEntity.getPos();
            Vec3d direction = itemPos.subtract(this.getPos()).normalize();
            itemEntity.setVelocity(direction.multiply(-0.2));

            if (this.squaredDistanceTo(itemEntity) < 1.0D) {
                this.giveItemToOwner(itemEntity.getStack());
                itemEntity.discard();
            }
        }
    }

    private void giveItemToOwner(ItemStack stack) {
        PlayerEntity owner = (PlayerEntity) this.getOwner();
        if (owner != null) {
            owner.getInventory().insertStack(stack);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (target instanceof LivingEntity && this.squaredDistanceTo(target) < ATTACK_RANGE * ATTACK_RANGE) {
            if (!((LivingEntity) target).isDead()) {

            }
            return true;
        }
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSitting(nbt.getBoolean("Sitting"));
    }

    @Override
    public EntityView method_48926() {
        return null;
    }

    public float method_43397(float f) {
        return MathHelper.lerp(f, this.field_38936, this.field_38935) / 5.0F;
    }

    public boolean isDancing() {
        return (Boolean)this.dataTracker.get(DANCING);
    }

    public float method_44368(float f) {
        return MathHelper.lerp(f, this.field_39474, this.field_39473) / 15.0F;
    }

    public boolean method_44360() {
        float f = this.field_39472 % 55.0F;
        return f < 15.0F;
    }

    public boolean canPickUpLoot() {
        return !this.isItemPickupCoolingDown() && this.isHoldingItem();
    }

    public boolean isHoldingItem() {
        return !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
    }

    private boolean isItemPickupCoolingDown() {
        return this.getBrain().isMemoryInState(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryModuleState.VALUE_PRESENT);
    }

    static {
        DANCING = DataTracker.registerData(YallaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    }
}
