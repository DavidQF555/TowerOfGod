package com.davidqf.minecraft.towerofgod.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;

import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    public static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ShinsuUser || event.getObject() instanceof PlayerEntity) {
            event.addCapability(SHINSU_STATS, new ShinsuUser.StatsProvider());
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModBus {
        @SubscribeEvent
        public static void registerCapabilities(FMLCommonSetupEvent event) {
            CapabilityManager.INSTANCE.register(ShinsuUser.IStats.class, new ShinsuUser.StatsStorage(), new ShinsuUser.Stats.Factory());
        }
    }
}
