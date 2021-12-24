package io.github.davidqf555.minecraft.towerofgod.common.packets;

import io.github.davidqf555.minecraft.towerofgod.client.ClientReference;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuCombinationGui;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class OpenCombinationGUIPacket {

    private static final BiConsumer<OpenCombinationGUIPacket, PacketBuffer> ENCODER = (message, buffer) -> {
        buffer.writeInt(message.unlocked.size());
        for (ShinsuTechnique technique : message.unlocked) {
            buffer.writeInt(technique.ordinal());
        }
    };
    private static final Function<PacketBuffer, OpenCombinationGUIPacket> DECODER = buffer -> {
        Set<ShinsuTechnique> unlocked = EnumSet.noneOf(ShinsuTechnique.class);
        ShinsuTechnique[] all = ShinsuTechnique.values();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            unlocked.add(all[buffer.readInt()]);
        }
        return new OpenCombinationGUIPacket(unlocked);
    };
    private static final BiConsumer<OpenCombinationGUIPacket, Supplier<NetworkEvent.Context>> CONSUMER = (message, context) -> {
        NetworkEvent.Context cont = context.get();
        message.handle(cont);
    };
    private final Set<ShinsuTechnique> unlocked;

    public OpenCombinationGUIPacket() {
        this(EnumSet.noneOf(ShinsuTechnique.class));
    }

    public OpenCombinationGUIPacket(Set<ShinsuTechnique> unlocked) {
        this.unlocked = unlocked;
    }

    public static void register(int index) {
        TowerOfGod.CHANNEL.registerMessage(index, OpenCombinationGUIPacket.class, ENCODER, DECODER, CONSUMER);
    }

    private void handle(NetworkEvent.Context context) {
        NetworkDirection dir = context.getDirection();
        if (dir == NetworkDirection.PLAY_TO_SERVER) {
            ServerPlayerEntity player = context.getSender();
            context.enqueueWork(() -> {
                for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
                    if (technique.getFactory().isUnlocked(player)) {
                        unlocked.add(technique);
                    }
                }
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenCombinationGUIPacket(unlocked));
            });
            context.setPacketHandled(true);
        } else if (dir == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> {
                PlayerEntity player = Minecraft.getInstance().player;
                ClientReference.combo = new ShinsuCombinationGui(unlocked, player.rotationYawHead, player.getPitch(1));
            });
            context.setPacketHandled(true);
        }
    }
}
