package io.github.davidqf555.minecraft.towerofgod.client.events;

import io.github.davidqf555.minecraft.towerofgod.client.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.client.gui.LighthouseScreen;
import io.github.davidqf555.minecraft.towerofgod.client.render.*;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ContainerRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
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
            if (client.player.getActivePotionEffect(EffectRegistry.REVERSE_FLOW.get()) != null) {
                ModifiableAttributeInstance attribute = client.player.getAttribute(Attributes.ATTACK_SPEED);
                if (attribute == null || attribute.getValue() <= 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void onFMLClientSetup(FMLClientSetupEvent event) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.LIGHTHOUSE.get(), LighthouseRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.OBSERVER.get(), ObserverRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.REGULAR.get(), BipedShinsuUserRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SHINSU.get(), ShinsuRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.CLICKER.get(), ClickerRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SHINSU_ARROW.get(), ShinsuArrowRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.RANKER.get(), BipedShinsuUserRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SPEAR.get(), SpearRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.DIRECTIONAL_LIGHTNING.get(), DirectionalLightningRenderer::new);
            KeyBindingsList.register();
            event.enqueueWork(() -> {
                ScreenManager.registerFactory(ContainerRegistry.LIGHTHOUSE.get(), new LighthouseScreen.Factory());
                ItemModelsProperties.registerProperty(ItemRegistry.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pull"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pull")));
                ItemModelsProperties.registerProperty(ItemRegistry.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pulling"), ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pulling")));
                for (RegistryObject<SpearItem> spear : ItemRegistry.SPEARS) {
                    ItemModelsProperties.registerProperty(spear.get(), SpearItem.THROWING, ItemModelsProperties.func_239417_a_(Items.TRIDENT, new ResourceLocation("throwing")));
                }
            });
        }

        @SubscribeEvent
        public static void onHandleColors(ColorHandlerEvent.Item event) {
            ShinsuItemColor shinsu = new ShinsuItemColor();
            for (RegistryObject<? extends Item> item : ItemRegistry.SHINSU_ITEMS) {
                event.getItemColors().register(shinsu, item::get);
            }
            DeviceItemColor device = new DeviceItemColor();
            for (RegistryObject<? extends Item> item : ItemRegistry.COLORED_DEVICE_ITEMS) {
                event.getItemColors().register(device, item::get);
            }
        }
    }
}