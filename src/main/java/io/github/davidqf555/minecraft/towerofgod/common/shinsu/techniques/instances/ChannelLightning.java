package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class ChannelLightning extends RayTraceTechnique {

    public ChannelLightning(Entity user, Vec3 direction, double range) {
        super(user, direction, range, true);
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public void doEffect(ServerLevel world, HitResult result) {
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(world);
        if (lightning != null) {
            Entity user = getUser(world);
            if (user instanceof ServerPlayer) {
                lightning.setCause((ServerPlayer) user);
            }
            lightning.setDamage((float) (ShinsuStats.get(user).getTension() * 2));
            Vec3 pos = result.getLocation();
            lightning.setPos(pos.x(), pos.y(), pos.z());
            lightning.setStart(new Vector3f((float) user.getX(), (float) user.getEyeY(), (float) user.getZ()));
            world.addFreshEntity(lightning);
        }
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.CHANNEL_LIGHTNING.get();
    }

    @Override
    public int getShinsuUse() {
        return 25;
    }

    public static class Factory implements ShinsuTechnique.IFactory<ChannelLightning> {

        @Override
        public Either<ChannelLightning, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new ChannelLightning(user, dir, 64));
        }

        @Override
        public ChannelLightning blankCreate() {
            return new ChannelLightning(null, Vec3.ZERO, 0);
        }

    }
}
