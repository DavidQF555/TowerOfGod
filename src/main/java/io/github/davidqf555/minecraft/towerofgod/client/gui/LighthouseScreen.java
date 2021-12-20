package io.github.davidqf555.minecraft.towerofgod.client.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LighthouseScreen extends ContainerScreen<LighthouseEntity.LighthouseContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/lighthouse.png");
    private static final TextureRenderData LIGHTHOUSE = new TextureRenderData(TEXTURE, 176, 165, 0, 0, 176, 71);
    private static final TextureRenderData INVENTORY = new TextureRenderData(TEXTURE, 176, 165, 0, 71, 176, 94);
    private static final int INVENTORY_TITLE_COLOR = 0xFF404040;

    public LighthouseScreen(LighthouseEntity.LighthouseContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        titleY -= 4;
        playerInventoryTitleY += 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        int offset = getBlitOffset();
        INVENTORY.render(new RenderContext(matrixStack, x, y + 72, offset, xSize, 94, 0xFFFFFFFF));
        int hex = container.lighthouse.getColor().getColorValue();
        LIGHTHOUSE.render(new RenderContext(matrixStack, x, y, offset, xSize, 71, ColorHelper.PackedColor.packColor(255, ColorHelper.PackedColor.getRed(hex), ColorHelper.PackedColor.getGreen(hex), ColorHelper.PackedColor.getBlue(hex))));
    }

    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        int hex = container.lighthouse.getColor().getColorValue();
        int color = ColorHelper.PackedColor.packColor(255, Math.min(255, ColorHelper.PackedColor.getRed(hex) + 64), Math.min(255, ColorHelper.PackedColor.getGreen(hex) + 64), Math.min(255, ColorHelper.PackedColor.getBlue(hex) + 64));
        font.drawText(matrixStack, title, (float) titleX, (float) titleY, color);
        font.drawText(matrixStack, playerInventory.getDisplayName(), (float) playerInventoryTitleX, (float) playerInventoryTitleY, INVENTORY_TITLE_COLOR);
    }

    public static class Factory implements ScreenManager.IScreenFactory<LighthouseEntity.LighthouseContainer, LighthouseScreen> {
        @Nonnull
        @Override
        public LighthouseScreen create(LighthouseEntity.LighthouseContainer con, PlayerInventory inv, ITextComponent text) {
            return new LighthouseScreen(con, inv, text);
        }
    }
}
