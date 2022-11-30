package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
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

public class Forest extends AreaTechnique {

    public Forest(Entity user, double minRadius, double radius) {
        super(user, minRadius, radius, 20, 5);
    }

    @Override
    public int getDuration() {
        return 500;
    }

    @Override
    protected void doEffect(ServerWorld world, Vector3d pos) {
        List<ConfiguredFeature<?, ?>> trees = world.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature instanceof TreeFeature).collect(Collectors.toList());
        if (!trees.isEmpty()) {
            BlockPos block = new BlockPos(pos).above();
            Random random = world.getRandom();
            ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
            if (TreeFeature.validTreePos(world, block)) {
                tree.place(world, world.getChunkSource().getGenerator(), random, block);
            }
        }
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FOREST.get();
    }

    @Override
    public int getShinsuUse() {
        return 20;
    }

    public static class Factory implements ShinsuTechnique.IFactory<Forest> {

        @Override
        public Either<Forest, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new Forest(user, 4, 32));
        }

        @Override
        public Forest blankCreate() {
            return new Forest(null, 0, 0);
        }
    }

}
