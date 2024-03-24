package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public abstract class GroundTechniqueInstance<C extends GroundTechniqueInstance.Config, S extends GroundTechniqueInstance.Data> extends ShinsuTechniqueType<C, S> {

    public GroundTechniqueInstance(Codec<C> config, Codec<S> data) {
        super(config, data);
    }

    public abstract void doEffect(LivingEntity user, ShinsuTechniqueInstance<C, S> inst, Vec3 pos);

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<C, S> inst) {
        C config = inst.getConfigured().getConfig();
        if (inst.getTicks() % config.period == 0) {
            S data = inst.getData();
            int count = inst.getTicks() / config.period;
            double x = data.startX + data.dX * config.speed * (count + 1);
            double z = data.startZ + data.dZ * config.speed * (count + 1);
            BlockPos pos = null;
            for (int dY = -config.maxYDif; dY < config.maxYDif; dY++) {
                BlockPos test = new BlockPos(x, data.prevY + dY, z);
                if (user.level.getBlockState(test).isCollisionShapeFullBlock(user.level, test) && user.level.getBlockState(test.above()).getCollisionShape(user.level, test.above()).isEmpty() && (pos == null || Math.abs(dY) < Math.abs(data.prevY - pos.getY()))) {
                    pos = test;
                }
            }
            if (pos == null) {
                inst.remove(user);
            } else {
                doEffect(user, inst, new Vec3(x, pos.getY(), z));
                data.prevY = pos.getY();
            }
        }
        super.tick(user, inst);
    }

    public static class Data extends ShinsuTechniqueInstanceData {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id),
                Codec.DOUBLE.fieldOf("start_x").forGetter(data -> data.startX),
                Codec.DOUBLE.fieldOf("start_z").forGetter(data -> data.startZ),
                Codec.DOUBLE.fieldOf("dX").forGetter(data -> data.dX),
                Codec.DOUBLE.fieldOf("dZ").forGetter(data -> data.dZ),
                Codec.INT.fieldOf("prev_y").forGetter(data -> data.prevY)
        ).apply(inst, Data::new));
        public final double startX, startZ, dX, dZ;
        public int prevY;

        public Data(UUID id, double startX, double startZ, double dX, double dZ, int prevY) {
            super(id);
            this.startX = startX;
            this.startZ = startZ;
            double length = Math.sqrt(dX * dX + dZ * dZ);
            this.dX = dX / length;
            this.dZ = dZ / length;
            this.prevY = prevY;
        }

    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(inst.group(
                        Codec.DOUBLE.fieldOf("speed").forGetter(config -> config.speed),
                        Codec.INT.fieldOf("period").forGetter(config -> config.period),
                        Codec.INT.fieldOf("max_y").forGetter(config -> config.maxYDif)
                )).apply(inst, Config::new));
        public final double speed;
        public final int period, maxYDif;

        public Config(Display display, MobUseCondition condition, Optional<Integer> duration, int cooldown, double speed, int period, int maxYDif) {
            super(display, condition, duration, cooldown);
            this.speed = speed;
            this.period = period;
            this.maxYDif = maxYDif;
        }

    }

}
