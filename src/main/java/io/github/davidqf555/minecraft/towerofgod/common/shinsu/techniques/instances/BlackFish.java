package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
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

    private int duration, light;

    public BlackFish(Entity user, int duration, int light) {
        super(user);
        this.duration = duration;
        this.light = light;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void tick(ServerWorld world) {
        Entity e = getUser(world);
        if (e instanceof LivingEntity && world.getLightEmission(e.blockPosition()) <= light) {
            ((LivingEntity) e).addEffect(new EffectInstance(Effects.INVISIBILITY, 2, 0, true, true, true));
        }
        super.tick(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.BLACK_FISH.get();
    }

    @Override
    public int getCooldown() {
        return getDuration() + 40;
    }

    @Override
    public int getShinsuUse() {
        return 10;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Light", Constants.NBT.TAG_INT)) {
            light = nbt.getInt("Light");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putInt("Duration", duration);
        nbt.putInt("Light", light);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<BlackFish> {

        @Override
        public Either<BlackFish, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new BlackFish(user, 300, 5));
        }

        @Override
        public BlackFish blankCreate() {
            return new BlackFish(null, 0, 0);
        }

    }
}
