package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.MoveCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MoveDevices extends BasicCommandTechnique<MoveDevices.Config, BasicCommandTechnique.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public MoveDevices() {
        super(Config.CODEC, Data.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Override
    protected DeviceCommand createCommand(LivingEntity user, Config config, FlyingDevice entity, UUID id) {
        Vec3 eye = user.getEyePosition(1);
        Vec3 end = eye.add(user.getLookAngle().scale(config.distance));
        EntityHitResult trace = ProjectileUtil.getEntityHitResult(user.level, user, eye, end, AABB.ofSize(eye, config.distance * 2, config.distance * 2, config.distance * 2), e -> true);
        Vec3 target = trace == null ? user.level.clip(new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity)).getLocation() : trace.getLocation();
        return new MoveCommand(entity, id, target, config.speed);
    }

    @Override
    protected Data createData(UUID id, List<UUID> devices) {
        return new Data(id, devices);
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(inst.group(
                        Codec.DOUBLE.fieldOf("distance").forGetter(config -> config.distance),
                        Codec.FLOAT.fieldOf("speed").forGetter(config -> config.speed)
                )).apply(inst, Config::new));
        public final double distance;
        public final float speed;

        public Config(Display display, MobUseCondition condition, Optional<Integer> duration, int cooldown, double distance, float speed) {
            super(display, condition, duration, cooldown);
            this.distance = distance;
            this.speed = speed;
        }

    }

}
