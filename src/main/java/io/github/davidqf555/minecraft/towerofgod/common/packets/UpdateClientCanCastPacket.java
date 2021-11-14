package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientCanCastPacket {

    private static final BiConsumer<UpdateClientCanCastPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.canCast.size());
        for (ShinsuTechnique technique : message.canCast) {
            buffer.writeString(technique.name());
        }
    };
    private static final Function<PacketBuffer, UpdateClientCanCastPacket> DECODER = buffer -> {
        List<ShinsuTechnique> canCast = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ShinsuTechnique technique = ShinsuTechnique.valueOf(buffer.readString());
            canCast.add(technique);
        }
        return new UpdateClientCanCastPacket(canCast);
    };
    private static final BiConsumer<UpdateClientCanCastPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final List<ShinsuTechnique> canCast;

    public UpdateClientCanCastPacket() {
        this(new ArrayList<>());
    }

    public UpdateClientCanCastPacket(List<ShinsuTechnique> canCast) {
        this.canCast = canCast;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientCanCastPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                Vector3d eye = player.getEyePosition(1);
                EntityRayTraceResult result = ProjectileHelper.rayTraceEntities(player.world, player, eye, eye.add(player.getLookVec().scale(ShinsuStats.ENTITY_RANGE)), AxisAlignedBB.fromVector(eye).grow(ShinsuStats.ENTITY_RANGE), null);
                Entity target = result == null ? null : result.getEntity();
                ShinsuStats stats = ShinsuStats.get(player);
                for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                    int level = stats.getData(technique.getType()).getLevel();
                    if (technique.getBuilder().doBuild(player, level, target, player.getLookVec()) != null) {
                        canCast.add(technique);
                    }
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientCanCastPacket(canCast));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> ClientReference.canCast = canCast);
            context.setPacketHandled(true);
        }
    }
}
