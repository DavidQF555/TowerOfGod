package io.github.davidqf555.minecraft.towerofgod.common.shinsu;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.BaangEntity;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateCastingPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CastingHelper {

    public static final Map<UUID, Pair<UUID, ConfiguredShinsuTechniqueType<?, ?>>> CASTING = new HashMap<>();

    private CastingHelper() {
    }

    public static boolean isCasting(Player player) {
        return CASTING.containsKey(player.getUUID());
    }

    @Nullable
    public static UUID getCastingBaang(Player player) {
        if (CASTING.containsKey(player.getUUID())) {
            return CASTING.get(player.getUUID()).getFirst();
        }
        return null;
    }

    public static void startCasting(ServerPlayer player, BaangEntity baang) {
        CASTING.put(player.getUUID(), Pair.of(baang.getUUID(), baang.getTechnique()));
        TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new UpdateCastingPacket(player.getUUID(), true));
    }

    public static boolean cast(ServerPlayer player) {
        if (isCasting(player)) {
            Pair<UUID, ConfiguredShinsuTechniqueType<?, ?>> pair = CASTING.get(player.getUUID());
            Vec3 eye = player.getEyePosition();
            EntityHitResult result = ProjectileUtil.getEntityHitResult(player.level, player, eye, eye.add(player.getLookAngle().scale(ShinsuTechniqueData.CAST_TARGET_RANGE)), AABB.ofSize(eye, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2, ShinsuTechniqueData.CAST_TARGET_RANGE * 2), entity -> entity instanceof LivingEntity);
            pair.getSecond().cast(player, result == null ? null : (LivingEntity) result.getEntity());
            stopCasting(player);
            return true;
        }
        return false;
    }

    public static void stopCasting(ServerPlayer player) {
        CASTING.remove(player.getUUID());
        TowerOfGod.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new UpdateCastingPacket(player.getUUID(), false));
    }

}
