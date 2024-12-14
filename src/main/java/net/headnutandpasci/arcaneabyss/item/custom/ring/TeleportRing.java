package net.headnutandpasci.arcaneabyss.item.custom.ring;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TeleportRing extends TrinketItem {
    private BlockPos savedLocation;

    public TeleportRing(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        assert player != null;
        if (world instanceof ServerWorld serverWorld) {
            double x = player.getX();
            double y = player.getY() + 1.0;
            double z = player.getZ();

            if (!world.isClient) {

                this.savedLocation = pos;
                player.sendMessage(Text.literal("Location saved: " + pos.toShortString()), true);


                serverWorld.playSound(null, x, y, z,
                        SoundEvents.ENTITY_PLAYER_LEVELUP,
                        player.getSoundCategory(), 0.75F, 1.0F);


                for (int i = 0; i < 20; i++) {
                    serverWorld.spawnParticles(
                            ParticleTypes.GLOW,
                            x + Math.random() * 0.5 - 0.25,
                            y + Math.random() * 0.5 - 0.25,
                            z + Math.random() * 0.5 - 0.25,
                            1,
                            0.0, 0.0, 0.0,
                            1.0
                    );
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && player instanceof ServerPlayerEntity) {
            if (this.savedLocation != null) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                        player.getSoundCategory(), 0.75F, 1.0F);
                Vec3d teleportPos = Vec3d.ofBottomCenter(savedLocation);
                player.teleport(teleportPos.x, teleportPos.y + 2, teleportPos.z);
                player.sendMessage(Text.literal("Teleported to: " + savedLocation.toShortString()), true);


                if (world instanceof ServerWorld serverWorld) {
                    double x = player.getX();
                    double y = player.getY() + 1.0;
                    double z = player.getZ();

                    for (int i = 0; i < 40; i++) {
                        serverWorld.spawnParticles(
                                ParticleTypes.PORTAL,
                                x + Math.random() * 0.5 - 0.25,
                                y + Math.random() * 0.5 - 0.75,
                                z + Math.random() * 0.5 - 0.25,
                                10,
                                0.0, 0.0, 0.0,
                                1.0
                        );
                    }

                }
            } else {
                player.sendMessage(Text.literal("No location saved."), true);
            }
        }
        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
