package net.headnutandpasci.arcaneabyss.item.custom;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.particle.ParticleTypes;

public class RubyStaffItem extends Item {

    public RubyStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            if (!player.getItemCooldownManager().isCoolingDown(this)) {
                Vec3d from = player.getPos(); // Player's current position
                Vec3d to = new Vec3d(from.x, world.getBottomY(), from.z); // Straight down to the bottom of the world

                // Perform the raycast
                BlockHitResult hitResult = world.raycast(new RaycastContext(
                        from,
                        to,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player
                ));

                if (hitResult.getType() == BlockHitResult.Type.BLOCK) {
                    Vec3d hitPos = hitResult.getPos(); // The position where the ray hits a block
                    double initialRadius = 6.0;

                    // Use the hit position as the center for particles and effects
                    generateCollapsingParticles(serverWorld, hitPos, initialRadius);
                    applyEffectToPlayersInRadius(serverWorld, hitPos, initialRadius);

                    // Play sound at player's position
                    world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE,
                            SoundCategory.PLAYERS, 1.0F, 1.0F);

                    // Set a cooldown of 90 seconds (90 * 20 ticks)
                    player.getItemCooldownManager().set(this, 90 * 20);
                }
            } else {
                player.sendMessage(Text.of("The Ruby Staff is still on cooldown!"), true);
            }
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
    private void generateCollapsingParticles(ServerWorld serverWorld, Vec3d center, double initialRadius) {
        int points = 50;
        int steps = 10;
        long delay = 5L;

        for (int step = 0; step <= steps; step++) {
            double radius = initialRadius * (1.0 - (double) step / steps);

            serverWorld.getServer().execute(() -> {
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = center.x + radius * Math.cos(angle);
                    double z = center.z + radius * Math.sin(angle);

                    serverWorld.spawnParticles(ParticleTypes.END_ROD, x, center.y + 0.2, z, 5, 0, 0, 0, 0);
                }
            });

            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {}
        }
    }

    private void applyEffectToPlayersInRadius(ServerWorld serverWorld, Vec3d center, double radius) {
        serverWorld.getPlayers(player -> player.getPos().isInRange(center, radius))
                .forEach(player -> player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH,
                        60 * 20,
                        1
                )));
    }
}