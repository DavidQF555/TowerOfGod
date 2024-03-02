package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Random;

public abstract class AreaTechnique<C extends AreaTechnique.Config, S> extends ShinsuTechniqueType<C, S> {

    private static final int TRIES = 16;

    public AreaTechnique(Codec<C> config, Codec<S> data) {
        super(config, data);
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<C, S> inst) {
        C config = inst.getConfigured().getConfig();
        if (user.level.getGameTime() % config.period == 0) {
            Random rand = user.getRandom();
            for (int i = 0; i < TRIES; i++) {
                float degree = rand.nextFloat() * (float) Math.PI * 2;
                double x = user.getX() + Mth.cos(degree) * (config.minRadius + rand.nextDouble() * (config.maxRadius - config.minRadius));
                double z = user.getZ() + Mth.sin(degree) * (config.minRadius + rand.nextDouble() * (config.maxRadius - config.minRadius));
                Optional<Integer> y = Optional.empty();
                for (int dY = -config.maxY; dY <= config.maxY; dY++) {
                    BlockPos test = new BlockPos(x, user.getY() + dY, z);
                    if (user.level.getBlockState(test).isCollisionShapeFullBlock(user.level, test) && user.level.getBlockState(test.above()).getCollisionShape(user.level, test.above()).isEmpty() && (y.isEmpty() || Math.abs(dY) < Math.abs(user.getY() - y.get()))) {
                        y = Optional.of(test.getY());
                    }
                }
                if (y.isPresent()) {
                    doEffect(user, inst, new Vec3(x, y.get() + 0.5, z));
                    break;
                }
            }
        }
        super.tick(user, inst);
    }

    protected abstract void doEffect(LivingEntity user, ShinsuTechniqueInstance<C, S> inst, Vec3 pos);

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(inst.group(
                        Codec.DOUBLE.fieldOf("min_radius").forGetter(config -> config.minRadius),
                        Codec.DOUBLE.fieldOf("max_radius").forGetter(config -> config.maxRadius),
                        Codec.INT.fieldOf("max_y").forGetter(config -> config.maxY),
                        Codec.INT.fieldOf("period").forGetter(config -> config.period)
                )).apply(inst, Config::new));
        public final double minRadius, maxRadius;
        public final int maxY, period;

        public Config(Display display, Optional<Integer> duration, int cooldown, double minRadius, double maxRadius, int maxY, int period) {
            super(display, duration, cooldown);
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
            this.maxY = maxY;
            this.period = period;
        }

    }

}
