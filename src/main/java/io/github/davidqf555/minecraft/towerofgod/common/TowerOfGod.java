package io.github.davidqf555.minecraft.towerofgod.common;

import io.github.davidqf555.minecraft.towerofgod.client.ClientConfigs;
import io.github.davidqf555.minecraft.towerofgod.registration.*;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

@Mod("towerofgod")
public class TowerOfGod {

    public static final String MOD_ID = "towerofgod";
    public static final ItemGroup TAB = new ItemGroup(MOD_ID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return ItemRegistry.SUSPENDIUM.get().getDefaultInstance();
        }
    };
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, TowerOfGod.MOD_ID),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public TowerOfGod() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
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
        PointOfInterestRegistry.TYPES.register(bus);
        RecipeRegistry.SERIALIZERS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        ShinsuShapeRegistry.SHAPES.register(bus);
        ShinsuQualityRegistry.QUALITIES.register(bus);
        ShinsuTechniqueRegistry.TECHNIQUES.register(bus);
        GroupRegistry.GROUPS.register(bus);
    }
}
