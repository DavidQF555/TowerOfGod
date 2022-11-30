package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
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
    public void tick(ServerWorld world) {
        Entity target = world.getEntity(this.target);
        if (target instanceof LivingEntity) {
            Entity user = getUser(world);
            if (user.distanceToSqr(target) > RANGE * RANGE) {
                remove(world);
                return;
            }
            double resistance = ShinsuStats.getNetResistance(user, target);
            ((LivingEntity) target).addEffect(new EffectInstance(EffectRegistry.REVERSE_FLOW.get(), 2, (int) (resistance * 2)));
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
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Target", Constants.NBT.TAG_INT_ARRAY)) {
            target = nbt.getUUID("Target");
        }
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putUUID("Target", target);
        nbt.putInt("Duration", getDuration());
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ReverseFlowControl> {

        @Override
        public Either<ReverseFlowControl, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return target instanceof LivingEntity && user.distanceToSqr(target) <= RANGE * RANGE ? Either.left(new ReverseFlowControl(user, target.getUUID(), 60)) : Either.right(Messages.getRequiresTarget(RANGE));
        }

        @Override
        public ReverseFlowControl blankCreate() {
            return new ReverseFlowControl(null, null, 0);
        }

    }
}
