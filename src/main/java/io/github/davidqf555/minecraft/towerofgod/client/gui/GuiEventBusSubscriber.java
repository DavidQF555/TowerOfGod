package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.util.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.common.packets.CastShinsuMessage;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class GuiEventBusSubscriber {

    public static StatsMeterGui.Shinsu shinsu = null;
    public static StatsMeterGui.Baangs baangs = null;
    private static ShinsuTechniqueBarGui bar = null;

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
    public static class ClientBus {

        @SubscribeEvent
        public static void preRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
            Minecraft client = Minecraft.getInstance();
            if (usingValid(client)) {
                if (!client.gameSettings.hideGUI && !client.player.isCreative()) {
                    if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.AIR || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT) {
                        event.getMatrixStack().translate(0, -10, 0);
                    } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) {
                        shinsu.render(event.getMatrixStack());
                        baangs.render(event.getMatrixStack());
                    }
                }
                if (KeyBindingsList.OPEN_SHINSU_TECHNIQUES_GUI.isKeyDown() || bar != null && bar.isLocked()) {
                    if (bar == null) {
                        MainWindow window = client.getMainWindow();
                        bar = new ShinsuTechniqueBarGui(window.getScaledWidth() / 2, window.getScaledHeight() / 2 + 20, client.player.rotationYawHead, ClientReference.equipped);
                    }
                    if (!client.gameSettings.hideGUI && event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                        bar.render(event.getMatrixStack(), (int) client.mouseHelper.getMouseX(), (int) client.mouseHelper.getMouseY(), client.getRenderPartialTicks());
                    }
                } else {
                    bar = null;
                }
            }
        }

        @SubscribeEvent
        public static void postRenderGameOverlay(RenderGameOverlayEvent.Post event) {
            Minecraft client = Minecraft.getInstance();
            if (usingValid(client) && !client.gameSettings.hideGUI && !client.player.isCreative() && (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.AIR || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT)) {
                event.getMatrixStack().translate(0, 10, 0);
            }
        }

        private static boolean unlockedTechniques() {
            for (ShinsuTechnique technique : ClientReference.known.keySet()) {
                if (ClientReference.known.get(technique) > 0) {
                    return true;
                }
            }
            return false;
        }

        private static boolean usingValid(Minecraft client) {
            return !ClientReference.equipped.isEmpty() && validStats() && client.player != null && !client.player.isSpectator();
        }

        private static boolean validStats() {
            return shinsu != null && baangs != null && (shinsu.getMax() > 0 || baangs.getMax() > 0);
        }

        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseInputEvent event) {
            Minecraft client = Minecraft.getInstance();
            if (bar != null) {
                Pair<ShinsuTechnique, String> selected = bar.getSelected();
                ShinsuTechnique technique = selected.getFirst();
                if (ClientReference.cooldowns.getOrDefault(technique, 0) <= 0 && event.getButton() == 0) {
                    int action = event.getAction();
                    if (bar.isLocked()) {
                        if (action == GLFW.GLFW_RELEASE) {
                            TowerOfGod.CHANNEL.sendToServer(new CastShinsuMessage(technique, selected.getSecond(), client.pointedEntity == null ? null : client.pointedEntity.getUniqueID()));
                            bar.setLocked(false);
                        }
                    } else if (action == GLFW.GLFW_PRESS) {
                        bar.setLocked(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onClickInput(InputEvent.ClickInputEvent event) {
            if (bar != null) {
                event.setSwingHand(false);
                if (event.isCancelable()) {
                    event.setCanceled(true);
                }
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
            if (event.phase == TickEvent.Phase.START) {
                if (bar != null) {
                    bar.tick();
                }
            }
        }

        @SubscribeEvent
        public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
            initializeMeters();
        }

        @SubscribeEvent
        public static void onClientPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            shinsu = null;
            baangs = null;
            bar = null;
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
