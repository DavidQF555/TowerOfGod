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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class GrowTree extends RayTraceTechnique {

    public GrowTree(Entity user, Vec3 direction, double range) {
        super(user, direction, range, true);
    }

    @Override
    public void doEffect(ServerLevel world, HitResult result) {
        BlockPos pos;
        switch (result.getType()) {
            case BLOCK:
                pos = ((BlockHitResult) result).getBlockPos().above();
                break;
            case ENTITY:
                pos = ((EntityHitResult) result).getEntity().blockPosition();
                break;
            default:
                return;
        }
        List<ConfiguredFeature<?, ?>> trees = world.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
        if (!trees.isEmpty()) {
            RandomSource random = world.getRandom();
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
        public Either<GrowTree, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new GrowTree(user, dir, 64));
        }

        @Override
        public GrowTree blankCreate() {
            return new GrowTree(null, Vec3.ZERO, 0);
        }
    }
}
