package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.AttributeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Boost extends ShinsuTechniqueType<Boost.Config, NoData> {

    private final IRequirement[] requirements = new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.FIRE)};

    public Boost() {
        super(Config.CODEC, NoData.CODEC);
    }

    @Nullable
    @Override
    public NoData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        if (user.level instanceof ServerLevel) {
            float width = user.getBbWidth() / 2;
            ((ServerLevel) user.level).sendParticles(ParticleTypes.FLAME, user.getX(), user.getY(), user.getZ(), config.particles, width, user.getBbHeight() * 0.25, width, 0);
            Vec3 dir = user.getLookAngle().scale(ShinsuStats.get(user).getTension() * 2);
            user.push(dir.x(), dir.y(), dir.z());
            user.hurtMarked = true;
            return NoData.INSTANCE;
        }
        return null;
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("particles").forGetter(config -> config.particles)
                ).apply(inst, Config::new));
        public final int particles;

        public Config(Display display, Optional<Integer> duration, int cooldown, int particles) {
            super(display, duration, cooldown);
            this.particles = particles;
        }

    }

}
