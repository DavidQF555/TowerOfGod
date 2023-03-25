package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class EarthShatter extends GroundTechniqueInstance {

    public EarthShatter(Entity user, double dX, double dZ) {
        super(user, dX, dZ, 4, 1, 4);
    }

    @Override
    public void doEffect(ServerLevel world, Vec3 pos) {
        RandomSource random = world.getRandom();
        int horizontalRadius = 3;
        int yRadius = 5;
        for (int dY = -yRadius; dY <= 1; dY++) {
            for (int dX = -horizontalRadius; dX < horizontalRadius; dX++) {
                for (int dZ = -horizontalRadius; dZ < horizontalRadius; dZ++) {
                    BlockPos effect = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()).offset(dX, dY, dZ);
                    BlockState state = world.getBlockState(effect);
                    if (!world.isEmptyBlock(effect) && state.getDestroySpeed(world, effect) > 0) {
                        FallingBlockEntity block = FallingBlockEntity.fall(world, effect, state);
                        block.dropItem = false;
                        block.setDeltaMovement(random.nextGaussian() * 0.25, random.nextDouble(), random.nextGaussian() * 0.25);
                        world.addFreshEntity(block);
                    }
                }
            }
        }
    }

    @Override
    public int getDuration() {
        return 10;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.EARTH_SHATTER.get();
    }

    @Override
    public int getShinsuUse() {
        return 70;
    }

    @Override
    public int getCooldown() {
        return 2400;
    }

    public static class Factory implements ShinsuTechnique.IFactory<EarthShatter> {

        @Override
        public Either<EarthShatter, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new EarthShatter(user, dir.x(), dir.z()));
        }

        @Override
        public EarthShatter blankCreate() {
            return new EarthShatter(null, 1, 0);
        }
    }
}
