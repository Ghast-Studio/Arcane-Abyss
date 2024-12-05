package net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan;

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

public class SlimeviathanEntity extends ArcaneBossSlime {
    private final CopyOnWriteArrayList<Integer> summonedMobIds;
    private final CopyOnWriteArrayList<Integer> summonedPillarIds;
    private int x = 0;

    public SlimeviathanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.summonedMobIds = new CopyOnWriteArrayList<>();
        this.summonedPillarIds = new CopyOnWriteArrayList<>();
    }

    public static DefaultAttributeContainer.Builder setAttributesGreenSlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 800.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f)
                .add(EntityAttributes.GENERIC_ARMOR, 20)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }

    @Override
    protected void initGoals() {
        //this.goalSelector.add(1, new SlimeResetGoal(this, 50));
        this.goalSelector.add(2, new TargetSwitchGoal(this, 10000));
        this.goalSelector.add(2, new SlimeShootGoal(this));
        this.goalSelector.add(2, new SlimeviathanStrikeGoal(this));
        this.goalSelector.add(2, new SlimeviathanSummonPillarGoal(this));
        this.goalSelector.add(2, new SlimeviathanSummonGoal(this));
        this.goalSelector.add(2, new SlimePushGoal(this));
        this.goalSelector.add(2, new SlimeCurseGoal(this));
        this.goalSelector.add(3, new ProjectileAttackGoal(this, 0, 20, 20.0F));
        this.goalSelector.add(4, new FaceTowardTargetGoal(this));
        this.goalSelector.add(5, new RandomLookGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void startBossFight() {
        if (this.isAlive() && this.isInState(State.SPAWNING)) {
            this.recalculateAttributes();
            this.setAwakeningTimer(160);
            this.setState(State.AWAKENING);
        }
    }

    @Override
    protected void phaseUpdateTick() {
        if (this.getHealth() < (this.getMaxHealth() * 0.50)) {
            this.setPhase(2);
        }
    }

    public void tick() {
        super.tick();

        this.summonedPillarIds.removeIf(id -> this.getWorld().getEntityById(id) == null);
        this.summonedMobIds.removeIf(id -> this.getWorld().getEntityById(id) == null);

        if (!this.summonedPillarIds.isEmpty() || !this.summonedMobIds.isEmpty()) {
            this.setInvulTimer(40);
        }

        if (this.isInState(State.PILLAR_SUMMON)) {
            x++;
            if (!this.summonedPillarIds.isEmpty() && x >= 1800) {
                for (PlayerEntity player : this.getPlayerNearby())
                    player.kill();

                x = 0;
            }

        }

        if (!this.isInState(State.PILLAR_SUMMON)) {
            x = 0;
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

        this.registerAbility(State.SUMMON, 20);
        this.registerAbility(State.PILLAR_SUMMON, 20);
        this.registerAbility(State.SHOOT_SLIME_BULLET, 45);
        this.registerAbility(State.STRIKE_SUMMON, 35);
        this.registerAbility(State.CURSE, 25);
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
    protected boolean isDistanceBasedAbility(State state) {
        return state == State.PUSH;
    }

    public CopyOnWriteArrayList<Integer> getSummonedMobIds() {
        return summonedMobIds;
    }

    public CopyOnWriteArrayList<Integer> getSummonedPillarIds() {
        return summonedPillarIds;
    }
}
