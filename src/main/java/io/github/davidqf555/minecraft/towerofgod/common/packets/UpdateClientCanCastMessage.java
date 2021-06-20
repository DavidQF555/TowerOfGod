package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.client.util.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
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

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientCanCastMessage {

    private static final BiConsumer<UpdateClientCanCastMessage, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.canCast.size());
        for (ShinsuTechnique technique : message.canCast.keySet()) {
            buffer.writeString(technique.name());
            Set<String> settings = message.canCast.get(technique);
            buffer.writeInt(settings.size());
            for (String setting : settings) {
                buffer.writeString(setting);
            }
        }
    };
    private static final Function<PacketBuffer, UpdateClientCanCastMessage> DECODER = buffer -> {
        Map<ShinsuTechnique, Set<String>> canCast = new EnumMap<>(ShinsuTechnique.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ShinsuTechnique technique = ShinsuTechnique.valueOf(buffer.readString());
            Set<String> settings = new HashSet<>();
            int total = buffer.readInt();
            for (int j = 0; j < total; j++) {
                settings.add(buffer.readString());
            }
            canCast.put(technique, settings);
        }
        return new UpdateClientCanCastMessage(canCast);
    };
    private static final BiConsumer<UpdateClientCanCastMessage, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, Set<String>> canCast;

    public UpdateClientCanCastMessage() {
        this(new EnumMap<>(ShinsuTechnique.class));
    }

    public UpdateClientCanCastMessage(Map<ShinsuTechnique, Set<String>> canCast) {
        this.canCast = canCast;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientCanCastMessage.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                int distance = 32;
                Vector3d eye = player.getEyePosition(1);
                EntityRayTraceResult result = ProjectileHelper.rayTraceEntities(player.world, player, eye, eye.add(player.getLookVec().scale(distance)), AxisAlignedBB.fromVector(eye).grow(distance), null);
                Entity target = result == null ? null : result.getEntity();
                IShinsuStats stats = IShinsuStats.get(player);
                for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                    int level = stats.getTechniqueLevel(technique);
                    Set<String> can = new HashSet<>();
                    for (String option : technique.getSettings().getOptions()) {
                        if (technique.getBuilder().doBuild(player, level, target, player.getLookVec(), option) != null) {
                            can.add(option);
                        }
                    }
                    canCast.put(technique, can);
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientCanCastMessage(canCast));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.canCast = canCast;
            });
            context.setPacketHandled(true);
        }
    }
}
