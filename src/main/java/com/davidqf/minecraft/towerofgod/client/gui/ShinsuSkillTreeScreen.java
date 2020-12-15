package com.davidqf.minecraft.towerofgod.client.gui;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
import com.davidqf.minecraft.towerofgod.client.util.KeyBindingsList;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuCriteriaCompletionMessage;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.packets.ShinsuStatsSyncMessage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShinsuSkillTreeScreen extends Screen {

    private static final TranslationTextComponent TITLE = new TranslationTextComponent(TowerOfGod.MOD_ID, "gui." + TowerOfGod.MOD_ID + ".shinsu_skill_tree");
    private static final RenderInfo BACKGROUND = new RenderInfo(new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/background.png"), 108, 72, 0, 0, 108, 72);
    private static final ResourceLocation WIDGETS = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_tree_widgets.png");
    private static final int TEXTURE_WIDTH = 108;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int MIN_DISTANCE = 20;
    private static final int UNLOCKED_LINE_COLOR = 0xFFFFFFFF;
    private static final int LOCKED_LINE_COLOR = 0xBBBBBBBB;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private ShinsuAdvancementInfoTabGui tab;
    private final IShinsuStats.AdvancementShinsuStats stats;
    private final Map<ShinsuAdvancement, List<int[]>> hLines;
    private final Map<ShinsuAdvancement, List<int[]>> vLines;
    private int posX;
    private int posY;

    public ShinsuSkillTreeScreen() {
        super(TITLE);
        tab = null;
        hLines = new HashMap<>();
        vLines = new HashMap<>();
        posX = 0;
        posY = 0;
        IShinsuStats s = IShinsuStats.get(Minecraft.getInstance().player);
        if (s instanceof IShinsuStats.AdvancementShinsuStats) {
            stats = ((IShinsuStats.AdvancementShinsuStats) s);
        } else {
            stats = null;
            closeScreen();
        }
    }

    @Override
    public void init() {
        for (ShinsuAdvancementButton button : getAdvancementButtons(width / 2, height / 2)) {
            addButton(button);
        }
        tab = new ShinsuAdvancementInfoTabGui(this);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyBindingsList.OPEN_TREE.getKey().getKeyCode()) {
            closeScreen();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            posX += deltaX;
            posY += deltaY;
            return true;
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        BACKGROUND.render(matrixStack, 0, 0, getBlitOffset(), width, height, 0xFFFFFFFF);
        renderLines(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        tab.render(matrixStack, mouseX, mouseY, partialTicks);
        renderText(matrixStack);
        RenderSystem.disableBlend();
    }

    private void renderLines(MatrixStack matrixStack) {
        for (List<int[]> list : hLines.values()) {
            for (int[] line : list) {
                hLine(matrixStack, line[0] + posX, line[1] + posX, line[2] + posY, line[3]);
            }
        }
        for (List<int[]> list : vLines.values()) {
            for (int[] line : list) {
                vLine(matrixStack, line[0] + posX, line[1] + posY, line[2] + posY, line[3]);
            }
        }
    }

    private void renderText(MatrixStack matrixStack) {
        int dif = 1;
        drawString(matrixStack, minecraft.fontRenderer, ShinsuAdvancementInfoTabGui.RewardIcon.RESISTANCE.copyRaw().appendString(" x" + stats.getResistance()), dif, dif, TEXT_COLOR);
        drawString(matrixStack, minecraft.fontRenderer, ShinsuAdvancementInfoTabGui.RewardIcon.TENSION.copyRaw().appendString(" x" + stats.getTension()), dif, dif + minecraft.fontRenderer.FONT_HEIGHT, TEXT_COLOR);
    }

    public void select(@Nullable ShinsuAdvancementButton button) {
        tab.setAdvancement(button == null ? null : button.advancement);
    }

    private List<ShinsuAdvancementButton> getAdvancementButtons(int x, int y) {
        Map<ShinsuAdvancement, Integer> roots = new HashMap<>();
        int yPos = 0;
        for (ShinsuAdvancement advancement : ShinsuAdvancement.values()) {
            if (advancement.getParent() == null) {
                int space = getYSpace(advancement);
                roots.put(advancement, space);
                yPos += space;
            }
        }
        yPos = y - yPos / 2;
        List<ShinsuAdvancementButton> buttons = new ArrayList<>();
        for (ShinsuAdvancement advancement : roots.keySet()) {
            int space = roots.get(advancement);
            buttons.addAll(getAdvancementButtons(advancement, space, x, yPos + space / 2));
            yPos += space;
        }
        return buttons;
    }

    private List<ShinsuAdvancementButton> getAdvancementButtons(ShinsuAdvancement advancement, int ySpace, int x, int y) {
        List<ShinsuAdvancementButton> buttons = new ArrayList<>();
        List<ShinsuAdvancement> children = advancement.getDirectChildren();
        List<int[]> hLines = new ArrayList<>();
        List<int[]> vLines = new ArrayList<>();
        if (!children.isEmpty()) {
            int lineColor = stats.getAdvancements().get(advancement).isComplete() ? UNLOCKED_LINE_COLOR : LOCKED_LINE_COLOR;
            hLines.add(new int[]{x + ShinsuAdvancementButton.WIDTH / 2, x + (ShinsuAdvancementButton.WIDTH + MIN_DISTANCE) / 2 - 1, y, lineColor});
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            int yPos = y - ySpace / 2;
            for (ShinsuAdvancement child : children) {
                int space = getYSpace(child);
                int bX = x + ShinsuAdvancementButton.WIDTH + MIN_DISTANCE;
                int bY = yPos + space / 2;
                minY = Math.min(minY, bY);
                maxY = Math.max(maxY, bY);
                hLines.add(new int[]{bX - (ShinsuAdvancementButton.WIDTH + MIN_DISTANCE) / 2, bX - ShinsuAdvancementButton.WIDTH / 2 - 1, bY, lineColor});
                buttons.addAll(getAdvancementButtons(child, space, bX, bY));
                yPos += space;
            }
            vLines.add(new int[]{x + (ShinsuAdvancementButton.WIDTH + MIN_DISTANCE) / 2, minY, maxY, lineColor});
        }
        this.hLines.put(advancement, hLines);
        this.vLines.put(advancement, vLines);
        buttons.add(new ShinsuAdvancementButton(this, advancement, x - ShinsuAdvancementButton.WIDTH / 2, y - ShinsuAdvancementButton.HEIGHT / 2));
        return buttons;
    }

    private int getYSpace(ShinsuAdvancement advancement) {
        int sum = 0;
        List<ShinsuAdvancement> children = advancement.getDirectChildren();
        if (children.isEmpty()) {
            sum += MIN_DISTANCE + ShinsuAdvancementButton.HEIGHT;
        } else {
            for (ShinsuAdvancement child : advancement.getDirectChildren()) {
                sum += getYSpace(child);
            }
        }
        return sum;
    }

    private void updateServer() {
        ShinsuStatsSyncMessage.INSTANCE.sendToServer(new ShinsuStatsSyncMessage(stats));
    }

    private static class ShinsuAdvancementButton extends Button {

        private static final int WIDTH = 20;
        private static final int HEIGHT = 20;
        private static final int COMPLETE_COLOR = 0xFFFFFFFF;
        private static final int UNLOCKED_COLOR = 0xFFBBBBBB;
        private static final int LOCKED_COLOR = 0x88888888;
        private static final RenderInfo RENDER = new RenderInfo(WIDGETS, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 12, 20, 20);
        private final ShinsuSkillTreeScreen screen;
        private final ShinsuAdvancement advancement;
        private final int baseX;
        private final int baseY;

        private ShinsuAdvancementButton(ShinsuSkillTreeScreen screen, ShinsuAdvancement advancement, int baseX, int baseY) {
            super(baseX, baseY, WIDTH, HEIGHT, StringTextComponent.EMPTY, press -> {
                if (press instanceof ShinsuAdvancementButton) {
                    screen.select((ShinsuAdvancementButton) press);
                }
            });
            this.screen = screen;
            this.advancement = advancement;
            this.baseX = baseX;
            this.baseY = baseY;
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            x = baseX + screen.posX;
            y = baseY + screen.posY;
            Map<ShinsuAdvancement, ShinsuAdvancementProgress> advancements = screen.stats.getAdvancements();
            boolean complete = advancements.get(advancement).isComplete();
            ShinsuAdvancement parent = advancement.getParent();
            boolean parentComplete = parent == null || advancements.get(parent).isComplete();
            int blitOffset = getBlitOffset();
            int color = complete ? COMPLETE_COLOR : (parentComplete ? UNLOCKED_COLOR : LOCKED_COLOR);
            RENDER.render(matrixStack, x, y, blitOffset, WIDTH, HEIGHT, color);
            advancement.getIcon().render(matrixStack, x, y, blitOffset, width, height, color);
        }
    }

    private static class ShinsuAdvancementInfoTabGui extends AbstractGui implements IRenderable {

        private static final int HEIGHT = 60;
        private static final RenderInfo RENDER = new RenderInfo(WIDGETS, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0, 108, 10);
        private static final int TEXT_COLOR = 0xFFFFFFFF;
        private static final ITextComponent REWARDS = new TranslationTextComponent("button." + TowerOfGod.MOD_ID + ".rewards").appendString(": ");
        private final ShinsuSkillTreeScreen screen;
        private ShinsuAdvancement advancement;
        private final CloseButton close;
        private final CompletionButton complete;
        private final List<RewardIcon> rewards;

        private ShinsuAdvancementInfoTabGui(ShinsuSkillTreeScreen screen) {
            this.screen = screen;
            advancement = null;
            close = new CloseButton(this, screen.width - CloseButton.WIDTH - 1, screen.height - HEIGHT + 1);
            complete = new CompletionButton(this, screen.width - CompletionButton.WIDTH - 1, screen.height - CompletionButton.HEIGHT - 1);
            screen.addListener(close);
            screen.addListener(complete);
            rewards = new ArrayList<>();
        }

        private void setAdvancement(@Nullable ShinsuAdvancement advancement) {
            this.advancement = advancement;
            rewards.clear();
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
            if (advancement == null) {
                close.active = false;
                complete.active = false;
            } else {
                RENDER.render(matrixStack, 0, screen.height - HEIGHT, getBlitOffset(), screen.width, HEIGHT, 0xFFFFFFFF);
                int x = screen.width / 40;
                int y = screen.height - 7 * HEIGHT / 8;
                drawString(matrixStack, screen.font, advancement.getName(), x, y, TEXT_COLOR);
                y += screen.font.FONT_HEIGHT;
                for (ITextComponent text : advancement.getCriteria().getText(screen.minecraft.player)) {
                    drawString(matrixStack, screen.font, text, x, y, TEXT_COLOR);
                    y += screen.font.FONT_HEIGHT;
                }
                int posX = screen.width / 2;
                int posY = screen.height - 7 * HEIGHT / 8;
                drawString(matrixStack, screen.font, REWARDS, posX, posY, TEXT_COLOR);
                posX += screen.font.func_238414_a_(REWARDS) + 1;
                if (rewards.isEmpty()) {
                    int iconY = posY + (screen.font.FONT_HEIGHT - RewardIcon.HEIGHT) / 2;
                    ShinsuAdvancement.Reward reward = advancement.getReward();
                    int shinsu = reward.getShinsu();
                    if (shinsu != 0) {
                        rewards.add(new RewardIcon(this, ShinsuIcons.SWIRL, posX, iconY, new StringTextComponent("+" + reward.getShinsu() + " ").append(RewardIcon.SHINSU)));
                        posX += RewardIcon.WIDTH + 1;
                    }
                    int baangs = reward.getBaangs();
                    if (baangs != 0) {
                        rewards.add(new RewardIcon(this, ShinsuIcons.BAANGS, posX, iconY, new StringTextComponent("+" + reward.getBaangs() + " ").append(RewardIcon.BAANGS)));
                        posX += RewardIcon.WIDTH + 1;
                    }
                    double resistance = reward.getResistance();
                    if (resistance != 1) {
                        rewards.add(new RewardIcon(this, ShinsuIcons.RESISTANCE, posX, iconY, new StringTextComponent("x" + reward.getResistance() + " ").append(RewardIcon.RESISTANCE)));
                        posX += RewardIcon.WIDTH + 1;
                    }
                    double tension = reward.getTension();
                    if (tension != 1) {
                        rewards.add(new RewardIcon(this, ShinsuIcons.TENSION, posX, iconY, new StringTextComponent("x" + reward.getTension() + " ").append(RewardIcon.TENSION)));
                        posX += RewardIcon.WIDTH + 1;
                    }
                    for (ShinsuTechnique technique : reward.getTechniques()) {
                        rewards.add(new RewardIcon(this, technique.getIcon(), posX, iconY, new TranslationTextComponent(RewardIcon.LEARN_TECHNIQUE_KEY, technique.getName())));
                        posX += RewardIcon.WIDTH + 1;
                    }
                }
                close.active = true;
                complete.active = true;
                close.render(matrixStack, mouseX, mouseY, partial);
                complete.render(matrixStack, mouseX, mouseY, partial);
                RewardIcon icon = null;
                for (RewardIcon reward : rewards) {
                    reward.render(matrixStack, mouseX, mouseY, partial);
                    if (icon == null && mouseX >= reward.x && mouseY >= reward.y && mouseX < reward.x + RewardIcon.WIDTH && mouseY < reward.y + RewardIcon.HEIGHT) {
                        icon = reward;
                    }
                }
                if (icon != null) {
                    icon.renderTooltip(matrixStack);
                }
            }
        }

        private static class RewardIcon extends AbstractGui implements IRenderable {

            private static final int WIDTH = 12;
            private static final int HEIGHT = 12;
            private static final RenderInfo BACKGROUND = new RenderInfo(WIDGETS, TEXTURE_WIDTH, TEXTURE_HEIGHT, 20, 21, 9, 9);
            private static final RenderInfo TOOLTIP = new RenderInfo(WIDGETS, TEXTURE_WIDTH, TEXTURE_HEIGHT, 61, 12, 32, 12);
            private static final TranslationTextComponent SHINSU = new TranslationTextComponent("button." + TowerOfGod.MOD_ID + ".shinsu");
            private static final TranslationTextComponent BAANGS = new TranslationTextComponent("button." + TowerOfGod.MOD_ID + ".baangs");
            private static final TranslationTextComponent RESISTANCE = new TranslationTextComponent("button." + TowerOfGod.MOD_ID + ".resistance");
            private static final TranslationTextComponent TENSION = new TranslationTextComponent("button." + TowerOfGod.MOD_ID + ".tension");
            private static final String LEARN_TECHNIQUE_KEY = "button." + TowerOfGod.MOD_ID + ".learn_technique";
            private static final int TOOLTIP_COLOR = 0xFF00FF00;
            private final int x;
            private final int y;
            private final RenderInfo icon;
            private final ShinsuAdvancementInfoTabGui tab;
            private final ITextComponent tooltip;

            private RewardIcon(ShinsuAdvancementInfoTabGui tab, RenderInfo icon, int x, int y, ITextComponent tooltip) {
                this.x = x;
                this.y = y;
                this.icon = icon;
                this.tab = tab;
                this.tooltip = tooltip;
            }

            @Override
            public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
                int blitOffset = getBlitOffset();
                BACKGROUND.render(matrixStack, x, y, blitOffset, WIDTH, HEIGHT, 0xFFFFFFFF);
                icon.render(matrixStack, x, y, blitOffset, WIDTH, HEIGHT, 0xFFFFFFFF);
            }

            private void renderTooltip(MatrixStack matrixStack) {
                int width = tab.screen.font.func_238414_a_(tooltip) + HEIGHT / 2;
                int height = tab.screen.font.FONT_HEIGHT + WIDTH / 2;
                int dY = HEIGHT / -2;
                TOOLTIP.render(matrixStack, x + (WIDTH - width) / 2f, y + (HEIGHT - height) / 2f + dY, getBlitOffset(), width, height, 0xFFFFFFFF);
                drawCenteredString(matrixStack, tab.screen.font, tooltip, x + WIDTH / 2, y + (HEIGHT - tab.screen.font.FONT_HEIGHT) / 2 + dY, TOOLTIP_COLOR);
            }
        }

        private static class CompletionButton extends Button {

            private static final int WIDTH = 48;
            private static final int HEIGHT = 18;
            private static final TranslationTextComponent COMPLETE = new TranslationTextComponent("button." + TowerOfGod.MOD_ID + ".complete");
            private static final RenderInfo RENDER = new RenderInfo(WIDGETS, TEXTURE_WIDTH, TEXTURE_HEIGHT, 29, 12, 32, 12);
            private static final int UNLOCKED_COLOR = 0xFFFF0000;
            private static final int COMPLETE_COLOR = 0xFF00FF00;
            private static final int LOCKED_COLOR = 0xFFFFFFFF;
            private final ShinsuAdvancementInfoTabGui tab;

            public CompletionButton(ShinsuAdvancementInfoTabGui tab, int x, int y) {
                super(x, y, WIDTH, HEIGHT, StringTextComponent.EMPTY, press -> {
                    PlayerEntity player = tab.screen.minecraft.player;
                    if (player != null) {
                        ShinsuAdvancement parent = tab.advancement.getParent();
                        ShinsuAdvancementCriteria criteria = tab.advancement.getCriteria();
                        if ((parent == null || tab.screen.stats.getAdvancements().get(parent).isComplete()) && criteria.canComplete(player)) {
                            ShinsuCriteriaCompletionMessage.INSTANCE.sendToServer(new ShinsuCriteriaCompletionMessage(tab.advancement));
                            tab.screen.stats.getAdvancements().get(tab.advancement).complete();
                            tab.screen.updateServer();
                            for (int[] line : tab.screen.hLines.get(tab.advancement)) {
                                line[3] = ShinsuSkillTreeScreen.UNLOCKED_LINE_COLOR;
                            }
                            for (int[] line : tab.screen.vLines.get(tab.advancement)) {
                                line[3] = ShinsuSkillTreeScreen.UNLOCKED_LINE_COLOR;
                            }
                        }
                    }
                });
                this.tab = tab;
            }

            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
                ShinsuAdvancement parent = tab.advancement.getParent();
                boolean complete = tab.screen.stats.getAdvancements().get(tab.advancement).isComplete();
                boolean parentComplete = parent == null || tab.screen.stats.getAdvancements().get(parent).isComplete();
                int hex = complete ? COMPLETE_COLOR : (parentComplete ? UNLOCKED_COLOR : LOCKED_COLOR);
                RENDER.render(matrixStack, x, y, getBlitOffset(), WIDTH, HEIGHT, hex);
                if (parentComplete) {
                    drawCenteredString(matrixStack, tab.screen.font, complete ? COMPLETE : new StringTextComponent(tab.advancement.getCriteria().getCount(tab.screen.minecraft.player) + "/" + tab.advancement.getCompletionAmount()), x + WIDTH / 2, y + (HEIGHT - tab.screen.font.FONT_HEIGHT) / 2, TEXT_COLOR);
                }
            }
        }

        private static class CloseButton extends Button {

            private static final int WIDTH = 10;
            private static final int HEIGHT = 10;
            private static final RenderInfo RENDER = new RenderInfo(WIDGETS, TEXTURE_WIDTH, TEXTURE_HEIGHT, 20, 12, 9, 9);
            private final ShinsuAdvancementInfoTabGui tab;

            public CloseButton(ShinsuAdvancementInfoTabGui tab, int x, int y) {
                super(x, y, WIDTH, HEIGHT, StringTextComponent.EMPTY, press -> tab.screen.select(null));
                this.tab = tab;
            }

            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
                RENDER.render(matrixStack, x, y, getBlitOffset(), WIDTH, HEIGHT, 0xFFFFFFFF);
            }
        }
    }
}
