package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class DeviceRenderer<T extends FlyingDevice, M extends EntityModel<T>> extends LivingRenderer<T, M> {

    public DeviceRenderer(EntityRendererManager rendererManager, M entityModelIn, float shadowSizeIn) {
        super(rendererManager, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn))) {
            return;
        }
        matrixStackIn.push();
        entityModel.swingProgress = getSwingProgress(entityIn, partialTicks);
        entityModel.isSitting = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
        entityModel.isChild = entityIn.isChild();
        float yaw = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
        float yawHead = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
        float netYaw = yawHead - yaw;
        float pitch = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        float age = handleRotationFloat(entityIn, partialTicks);
        applyRotations(entityIn, matrixStackIn, age, yaw, partialTicks);
        matrixStackIn.scale(-1, -1, 1);
        preRenderCallback(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0, -1.501, 0);
        float limbSwingAmount = 0;
        float limbSwing = 0;
        if (!entityModel.isSitting && entityIn.isAlive()) {
            limbSwingAmount = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
            limbSwing = entityIn.limbSwing - entityIn.limbSwingAmount * (1 - partialTicks);
            if (limbSwingAmount > 1) {
                limbSwingAmount = 1;
            }
        }
        entityModel.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
        entityModel.setRotationAngles(entityIn, limbSwing, limbSwingAmount, age, netYaw, pitch);
        Minecraft minecraft = Minecraft.getInstance();
        boolean visible = isVisible(entityIn);
        boolean invisible = !visible && !entityIn.isInvisibleToPlayer(minecraft.player);
        boolean glowing = minecraft.isEntityGlowing(entityIn);
        float alpha = getAlpha(invisible);
        RenderType type = func_230496_a_(entityIn, visible, alpha < 1, glowing);
        if (type != null) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(type);
            int overlay = getPackedOverlay(entityIn, getOverlayProgress(entityIn, partialTicks));
            int hex = entityIn.getColor().getColorValue();
            float red = ColorHelper.PackedColor.getRed(hex) / 255f;
            float green = ColorHelper.PackedColor.getGreen(hex) / 255f;
            float blue = ColorHelper.PackedColor.getBlue(hex) / 255f;
            entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, overlay, red, green, blue, alpha);
        }
        for (LayerRenderer<T, M> renderer : layerRenderers) {
            renderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, partialTicks, age, netYaw, pitch);
        }
        matrixStackIn.pop();
        RenderNameplateEvent nameplate = new RenderNameplateEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        MinecraftForge.EVENT_BUS.post(nameplate);
        if (nameplate.getResult() != Event.Result.DENY && (nameplate.getResult() == Event.Result.ALLOW || canRenderName(entityIn))) {
            renderName(entityIn, nameplate.getContent(), matrixStackIn, bufferIn, packedLightIn);
        }
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
    }

    protected float getAlpha(boolean invisible) {
        return invisible ? 0.15f : 1;
    }

    @Override
    protected boolean canRenderName(T entity) {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }
}
