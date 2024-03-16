package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
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

public class Forest extends AreaTechnique<AreaTechnique.Config, ShinsuTechniqueInstanceData> {

    public Forest() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Nullable
    @Override
    public ShinsuTechniqueInstanceData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    @Override
    protected void doEffect(LivingEntity user, ShinsuTechniqueInstance<Config, ShinsuTechniqueInstanceData> inst, Vec3 pos) {
        if (user.level instanceof ServerLevel) {
            List<ConfiguredFeature<?, ?>> trees = ((ServerLevel) user.level).getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
            if (!trees.isEmpty()) {
                BlockPos block = new BlockPos(pos).above();
                RandomSource random = user.getRandom();
                ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
                if (TreeFeature.validTreePos(user.level, block)) {
                    tree.place((ServerLevel) user.level, ((ServerLevel) user.level).getChunkSource().getGenerator(), random, block);
                }
            }
        }
    }
}
