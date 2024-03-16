package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TreeWall extends GroundTechniqueInstance<GroundTechniqueInstance.Config, GroundTechniqueInstance.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public TreeWall() {
        super(Config.CODEC, Data.CODEC);
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return new Data(Mth.createInsecureUUID(), user.getX(), user.getZ(), user.getLookAngle().x(), user.getLookAngle().z(), user.getBlockY());
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Override
    public void doEffect(LivingEntity user, ShinsuTechniqueInstance<Config, Data> inst, Vec3 pos) {
        if (user.level instanceof ServerLevel) {
            List<ConfiguredFeature<?, ?>> trees = user.level.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
            if (!trees.isEmpty()) {
                RandomSource random = user.getRandom();
                int horizontalRadius = 3;
                int yRadius = 5;
                for (int dY = -yRadius; dY < yRadius; dY++) {
                    for (int dX = -horizontalRadius; dX < horizontalRadius; dX++) {
                        for (int dZ = -horizontalRadius; dZ < horizontalRadius; dZ++) {
                            BlockPos effect = new BlockPos(pos).offset(dX, dY, dZ);
                            if (TreeFeature.validTreePos(user.level, effect)) {
                                ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
                                tree.place((ServerLevel) user.level, ((ServerLevel) user.level).getChunkSource().getGenerator(), random, effect);
                            }
                        }
                    }
                }
            }
        }

    }

}
