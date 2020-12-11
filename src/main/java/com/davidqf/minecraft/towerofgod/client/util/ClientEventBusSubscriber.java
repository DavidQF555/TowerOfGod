package com.davidqf.minecraft.towerofgod.client.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.*;
import com.davidqf.minecraft.towerofgod.client.render.LighthouseRenderer;
import com.davidqf.minecraft.towerofgod.client.render.ObserverRenderer;
import com.davidqf.minecraft.towerofgod.client.render.RegularRenderer;
import com.davidqf.minecraft.towerofgod.client.render.ShinsuRenderer;
import com.davidqf.minecraft.towerofgod.common.entities.LighthouseEntity;

import com.davidqf.minecraft.towerofgod.common.packets.PlayerEquipMessage;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuUserTickMessage;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuStatsSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    private static ShinsuSkillWheelGui wheel = null;

    @SubscribeEvent
    public static void postRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (KeyBindingsList.OPEN_WHEEL.isKeyDown() || (wheel != null && wheel.isLocked())) {
            if (wheel == null) {
                wheel = new ShinsuSkillWheelGui();
            }
            wheel.render(event.getMatrixStack());
        } else {
            wheel = null;
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent event) {
        if (wheel != null && wheel.getSelected() != null && event.getButton() == 0) {
            int action = event.getAction();
            if (action == GLFW.GLFW_RELEASE && wheel.isLocked()) {
                Minecraft client = Minecraft.getInstance();
                IShinsuStats stats = IShinsuStats.get(client.player);
                stats.cast(client.player, wheel.getSelected(), client.pointedEntity, client.player.getLookVec());
                wheel = null;
            } else if (action == GLFW.GLFW_PRESS) {
                wheel.lock();
            }
        }
    }

    @SubscribeEvent
    public static void onGuiScreenEvent(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen screen = event.getGui();
        if (screen instanceof InventoryScreen) {
            InventoryScreen inventory = (InventoryScreen) screen;
            int x = inventory.getGuiLeft() + inventory.getXSize() * 3 / 4;
            int y = inventory.getGuiTop() + 61;
            event.addWidget(new ShinsuEquipScreen.OpenButton(screen, x, y));
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
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft client = Minecraft.getInstance();
            if (event.phase == TickEvent.Phase.START) {
                ClientPlayerEntity player = client.player;
                if (wheel != null) {
                    wheel.tick();
                }
                if (player != null) {
                    ShinsuUserTickMessage.INSTANCE.sendToServer(new ShinsuUserTickMessage());
                }
            }
                if (KeyBindingsList.OPEN_TREE.isPressed()) {
                    if (client.currentScreen == null) {
                        client.displayGuiScreen(new ShinsuSkillTreeScreen());
                    }
                }
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            PlayerEntity player = event.getPlayer();
            ShinsuStatsSyncMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ShinsuStatsSyncMessage(IShinsuStats.get(player)));
            PlayerEquipMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PlayerEquipMessage(IPlayerShinsuEquips.get(player)));
        }

        @SubscribeEvent
        public static void onEvent(Event event) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                for (ShinsuAdvancement unlocked : ShinsuAdvancement.getUnlocked(player)) {
                    unlocked.getCriteria().onEvent(player, event);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerInteract(PlayerInteractEvent event) {
            if (event.isCancelable() && event.getEntityLiving().getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get()) != null) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onClonePlayerEvent(PlayerEvent.Clone event){
            if(event.isWasDeath()){
                PlayerEntity original = event.getOriginal();
                Entity clone = event.getEntity();
                IShinsuStats.get(clone).deserialize(IShinsuStats.get(original).serialize());
                IPlayerShinsuEquips.get(clone).deserialize(IPlayerShinsuEquips.get(original).serialize());
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
