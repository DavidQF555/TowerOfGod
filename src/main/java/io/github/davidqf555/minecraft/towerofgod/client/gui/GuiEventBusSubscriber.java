package io.github.davidqf555.minecraft.towerofgod.client.gui;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.CastShinsuPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ClientOpenCombinationGUIPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ClientUpdateCastingPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
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
        if (client.player != null && !client.player.isSpectator()) {
            if (ClientReference.shinsu != null && ClientReference.shinsu.getMax() > 0 && !client.options.hideGui && !client.player.isCreative() && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                ClientReference.shinsu.render(event.getMatrixStack());
            }
            if (KeyBindingsList.SHINSU_TECHNIQUE_GUI.isDown()) {
                if (ClientReference.combo == null) {
                    TowerOfGod.CHANNEL.sendToServer(new ClientOpenCombinationGUIPacket());
                    TowerOfGod.CHANNEL.sendToServer(new ClientUpdateCastingPacket(true));
                } else if (!client.options.hideGui && event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                    MainWindow window = client.getWindow();
                    ClientReference.combo.render(event.getMatrixStack(), (window.getGuiScaledWidth() - ClientReference.combo.getWidth()) / 2f, (window.getGuiScaledHeight() - ClientReference.combo.getHeight()) / 2f - 50);
                }
            } else {
                ClientReference.combo = null;
                TowerOfGod.CHANNEL.sendToServer(new ClientUpdateCastingPacket(false));
            }
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.KeyInputEvent event) {
        if (ClientReference.combo != null && KeyBindingsList.SHINSU_TECHNIQUE_GUI.matches(event.getKey(), event.getScanCode()) && event.getAction() == GLFW.GLFW_RELEASE) {
            ShinsuTechnique selected = ClientReference.combo.getSelected();
            if (selected != null && !ClientReference.ERRORS.containsKey(selected)) {
                TowerOfGod.CHANNEL.sendToServer(new CastShinsuPacket(selected));
                ClientReference.combo = null;
                TowerOfGod.CHANNEL.sendToServer(new ClientUpdateCastingPacket(false));
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
            ClientReference.combo = null;
        }

        private static void initializeMeters() {
            ClientReference.shinsu = new ShinsuMeterGui(0, 0, 7, 90, 0, 0, 100);
            setMeterPositions();
        }

        private static void setMeterPositions() {
            if (ClientReference.shinsu != null) {
                MainWindow window = Minecraft.getInstance().getWindow();
                int width = window.getGuiScaledWidth();
                int height = window.getGuiScaledHeight();
                ClientReference.shinsu.setX(width - 9);
                ClientReference.shinsu.setY(height / 2 - 45);
            }
        }
    }
}
