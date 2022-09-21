package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ClientReference {

    public static final Map<ShinsuTechnique, ITextComponent> ERRORS = new HashMap<>();
    public static StatsMeterGui shinsu = null;
    public static StatsMeterGui baangs = null;
    public static ShinsuCombinationGui combo = null;

    private ClientReference() {
    }

    public static void renderTextureData(TextureRenderData data, RenderContext context) {
        Matrix4f matrix = context.getMatrixStack().last().pose();
        float x = context.getX();
        float y = context.getY();
        int width = context.getWidth();
        int height = context.getHeight();
        float x2 = x + width;
        float y2 = y + height;
        int color = context.getColor();
        int a = ColorHelper.PackedColor.alpha(color);
        int r = ColorHelper.PackedColor.red(color);
        int g = ColorHelper.PackedColor.green(color);
        int b = ColorHelper.PackedColor.blue(color);
        float minU = data.getStartX() * 1f / data.getTextureWidth();
        float maxU = (data.getStartX() + data.getBlitWidth()) * 1f / data.getTextureWidth();
        float minV = data.getStartY() * 1f / data.getTextureHeight();
        float maxV = (data.getStartY() + data.getBlitHeight()) * 1f / data.getTextureHeight();
        float blitOffset = context.getBlitOffset();
        Minecraft.getInstance().getTextureManager().bind(data.getTexture());
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix, x, y2, blitOffset).color(r, g, b, a).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, x2, y2, blitOffset).color(r, g, b, a).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, x2, y, blitOffset).color(r, g, b, a).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, x, y, blitOffset).color(r, g, b, a).uv(minU, minV).endVertex();
        bufferbuilder.end();
        RenderSystem.enableBlend();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.disableBlend();
    }

    public static void renderItemStackData(ItemStackRenderData data, RenderContext context) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(data.get(), (int) context.getX(), (int) context.getY());
    }

    public static void openCombinationGUI(Set<ShinsuTechnique> unlocked) {
        PlayerEntity player = Minecraft.getInstance().player;
        ClientReference.combo = new ShinsuCombinationGui(unlocked, ShinsuAttribute.getColor(ClientReference.getAttribute(player)), player.yHeadRot, player.getViewXRot(1));
    }

    public static void openGuideScreen(ShinsuTechnique[] pages, int color) {
        Minecraft.getInstance().setScreen(new GuideScreen(pages, 221, 180, color));
    }

    public static void updateDimensions(RegistryKey<World> key) {
        Minecraft.getInstance().player.connection.levels().add(key);
    }

    public static void handleUpdateCastingPacket(int id, boolean casting) {
        World world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity instanceof PlayerEntity) {
                setCasting((PlayerEntity) entity, casting);
            }
        }
    }

    public static void handleUpdateAttributePacket(int id, @Nullable ShinsuAttribute attribute) {
        World world = Minecraft.getInstance().level;
        if (world != null) {
            Entity entity = world.getEntity(id);
            if (entity instanceof PlayerEntity) {
                setAttribute((PlayerEntity) entity, attribute);
            }
        }
    }

    public static boolean isCasting(PlayerEntity entity) {
        CompoundNBT data = entity.getPersistentData();
        if (data.contains(TowerOfGod.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT child = data.getCompound(TowerOfGod.MOD_ID);
            return child.contains("Casting", Constants.NBT.TAG_BYTE) && child.getBoolean("Casting");
        }
        return false;
    }

    public static void setCasting(PlayerEntity entity, boolean casting) {
        CompoundNBT data = entity.getPersistentData();
        CompoundNBT child;
        if (data.contains(TowerOfGod.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            child = data.getCompound(TowerOfGod.MOD_ID);
        } else {
            child = new CompoundNBT();
            data.put(TowerOfGod.MOD_ID, child);
        }
        child.putBoolean("Casting", casting);
    }

    @Nullable
    public static ShinsuAttribute getAttribute(PlayerEntity entity) {
        CompoundNBT data = entity.getPersistentData();
        if (data.contains(TowerOfGod.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT child = data.getCompound(TowerOfGod.MOD_ID);
            return child.contains("Attribute", Constants.NBT.TAG_STRING) ? ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(child.getString("Attribute"))) : null;
        }
        return null;
    }

    public static void setAttribute(PlayerEntity entity, @Nullable ShinsuAttribute attribute) {
        CompoundNBT data = entity.getPersistentData();
        CompoundNBT child;
        if (data.contains(TowerOfGod.MOD_ID, Constants.NBT.TAG_COMPOUND)) {
            child = data.getCompound(TowerOfGod.MOD_ID);
        } else {
            child = new CompoundNBT();
            data.put(TowerOfGod.MOD_ID, child);
        }
        if (attribute == null) {
            child.remove("Attribute");
        } else {
            child.putString("Attribute", attribute.getRegistryName().toString());
        }
    }
}
