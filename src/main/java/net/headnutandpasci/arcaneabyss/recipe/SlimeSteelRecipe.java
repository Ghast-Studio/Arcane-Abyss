package net.headnutandpasci.arcaneabyss.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.stream.IntStream;

public class SlimeSteelRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input;

    public SlimeSteelRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient) return false;

        return IntStream.range(0, 3).allMatch(i -> this.input.get(i).test(inventory.getStack(i)));
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return this.output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(output.getItem());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String getGroup() {
        return "arcaneabyss:slime_steel_machine/main";
    }

    public static class Type implements RecipeType<SlimeSteelRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "slime_steel";

        private Type() {
        }
    }

    public static class Serializer implements RecipeSerializer<SlimeSteelRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "slime_steel";

        @Override
        public SlimeSteelRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(3, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new SlimeSteelRecipe(id, output, inputs);
        }

        @Override
        public SlimeSteelRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            inputs.replaceAll(ingredient -> Ingredient.fromPacket(buf));

            ItemStack output = buf.readItemStack();
            return new SlimeSteelRecipe(id, output, inputs);
        }

        @Override
        public void write(PacketByteBuf buf, SlimeSteelRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
        }
    }
}
