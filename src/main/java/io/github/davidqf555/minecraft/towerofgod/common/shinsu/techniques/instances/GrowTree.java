package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
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

public class GrowTree extends RayTraceTechnique {

    public GrowTree(Entity user, Vector3d direction, double range) {
        super(user, direction, range, true);
    }

    @Override
    public void doEffect(ServerWorld world, RayTraceResult result) {
        BlockPos pos;
        switch (result.getType()) {
            case BLOCK:
                pos = ((BlockRayTraceResult) result).getBlockPos().above();
                break;
            case ENTITY:
                pos = ((EntityRayTraceResult) result).getEntity().blockPosition();
                break;
            default:
                return;
        }
        List<ConfiguredFeature<?, ?>> trees = world.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature instanceof TreeFeature).collect(Collectors.toList());
        if (!trees.isEmpty()) {
            Random random = world.getRandom();
            ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
            if (TreeFeature.validTreePos(world, pos)) {
                tree.place(world, world.getChunkSource().getGenerator(), random, pos);
            }
        }
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.GROW_TREE.get();
    }

    @Override
    public int getShinsuUse() {
        return 20;
    }

    @Override
    public int getCooldown() {
        return 400;
    }

    public static class Factory implements ShinsuTechnique.IFactory<GrowTree> {

        @Override
        public Either<GrowTree, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new GrowTree(user, dir, 64));
        }

        @Override
        public GrowTree blankCreate() {
            return new GrowTree(null, Vector3d.ZERO, 0);
        }
    }
}
