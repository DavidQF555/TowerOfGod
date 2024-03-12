package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlackFish extends ShinsuTechniqueType<BlackFish.Config, ShinsuTechniqueInstanceData> {

    private final MobEffectInstance effect = new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, true, true, true);

    public BlackFish() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Nullable
    @Override
    public ShinsuTechniqueInstanceData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<Config, ShinsuTechniqueInstanceData> inst) {
        if (user.level.getLightEmission(user.blockPosition()) <= inst.getConfigured().getConfig().light) {
            user.addEffect(effect);
        }
        super.tick(user, inst);
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("light").forGetter(config -> config.light)
                ).apply(inst, Config::new));
        public final int light;

        public Config(Display display, Optional<Integer> duration, int cooldown, int light) {
            super(display, duration, cooldown);
            this.light = light;
        }

    }

}
