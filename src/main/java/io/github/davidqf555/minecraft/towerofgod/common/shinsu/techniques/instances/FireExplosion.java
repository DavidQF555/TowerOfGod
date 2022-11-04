package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class FireExplosion extends GroundTechniqueInstance {

    private int level;

    public FireExplosion(LivingEntity user, double dX, double dZ, int level) {
        super(user, dX, dZ, 3, 1, 8);
        this.level = level;
    }

    @Override
    public int getDuration() {
        return 15;
    }

    @Override
    public void doEffect(ServerWorld world, Vector3d pos) {
        world.explode(getUser(world), pos.x(), pos.y(), pos.z(), 2, true, Explosion.Mode.NONE);
        world.sendParticles(ParticleTypes.FLAME, pos.x(), pos.y(), pos.z(), 100, 2, 2, 2, 0.2);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.ERUPTION.get();
    }

    @Override
    public int getShinsuUse() {
        return level * 5;
    }

    @Override
    public int getBaangsUse() {
        return 1 + level / 5;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("Level", level);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            level = nbt.getInt("Level");
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<FireExplosion> {

        @Override
        public Either<FireExplosion, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            return Either.left(new FireExplosion(user, dir.x(), dir.z(), level));
        }

        @Override
        public FireExplosion blankCreate() {
            return new FireExplosion(null, 1, 0, 0);
        }

    }
}
