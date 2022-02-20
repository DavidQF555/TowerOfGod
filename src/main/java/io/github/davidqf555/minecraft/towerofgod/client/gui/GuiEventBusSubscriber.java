package io.github.davidqf555.minecraft.towerofgod.client.gui;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.CastShinsuPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenCombinationGUIPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public final class GuiEventBusSubscriber {

    private GuiEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void preRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        if (usingValid(client)) {
            if (renderBars() && !client.gameSettings.hideGUI && !client.player.isCreative()) {
                if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.AIR || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT) {
                    event.getMatrixStack().translate(0, -10, 0);
                } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) {
                    ClientReference.shinsu.render(event.getMatrixStack());
                    ClientReference.baangs.render(event.getMatrixStack());
                }
            }
            if (KeyBindingsList.SHINSU_TECHNIQUE_GUI.isKeyDown()) {
                if (ClientReference.combo == null) {
                    TowerOfGod.CHANNEL.sendToServer(new OpenCombinationGUIPacket());
                } else if (!client.gameSettings.hideGUI && event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                    MainWindow window = client.getMainWindow();
                    ClientReference.combo.render(event.getMatrixStack(), (window.getScaledWidth() - ClientReference.combo.getWidth()) / 2f, (window.getScaledHeight() - ClientReference.combo.getHeight()) / 2f - 50);
                }
            } else {
                ClientReference.combo = null;
            }
        }
    }

    @SubscribeEvent
    public static void postRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (usingValid(client) && renderBars() && !client.gameSettings.hideGUI && !client.player.isCreative() && (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD || event.getType() == RenderGameOverlayEvent.ElementType.ARMOR || event.getType() == RenderGameOverlayEvent.ElementType.AIR || event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT)) {
            event.getMatrixStack().translate(0, 10, 0);
        }
    }

    private static boolean usingValid(Minecraft client) {
        return client.player != null && !client.player.isSpectator();
    }

    private static boolean renderBars() {
        return ClientReference.shinsu != null && ClientReference.baangs != null && (ClientReference.shinsu.getMax() > 0 || ClientReference.baangs.getMax() > 0);
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.KeyInputEvent event) {
        if (ClientReference.combo != null && KeyBindingsList.SHINSU_TECHNIQUE_GUI.matchesKey(event.getKey(), event.getScanCode()) && event.getAction() == GLFW.GLFW_RELEASE) {
            ShinsuTechnique selected = ClientReference.combo.getSelected();
            if (selected != null && !ClientReference.ERRORS.containsKey(selected)) {
                TowerOfGod.CHANNEL.sendToServer(new CastShinsuPacket(selected));
                ClientReference.combo = null;
            }
        }
    }

    @SubscribeEvent
    public static void onClickInput(InputEvent.ClickInputEvent event) {
        if (ClientReference.combo != null && event.isCancelable()) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        ForgeBus.setMeterPositions();
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus {

        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                if (ClientReference.combo != null) {
                    ClientReference.combo.tick();
                }
            }
        }

        @SubscribeEvent
        public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggedInEvent event) {
            initializeMeters();
        }

        @SubscribeEvent
        public static void onClientPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
            ClientReference.shinsu = null;
            ClientReference.baangs = null;
            ClientReference.combo = null;
        }

        private static void initializeMeters() {
            ClientReference.shinsu = new StatsMeterGui(0, 0, 85, 5, 0, 0, 200);
            ClientReference.baangs = new StatsMeterGui(0, 0, 85, 5, 0, 0, 20);
            setMeterPositions();
        }

        private static void setMeterPositions() {
            MainWindow window = Minecraft.getInstance().getMainWindow();
            int width = window.getScaledWidth();
            int y = window.getScaledHeight() - 36;
            if (ClientReference.shinsu != null) {
                ClientReference.shinsu.setX(width / 2 - 91);
                ClientReference.shinsu.setY(y);
            }
            if (ClientReference.baangs != null) {
                ClientReference.baangs.setX(width / 2 + 6);
                ClientReference.baangs.setY(y);
            }
        }
    }
}
