package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LighthouseFlowControlCommand extends DeviceCommand {

    private final List<LivingEntity> targets;
    private double range;

    public LighthouseFlowControlCommand(FlyingDevice device, UUID technique, double range, int duration) {
        super(device, technique, duration);
        this.range = range;
        targets = new ArrayList<>();
    }

    public static LighthouseFlowControlCommand emptyBuild(FlyingDevice entity) {
        return new LighthouseFlowControlCommand(entity, null, 0, 0);
    }

    @Override
    public void tick() {
        FlyingDevice device = getEntity();
        Entity owner = device.getOwner();
        targets.clear();
        targets.addAll(device.level.getLoadedEntitiesOfClass(LivingEntity.class, AxisAlignedBB.unitCubeFromLowerCorner(device.position()).inflate(range), EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(entity -> entity.distanceToSqr(device) <= range * range && !device.isAlliedTo(entity))));
        Effect effect = EffectRegistry.REVERSE_FLOW.get();
        for (LivingEntity entity : targets) {
            double resistance = ShinsuStats.getNetResistance((ServerWorld) device.level, owner, entity);
            int amplifier = (int) (getAffectingLighthouses(entity) / resistance);
            entity.addEffect(new EffectInstance(effect, 2, amplifier - 1, false, true));
        }
        Random rand = device.getRandom();
        int particles = (int) (Math.PI * range * range / 3);
        for (int i = 0; i < particles; i++) {
            float yaw = rand.nextFloat() * 2 * (float) Math.PI;
            float pitch = rand.nextFloat() * 2 * (float) Math.PI;
            double dist = rand.nextDouble() * range;
            double dY = MathHelper.sin(pitch) * dist;
            double cos = MathHelper.cos(pitch);
            double dX = MathHelper.sin(yaw) * cos * dist;
            double dZ = MathHelper.cos(yaw) * cos * dist;
            Vector3d pos = device.position().add(dX, dY, dZ);
            ((ServerWorld) device.level).getServer().getPlayerList().broadcast(null, pos.x(), pos.y(), pos.z(), 64, device.level.dimension(), new SSpawnParticlePacket(RedstoneParticleData.REDSTONE, false, pos.x(), pos.y(), pos.z(), 0, 0, 0, 0, 0));
        }
    }

    private int getAffectingLighthouses(LivingEntity entity) {
        return entity.level.getLoadedEntitiesOfClass(LighthouseEntity.class, AxisAlignedBB.unitCubeFromLowerCorner(entity.position()).inflate(range), target -> target.distanceToSqr(entity) <= range * range && target.goalSelector.getRunningGoals().map(PrioritizedGoal::getGoal).filter(goal -> goal instanceof LighthouseFlowControlCommand).map(goal -> (LighthouseFlowControlCommand) goal).anyMatch(command -> command.targets.contains(entity))).size();
    }

    @Override
    public CommandType getType() {
        return CommandType.LIGHTHOUSE_FLOW_CONTROL;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Range", Constants.NBT.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("Range", range);
        return nbt;
    }
}
