package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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
    public void onUse(ServerLevel world) {
        Entity user = getUser(world);
        Entity t = world.getEntity(target);
        if (user != null && t instanceof LivingEntity target && user.distanceToSqr(t) <= RANGE * RANGE) {
            float damage = (float) (5 / ShinsuStats.getNetResistance(user, target));
            target.hurt(world.damageSources().magic(), damage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (damage * 20), (int) (damage / 2), false, false, false));
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
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Target", Tag.TAG_INT_ARRAY)) {
            target = nbt.getUUID("Target");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putUUID("Target", target);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FlareWaveExplosion> {

        @Override
        public Either<FlareWaveExplosion, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return target instanceof LivingEntity && user.distanceToSqr(target) <= RANGE * RANGE ? Either.left(new FlareWaveExplosion(user, target.getUUID())) : Either.right(Messages.getRequiresTarget(RANGE));
        }

        @Override
        public FlareWaveExplosion blankCreate() {
            return new FlareWaveExplosion(null, null);
        }

    }

}
