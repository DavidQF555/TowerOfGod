package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechnique;
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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientUpdateClientErrorPacket {

    private static final BiConsumer<ClientUpdateClientErrorPacket, PacketBuffer> ENCODER = (message, buffer) -> {
    };
    private static final Function<PacketBuffer, ClientUpdateClientErrorPacket> DECODER = buffer -> new ClientUpdateClientErrorPacket();
    private static final BiConsumer<ClientUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientUpdateClientErrorPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        context.enqueueWork(() -> {
            Vector3d eye = player.getEyePosition(1);
            EntityRayTraceResult result = ProjectileHelper.rayTraceEntities(player.world, player, eye, eye.add(player.getLookVec().scale(ShinsuStats.ENTITY_RANGE)), AxisAlignedBB.fromVector(eye).grow(ShinsuStats.ENTITY_RANGE), null);
            Entity target = result == null ? null : result.getEntity();
            Map<ShinsuTechnique, ITextComponent> errors = new EnumMap<>(ShinsuTechnique.class);
            for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                technique.getFactory().doCreate(player, target, player.getLookVec()).ifRight(error -> errors.put(technique, error));
            }
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerUpdateClientErrorPacket(errors));
        });
        context.setPacketHandled(true);
    }
}
