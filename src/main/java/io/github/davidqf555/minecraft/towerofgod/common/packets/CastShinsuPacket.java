package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CastShinsuPacket {

    private static final BiConsumer<CastShinsuPacket, FriendlyByteBuf> ENCODER = (message, buffer) -> buffer.writeResourceLocation(message.technique.getId());
    private static final Function<FriendlyByteBuf, CastShinsuPacket> DECODER = buffer -> {
        ShinsuTechnique technique = ShinsuTechniqueRegistry.getRegistry().getValue(buffer.readResourceLocation());
        return new CastShinsuPacket(technique);
    };
    private static final BiConsumer<CastShinsuPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique technique;

    public CastShinsuPacket(ShinsuTechnique technique) {
        this.technique = technique;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, CastShinsuPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
                    if (technique.isObtainable()) {
                        Vec3 eye = player.getEyePosition(1);
                        EntityHitResult result = ProjectileUtil.getEntityHitResult(player.level, player, eye, eye.add(player.getLookAngle().scale(ShinsuTechniqueData.CAST_TARGET_RANGE)), AABB.ofSize(eye, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2), entity -> true);
                        technique.cast(player, result == null ? null : result.getEntity(), player.getLookAngle());
                    }
                }
        );
        context.setPacketHandled(true);
    }

}
