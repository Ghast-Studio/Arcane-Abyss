package net.headnutandpasci.arcaneabyss.item.custom;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.item.ModToolMaterial;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeStaffItem extends ToolItem {
    public static final int MAX_UPGRADE_LEVEL = 3;
    private static final String NBT_UPGRADE_LEVEL = "upgrade_level";

    public SlimeStaffItem(Settings settings) {
        super(ModToolMaterial.RUBY, settings);
    }

    public void upgradeStaff(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        int currentLevel = nbt.getInt(NBT_UPGRADE_LEVEL);

        if (currentLevel < MAX_UPGRADE_LEVEL) {
            nbt.putInt(NBT_UPGRADE_LEVEL, currentLevel + 1);
        }
    }

    public int getUpgradeLevel(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.getInt(NBT_UPGRADE_LEVEL);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int upgradeLevel = getUpgradeLevel(stack);
        if (upgradeLevel > 0) {
            tooltip.add(Text.translatable("tooltip.arcaneabyss.slime_staff.upgrade_level", upgradeLevel));
        }

        tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_staff.radius", this.getRadiusForLevel(upgradeLevel)));
        tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_staff.cooldown", StringHelper.formatTicks(this.getCoolDownForLevel(upgradeLevel))));
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("tooltip.arcaneabyss.ruby_staff.description").formatted(Formatting.GRAY));

        ImmutableList<StatusEffectInstance> effects = getStatusEffectsForLevel(upgradeLevel);
        effects.forEach(effect -> tooltip.add(Util.getStatusEffectDescription(effect, Text.of("for"), Formatting.BLUE)));

        if (stack.hasEnchantments())
            tooltip.add(Text.empty());

        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            if (!player.getItemCooldownManager().isCoolingDown(this)) {
                Vec3d from = player.getPos();
                Vec3d to = new Vec3d(from.x, world.getBottomY(), from.z);

                BlockHitResult hitResult = world.raycast(new RaycastContext(
                        from,
                        to,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player
                ));

                double initialRadius = this.getRadiusForLevel(this.getUpgradeLevel(player.getStackInHand(hand)));
                if (hitResult.getType() == BlockHitResult.Type.BLOCK) {
                    Vec3d hitPos = hitResult.getPos();
                    this.generateCollapsingParticles(serverWorld, hitPos, initialRadius);
                } else {
                    this.generateCollapsingParticles(serverWorld, player.getPos(), initialRadius);
                }

                this.applyEffectToPlayersInRadius(serverWorld, player.getPos(), initialRadius);
                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE,
                        SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.getItemCooldownManager().set(this, this.getCoolDownForLevel(this.getUpgradeLevel(player.getStackInHand(hand))));
            }
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    private int getCoolDownForLevel(int level) {
        return switch (level) {
            case 1 -> 180 * 20;
            case 2 -> 160 * 20;
            case 3 -> 140 * 20;
            default -> 200 * 20;
        };
    }

    private float getRadiusForLevel(int level) {
        return switch (level) {
            case 1 -> 6.0f;
            case 2 -> 8.0f;
            case 3 -> 10.0f;
            default -> 4.0f;
        };
    }

    private ImmutableList<StatusEffectInstance> getStatusEffectsForLevel(int level) {
        return switch (level) {
            case 0 -> ImmutableList.of(
                    new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 0)
            );
            case 1 -> ImmutableList.of(
                    new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 1)
            );
            case 2 -> ImmutableList.of(
                    new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 2),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 30 * 20, 0)
            );
            case 3 -> ImmutableList.of(
                    new StatusEffectInstance(StatusEffects.STRENGTH, 60 * 20, 2),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 30 * 20, 1),
                    new StatusEffectInstance(StatusEffects.REGENERATION, 30 * 20, 0)
            );
            default -> ImmutableList.of();
        };
    }

    private void generateCollapsingParticles(ServerWorld serverWorld, Vec3d center, double initialRadius) {
        int points = 50;
        int steps = 10;

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
        }
    }

    private void applyEffectToPlayersInRadius(ServerWorld serverWorld, Vec3d center, double radius) {
        serverWorld.getPlayers(player -> player.getPos().isInRange(center, radius))
                .forEach(player -> {
                    int level = this.getUpgradeLevel(player.getMainHandStack());
                    ImmutableList<StatusEffectInstance> effects = this.getStatusEffectsForLevel(level);

                    effects.forEach(player::addStatusEffect);
                });
    }
}