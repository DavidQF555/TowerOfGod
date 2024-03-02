package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Forest extends AreaTechnique<AreaTechnique.Config, NoData> {

    public Forest() {
        super(Config.CODEC, NoData.CODEC);
    }

    @Nullable
    @Override
    public NoData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return NoData.INSTANCE;
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    @Override
    protected void doEffect(LivingEntity user, ShinsuTechniqueInstance<Config, NoData> inst, Vec3 pos) {
        if (user.level instanceof ServerLevel) {
            List<ConfiguredFeature<?, ?>> trees = ((ServerLevel) user.level).getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
            if (!trees.isEmpty()) {
                BlockPos block = new BlockPos(pos).above();
                Random random = user.getRandom();
                ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
                if (TreeFeature.validTreePos(user.level, block)) {
                    tree.place((ServerLevel) user.level, ((ServerLevel) user.level).getChunkSource().getGenerator(), random, block);
                }
            }
        }
    }
}
