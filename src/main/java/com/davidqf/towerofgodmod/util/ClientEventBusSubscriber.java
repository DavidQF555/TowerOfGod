package com.davidqf.towerofgodmod.util;

import com.davidqf.towerofgodmod.TowerOfGod;
import com.davidqf.towerofgodmod.client.render.LighthouseRenderer;
import com.davidqf.towerofgodmod.entities.LighthouseEntity;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseRenderer::new);
		ScreenManager.registerFactory(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), new LighthouseEntity.LighthouseScreen.Factory());
	}

}
