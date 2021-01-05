package com.davidqf.minecraft.towerofgod.client.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.*;
import com.davidqf.minecraft.towerofgod.client.render.LighthouseRenderer;
import com.davidqf.minecraft.towerofgod.client.render.ObserverRenderer;
import com.davidqf.minecraft.towerofgod.client.render.RegularRenderer;
import com.davidqf.minecraft.towerofgod.client.render.ShinsuRenderer;
import com.davidqf.minecraft.towerofgod.common.entities.LighthouseEntity;

import com.davidqf.minecraft.towerofgod.common.packets.*;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import com.google.common.collect.Maps;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    public static StatsMeterGui.Shinsu shinsu = null;
    public static StatsMeterGui.Baangs baangs = null;
    private static ShinsuSkillWheelGui wheel = null;
    private static IShinsuStats clonedStats = null;
    private static IPlayerShinsuEquips clonedEquips = null;

    @SubscribeEvent
    public static void preRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        if (!client.gameSettings.hideGUI && !client.player.isCreative() && usingValid()) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
                event.getMatrixStack().translate(0, -10, 0);
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
                shinsu.render(event.getMatrixStack());
                baangs.render(event.getMatrixStack());
                UpdateStatsMetersMessage.INSTANCE.sendToServer(new UpdateStatsMetersMessage(0, 0, 0, 0));
            }
        }
    }

    @SubscribeEvent
    public static void postRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (usingValid()) {
            Minecraft client = Minecraft.getInstance();
            if (!client.player.isCreative() && (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR)) {
                event.getMatrixStack().translate(0, 10, 0);
            }
            if (KeyBindingsList.OPEN_WHEEL.isKeyDown() || wheel != null && wheel.isLocked()) {
                if (wheel == null) {
                    wheel = new ShinsuSkillWheelGui();
                }
                if (!client.gameSettings.hideGUI) {
                    UpdateClientCooldownsMessage.INSTANCE.sendToServer(new UpdateClientCooldownsMessage());
                    UpdateClientCanCastMessage.INSTANCE.sendToServer(new UpdateClientCanCastMessage(client.pointedEntity == null ? null : client.pointedEntity.getUniqueID()));
                    wheel.render(event.getMatrixStack());
                }
            } else {
                wheel = null;
            }
        }
    }

    private static boolean unlockedTechniques() {
        for (ShinsuTechnique technique : ShinsuEquipScreen.known.keySet()) {
            if (ShinsuEquipScreen.known.get(technique) > 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean usingValid() {
        boolean equipped = false;
        for (ShinsuTechnique technique : ShinsuSkillWheelGui.equipped) {
            if (technique != null) {
                equipped = true;
                break;
            }
        }
        return equipped && validStats();
    }

    private static boolean validStats() {
        return shinsu != null && baangs != null && (shinsu.getMax() > 0 || baangs.getMax() > 0);
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            if (wheel != null && wheel.getSelected() != null && ShinsuSkillWheelGui.canCast.get(wheel.getSelected()) && event.getButton() == 0) {
                int action = event.getAction();
                if (wheel.isLocked()) {
                    if (action == GLFW.GLFW_RELEASE) {
                        CastShinsuMessage.INSTANCE.sendToServer(new CastShinsuMessage(wheel.getSelected(), client.pointedEntity == null ? null : client.pointedEntity.getUniqueID()));
                        wheel = null;
                    }
                } else if (action == GLFW.GLFW_PRESS) {
                    wheel.lock();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGuiScreenEvent(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen screen = event.getGui();
        if (unlockedTechniques() && validStats()) {
            if (screen instanceof InventoryScreen) {
                InventoryScreen inventory = (InventoryScreen) screen;
                int xSize = inventory.getXSize();
                int ySize = inventory.getYSize();
                int x = inventory.getGuiLeft() + xSize * 3 / 4;
                int y = inventory.getGuiTop() + ySize * 61 / 166;
                int width = 20 * xSize / 176;
                int height = 18 * ySize / 166;
                event.addWidget(new ShinsuEquipScreen.OpenButton(screen, x, y, width, height));
            } else if (screen instanceof CreativeScreen) {
                CreativeScreen creative = (CreativeScreen) screen;
                int xSize = creative.getXSize();
                int ySize = creative.getYSize();
                int x = creative.getGuiLeft() + xSize * 126 / 195;
                int y = creative.getGuiTop() + ySize * 32 / 136;
                int width = 20 * xSize / 195;
                int height = 18 * ySize / 136;
                event.addWidget(new ShinsuEquipScreen.OpenButton(screen, x, y, width, height));
            }
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
                    ShinsuStatsTickMessage.INSTANCE.sendToServer(new ShinsuStatsTickMessage());
                }
            }
        }

        @SubscribeEvent
        public static void onServerPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            Entity entity = event.getEntity();
            IShinsuStats stats = IShinsuStats.get(entity);
            Map<ShinsuTechnique, Integer> known = Maps.newEnumMap(ShinsuTechnique.class);
            for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                known.put(technique, stats.getTechniqueLevel(technique));
            }
            UpdateClientKnownMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientKnownMessage(known));
            UpdateStatsMetersMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
            IPlayerShinsuEquips equipped = IPlayerShinsuEquips.get(entity);
            UpdateClientEquippedMessage.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientEquippedMessage(equipped.getEquipped()));
        }

        @SubscribeEvent
        public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
            initializeMeters();
        }

        @SubscribeEvent
        public static void onClientPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            shinsu = null;
            baangs = null;
            wheel = null;
        }

        @SubscribeEvent
        public static void onPlayerInteract(PlayerInteractEvent event) {
            if (event.isCancelable() && event.getEntityLiving().getActivePotionEffect(RegistryHandler.REVERSE_FLOW_EFFECT.get()) != null) {
                event.setCanceled(true);
            }
        }

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

        private static void initializeMeters() {
            MainWindow window = Minecraft.getInstance().getMainWindow();
            int y = window.getScaledHeight() - 36;
            shinsu = new StatsMeterGui.Shinsu(window.getScaledWidth() / 2 - 91, y, 85, 5, 0, 0);
            baangs = new StatsMeterGui.Baangs(window.getScaledWidth() / 2 + 6, y, 85, 5, 0, 0);
        }
    }
}
