package com.davidqf.minecraft.towerofgod.client.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.*;
import com.davidqf.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import com.davidqf.minecraft.towerofgod.common.entities.LighthouseEntity;

import com.davidqf.minecraft.towerofgod.common.capabilities.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    private static IShinsuStats clonedStats = null;
    private static IPlayerShinsuEquips clonedEquips = null;

    @SubscribeEvent
    public static void onRawMouseInput(InputEvent.RawMouseEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (event.isCancelable() && event.getAction() != GLFW.GLFW_RELEASE && client.currentScreen == null && client.player.getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get()) != null) {
            event.setCanceled(true);
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseRenderer::new);
            ScreenManager.registerFactory(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), new LighthouseEntity.LighthouseScreen.Factory());
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.OBSERVER_ENTITY.get(), ObserverRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.REGULAR_ENTITY.get(), RegularRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SHINSU_ENTITY.get(), ShinsuRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CLICKER_ENTITY.get(), ClickerRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SHINSU_ARROW_ENTITY.get(), ShinsuArrowRenderer::new);
        }

        @SubscribeEvent
        public static void setupFMLClient(FMLClientSetupEvent event) {
            ItemModelsProperties.func_239418_a_(RegistryHandler.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pull"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pull")));
            ItemModelsProperties.func_239418_a_(RegistryHandler.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pulling"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pulling")));
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {

        @SubscribeEvent
        public static void onClonePlayerEvent(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                ServerPlayerEntity original = (ServerPlayerEntity) event.getOriginal();
                clonedStats = IShinsuStats.get(original);
                clonedEquips = IPlayerShinsuEquips.get(original);
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            if (player.getUniqueID().equals(Minecraft.getInstance().player.getUniqueID())) {
                IShinsuStats stats = IShinsuStats.get(player);
                stats.deserialize(clonedStats.serialize());
                IPlayerShinsuEquips equips = IPlayerShinsuEquips.get(player);
                equips.deserialize(clonedEquips.serialize());
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if (entity.getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get()) != null) {
                entity.setVelocity(0, 0, 0);
            }
        }
    }

}
