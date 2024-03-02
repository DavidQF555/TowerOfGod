package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class FlareWaveExplosion extends ShinsuTechniqueType<FlareWaveExplosion.Config, NoData> {

    public FlareWaveExplosion() {
        super(Config.CODEC, NoData.CODEC);
    }

    @Nullable
    @Override
    public NoData onUse(LivingEntity user, FlareWaveExplosion.Config config, @Nullable LivingEntity target) {
        if (target != null && user.distanceToSqr(target) <= config.range * config.range) {
            target.hurt(DamageSource.MAGIC, config.damage);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, config.slowDuration, config.slowMag, false, false, false));
        }
        return null;
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(inst.group(
                        Codec.FLOAT.fieldOf("damage").forGetter(config -> config.damage),
                        Codec.DOUBLE.fieldOf("range").forGetter(config -> config.range),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("slow_duration").forGetter(config -> config.slowDuration),
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("slow_magnitude").forGetter(config -> config.slowMag)
                )).apply(inst, Config::new));
        public final float damage;
        public final double range;
        public final int slowDuration, slowMag;

        public Config(Display display, Optional<Integer> duration, int cooldown, float damage, double range, int slowDuration, int slowMag) {
            super(display, duration, cooldown);
            this.damage = damage;
            this.range = range;
            this.slowDuration = slowDuration;
            this.slowMag = slowMag;
        }

    }

}
