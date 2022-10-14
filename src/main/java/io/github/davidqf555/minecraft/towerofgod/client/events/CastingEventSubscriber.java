package io.github.davidqf555.minecraft.towerofgod.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.model.CastingModelHelper;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class CastingEventSubscriber {

    private static long prevGameTime = -1;

    private CastingEventSubscriber() {
    }

    @SubscribeEvent
    public static void preRenderPlayer(RenderPlayerEvent.Pre event) {
        Player entity = event.getPlayer();
        if (ClientReference.isCasting(entity)) {
            HumanoidModel<AbstractClientPlayer> model = event.getRenderer().getModel();
            model.rightArm.visible = false;
            model.leftArm.visible = false;
            long gameTime = entity.level.getGameTime();
            if (gameTime != prevGameTime) {
                CastingModelHelper.spawnParticles(entity, ShinsuAttribute.getParticles(ClientReference.getAttribute(entity)));
                prevGameTime = gameTime;
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (event.phase == TickEvent.Phase.END && player != null && ClientReference.isCasting(player)) {
            CastingModelHelper.spawnParticles(player, ShinsuAttribute.getParticles(ClientReference.getAttribute(player)));
        }
    }

    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && ClientReference.isCasting(player)) {
            player.input.leftImpulse *= 0.2;
            player.input.forwardImpulse *= 0.2;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (ClientReference.isCasting(client.player)) {
            for (HumanoidArm side : HumanoidArm.values()) {
                renderPlayerArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getEquipProgress(), event.getSwingProgress(), side);
            }
            event.setCanceled(true);
        }
    }

    private static void renderPlayerArm(PoseStack matrixStack, MultiBufferSource buffer, int light, float equip, float swing, HumanoidArm side) {
        Minecraft client = Minecraft.getInstance();
        float factor = side != HumanoidArm.LEFT ? 1 : -1;
        float sqrtSwing = Mth.sqrt(swing);
        float f2 = -0.3f * Mth.sin(sqrtSwing * (float) Math.PI);
        float f3 = 0.4f * Mth.sin(sqrtSwing * ((float) Math.PI * 2F));
        float f4 = -0.4f * Mth.sin(swing * (float) Math.PI);
        matrixStack.pushPose();
        matrixStack.translate(factor * (f2 + 0.64000005F), f3 - 0.6F + equip * -0.6F, f4 + -0.71999997F);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(factor * 45));
        float f5 = Mth.sin(swing * swing * (float) Math.PI);
        float f6 = Mth.sin(sqrtSwing * (float) Math.PI);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(factor * f6 * 70));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(factor * f5 * -20));
        client.getTextureManager().bindForSetup(client.player.getSkinTextureLocation());
        matrixStack.translate(factor * -1, 3.6F, 3.5);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(factor * 120));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(200));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(factor * -135));
        matrixStack.translate(factor * 5.6F, 0, 0);
        renderHand(matrixStack, buffer, light, client.player, side);
        matrixStack.popPose();
    }


    private static void renderHand(PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side) {
        PlayerModel<AbstractClientPlayer> model = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel();
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0, 0, 0, 0, 0);
        ModelPart arm;
        ModelPart sleeve;
        if (side == HumanoidArm.RIGHT) {
            arm = model.rightArm;
            sleeve = model.rightSleeve;
        } else {
            arm = model.leftArm;
            sleeve = model.leftSleeve;
        }
        boolean isSpectator = player.isSpectator();
        arm.visible = !isSpectator;
        sleeve.visible = !isSpectator;
        arm.xRot = 0;
        arm.render(stack, buffer.getBuffer(RenderType.entitySolid(player.getSkinTextureLocation())), light, OverlayTexture.NO_OVERLAY);
        sleeve.xRot = 0;
        sleeve.render(stack, buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation())), light, OverlayTexture.NO_OVERLAY);
    }

}
