package net.headnutandpasci.arcaneabyss.entity.slime.boss.black;

import net.headnutandpasci.arcaneabyss.entity.slime.boss.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.BossState;
import net.headnutandpasci.arcaneabyss.util.random.WeightedBag;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlackSlimeEntity extends ArcaneBossSlime<BossState> implements SkinOverlayOwner {

    private static final TrackedData<Integer> PHASE = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DATA_STATE = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<BlockPos> SPAWN_POINT = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> AWAKENING_TICKS = DataTracker.registerData(BlackSlimeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int DEFAULT_INVUL_TIMER = 200;

    private final CopyOnWriteArrayList<Integer> summonedMobIds;

    protected int attackTimer;
    protected int playerUpdateTimer;

    public BlackSlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, BossState.class);
        this.summonedMobIds = new CopyOnWriteArrayList<>();
    }

    public static DefaultAttributeContainer.Builder setAttributesGreenSlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f)
                .add(EntityAttributes.GENERIC_ARMOR, 10)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }

    @Override
    protected void initGoals() {
        /*EnumMap<SlimeShootGoal.BulletType, Integer> bulletTypePhases = new EnumMap<>(SlimeShootGoal.BulletType.class);
        bulletTypePhases.put(SlimeShootGoal.BulletType.SINGLE, 1);
        bulletTypePhases.put(SlimeShootGoal.BulletType.RAPID_SINGLE, 1);
        bulletTypePhases.put(SlimeShootGoal.BulletType.MULTI, 2);
        bulletTypePhases.put(SlimeShootGoal.BulletType.RAPID_MULTI, 2);

        this.goalSelector.add(1, new SlimeResetGoal(this, getFollowDistance()));
        this.goalSelector.add(1, new SlimeShootGoal(this, bulletTypePhases));
        this.goalSelector.add(2, new SlimeSummonGoal(this, List.of(ModEntities.BLUE_SLIME, ModEntities.DARK_BLUE_SLIME)));
        this.goalSelector.add(2, new SlimePushGoal(this));*/
        this.goalSelector.add(3, new SwimmingGoal(this));
        this.goalSelector.add(4, new FaceTowardTargetGoal(this));
        this.goalSelector.add(5, new RandomLookGoal(this));
        this.goalSelector.add(6, new MoveGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }


    public void startBossFight() {
        if (this.isAlive() && this.dataTracker.get(PHASE) < 1 && !this.isInState(BossState.AWAKENING)) {
            /*Box bossArena = new Box(this.getBlockPos()).expand(getFollowDistance());

            List<ServerPlayer> players = this.level().getEntitiesOfClass(ServerPlayer.class, bossArena);
            for (ServerPlayer p : players) {
                playerUUIDs.add(p.getUUID());
            }
            int playerCount = players.size();
            EntityScale.scaleBossHealth(this, playerCount);
            EntityScale.scaleBossAttack(this, playerCount);
            this.dataTracker.set(PLAYER_COUNT, playerCount);*/

            this.dataTracker.set(AWAKENING_TICKS, 160);
            this.dataTracker.set(DATA_STATE, BossState.AWAKENING.toInt());

            if (this.getMoveControl() instanceof ArcaneSlimeMoveControl control) {
                control.setDisabled(false);
            }
        }
    }


    private void abilitySelectionTick() {
        if (this.getTarget() != null) {
            if (attackTimer > 0) {
                --this.attackTimer;
            } else {
                WeightedBag<BossState> attackPool = new WeightedBag<>();
                Box box = (new Box(this.getBlockPos())).expand(10);
                List<PlayerEntity> pushTargets = this.getWorld().getEntitiesByType(EntityType.PLAYER, box, (player) -> !player.isCreative() || !player.isSpectator());

                /*if (!pushTargets.isEmpty()) {
                    attackPool.addEntry(State.SUMMON, 1);
                    attackPool.addEntry(State.SHOOT_SLIME_BULLET, 1);
                } else {
                    attackPool.addEntry(State.SUMMON, 1);
                    attackPool.addEntry(State.PUSH, 1);
                    attackPool.addEntry(State.SHOOT_SLIME_BULLET, 1);
                }*/
                attackPool.addEntry(BossState.SHOOT_SLIME_BULLET, 1);

                this.dataTracker.set(DATA_STATE, attackPool.getRandom().toInt());
            }
        }
    }

    private void phaseUpdateTick() {
        if (this.getHealth() < (this.getMaxHealth() * 0.5)) {
            this.setPhase(2);
        }
    }


    public void tick() {
        super.tick();

        this.summonedMobIds.removeIf(id -> this.getWorld().getEntityById(id) == null);
        if (!this.summonedMobIds.isEmpty()) this.setInvulTimer(40);

        if (--this.playerUpdateTimer < 1) {
            this.playerUpdateTimer = 20 * 2;

            if (this.isInState(BossState.SPAWNING)) {
                List<PlayerEntity> playerNearby = this.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.getBlockPos()).expand(this.getFollowDistance()), (player) -> true);

                if (!playerNearby.isEmpty()) {
                    this.startBossFight();

                    if (!(this.getWorld().isClient() || this.getServer() == null)) {
                        playerNearby.forEach(player -> {
                            ServerPlayerEntity serverPlayer = this.getServer().getPlayerManager().getPlayer(player.getUuid());
                            this.getBossBar().addPlayer(serverPlayer);
                        });
                    }
                }
            }
        }

        if (this.isAlive()) {
            this.abilitySelectionTick();
            this.phaseUpdateTick();

        }

    }

}
