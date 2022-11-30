package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ThrowRock extends ShinsuTechniqueInstance {

    private Vector3d direction;
    private double velocity;

    public ThrowRock(Entity user, Vector3d direction, double velocity) {
        super(user);
        this.direction = direction.normalize();
        this.velocity = velocity;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        if (user != null) {
            Vector3d pos = user.getEyePosition(1).add(0, -0.5, 0).add(direction);
            BlockPos blockPos = new BlockPos(pos);
            if (world.isEmptyBlock(blockPos)) {
                world.setBlockAndUpdate(blockPos, Blocks.STONE.defaultBlockState());
            }
            FallingBlockEntity block = new FallingBlockEntity(world, pos.x(), pos.y(), pos.z(), Blocks.STONE.defaultBlockState());
            block.setDeltaMovement(user.getDeltaMovement().add(direction.scale(velocity)));
            block.setHurtsEntities(true);
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("Velocity", velocity);
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Velocity", Constants.NBT.TAG_DOUBLE)) {
            velocity = nbt.getDouble("Velocity");
        }
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<ThrowRock> {

        @Override
        public Either<ThrowRock, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ThrowRock(user, dir, 2));
        }

        @Override
        public ThrowRock blankCreate() {
            return new ThrowRock(null, Vector3d.ZERO, 0);
        }

    }
}
