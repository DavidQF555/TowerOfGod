package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientUpdateClientErrorPacket {

    private static final BiConsumer<ClientUpdateClientErrorPacket, PacketBuffer> ENCODER = (message, buffer) -> buffer.writeResourceLocation(message.technique.getRegistryName());
    private static final Function<PacketBuffer, ClientUpdateClientErrorPacket> DECODER = buffer -> new ClientUpdateClientErrorPacket(ShinsuTechniqueRegistry.getRegistry().getValue(buffer.readResourceLocation()));
    private static final BiConsumer<ClientUpdateClientErrorPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };

    private final ShinsuTechnique technique;

    public ClientUpdateClientErrorPacket(ShinsuTechnique technique) {
        this.technique = technique;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, ClientUpdateClientErrorPacket.class, ENCODER, DECODER, CONSUMER, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private void handle(NetworkEvent.Context context) {
        ServerPlayerEntity player = context.getSender();
        context.enqueueWork(() -> {
            Vector3d eye = player.getEyePosition(1);
            EntityRayTraceResult result = ProjectileHelper.getEntityHitResult(player.level, player, eye, eye.add(player.getLookAngle().scale(ShinsuTechniqueData.CAST_TARGET_RANGE)), AxisAlignedBB.ofSize(ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2).move(eye), null);
            Entity target = result == null ? null : result.getEntity();
            Optional<ITextComponent> error = technique.create(player, target, player.getLookAngle()).right();
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ServerUpdateClientErrorPacket(error));
        });
        context.setPacketHandled(true);
    }
}
