package io.github.davidqf555.minecraft.towerofgod.common;

import io.github.davidqf555.minecraft.towerofgod.client.ClientConfigs;
import io.github.davidqf555.minecraft.towerofgod.registration.*;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

@Mod("towerofgod")
public class TowerOfGod {

    public static final String MOD_ID = "towerofgod";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(new ResourceLocation(TowerOfGod.MOD_ID, TowerOfGod.MOD_ID)).simpleChannel();

    public TowerOfGod() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC);
        registerRegistries(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        ContainerRegistry.TYPES.register(bus);
        EffectRegistry.EFFECTS.register(bus);
        EntityRegistry.TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        LootModifierRegistry.SERIALIZERS.register(bus);
        RecipeRegistry.SERIALIZERS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        ShinsuShapeRegistry.SHAPES.register(bus);
        ShinsuAttributeRegistry.QUALITIES.register(bus);
        ShinsuTechniqueRegistry.TECHNIQUES.register(bus);
        GroupRegistry.GROUPS.register(bus);
        ArgumentTypeRegistry.INFO.register(bus);
    }
}
