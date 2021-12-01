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
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UpdateClientErrorPacket {

    private static final BiConsumer<UpdateClientErrorPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.errors.size());
        for (Map.Entry<ShinsuTechnique, ITextComponent> error : message.errors.entrySet()) {
            buffer.writeString(error.getKey().name());
            buffer.writeTextComponent(error.getValue());
        }
    };
    private static final Function<PacketBuffer, UpdateClientErrorPacket> DECODER = buffer -> {
        Map<ShinsuTechnique, ITextComponent> errors = new EnumMap<>(ShinsuTechnique.class);
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            ShinsuTechnique technique = ShinsuTechnique.valueOf(buffer.readString());
            errors.put(technique, buffer.readTextComponent());
        }
        return new UpdateClientErrorPacket(errors);
    };
    private static final BiConsumer<UpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Map<ShinsuTechnique, ITextComponent> errors;

    public UpdateClientErrorPacket() {
        this(new EnumMap<>(ShinsuTechnique.class));
    }

    public UpdateClientErrorPacket(Map<ShinsuTechnique, ITextComponent> errors) {
        this.errors = errors;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, UpdateClientErrorPacket.class, ENCODER, DECODER, CONSUMER);
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
                    technique.getBuilder().doBuild(player, level, target, player.getLookVec()).ifRight(error -> errors.put(technique, error));
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateClientErrorPacket(errors));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                ClientReference.ERRORS.clear();
                ClientReference.ERRORS.putAll(errors);
            });
            context.setPacketHandled(true);
        }
    }
}
