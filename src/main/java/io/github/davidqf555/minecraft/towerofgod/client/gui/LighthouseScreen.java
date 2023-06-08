package io.github.davidqf555.minecraft.towerofgod.client.gui;

import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LighthouseScreen extends AbstractContainerScreen<LighthouseEntity.LighthouseContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/lighthouse.png");
    private static final TextureRenderData LIGHTHOUSE = new TextureRenderData(TEXTURE, 176, 165, 0, 0, 176, 71);
    private static final TextureRenderData INVENTORY = new TextureRenderData(TEXTURE, 176, 165, 0, 71, 176, 94);
    private static final int INVENTORY_TITLE_COLOR = 0xFF404040;

    public LighthouseScreen(LighthouseEntity.LighthouseContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        titleLabelY -= 4;
        inventoryLabelY += 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        INVENTORY.render(new RenderContext(graphics.pose(), x, y + 72, 0, imageWidth, 94, 0xFFFFFFFF));
        int hex = menu.lighthouse.getColor().getTextColor();
        LIGHTHOUSE.render(new RenderContext(graphics.pose(), x, y, 0, imageWidth, 71, FastColor.ARGB32.color(255, FastColor.ARGB32.red(hex), FastColor.ARGB32.green(hex), FastColor.ARGB32.blue(hex))));
    }

    @Override
    public void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int hex = menu.lighthouse.getColor().getTextColor();
        int color = FastColor.ARGB32.color(255, Math.min(255, FastColor.ARGB32.red(hex) + 64), Math.min(255, FastColor.ARGB32.green(hex) + 64), Math.min(255, FastColor.ARGB32.blue(hex) + 64));
        graphics.drawString(font, title, titleLabelX, titleLabelY, color, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, INVENTORY_TITLE_COLOR, false);
    }

}
