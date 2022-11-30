package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ReverseFlowControl extends ShinsuTechniqueInstance {

    private static final double RANGE = 3;
    private int duration;
    private UUID target;

    public ReverseFlowControl(Entity user, UUID target, int duration) {
        super(user);
        this.target = target;
        this.duration = duration;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.REVERSE_FLOW_CONTROL.get();
    }

    @Override
    public void tick(ServerLevel world) {
        Entity target = world.getEntity(this.target);
        if (target instanceof LivingEntity) {
            Entity user = getUser(world);
            if (user.distanceToSqr(target) > RANGE * RANGE) {
                remove(world);
                return;
            }
            double resistance = ShinsuStats.getNetResistance(user, target);
            ((LivingEntity) target).addEffect(new MobEffectInstance(EffectRegistry.REVERSE_FLOW.get(), 2, (int) (resistance * 2)));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return 600;
    }

    @Override
    public int getShinsuUse() {
        return 30;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Target", Tag.TAG_INT_ARRAY)) {
            target = nbt.getUUID("Target");
        }
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putUUID("Target", target);
        nbt.putInt("Duration", getDuration());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ReverseFlowControl> {

        @Override
        public Either<ReverseFlowControl, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return target instanceof LivingEntity && user.distanceToSqr(target) <= RANGE * RANGE ? Either.left(new ReverseFlowControl(user, target.getUUID(), 60)) : Either.right(Messages.getRequiresTarget(RANGE));
        }

        @Override
        public ReverseFlowControl blankCreate() {
            return new ReverseFlowControl(null, null, 0);
        }

    }
}
