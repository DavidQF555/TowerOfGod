package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class FlareWaveExplosion extends ShinsuTechniqueInstance {

    private static final double RANGE = 1;
    private UUID target;

    public FlareWaveExplosion(Entity user, UUID target) {
        super(user);
        this.target = target;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FLARE_WAVE_EXPLOSION.get();
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        Entity t = world.getEntity(target);
        if (user != null && t instanceof LivingEntity && user.distanceToSqr(t) <= RANGE * RANGE) {
            LivingEntity target = (LivingEntity) t;
            float damage = (float) (5 / ShinsuStats.getNetResistance(user, target));
            target.hurt(DamageSource.MAGIC, damage);
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (int) (damage * 20), (int) (damage / 2), false, false, false));
        }
    }

    @Override
    public int getCooldown() {
        return 400;
    }

    @Override
    public int getShinsuUse() {
        return 25;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Target", Constants.NBT.TAG_INT_ARRAY)) {
            target = nbt.getUUID("Target");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putUUID("Target", target);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FlareWaveExplosion> {

        @Override
        public Either<FlareWaveExplosion, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return target instanceof LivingEntity && user.distanceToSqr(target) <= RANGE * RANGE ? Either.left(new FlareWaveExplosion(user, target.getUUID())) : Either.right(Messages.getRequiresTarget(RANGE));
        }

        @Override
        public FlareWaveExplosion blankCreate() {
            return new FlareWaveExplosion(null, null);
        }

    }

}
