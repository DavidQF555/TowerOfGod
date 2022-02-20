package io.github.davidqf555.minecraft.towerofgod.common.techinques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BodyReinforcement extends ShinsuTechniqueInstance {

    private int initial, level;

    public BodyReinforcement(LivingEntity user, int initial, int level) {
        super(user);
        this.initial = initial;
        this.level = level;
    }

    @Override
    public int getInitialDuration() {
        return initial;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.BODY_REINFORCEMENT;
    }

    @Override
    public void periodicTick(ServerWorld world, int period) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) e;
            int amp = (int) (level * ShinsuStats.get(user).getTension(world));
            user.addPotionEffect(new EffectInstance(RegistryHandler.BODY_REINFORCEMENT_EFFECT.get(), Math.min(period, ticksLeft()) + 1, amp - 1, false, true, true));
        }
        super.periodicTick(world, period);
    }

    @Override
    public int getCooldown() {
        return getInitialDuration() + 150;
    }

    @Override
    public int getShinsuUse() {
        return level + 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            level = nbt.getInt("Level");
        }
        if (nbt.contains("Initial", Constants.NBT.TAG_INT)) {
            initial = nbt.getInt("Initial");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("Level", level);
        nbt.putInt("Initial", initial);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<BodyReinforcement> {

        @Override
        public Either<BodyReinforcement, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            return Either.left(new BodyReinforcement(user, 200 + level * 100, level));
        }

        @Override
        public BodyReinforcement blankCreate() {
            return new BodyReinforcement(null, 0, 0);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.BODY_REINFORCEMENT;
        }

        @Override
        public IRequirement[] getRequirements() {
            return new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 1)};
        }
    }
}
