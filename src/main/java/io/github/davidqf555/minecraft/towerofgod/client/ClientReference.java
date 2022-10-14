package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import io.github.davidqf555.minecraft.towerofgod.client.gui.GuideScreen;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuCombinationGui;
import io.github.davidqf555.minecraft.towerofgod.client.gui.StatsMeterGui;
import io.github.davidqf555.minecraft.towerofgod.client.render.RenderContext;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ItemStackRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.TextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ClientReference {

    public static final Map<ShinsuTechnique, Component> ERRORS = new HashMap<>();
    public static final StatsMeterGui SHINSU = new StatsMeterGui(-91, 0, 0, 200);
    public static final StatsMeterGui BAANGS = new StatsMeterGui(6, 0, 0, 20);
    public static final ShinsuCombinationGui COMBO = new ShinsuCombinationGui();

    private ClientReference() {
    }

    public static void renderTextureData(TextureRenderData data, RenderContext context) {
        Matrix4f matrix = context.getPoseStack().last().pose();
        float x = context.getX();
        float y = context.getY();
        int width = context.getWidth();
        int height = context.getHeight();
        float x2 = x + width;
        float y2 = y + height;
        int color = context.getColor();
        int a = FastColor.ARGB32.alpha(color);
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        float minU = data.getStartX() * 1f / data.getTextureWidth();
        float maxU = (data.getStartX() + data.getBlitWidth()) * 1f / data.getTextureWidth();
        float minV = data.getStartY() * 1f / data.getTextureHeight();
        float maxV = (data.getStartY() + data.getBlitHeight()) * 1f / data.getTextureHeight();
        float blitOffset = context.getBlitOffset();
        RenderSystem.setShaderTexture(0, data.getTexture());
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix, x, y2, blitOffset).color(r, g, b, a).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, blitOffset).color(r, g, b, a).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, x2, y, blitOffset).color(r, g, b, a).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, x, y, blitOffset).color(r, g, b, a).uv(minU, minV).endVertex();
        bufferbuilder.end();
        RenderSystem.enableBlend();
        BufferUploader.end(bufferbuilder);
        RenderSystem.disableBlend();
    }

    public static void renderItemStackData(ItemStackRenderData data, RenderContext context) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(data.get(), (int) context.getX(), (int) context.getY());
    }

    public static void openCombinationGUI(Set<ShinsuTechnique> unlocked) {
        COMBO.setUnlocked(unlocked);
    }

    public static void openGuideScreen(ShinsuTechnique[] pages, int color) {
        Minecraft.getInstance().setScreen(new GuideScreen(pages, 221, 180, color));
    }

    public static void handleUpdateCastingPacket(int id, boolean casting) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity instanceof Player) {
                setCasting((Player) entity, casting);
            }
        }
    }

    public static void handleUpdateAttributePacket(int id, @Nullable ShinsuAttribute attribute) {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity instanceof Player) {
                setAttribute((Player) entity, attribute);
            }
        }
    }

    public static boolean isCasting(Player entity) {
        CompoundTag data = entity.getPersistentData();
        if (data.contains(TowerOfGod.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag child = data.getCompound(TowerOfGod.MOD_ID);
            return child.contains("Casting", Tag.TAG_BYTE) && child.getBoolean("Casting");
        }
        return false;
    }

    public static void setCasting(Player entity, boolean casting) {
        CompoundTag data = entity.getPersistentData();
        CompoundTag child;
        if (data.contains(TowerOfGod.MOD_ID, Tag.TAG_COMPOUND)) {
            child = data.getCompound(TowerOfGod.MOD_ID);
        } else {
            child = new CompoundTag();
            data.put(TowerOfGod.MOD_ID, child);
        }
        child.putBoolean("Casting", casting);
    }

    @Nullable
    public static ShinsuAttribute getAttribute(Player entity) {
        CompoundTag data = entity.getPersistentData();
        if (data.contains(TowerOfGod.MOD_ID, Tag.TAG_COMPOUND)) {
            CompoundTag child = data.getCompound(TowerOfGod.MOD_ID);
            return child.contains("Attribute", Tag.TAG_STRING) ? ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(child.getString("Attribute"))) : null;
        }
        return null;
    }

    public static void setAttribute(Player entity, @Nullable ShinsuAttribute attribute) {
        CompoundTag data = entity.getPersistentData();
        CompoundTag child;
        if (data.contains(TowerOfGod.MOD_ID, Tag.TAG_COMPOUND)) {
            child = data.getCompound(TowerOfGod.MOD_ID);
        } else {
            child = new CompoundTag();
            data.put(TowerOfGod.MOD_ID, child);
        }
        if (attribute == null) {
            child.remove("Attribute");
        } else {
            child.putString("Attribute", attribute.getRegistryName().toString());
        }
    }
}
