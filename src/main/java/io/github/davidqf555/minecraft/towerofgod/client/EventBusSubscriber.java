package io.github.davidqf555.minecraft.towerofgod.client;

import io.github.davidqf555.minecraft.towerofgod.client.render.*;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public class EventBusSubscriber {

    @SubscribeEvent
    public static void onRawMouseInput(InputEvent.RawMouseEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (event.isCancelable() && event.getAction() != GLFW.GLFW_RELEASE && client.currentScreen == null) {
            if (client.player.getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get()) != null) {
                ModifiableAttributeInstance attribute = client.player.getAttribute(Attributes.ATTACK_SPEED);
                if (attribute == null || attribute.getValue() <= 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {

        @SubscribeEvent
        public static void onFMLClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.OBSERVER_ENTITY.get(), ObserverRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.REGULAR_ENTITY.get(), BipedShinsuUserRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SHINSU_ENTITY.get(), ShinsuRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CLICKER_ENTITY.get(), ClickerRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SHINSU_ARROW_ENTITY.get(), ShinsuArrowRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.RANKER_ENTITY.get(), BipedShinsuUserRenderer::new);
            KeyBindingsList.register();
            event.enqueueWork(() -> {
                ScreenManager.registerFactory(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), new LighthouseEntity.LighthouseScreen.Factory());
                ItemModelsProperties.registerProperty(RegistryHandler.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pull"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pull")));
                ItemModelsProperties.registerProperty(RegistryHandler.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pulling"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pulling")));
            });
        }
    }
}
