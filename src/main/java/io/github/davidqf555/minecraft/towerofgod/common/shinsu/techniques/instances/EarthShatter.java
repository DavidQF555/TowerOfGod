package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class EarthShatter extends GroundTechniqueInstance {

    public EarthShatter(Entity user, double dX, double dZ) {
        super(user, dX, dZ, 4, 1, 4);
    }

    @Override
    public void doEffect(ServerWorld world, Vector3d pos) {
        Random random = world.getRandom();
        int horizontalRadius = 3;
        int yRadius = 5;
        for (int dY = -yRadius; dY <= 1; dY++) {
            for (int dX = -horizontalRadius; dX < horizontalRadius; dX++) {
                for (int dZ = -horizontalRadius; dZ < horizontalRadius; dZ++) {
                    BlockPos effect = new BlockPos(pos).offset(dX, dY, dZ);
                    BlockState state = world.getBlockState(effect);
                    if (!world.isEmptyBlock(effect) && state.getDestroySpeed(world, effect) > 0) {
                        FallingBlockEntity block = new FallingBlockEntity(world, effect.getX() + 0.5, effect.getY(), effect.getZ() + 0.5, state);
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
        public Either<EarthShatter, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new EarthShatter(user, dir.x(), dir.z()));
        }

        @Override
        public EarthShatter blankCreate() {
            return new EarthShatter(null, 1, 0);
        }
    }
}
