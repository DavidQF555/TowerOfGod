package io.github.davidqf555.minecraft.towerofgod.client;

import io.github.davidqf555.minecraft.towerofgod.client.gui.LighthouseScreen;
import io.github.davidqf555.minecraft.towerofgod.client.render.*;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

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

        private ModBus() {
        }

        @SubscribeEvent
        public static void onFMLClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.OBSERVER_ENTITY.get(), ObserverRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.REGULAR_ENTITY.get(), BipedShinsuUserRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SHINSU_ENTITY.get(), ShinsuRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.CLICKER_ENTITY.get(), ClickerRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.SHINSU_ARROW_ENTITY.get(), ShinsuArrowRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.RANKER_ENTITY.get(), BipedShinsuUserRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.DIRECTIONAL_LIGHTNING_ENTITY.get(), DirectionalLightningRenderer::new);
            KeyBindingsList.register();
            event.enqueueWork(() -> {
                ScreenManager.registerFactory(RegistryHandler.LIGHTHOUSE_CONTAINER.get(), new LighthouseScreen.Factory());
                ItemModelsProperties.registerProperty(RegistryHandler.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pull"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pull")));
                ItemModelsProperties.registerProperty(RegistryHandler.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pulling"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pulling")));
            });
        }

        @SubscribeEvent
        public static void onHandleColors(ColorHandlerEvent.Item event) {
            ShinsuItemColor shinsu = new ShinsuItemColor();
            for (RegistryObject<? extends Item> item : RegistryHandler.SHINSU_ITEMS) {
                event.getItemColors().register(shinsu, item::get);
            }
            DeviceItemColor device = new DeviceItemColor();
            for (RegistryObject<? extends Item> item : RegistryHandler.COLORED_DEVICE_ITEMS) {
                event.getItemColors().register(device, item::get);
            }
        }
    }
}
