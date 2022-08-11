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
        targets.addAll(device.world.getLoadedEntitiesWithinAABB(LivingEntity.class, AxisAlignedBB.fromVector(device.getPositionVec()).grow(range), EntityPredicates.CAN_AI_TARGET.and(entity -> entity.getDistanceSq(device) <= range * range && !device.isOnSameTeam(entity))));
        Effect effect = EffectRegistry.REVERSE_FLOW.get();
        for (LivingEntity entity : targets) {
            double resistance = ShinsuStats.getNetResistance((ServerWorld) device.world, owner, entity);
            int amplifier = (int) (getAffectingLighthouses(entity) / resistance);
            entity.addPotionEffect(new EffectInstance(effect, 2, amplifier - 1, false, true));
        }
        Random rand = device.getRNG();
        int particles = (int) (Math.PI * range * range / 3);
        for (int i = 0; i < particles; i++) {
            float yaw = rand.nextFloat() * 2 * (float) Math.PI;
            float pitch = rand.nextFloat() * 2 * (float) Math.PI;
            double dist = rand.nextDouble() * range;
            double dY = MathHelper.sin(pitch) * dist;
            double cos = MathHelper.cos(pitch);
            double dX = MathHelper.sin(yaw) * cos * dist;
            double dZ = MathHelper.cos(yaw) * cos * dist;
            Vector3d pos = device.getPositionVec().add(dX, dY, dZ);
            ((ServerWorld) device.world).getServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 64, device.world.getDimensionKey(), new SSpawnParticlePacket(RedstoneParticleData.REDSTONE_DUST, false, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0));
        }
    }

    private int getAffectingLighthouses(LivingEntity entity) {
        return entity.world.getLoadedEntitiesWithinAABB(LighthouseEntity.class, AxisAlignedBB.fromVector(entity.getPositionVec()).grow(range), target -> target.getDistanceSq(entity) <= range * range && target.goalSelector.getRunningGoals().map(PrioritizedGoal::getGoal).filter(goal -> goal instanceof LighthouseFlowControlCommand).map(goal -> (LighthouseFlowControlCommand) goal).anyMatch(command -> command.targets.contains(entity))).size();
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
