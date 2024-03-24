package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ScoutCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Scout extends BasicCommandTechnique<Scout.Config, BasicCommandTechnique.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public Scout() {
        super(Config.CODEC, Data.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Override
    protected DeviceCommand createCommand(LivingEntity user, Config config, FlyingDevice entity, UUID id) {
        Vec3 eye = user.getEyePosition(1);
        Vec3 end = eye.add(user.getLookAngle().scale(config.range));
        BlockPos target;
        EntityHitResult trace = ProjectileUtil.getEntityHitResult(user.level, user, eye, end, AABB.ofSize(eye, config.range * 2, config.range * 2, config.range * 2), e -> true);
        if (trace == null) {
            BlockHitResult result = user.level.clip(new ClipContext(eye, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            target = result.getBlockPos().relative(result.getDirection());
        } else {
            target = trace.getEntity().blockPosition();
        }
        return new ScoutCommand(entity, id, target, config.speed, config.radius, config.getDuration().orElseThrow());
    }

    @Override
    protected Data createData(UUID id, List<UUID> devices) {
        return new Data(id, devices);
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(inst.group(
                        Codec.DOUBLE.fieldOf("range").forGetter(config -> config.range),
                        Codec.INT.fieldOf("radius").forGetter(config -> config.radius),
                        Codec.FLOAT.fieldOf("speed").forGetter(config -> config.speed)
                )).apply(inst, Config::new));
        public final double range;
        public final int radius;
        public final float speed;

        public Config(Display display, MobUseCondition condition, Optional<Integer> duration, int cooldown, double range, int radius, float speed) {
            super(display, condition, duration, cooldown);
            this.range = range;
            this.radius = radius;
            this.speed = speed;
        }

    }

}
