package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
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
    private float damage;
    private int amp;
    private UUID target;

    public FlareWaveExplosion(LivingEntity user, UUID target, float damage, int amp) {
        super(user);
        this.target = target;
        this.damage = damage;
        this.amp = amp;
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
            double resistance = ShinsuStats.getNetResistance(world, user, target);
            target.hurt(DamageSource.MAGIC, damage / (float) resistance);
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (int) (60 / resistance), amp - 1, false, false, false));
        }
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public int getShinsuUse() {
        return amp * 3 + 7;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Target", Constants.NBT.TAG_INT_ARRAY)) {
            target = nbt.getUUID("Target");
        }
        if (nbt.contains("Damage", Constants.NBT.TAG_FLOAT)) {
            damage = nbt.getFloat("Damage");
        }
        if (nbt.contains("Amplification", Constants.NBT.TAG_INT)) {
            amp = nbt.getInt("Amplification");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putUUID("Target", target);
        nbt.putFloat("Damage", damage);
        nbt.putInt("Amplification", amp);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<FlareWaveExplosion> {

        @Override
        public Either<FlareWaveExplosion, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.DISRUPTION).getLevel();
            return target instanceof LivingEntity && user.distanceToSqr(target) <= RANGE * RANGE ? Either.left(new FlareWaveExplosion(user, target.getUUID(), level * 2.5f, level)) : Either.right(Messages.getRequiresTarget(RANGE));
        }

        @Override
        public FlareWaveExplosion blankCreate() {
            return new FlareWaveExplosion(null, null, 0, 0);
        }

    }

}
