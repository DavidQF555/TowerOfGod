package io.github.davidqf555.minecraft.towerofgod.client.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LighthouseScreen extends ContainerScreen<LighthouseEntity.LighthouseContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/lighthouse.png");
    private static final TextureRenderData LIGHTHOUSE = new TextureRenderData(TEXTURE, 176, 165, 0, 0, 176, 71);
    private static final TextureRenderData INVENTORY = new TextureRenderData(TEXTURE, 176, 165, 0, 71, 176, 94);
    private static final int INVENTORY_TITLE_COLOR = 0xFF404040;

    public LighthouseScreen(LighthouseEntity.LighthouseContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        titleLabelY -= 4;
        inventoryLabelY += 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int offset = getBlitOffset();
        INVENTORY.render(new RenderContext(matrixStack, x, y + 72, offset, imageWidth, 94, 0xFFFFFFFF));
        int hex = menu.lighthouse.getColor().getColorValue();
        LIGHTHOUSE.render(new RenderContext(matrixStack, x, y, offset, imageWidth, 71, ColorHelper.PackedColor.color(255, ColorHelper.PackedColor.red(hex), ColorHelper.PackedColor.green(hex), ColorHelper.PackedColor.blue(hex))));
    }

    @Override
    public void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        int hex = menu.lighthouse.getColor().getColorValue();
        int color = ColorHelper.PackedColor.color(255, Math.min(255, ColorHelper.PackedColor.red(hex) + 64), Math.min(255, ColorHelper.PackedColor.green(hex) + 64), Math.min(255, ColorHelper.PackedColor.blue(hex) + 64));
        font.draw(matrixStack, title, (float) titleLabelX, (float) titleLabelY, color);
        font.draw(matrixStack, inventory.getDisplayName(), (float) inventoryLabelX, (float) inventoryLabelY, INVENTORY_TITLE_COLOR);
    }

}
