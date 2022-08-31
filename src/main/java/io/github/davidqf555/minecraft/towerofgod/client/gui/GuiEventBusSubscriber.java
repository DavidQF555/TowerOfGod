package io.github.davidqf555.minecraft.towerofgod.client.gui;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public final class GuiEventBusSubscriber {

    private GuiEventBusSubscriber() {
    }

    @SubscribeEvent
    public static void preRenderGameOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !client.player.isSpectator()) {
            if (StatsMeterGui.shouldRender() && shouldMove(event.getOverlay().overlay())) {
                event.getPoseStack().translate(0, -10, 0);
            }
        }
    }

    @SubscribeEvent
    public static void postRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && !client.player.isSpectator() && StatsMeterGui.shouldRender() && shouldMove(event.getOverlay().overlay())) {
            event.getPoseStack().translate(0, 10, 0);
        }
    }

    private static boolean shouldMove(IGuiOverlay overlay) {
        return overlay.equals(VanillaGuiOverlay.AIR_LEVEL) || overlay.equals(VanillaGuiOverlay.PLAYER_HEALTH) || overlay.equals(VanillaGuiOverlay.FOOD_LEVEL) || overlay.equals(VanillaGuiOverlay.MOUNT_HEALTH);
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static final class ForgeBus {

        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                if (ClientReference.COMBO.isEnabled()) {
                    ClientReference.COMBO.tick();
                }
            }
        }

    }
}
