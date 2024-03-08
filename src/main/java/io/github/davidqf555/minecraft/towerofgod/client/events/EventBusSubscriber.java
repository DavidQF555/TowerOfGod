package io.github.davidqf555.minecraft.towerofgod.client.events;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.client.gui.LighthouseScreen;
import io.github.davidqf555.minecraft.towerofgod.client.model.LighthouseModel;
import io.github.davidqf555.minecraft.towerofgod.client.model.ObserverModel;
import io.github.davidqf555.minecraft.towerofgod.client.model.SpearModel;
import io.github.davidqf555.minecraft.towerofgod.client.render.*;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ContainerRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EventBusSubscriber {

    private EventBusSubscriber() {
    }

    @SubscribeEvent
    public static void onRawMouseInput(InputEvent.RawMouseEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (event.isCancelable() && event.getAction() != GLFW.GLFW_RELEASE && client.screen == null && client.player.getEffect(EffectRegistry.REVERSE_FLOW.get()) != null) {
            AttributeInstance attribute = client.player.getAttribute(Attributes.ATTACK_SPEED);
            if (attribute == null || attribute.getValue() <= 0) {
                event.setCanceled(true);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {

        private ModBus() {
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityRegistry.LIGHTHOUSE.get(), LighthouseRenderer::new);
            event.registerEntityRenderer(EntityRegistry.OBSERVER.get(), ObserverRenderer::new);
            event.registerEntityRenderer(EntityRegistry.REGULAR.get(), BipedShinsuUserRenderer::new);
            event.registerEntityRenderer(EntityRegistry.SHINSU.get(), ShinsuRenderer::new);
            event.registerEntityRenderer(EntityRegistry.CLICKER.get(), ClickerRenderer::new);
            event.registerEntityRenderer(EntityRegistry.SHINSU_ARROW.get(), ShinsuArrowRenderer::new);
            event.registerEntityRenderer(EntityRegistry.RANKER.get(), BipedShinsuUserRenderer::new);
            event.registerEntityRenderer(EntityRegistry.SPEAR.get(), SpearRenderer::new);
            event.registerEntityRenderer(EntityRegistry.SHINSU_SPEAR.get(), ShinsuSpearRenderer::new);
            event.registerEntityRenderer(EntityRegistry.DIRECTIONAL_LIGHTNING.get(), DirectionalLightningRenderer::new);
            event.registerEntityRenderer(EntityRegistry.MENTOR.get(), BipedShinsuUserRenderer::new);
        }

        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
            Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values().stream().map(renderer -> (LivingEntityRenderer<Player, HumanoidModel<Player>>) renderer).forEach(ModBus::addCastingLayer);
        }

        @SubscribeEvent
        public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(LighthouseRenderer.LOCATION, LighthouseModel::createLayer);
            event.registerLayerDefinition(ObserverRenderer.LOCATION, ObserverModel::createLayer);
            event.registerLayerDefinition(SpearRenderer.LOCATION, SpearModel::createLayer);
        }

        @SubscribeEvent
        public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(SpearItemStackRenderer.INSTANCE);
        }

        @SubscribeEvent
        public static void onFMLClientSetup(FMLClientSetupEvent event) {
            KeyBindingsList.register();
            event.enqueueWork(() -> {
                MenuScreens.register(ContainerRegistry.LIGHTHOUSE.get(), LighthouseScreen::new);
                ItemProperties.register(ItemRegistry.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pull"), ItemProperties.getProperty(Items.BOW, new ResourceLocation("pull")));
                ItemProperties.register(ItemRegistry.SHINSU_BOW.get(), new ResourceLocation(TowerOfGod.MOD_ID, "pulling"), ItemProperties.getProperty(Items.BOW, new ResourceLocation("pulling")));
                for (RegistryObject<? extends SpearItem> spear : ItemRegistry.SPEARS) {
                    ItemProperties.register(spear.get(), SpearItem.THROWING, ItemProperties.getProperty(Items.TRIDENT, new ResourceLocation("throwing")));
                }
            });
        }

        private static <T extends Player, M extends HumanoidModel<T>> void addCastingLayer(LivingEntityRenderer<T, M> renderer) {
            renderer.addLayer(new CastingLayerRenderer<>(renderer));
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
