package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CastShinsuPacket {

    private static final BiConsumer<CastShinsuPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeResourceLocation(message.technique.getRegistryName());
    };
    private static final Function<PacketBuffer, CastShinsuPacket> DECODER = buffer -> {
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
        ServerPlayerEntity player = context.getSender();
        context.enqueueWork(() -> {
            Vector3d eye = player.getEyePosition(1);
            EntityRayTraceResult result = ProjectileHelper.getEntityHitResult(player.level, player, eye, eye.add(player.getLookAngle().scale(ShinsuStats.ENTITY_RANGE)), AxisAlignedBB.unitCubeFromLowerCorner(eye).inflate(ShinsuStats.ENTITY_RANGE), null);
            technique.cast(player, result == null ? null : result.getEntity(), player.getLookAngle());
        });
        context.setPacketHandled(true);
    }

}
