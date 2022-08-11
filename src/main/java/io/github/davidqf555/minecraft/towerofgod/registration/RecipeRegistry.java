package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.DeviceDyeRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class RecipeRegistry {

    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<IRecipeSerializer<DeviceDyeRecipe>> DEVICE_DYE = register("device_dye", () -> new SpecialRecipeSerializer<>(DeviceDyeRecipe::new));

    private RecipeRegistry() {
    }

    private static <T extends IRecipeSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer) {
        return SERIALIZERS.register(name, serializer);
    }
}
