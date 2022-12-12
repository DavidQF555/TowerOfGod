package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.DeviceDyeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class RecipeRegistry {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<DeviceDyeRecipe>> DEVICE_DYE = register("device_dye", () -> new SimpleCraftingRecipeSerializer<>(DeviceDyeRecipe::new));

    private RecipeRegistry() {
    }

    private static <T extends RecipeSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer) {
        return SERIALIZERS.register(name, serializer);
    }
}
