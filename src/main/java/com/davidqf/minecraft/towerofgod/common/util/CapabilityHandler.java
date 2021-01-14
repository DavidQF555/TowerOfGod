package com.davidqf.minecraft.towerofgod.common.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;

import com.davidqf.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.items.ShinsuItemColor;
import com.davidqf.minecraft.towerofgod.common.packets.*;
import com.davidqf.minecraft.towerofgod.common.entities.ShinsuUserEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation PLAYER_EQUIPS = new ResourceLocation(TowerOfGod.MOD_ID, "player_equips");
    private static int index = 0;

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof ShinsuUserEntity || entity instanceof PlayerEntity) {
            event.addCapability(SHINSU_STATS, new IShinsuStats.Provider());
        }
        if (entity instanceof PlayerEntity) {
            event.addCapability(PLAYER_EQUIPS, new IPlayerShinsuEquips.Provider());
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        StatsCommand.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModBus {

        @SubscribeEvent
        public static void onHandleColors(ColorHandlerEvent.Item event) {
            ShinsuItemColor color = new ShinsuItemColor();
            for (RegistryObject<? extends Item> item : RegistryHandler.SHINSU_ITEMS) {
                event.getItemColors().register(color, item::get);
            }
        }

        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            CapabilityManager.INSTANCE.register(IShinsuStats.class, new IShinsuStats.Storage(), IShinsuStats.ShinsuStats::new);
            CapabilityManager.INSTANCE.register(IPlayerShinsuEquips.class, new IPlayerShinsuEquips.Storage(), new IPlayerShinsuEquips.PlayerShinsuEquips.Factory());
            ChangeEquipsMessage.register(index++);
            CastShinsuMessage.register(index++);
            ShinsuStatsTickMessage.register(index++);
            UpdateStatsMetersMessage.register(index++);
            UpdateClientCooldownsMessage.register(index++);
            UpdateClientCanCastMessage.register(index++);
            UpdateClientKnownMessage.register(index++);
            UpdateClientEquippedMessage.register(index++);
        }
    }
}
