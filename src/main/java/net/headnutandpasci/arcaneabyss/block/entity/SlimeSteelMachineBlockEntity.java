package net.headnutandpasci.arcaneabyss.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.headnutandpasci.arcaneabyss.block.ModBlockEntities;
import net.headnutandpasci.arcaneabyss.block.entity.interfaces.ImplementedInventory;
import net.headnutandpasci.arcaneabyss.recipe.SlimeSteelRecipe;
import net.headnutandpasci.arcaneabyss.screen.SlimeSteelMachineScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SlimeSteelMachineBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private static final int INPUT_SLOT1 = 0;
    private static final int INPUT_SLOT2 = 1;
    private static final int INPUT_SLOT3 = 2;
    private static final int OUTPUT_SLOT = 3;
    protected final PropertyDelegate propertyDelegate;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private int progress = 0;
    private int maxProgress = 72;

    public SlimeSteelMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLIMESTEEL_MACHINE_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SlimeSteelMachineBlockEntity.this.progress;
                    case 1 -> SlimeSteelMachineBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SlimeSteelMachineBlockEntity.this.progress = value;
                    case 1 -> SlimeSteelMachineBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    public static void tick(@NotNull World world, BlockPos pos, BlockState state, SlimeSteelMachineBlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (entity.isOutputSlotEmptyOrReceivable()) {
            if (entity.hasRecipe()) {
                entity.increaseCraftProgress();
                markDirty(world, pos, state);

                if (entity.hasCraftingFinished()) {
                    entity.craftItem();
                    entity.resetProgress();
                }
            } else {
                entity.resetProgress();
            }
        } else {
            entity.resetProgress();
            markDirty(world, pos, state);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("slimesteel_machine.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("slimesteel_machine.progress");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("displayname.arcaneabyss.slimesteel_machine.displayname");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SlimeSteelMachineScreenHandler(syncId, playerInventory, this, this.propertyDelegate, this);
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        if (this.getWorld() == null || this.getWorld().isClient()) {
            return;
        }

        SimpleInventory inventory = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inventory.setStack(i, this.getStack(i));
        }

        return;

/*        Optional<SlimeSteelRecipe> recipe = this.getWorld().getRecipeManager()
                .getFirstMatch(SlimeSteelRecipe.Type.INSTANCE, inventory, this.getWorld());

        if (hasRecipe()) {
            removeInputs();

            if (recipe.isEmpty()) return;
            Item output = recipe.get().getOutput(null).getItem();

            this.setStack(OUTPUT_SLOT, new ItemStack(output,
                    this.getStack(OUTPUT_SLOT).getCount() + 1));

            this.resetProgress();
        }*/
    }

    private void removeInputs() {
        this.removeStack(INPUT_SLOT1, 1);
        this.removeStack(INPUT_SLOT2, 1);
        this.removeStack(INPUT_SLOT3, 1);
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        if (this.getWorld() == null || this.getWorld().isClient()) {
            return false;
        }

        SimpleInventory inventory = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inventory.setStack(i, this.getStack(i));
        }

        return false;

/*        Optional<SlimeSteelRecipe> match = this.getWorld().getRecipeManager()
                .getFirstMatch(SlimeSteelRecipe.Type.INSTANCE, inventory, this.getWorld());

        if (match.isEmpty()) return false;
        ItemStack output = match.get().getOutput(null);

        return canInsertAmountIntoOutputSlot(output) && canInsertItemIntoOutputSlot(output.getItem());*/
    }

    private boolean canInsertItemIntoOutputSlot(@NotNull Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(@NotNull ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }
}
