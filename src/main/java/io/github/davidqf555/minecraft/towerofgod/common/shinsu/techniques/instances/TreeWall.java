package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TreeWall extends GroundTechniqueInstance {

    public TreeWall(LivingEntity user, double dX, double dZ) {
        super(user, dX, dZ, 1.5, 1, 8);
    }

    @Override
    public void doEffect(ServerWorld world, Vector3d pos) {
        List<ConfiguredFeature<?, ?>> trees = world.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature instanceof TreeFeature).collect(Collectors.toList());
        if (!trees.isEmpty()) {
            Random random = world.getRandom();
            int horizontalRadius = 3;
            int yRadius = 5;
            for (int dY = -yRadius; dY < yRadius; dY++) {
                for (int dX = -horizontalRadius; dX < horizontalRadius; dX++) {
                    for (int dZ = -horizontalRadius; dZ < horizontalRadius; dZ++) {
                        BlockPos effect = new BlockPos(pos).offset(dX, dY, dZ);
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
        return 20;
    }

    @Override
    public int getBaangsUse() {
        return 2;
    }

    public static class Factory implements ShinsuTechnique.IFactory<TreeWall> {

        @Override
        public Either<TreeWall, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new TreeWall(user, dir.x(), dir.z()));
        }

        @Override
        public TreeWall blankCreate() {
            return new TreeWall(null, 1, 0);
        }
    }

}
