package net.headnutandpasci.arcaneabyss.entity.slime.boss.black;

import net.headnutandpasci.arcaneabyss.entity.ai.goal.*;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        this.goalSelector.add(3, new ProjectileAttackGoal(this, 0, 20, 20.0F));
        this.goalSelector.add(4, new FaceTowardTargetGoal(this));
        this.goalSelector.add(5, new RandomLookGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.summonedMobIds.isEmpty()) {
            this.summonedMobIds.removeIf(id -> this.getWorld().getEntityById(id) == null);
            this.setInvulTimer(40);
        }
    }

    @Override
    protected void startBossFight() {
        if (this.isAlive() && this.isInState(ArcaneBossSlime.State.SPAWNING)) {
            System.out.println("Starting Boss Fight");
            this.recalculateAttributes();
            this.setAwakeningTimer(160);
            this.setState(State.AWAKENING);
        }
    }

    @Override
    protected boolean inAttackState() {
        return this.isInState(State.SHOOT_SLIME_BULLET) ||
                this.isInState(State.SUMMON) ||
                this.isInState(State.PUSH) ||
                this.isInState(State.CURSE) ||
                this.isInState(State.PILLAR_SUMMON) ||
                this.isInState(State.STRIKE_SUMMON);
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

    @Override
    protected void initAbilities() {
        this.registerAbility(State.PUSH, 200, bossSlime -> bossSlime.getWorld().getClosestPlayer(bossSlime, 3.0D) != null);

        this.registerAbility(ArcaneBossSlime.State.CURSE, 15);
        this.registerAbility(ArcaneBossSlime.State.SUMMON, 15);
        this.registerAbility(ArcaneBossSlime.State.SHOOT_SLIME_BULLET, 15);
    }

    @Override
    protected boolean isDistanceBasedAbility(State state) {
        return state == State.PUSH;
    }

    public CopyOnWriteArrayList<Integer> getSummonedMobIds() {
        return summonedMobIds;
    }
}