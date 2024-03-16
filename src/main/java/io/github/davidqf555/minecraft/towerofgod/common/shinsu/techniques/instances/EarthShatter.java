package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class EarthShatter extends GroundTechniqueInstance<GroundTechniqueInstance.Config, GroundTechniqueInstance.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public EarthShatter() {
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
        RandomSource random = user.getRandom();
        int horizontalRadius = 3;
        int yRadius = 5;
        for (int dY = -yRadius; dY <= 1; dY++) {
            for (int dX = -horizontalRadius; dX < horizontalRadius; dX++) {
                for (int dZ = -horizontalRadius; dZ < horizontalRadius; dZ++) {
                    BlockPos effect = new BlockPos(pos).offset(dX, dY, dZ);
                    BlockState state = user.level.getBlockState(effect);
                    if (!user.level.isEmptyBlock(effect) && state.getDestroySpeed(user.level, effect) > 0) {
                        FallingBlockEntity block = FallingBlockEntity.fall(user.level, effect, state);
                        block.dropItem = false;
                        block.setDeltaMovement(random.nextGaussian() * 0.25, random.nextDouble(), random.nextGaussian() * 0.25);
                        user.level.addFreshEntity(block);
                    }
                }
            }
        }
    }

}
