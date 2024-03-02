package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GrowTree extends RayTraceTechnique<RayTraceTechnique.Config, NoData> {

    public GrowTree() {
        super(Config.CODEC, NoData.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    @Nullable
    @Override
    protected NoData doEffect(LivingEntity user, Config config, @Nullable LivingEntity target, HitResult result) {
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
                Random random = user.getRandom();
                ConfiguredFeature<?, ?> tree = trees.get(random.nextInt(trees.size()));
                if (TreeFeature.validTreePos(user.level, pos)) {
                    tree.place((ServerLevel) user.level, ((ServerLevel) user.level).getChunkSource().getGenerator(), random, pos);
                }
                return NoData.INSTANCE;
            }
        }
        return null;
    }

}
