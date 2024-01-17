package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class TreeWall extends GroundTechniqueInstance {

    public TreeWall(Entity user, double dX, double dZ) {
        super(user, dX, dZ, 1.5, 1, 8);
    }

    @Override
    public void doEffect(ServerLevel world, Vec3 pos) {
        List<ConfiguredFeature<?, ?>> trees = world.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
        if (!trees.isEmpty()) {
            RandomSource random = world.getRandom();
            int horizontalRadius = 3;
            int yRadius = 5;
            BlockPos base = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());
            for (int dY = -yRadius; dY < yRadius; dY++) {
                for (int dX = -horizontalRadius; dX < horizontalRadius; dX++) {
                    for (int dZ = -horizontalRadius; dZ < horizontalRadius; dZ++) {
                        BlockPos effect = base.offset(dX, dY, dZ);
                        if (TreeFeature.validTreePos(world, effect)) {
                            ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
                            tree.place(world, world.getChunkSource().getGenerator(), random, effect);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getDuration() {
        return 120;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.TREE_WALL.get();
    }

    @Override
    public int getShinsuUse() {
        return 70;
    }

    @Override
    public int getCooldown() {
        return 2400;
    }

    public static class Factory implements ShinsuTechnique.IFactory<TreeWall> {

        @Override
        public Either<TreeWall, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new TreeWall(user, dir.x(), dir.z()));
        }

        @Override
        public TreeWall blankCreate() {
            return new TreeWall(null, 1, 0);
        }
    }

}
