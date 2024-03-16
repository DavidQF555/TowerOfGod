package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ThrowRock extends ShinsuTechniqueType<ShinsuTechniqueConfig, ShinsuTechniqueInstanceData> {

    public ThrowRock() {
        super(ShinsuTechniqueConfig.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Override
    public ShinsuTechniqueInstanceData onUse(LivingEntity user, ShinsuTechniqueConfig config, @Nullable LivingEntity target) {
        Vec3 direction = user.getLookAngle();
        Vec3 pos = user.getEyePosition(1).add(0, -0.5, 0).add(direction);
        BlockPos blockPos = new BlockPos(pos);
        if (user.level.isEmptyBlock(blockPos)) {
            user.level.setBlockAndUpdate(blockPos, Blocks.STONE.defaultBlockState());
        }
        FallingBlockEntity block = FallingBlockEntity.fall(user.level, blockPos, Blocks.STONE.defaultBlockState());
        block.setPos(pos);
        block.setDeltaMovement(user.getDeltaMovement().add(direction.scale(ShinsuStats.get(user).getTension() + 1)));
        block.setHurtsEntities(0.5f, 8);
        block.dropItem = false;
        user.level.addFreshEntity(block);
        return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

}
