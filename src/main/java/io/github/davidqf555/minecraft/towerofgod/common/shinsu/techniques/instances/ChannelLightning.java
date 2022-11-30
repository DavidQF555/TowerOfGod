package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class ChannelLightning extends RayTraceTechnique {

    public ChannelLightning(Entity user, Vector3d direction, double range) {
        super(user, direction, range, true);
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public void doEffect(ServerWorld world, RayTraceResult result) {
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(world);
        if (lightning != null) {
            Entity user = getUser(world);
            if (user instanceof ServerPlayerEntity) {
                lightning.setCause((ServerPlayerEntity) user);
            }
            lightning.setDamage((float) (ShinsuStats.get(user).getTension() * 2));
            Vector3d pos = result.getLocation();
            lightning.setPos(pos.x(), pos.y(), pos.z());
            lightning.setStart(new Vector3f(user.getEyePosition(1)));
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
        public Either<ChannelLightning, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ChannelLightning(user, dir, 64));
        }

        @Override
        public ChannelLightning blankCreate() {
            return new ChannelLightning(null, Vector3d.ZERO, 0);
        }

    }
}
