package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Thunderstorm extends AreaTechnique {

    public Thunderstorm(Entity user, double minRadius, double radius) {
        super(user, minRadius, radius, 16, 2);
    }

    @Override
    public int getDuration() {
        return 500;
    }

    @Override
    protected void doEffect(ServerLevel world, Vec3 pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPos(pos.x(), pos.y() + 0.5, pos.z());
            world.addFreshEntity(lightning);
        }
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.THUNDERSTORM.get();
    }

    @Override
    public int getShinsuUse() {
        return 60;
    }

    @Override
    public int getCooldown() {
        return 2000;
    }

    public static class Factory implements ShinsuTechnique.IFactory<Thunderstorm> {

        @Override
        public Either<Thunderstorm, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new Thunderstorm(user, 4, 16));
        }

        @Override
        public Thunderstorm blankCreate() {
            return new Thunderstorm(null, 0, 0);
        }

    }

}
