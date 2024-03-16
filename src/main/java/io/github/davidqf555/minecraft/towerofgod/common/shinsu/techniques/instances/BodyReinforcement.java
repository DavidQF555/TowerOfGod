package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BodyReinforcement extends ShinsuTechniqueType<BodyReinforcement.Config, ShinsuTechniqueInstanceData> {

    public BodyReinforcement() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Nullable
    @Override
    public ShinsuTechniqueInstanceData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<Config, ShinsuTechniqueInstanceData> inst) {
        user.addEffect(new MobEffectInstance(EffectRegistry.BODY_REINFORCEMENT.get(), 2, inst.getConfigured().getConfig().amp, false, true, true));
        super.tick(user, inst);
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("amp").forGetter(config -> config.amp)
                ).apply(inst, Config::new));
        public final int amp;

        public Config(Display display, Optional<Integer> duration, int cooldown, int amp) {
            super(display, duration, cooldown);
            this.amp = amp;
        }

    }

}
