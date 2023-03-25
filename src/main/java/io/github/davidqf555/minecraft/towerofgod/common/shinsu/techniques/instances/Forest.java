package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class Forest extends AreaTechnique {

    public Forest(Entity user, double minRadius, double radius) {
        super(user, minRadius, radius, 20, 5);
    }

    @Override
    public int getDuration() {
        return 500;
    }

    @Override
    protected void doEffect(ServerLevel world, Vec3 pos) {
        List<ConfiguredFeature<?, ?>> trees = world.getServer().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
        if (!trees.isEmpty()) {
            BlockPos block = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z()).above();
            RandomSource random = world.getRandom();
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
        return 60;
    }

    @Override
    public int getCooldown() {
        return 2000;
    }

    public static class Factory implements ShinsuTechnique.IFactory<Forest> {

        @Override
        public Either<Forest, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new Forest(user, 4, 32));
        }

        @Override
        public Forest blankCreate() {
            return new Forest(null, 0, 0);
        }
    }

}
