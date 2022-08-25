package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
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
    private int duration, level;
    private UUID target;

    public ReverseFlowControl(LivingEntity user, UUID target, int duration, int level) {
        super(user);
        this.target = target;
        this.duration = duration;
        this.level = level;
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
            int amp = (int) (resistance * level);
            ((LivingEntity) target).addEffect(new EffectInstance(EffectRegistry.REVERSE_FLOW.get(), 2, amp - 1));
        }
        super.tick(world);
    }

    @Override
    public int getCooldown() {
        return getDuration() + 500;
    }

    @Override
    public int getShinsuUse() {
        return 5 + level * 5;
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
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Level", Constants.NBT.TAG_INT)) {
            level = nbt.getInt("Level");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putUUID("Target", target);
        nbt.putInt("Duration", getDuration());
        nbt.putInt("Level", level);
        return nbt;
    }

    @MethodsReturnNonnullByDefault
    public static class Factory implements ShinsuTechnique.IFactory<ReverseFlowControl> {

        @Override
        public Either<ReverseFlowControl, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.DISRUPTION).getLevel();
            return target instanceof LivingEntity && user.distanceToSqr(target) <= RANGE * RANGE ? Either.left(new ReverseFlowControl(user, target.getUUID(), 20 + level * 10, level)) : Either.right(Messages.getRequiresTarget(RANGE));
        }

        @Override
        public ReverseFlowControl blankCreate() {
            return new ReverseFlowControl(null, null, 0, 0);
        }

    }
}
