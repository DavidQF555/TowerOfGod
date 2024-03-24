package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfiguredTechniqueTypeRegistry {

    public static final ResourceKey<Registry<ConfiguredShinsuTechniqueType<?, ?>>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(TowerOfGod.MOD_ID, "configured_shinsu_techniques"));

    private ConfiguredTechniqueTypeRegistry() {
    }

    public static Registry<ConfiguredShinsuTechniqueType<?, ?>> getRegistry(RegistryAccess access) {
        return access.registryOrThrow(REGISTRY);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<ConfiguredShinsuTechniqueType<?, ?>>().setType((Class<ConfiguredShinsuTechniqueType<?, ?>>) (Class<?>) ConfiguredTechniqueTypeRegistry.class).setName(REGISTRY.location()).dataPackRegistry(ConfiguredShinsuTechniqueType.DIRECT_CODEC, ConfiguredShinsuTechniqueType.DIRECT_CODEC));
    }

}
