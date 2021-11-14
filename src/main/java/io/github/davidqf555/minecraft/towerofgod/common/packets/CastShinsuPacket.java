package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CastShinsuPacket {

    private static final BiConsumer<CastShinsuPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeString(message.technique.name());
    };
    private static final Function<PacketBuffer, CastShinsuPacket> DECODER = buffer -> {
        ShinsuTechnique technique = ShinsuTechnique.valueOf(buffer.readString());
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
        TowerOfGod.CHANNEL.registerMessage(index, CastShinsuPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                Vector3d eye = player.getEyePosition(1);
                EntityRayTraceResult result = ProjectileHelper.rayTraceEntities(player.world, player, eye, eye.add(player.getLookVec().scale(ShinsuStats.ENTITY_RANGE)), AxisAlignedBB.fromVector(eye).grow(ShinsuStats.ENTITY_RANGE), null);
                ShinsuStats stats = ShinsuStats.get(player);
                stats.cast(player, technique, result == null ? null : result.getEntity(), player.getLookVec());
            });
            context.setPacketHandled(true);
        }
    }

}
