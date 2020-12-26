package com.davidqf.minecraft.towerofgod.client.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.*;
import com.davidqf.minecraft.towerofgod.client.render.LighthouseRenderer;
import com.davidqf.minecraft.towerofgod.client.render.ObserverRenderer;
import com.davidqf.minecraft.towerofgod.client.render.RegularRenderer;
import com.davidqf.minecraft.towerofgod.client.render.ShinsuRenderer;
import com.davidqf.minecraft.towerofgod.common.entities.LighthouseEntity;

import com.davidqf.minecraft.towerofgod.common.packets.PlayerEquipMessage;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuTechniqueMessage;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuStatsSyncMessage;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
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

import java.util.List;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    private static StatsMeterGui.Shinsu shinsu = null;
    private static StatsMeterGui.Baangs baangs = null;
    private static ShinsuSkillWheelGui wheel = null;

    @SubscribeEvent
    public static void preRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        IShinsuStats stats = IShinsuStats.get(Minecraft.getInstance().player);
        if (stats.getShinsu() > 0 || stats.getBaangs() > 0) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
                event.getMatrixStack().translate(0, -10, 0);
            } else if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
                if (shinsu != null) {
                    shinsu.render(event.getMatrixStack());
                }
                if (baangs != null) {
                    baangs.render(event.getMatrixStack());
                }
            }
        }
    }

    @SubscribeEvent
    public static void postRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        IShinsuStats stats = IShinsuStats.get(client.player);
        if ((event.getType() == RenderGameOverlayEvent.ElementType.HEALTH || event.getType() == RenderGameOverlayEvent.ElementType.FOOD) && (stats.getShinsu() > 0 || stats.getBaangs() > 0)) {
            event.getMatrixStack().translate(0, 10, 0);
        }
        if (!client.gameSettings.hideGUI) {
            if (KeyBindingsList.OPEN_WHEEL.isKeyDown() || (wheel != null && wheel.isLocked())) {
                if (wheel == null) {
                    wheel = new ShinsuSkillWheelGui();
                }
                wheel.render(event.getMatrixStack());
            } else {
                wheel = null;
            }
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            IShinsuStats stats = IShinsuStats.get(client.player);
            if (wheel != null && wheel.getSelected() != null && wheel.getSelected().getBuilder().canCast(wheel.getSelected(), client.player, stats.getTechniqueLevel(wheel.getSelected()), client.pointedEntity, client.player.getLookVec()) && event.getButton() == 0) {
                int action = event.getAction();
                if (action == GLFW.GLFW_RELEASE && wheel.isLocked()) {
                    stats.cast(client.player, wheel.getSelected(), client.pointedEntity, client.player.getLookVec());
                    wheel = null;
                } else if (action == GLFW.GLFW_PRESS) {
                    wheel.lock();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGuiScreenEvent(GuiScreenEvent.InitGuiEvent.Post event) {
        Screen screen = event.getGui();
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
                    boolean changed = false;
                    IShinsuStats stats = IShinsuStats.get(player);
                    List<ShinsuTechniqueInstance> techniques = stats.getTechniques();
                    for (int i = techniques.size() - 1; i >= 0; i--) {
                        ShinsuTechniqueInstance attack = techniques.get(i);
                        attack.tick(player.world);
                        ShinsuTechniqueMessage.INSTANCE.sendToServer(new ShinsuTechniqueMessage(ShinsuTechniqueMessage.Action.TICK, attack));
                        if (attack.ticksLeft() <= 0) {
                            attack.onEnd(player.world);
                            ShinsuTechniqueMessage.INSTANCE.sendToServer(new ShinsuTechniqueMessage(ShinsuTechniqueMessage.Action.END, attack));
                            stats.removeTechnique(attack);
                        }
                        changed = true;
                    }
                    for (ShinsuTechnique key : ShinsuTechnique.values()) {
                        int time = stats.getCooldown(key);
                        if (time > 0) {
                            stats.addCooldown(key, time - 1);
                            changed = true;
                        }
                    }
                    if (changed) {
                        ShinsuStatsSyncMessage.INSTANCE.sendToServer(new ShinsuStatsSyncMessage(stats));
                    }
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
            MainWindow window = Minecraft.getInstance().getMainWindow();
            int y = window.getScaledHeight() - 36;
            shinsu = new StatsMeterGui.Shinsu(player, window.getScaledWidth() / 2 - 91, y, 85, 5);
            baangs = new StatsMeterGui.Baangs(player, window.getScaledWidth() / 2 + 6, y, 85, 5);
        }

        @SubscribeEvent
        public static void onEvent(Event event) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                IShinsuStats stats = IShinsuStats.get(player);
                if (stats instanceof IShinsuStats.AdvancementShinsuStats) {
                    for (ShinsuAdvancement advancement : ((IShinsuStats.AdvancementShinsuStats) stats).getUnlockedAdvancements()) {
                        ShinsuAdvancementCriteria criteria = advancement.getCriteria();
                        if (criteria.correctEvent(event)) {
                            criteria.onEvent(player, event);
                        }
                    }
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
        public static void onClonePlayerEvent(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
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
