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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class GrowTree extends RayTraceTechnique<RayTraceTechnique.Config, ShinsuTechniqueInstanceData> {

    public GrowTree() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    @Nullable
    @Override
    protected ShinsuTechniqueInstanceData doEffect(LivingEntity user, Config config, @Nullable LivingEntity target, HitResult result) {
        if (user.level instanceof ServerLevel) {
            BlockPos pos;
            switch (result.getType()) {
                case BLOCK:
                    pos = ((BlockHitResult) result).getBlockPos().above();
                    break;
                case ENTITY:
                    pos = ((EntityHitResult) result).getEntity().blockPosition();
                    break;
                default:
                    return null;
            }
            List<ConfiguredFeature<?, ?>> trees = user.level.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY).stream().filter(feature -> feature.feature() instanceof TreeFeature).toList();
            if (!trees.isEmpty()) {
                RandomSource random = user.getRandom();
                ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
                if (TreeFeature.validTreePos(user.level, pos)) {
                    tree.place((ServerLevel) user.level, ((ServerLevel) user.level).getChunkSource().getGenerator(), random, pos);
                }
                return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
            }
        }
        return null;
    }

}
