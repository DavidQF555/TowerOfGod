package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
        targets.addAll(device.level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(device.position(), range, range, range), EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity -> entity.distanceToSqr(device) <= range * range && !device.isAlliedTo(entity))));
        MobEffect effect = EffectRegistry.REVERSE_FLOW.get();
        for (LivingEntity entity : targets) {
            double resistance = ShinsuStats.getNetResistance(owner, entity);
            int amplifier = (int) (getAffectingLighthouses(entity) / resistance);
            entity.addEffect(new MobEffectInstance(effect, 2, amplifier - 1, false, true));
        }
        Random rand = device.getRandom();
        int particles = (int) (Math.PI * range * range / 3);
        for (int i = 0; i < particles; i++) {
            float yaw = rand.nextFloat() * 2 * (float) Math.PI;
            float pitch = rand.nextFloat() * 2 * (float) Math.PI;
            double dist = rand.nextDouble() * range;
            double dY = Mth.sin(pitch) * dist;
            double cos = Mth.cos(pitch);
            double dX = Mth.sin(yaw) * cos * dist;
            double dZ = Mth.cos(yaw) * cos * dist;
            Vec3 pos = device.position().add(dX, dY, dZ);
            ((ServerLevel) device.level).getServer().getPlayerList().broadcast(null, pos.x(), pos.y(), pos.z(), 64, device.level.dimension(), new ClientboundLevelParticlesPacket(DustParticleOptions.REDSTONE, false, pos.x(), pos.y(), pos.z(), 0, 0, 0, 0, 0));
        }
    }

    private int getAffectingLighthouses(LivingEntity entity) {
        return entity.level.getEntitiesOfClass(LighthouseEntity.class, AABB.ofSize(entity.position(), range, range, range), target -> target.distanceToSqr(entity) <= range * range && target.goalSelector.getRunningGoals().map(WrappedGoal::getGoal).filter(goal -> goal instanceof LighthouseFlowControlCommand).map(goal -> (LighthouseFlowControlCommand) goal).anyMatch(command -> command.targets.contains(entity))).size();
    }

    @Override
    public CommandType getType() {
        return CommandType.LIGHTHOUSE_FLOW_CONTROL;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Range", Tag.TAG_DOUBLE)) {
            range = nbt.getDouble("Range");
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.putDouble("Range", range);
        return nbt;
    }
}
