package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.ShinsuToolLootModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class LootModifierRegistry {

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<ShinsuToolLootModifier.Serializer> SHINSU_TOOL = register("shinsu_tool", ShinsuToolLootModifier.Serializer::new);

    private LootModifierRegistry() {
    }

    private static <T extends GlobalLootModifierSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer) {
        return SERIALIZERS.register(name, serializer);
    }
}
