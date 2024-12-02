package net.headnutandpasci.arcaneabyss.entity.slime.boss.black;

import net.headnutandpasci.arcaneabyss.entity.ai.goal.*;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.concurrent.CopyOnWriteArrayList;

public class BlackSlimeEntity extends ArcaneBossSlime {
    private final CopyOnWriteArrayList<Integer> summonedMobIds;

    public BlackSlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.summonedMobIds = new CopyOnWriteArrayList<>();
    }

    public static DefaultAttributeContainer.Builder setAttributesGreenSlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }

    @Override
    protected void initGoals() {
        //this.goalSelector.add(1, new SlimeResetGoal(this, 50));
        this.goalSelector.add(2, new TargetSwitchGoal(this, 10000));
        this.goalSelector.add(2, new SlimeShootGoal(this));
        this.goalSelector.add(2, new SlimeCurseGoal(this));
        this.goalSelector.add(2, new SlimeSummonGoal(this));
        this.goalSelector.add(2, new SlimePushGoal(this));
        this.goalSelector.add(3, new FaceTowardTargetGoal(this));
        this.goalSelector.add(4, new RandomLookGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void startBossFight() {
        if (this.isAlive() && this.isInState(ArcaneBossSlime.State.SPAWNING)) {
            System.out.println("Starting Boss Fight");
            this.updatePlayers();
            this.recalculateAttributes();
            this.setAwakeningTimer(160);
            this.setState(State.AWAKENING);
        }
    }

    @Override
    protected void abilitySelectionTick() {
        if (this.getAttackTimer() <= 0) {
            WeightedRandomBag<ArcaneBossSlime.State> attackPool = new WeightedRandomBag<>();

            if (!this.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.getBlockPos()).expand(3), (player) -> !player.isInvulnerable()).isEmpty()) {
                attackPool.addEntry(ArcaneBossSlime.State.PUSH, 200);
                attackPool.addEntry(ArcaneBossSlime.State.CURSE, 100);
            }

            attackPool.addEntry(ArcaneBossSlime.State.SUMMON, 5);
            attackPool.addEntry(ArcaneBossSlime.State.SHOOT_SLIME_BULLET, 30);

            this.setState(attackPool.getRandom());
        }
    }

    @Override
    protected void phaseUpdateTick() {
        if (this.getHealth() < (this.getMaxHealth() * 0.5)) {
            this.setPhase(2);
        }
    }

    @Override
    protected void recalculateAttributes() {
        if (this.getPlayerNearby() == null) {
            System.err.println("Error: playerNearby list is null!");
            return;
        }

        int playerCount = this.getPlayerNearby().size();
        double scalingFactor = Math.max(1.0, playerCount);

        double baseHealth = 800.0;
        double scaledHealth = baseHealth * scalingFactor;

        EntityAttributeInstance maxHealthAttr = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(scaledHealth);
        } else {
            return;
        }

        if (this.getHealth() > scaledHealth) {
            this.setHealth((float) scaledHealth);
        } else {
            this.heal((float) (scaledHealth - this.getHealth()));
        }

        double baseArmor = 20.0;
        EntityAttributeInstance armorAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null) {
            armorAttr.setBaseValue(baseArmor * scalingFactor);
        }
    }

    public void tick() {
        super.tick();

        this.summonedMobIds.removeIf(id -> this.getWorld().getEntityById(id) == null);
        if (!this.summonedMobIds.isEmpty()) this.setInvulTimer(40);
    }

    public CopyOnWriteArrayList<Integer> getSummonedMobIds() {
        return summonedMobIds;
    }
}