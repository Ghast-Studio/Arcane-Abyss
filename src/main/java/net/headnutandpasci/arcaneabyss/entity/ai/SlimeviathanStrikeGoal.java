package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeviathanProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Timer;

public class SlimeviathanStrikeGoal extends Goal {
    private final SlimeviathanEntity slimeviathanEntity;
    private int DURATION = 120; // Must be even duration since bullet are shot at even ticks
    private String type;
    private int timer = 0;

    public SlimeviathanStrikeGoal(SlimeviathanEntity slimeviathanEntity) {
        this.slimeviathanEntity = slimeviathanEntity;
    }

    @Override
    public boolean canStart() {
        return (slimeviathanEntity.isAttacking(SlimeviathanEntity.State.SHOOT_SLIME_BULLET)) && slimeviathanEntity.getTarget() != null;
    }
    @Override
    public void start() {
        super.start();
        slimeviathanEntity.setAttackTimer(DURATION);
        WeightedRandomBag<String> bulletPatterns = new WeightedRandomBag<>();
        if (slimeviathanEntity.getState() == SlimeviathanEntity.State.SHOOT_SLIME_BULLET) {
            slimeviathanEntity.triggerRangeAttackAnimation();
            if (slimeviathanEntity.getPhase() == 1) {
                bulletPatterns.addEntry("Shot", 1);
            }
        }
        type = bulletPatterns.getRandom();
    }
    @Override
    public void tick() {

        if(this.type == null){
            return;
        }
        if (slimeviathanEntity.getAttackTimer() == 0) {
           slimeviathanEntity.stopAttacking(0);
        }



        timer++;

        ServerPlayerEntity player = (ServerPlayerEntity) slimeviathanEntity.getTarget();

        if (player != null) {
            Vec3d playerPos = player.getPos();

            if(timer >= 20) {



                new Thread(() -> {
                    for (int i = 0; i < 15; i++) {
                        try {
                            spawnCircleParticles(player.getWorld(), playerPos, 4.0, 200);
                            // Sleep for 1000 milliseconds (1 second)
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                performShot();
                slimeviathanEntity.setInvulTimer(40);

                timer = 0;
                    DURATION = DURATION - 20;
                    slimeviathanEntity.setAttackTimer(DURATION);

            }
        }
        assert player != null;

    }

    public void spawnCircleParticles(World world, Vec3d center, double radius, int particleCount) {

        for (int i = 0; i < particleCount; i++) {

            double angle = 2 * Math.PI * i / particleCount;


            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);


            double y = center.y;


            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }
    private void shootSkullAt(Vec3d spawn, Vec3d direction) {
        double g = direction.x - spawn.x;
        double h = direction.y - spawn.y - 3f;
        double i = direction.z - spawn.z;

        SlimeviathanProjectileEntity witherSkullEntity = new SlimeviathanProjectileEntity(this.slimeviathanEntity.getWorld(), this.slimeviathanEntity, g, h, i);
        Vec3d velocity = new Vec3d(0, 0, 0);
        witherSkullEntity.setSlowVelocity(velocity);
        witherSkullEntity.setOwner(this.slimeviathanEntity);
        witherSkullEntity.setPos(spawn.x + 3f, spawn.y + 10.0f, spawn.z);
        this.slimeviathanEntity.getWorld().spawnEntity(witherSkullEntity);
    }


    private void performShot() {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;


        Vec3d spawn = this.slimeviathanEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);

        int bulletCount = 1;
        for (int i = 0; i < bulletCount; i++) {
            spawn = VectorUtils.rotateVectorCC(spawn, this.slimeviathanEntity.getRotationVector(), (float) Math.toRadians((double) 360 / bulletCount) * i);
            Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.0001 - 7.0, target.getZ());
            this.shootSkullAt(this.slimeviathanEntity.getPos().add(spawn), direction);
        }
    }

}
