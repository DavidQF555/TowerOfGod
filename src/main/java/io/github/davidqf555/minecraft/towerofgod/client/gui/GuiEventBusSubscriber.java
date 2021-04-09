package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.google.common.collect.Maps;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IPlayerShinsuEquips;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.packets.*;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class GuiEventBusSubscriber {

    public static StatsMeterGui.Shinsu shinsu = null;
    public static StatsMeterGui.Baangs baangs = null;
    private static ShinsuSkillWheelGui wheel = null;

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
    public static class ClientBus {

        @SubscribeEvent
        public static void preRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
            Minecraft client = Minecraft.getInstance();
            if (usingValid()) {
                if (!client.gameSettings.hideGUI && !client.player.isCreative()) {
                    if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.AIR || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT) {
                        event.getMatrixStack().translate(0, -10, 0);
                    } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) {
                        shinsu.render(event.getMatrixStack());
                        baangs.render(event.getMatrixStack());
                    }
                }
                if (KeyBindingsList.OPEN_WHEEL.isKeyDown() || wheel != null && wheel.isLocked()) {
                    if (wheel == null) {
                        wheel = new ShinsuSkillWheelGui();
                    }
                    if (!client.gameSettings.hideGUI && event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                        wheel.render(event.getMatrixStack());
                    }
                } else {
                    wheel = null;
                }
            }
        }

        @SubscribeEvent
        public static void postRenderGameOverlay(RenderGameOverlayEvent.Post event) {
            Minecraft client = Minecraft.getInstance();
            if (usingValid() && !client.gameSettings.hideGUI && !client.player.isCreative() && (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.AIR || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT)) {
                event.getMatrixStack().translate(0, 10, 0);
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
            if (wheel != null) {
                ShinsuTechnique selected = wheel.getSelected();
                if (selected != null && ShinsuSkillWheelGui.cooldowns.getOrDefault(selected, 0) <= 0 && event.getButton() == 0) {
                    int action = event.getAction();
                    if (wheel.isLocked()) {
                        if (action == GLFW.GLFW_RELEASE) {
                            TowerOfGod.CHANNEL.sendToServer(new CastShinsuMessage(selected, client.pointedEntity == null ? null : client.pointedEntity.getUniqueID()));
                            wheel = null;
                        }
                    } else if (action == GLFW.GLFW_PRESS) {
                        wheel.lock();
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onClickInput(InputEvent.ClickInputEvent event) {
            if (wheel != null) {
                event.setSwingHand(false);
            }
        }

        @SubscribeEvent
        public static void onPlayerInteract(PlayerInteractEvent event) {
            if (wheel != null && event.isCancelable()) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onGuiScreenEvent(GuiScreenEvent.InitGuiEvent.Post event) {
            Screen screen = event.getGui();
            if (unlockedTechniques() && validStats()) {
                if (screen instanceof InventoryScreen) {
                    InventoryScreen inventory = (InventoryScreen) screen;
                    int x = inventory.getGuiLeft() + 132;
                    int y = inventory.getGuiTop() + 61;
                    event.addWidget(new ShinsuEquipScreen.OpenButton(screen, x, y, 20, 18));
                } else if (screen instanceof CreativeScreen) {
                    CreativeScreen creative = (CreativeScreen) screen;
                    int x = creative.getGuiLeft() + 126;
                    int y = creative.getGuiTop() + 32;
                    event.addWidget(new ShinsuEquipScreen.OpenButton(screen, x, y, 20, 18));
                }
            }
        }

        @SubscribeEvent
        public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
            ForgeBus.setMeterPositions();
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
                    TowerOfGod.CHANNEL.sendToServer(new ShinsuStatsTickMessage());
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
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientKnownMessage(known));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateStatsMetersMessage(stats.getShinsu(), stats.getMaxShinsu(), stats.getBaangs(), stats.getMaxBaangs()));
            IPlayerShinsuEquips equipped = IPlayerShinsuEquips.get(entity);
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new UpdateClientEquippedMessage(equipped.getEquipped()));
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

        private static void initializeMeters() {
            shinsu = new StatsMeterGui.Shinsu(0, 0, 85, 5, 0, 0);
            baangs = new StatsMeterGui.Baangs(0, 0, 85, 5, 0, 0);
            setMeterPositions();
        }

        private static void setMeterPositions() {
            MainWindow window = Minecraft.getInstance().getMainWindow();
            int width = window.getScaledWidth();
            int y = window.getScaledHeight() - 36;
            if (shinsu != null) {
                shinsu.setX(width / 2 - 91);
                shinsu.setY(y);
            }
            if (baangs != null) {
                baangs.setX(width / 2 + 6);
                baangs.setY(y);
            }
        }
    }
}
