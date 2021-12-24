package io.github.davidqf555.minecraft.towerofgod.common.techinques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements.TypeLevelRequirement;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlackFish extends ShinsuTechniqueInstance {

    private int initial, light;

    public BlackFish(LivingEntity user, int initial, int light) {
        super(user);
        this.initial = initial;
        this.light = light;
    }

    @Override
    public int getInitialDuration() {
        return initial;
    }

    @Override
    public void periodicTick(ServerWorld world, int period) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity && world.getLight(e.getPosition()) <= light) {
            ((LivingEntity) e).addPotionEffect(new EffectInstance(Effects.INVISIBILITY, Math.min(period, ticksLeft()) + 1, 0, true, true, true));
        }
        super.periodicTick(world, period);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.BLACK_FISH;
    }

    @Override
    public int getCooldown() {
        return getInitialDuration() + 40;
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Initial", Constants.NBT.TAG_INT)) {
            initial = nbt.getInt("Initial");
        }
        if (nbt.contains("Light", Constants.NBT.TAG_INT)) {
            light = nbt.getInt("Light");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("Initial", initial);
        nbt.putInt("Light", light);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<BlackFish> {

        @Override
        public Either<BlackFish, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            return Either.left(new BlackFish(user, level * 40 + 100, level));
        }

        @Override
        public BlackFish blankCreate() {
            return new BlackFish(null, 0, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.BLACK_FISH;
        }

        @Override
        public IRequirement[] getRequirements() {
            return new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 5)};
        }
    }
}
