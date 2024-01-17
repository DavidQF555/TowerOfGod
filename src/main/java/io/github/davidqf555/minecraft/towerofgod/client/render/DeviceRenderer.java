package io.github.davidqf555.minecraft.towerofgod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class DeviceRenderer<T extends FlyingDevice, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public DeviceRenderer(EntityRendererProvider.Context rendererManager, M entityModelIn, float shadowSizeIn) {
        super(rendererManager, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn))) {
            return;
        }
        matrixStackIn.pushPose();
        model.attackTime = getAttackAnim(entityIn, partialTicks);
        model.riding = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
        model.young = entityIn.isBaby();
        float yaw = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float yawHead = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float netYaw = yawHead - yaw;
        float pitch = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        float age = getBob(entityIn, partialTicks);
        setupRotations(entityIn, matrixStackIn, age, yaw, partialTicks);
        matrixStackIn.scale(-1, -1, 1);
        scale(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0, -1.501, 0);
        float limbSwingAmount = 0;
        float limbSwing = 0;
        if (!model.riding && entityIn.isAlive()) {
            limbSwingAmount = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            limbSwing = entityIn.animationPosition - entityIn.animationSpeed * (1 - partialTicks);
            if (limbSwingAmount > 1) {
                limbSwingAmount = 1;
            }
        }
        model.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(entityIn, limbSwing, limbSwingAmount, age, netYaw, pitch);
        Minecraft minecraft = Minecraft.getInstance();
        boolean visible = isBodyVisible(entityIn);
        boolean invisible = !visible && !entityIn.isInvisibleTo(minecraft.player);
        boolean glowing = minecraft.shouldEntityAppearGlowing(entityIn);
        float alpha = getAlpha(invisible);
        RenderType type = getRenderType(entityIn, visible, alpha < 1, glowing);
        if (type != null) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(type);
            int overlay = getOverlayCoords(entityIn, getWhiteOverlayProgress(entityIn, partialTicks));
            int hex = entityIn.getColor().getTextColor();
            float red = FastColor.ARGB32.red(hex) / 255f;
            float green = FastColor.ARGB32.green(hex) / 255f;
            float blue = FastColor.ARGB32.blue(hex) / 255f;
            model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, red, green, blue, alpha);
        }
        for (RenderLayer<T, M> renderer : layers) {
            renderer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, partialTicks, age, netYaw, pitch);
        }
        matrixStackIn.popPose();
        RenderNameTagEvent nameplate = new RenderNameTagEvent(entityIn, entityIn.getDisplayName(), this, matrixStackIn, bufferIn, packedLightIn, partialTicks);
        MinecraftForge.EVENT_BUS.post(nameplate);
        if (nameplate.getResult() != Event.Result.DENY && (nameplate.getResult() == Event.Result.ALLOW || shouldShowName(entityIn))) {
            renderNameTag(entityIn, nameplate.getContent(), matrixStackIn, bufferIn, packedLightIn);
        }
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn));
    }

    protected float getAlpha(boolean invisible) {
        return invisible ? 0.15f : 1;
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return entity.shouldShowName() && entity.hasCustomName();
    }
}
