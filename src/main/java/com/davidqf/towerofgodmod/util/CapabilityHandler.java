package com.davidqf.towerofgodmod.util;

import com.davidqf.towerofgodmod.TowerOfGod;
import com.davidqf.towerofgodmod.entities.LighthouseEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

	public static final ResourceLocation LIGHTHOUSE_DATA = new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_data");

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof LighthouseEntity) {
			event.addCapability(LIGHTHOUSE_DATA, new LighthouseEntity.DataProvider());
		}
	}

	@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ModBus {
		@SubscribeEvent
		public static void registerCapabilities(FMLCommonSetupEvent event) {
			CapabilityManager.INSTANCE.register(LighthouseEntity.IData.class, new LighthouseEntity.DataStorage(), new LighthouseEntity.Data.Factory());
		}
	}
}
