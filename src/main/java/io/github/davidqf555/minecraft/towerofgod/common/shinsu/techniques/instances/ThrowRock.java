package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ThrowRock extends ShinsuTechniqueInstance {

    private Vec3 direction;

    public ThrowRock(Entity user, Vec3 direction) {
        super(user);
        this.direction = direction.normalize();
    }

    @Override
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        if (user != null) {
            Vec3 pos = user.getEyePosition(1).add(0, -0.5, 0).add(direction);
            BlockPos blockPos = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());
            if (world.isEmptyBlock(blockPos)) {
                world.setBlockAndUpdate(blockPos, Blocks.STONE.defaultBlockState());
            }
            FallingBlockEntity block = FallingBlockEntity.fall(world, blockPos, Blocks.STONE.defaultBlockState());
            block.setPos(pos);
            block.setDeltaMovement(user.getDeltaMovement().add(direction.scale(ShinsuStats.get(user).getTension() + 1)));
            block.setHurtsEntities(0.5f, 8);
            block.dropItem = false;
            world.addFreshEntity(block);
        }
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.THROW_ROCK.get();
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Tag.TAG_DOUBLE) && nbt.contains("Y", Tag.TAG_DOUBLE) && nbt.contains("Z", Tag.TAG_DOUBLE)) {
            direction = new Vec3(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<ThrowRock> {

        @Override
        public Either<ThrowRock, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.left(new ThrowRock(user, dir));
        }

        @Override
        public ThrowRock blankCreate() {
            return new ThrowRock(null, Vec3.ZERO);
        }

    }
}
