package io.github.davidqf555.minecraft.towerofgod.registration;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.ShinsuToolLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class LootModifierRegistry {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<Codec<ShinsuToolLootModifier>> SHINSU_TOOL = register("shinsu_tool", () -> ShinsuToolLootModifier.CODEC);

    private LootModifierRegistry() {
    }

    private static <T extends IGlobalLootModifier> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> serializer) {
        return SERIALIZERS.register(name, serializer);
    }
}
