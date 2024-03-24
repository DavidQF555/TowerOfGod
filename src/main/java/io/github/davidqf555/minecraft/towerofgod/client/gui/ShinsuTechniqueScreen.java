package io.github.davidqf555.minecraft.towerofgod.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.ClientUpdateBaangsPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShinsuTechniqueScreen extends Screen {

    private static final Component TITLE = TextComponent.EMPTY;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/techniques.png");
    private static final TextureRenderData MAIN = new TextureRenderData(TEXTURE, 256, 256, 0, 0, 195, 136);
    private final Pair<ConfiguredShinsuTechniqueType<?, ?>, Integer>[] unlocked;
    private final Slot[][] slots = new Slot[6][9];
    private final int xSize, ySize, max;
    private int total;

    public ShinsuTechniqueScreen(Set<ConfiguredShinsuTechniqueType<?, ?>> unlocked, Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs, int max) {
        this(unlocked, baangs, max, 195, 136);
    }

    public ShinsuTechniqueScreen(Set<ConfiguredShinsuTechniqueType<?, ?>> unlocked, Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs, int max, int xSize, int ySize) {
        super(TITLE);
        this.xSize = xSize;
        this.ySize = ySize;
        this.max = max;
        this.unlocked = new Pair[unlocked.size()];
        int i = 0;
        for (ConfiguredShinsuTechniqueType<?, ?> config : unlocked) {
            int count = baangs.getOrDefault(config, 0);
            this.unlocked[i++] = Pair.of(config, count);
            total += count;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partial) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        MAIN.render(new RenderContext(poseStack, x, y, getBlitOffset(), xSize, ySize, 0xFFFFFFFF));
        super.render(poseStack, mouseX, mouseY, partial);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 9; i++) {
                slots[j][i] = new Slot(x + 9 + 18 * i, y + 15 + 18 * j, 16, 16);
                addRenderableWidget(slots[j][i]);
            }
        }
        addRenderableWidget(new Slider(x + 175, y + 15, 12, 15, y + 15, y + 106));
        updateSlots(0);
    }

    private void updateSlots(int startRow) {
        int index = startRow * 9;
        for (Slot[] slot : slots) {
            for (Slot value : slot) {
                if (index >= unlocked.length) {
                    value.index = -1;
                } else {
                    value.index = index;
                }
                index++;
            }
        }
    }

    private class Slot extends AbstractWidget {

        private int index = -1;

        public Slot(int x, int y, int width, int height) {
            super(x, y, width, height, TextComponent.EMPTY);
        }

        @Override
        public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
            if (index != -1) {
                unlocked[index].getFirst().getConfig().getDisplay().getIcon().render(new RenderContext(pose, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
                int count = unlocked[index].getSecond();
                if (count > 0) {
                    font.drawShadow(pose, count + "", x, y, 0xFF0000);
                }
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (index != -1) {
                if (total >= max) {
                    total -= unlocked[index].getSecond();
                    unlocked[index] = Pair.of(unlocked[index].getFirst(), 0);
                } else {
                    unlocked[index] = Pair.of(unlocked[index].getFirst(), unlocked[index].getSecond() + 1);
                    total++;
                }
                Registry<ConfiguredShinsuTechniqueType<?, ?>> registry = ConfiguredTechniqueTypeRegistry.getRegistry(ClientReference.getRegistryAccess());
                Map<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> baangs = new HashMap<>();
                Arrays.stream(unlocked).forEach(pair -> {
                    ResourceKey<ConfiguredShinsuTechniqueType<?, ?>> key = ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, registry.getKey(pair.getFirst()));
                    baangs.merge(key, pair.getSecond(), Integer::sum);
                });
                TowerOfGod.CHANNEL.sendToServer(new ClientUpdateBaangsPacket(baangs));
            }
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }

    private class Slider extends AbstractWidget {

        private static final TextureRenderData SCROLLER_LIGHT = new TextureRenderData(TEXTURE, 256, 256, 195, 0, 12, 15);
        private static final TextureRenderData SCROLLER_DARK = new TextureRenderData(TEXTURE, 256, 256, 207, 0, 12, 15);
        private final int min, max;

        public Slider(int x, int y, int width, int height, int min, int max) {
            super(x, y, width, height, TextComponent.EMPTY);
            this.min = min;
            this.max = max;
        }

        @Override
        public void renderButton(PoseStack pose, int mouseX, int mouseY, float partial) {
            IRenderData render = isHoveredOrFocused() ? SCROLLER_DARK : SCROLLER_LIGHT;
            render.render(new RenderContext(pose, x, y, getBlitOffset(), width, height, 0xFFFFFFFF));
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            y = (int) Mth.clamp(mouseY, min, max);
            int totalRows = Math.max(1, Mth.ceil(unlocked.length / 9.0) - 5);
            int startRow = (y - min) * totalRows / (max - min + 1);
            updateSlots(startRow);
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

    }
}
