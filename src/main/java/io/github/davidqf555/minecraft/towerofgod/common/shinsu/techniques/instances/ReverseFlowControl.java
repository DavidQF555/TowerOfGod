package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EffectRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ReverseFlowControl extends ShinsuTechniqueType<ReverseFlowControl.Config, ReverseFlowControl.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public ReverseFlowControl() {
        super(Config.CODEC, Data.CODEC);
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return target == null || user.distanceToSqr(target) > config.range * config.range ? null : new Data(target.getUUID());
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<Config, Data> inst) {
        if (user.level instanceof ServerLevel) {
            Entity target = ((ServerLevel) user.level).getEntity(inst.getData().id);
            if (!(target instanceof LivingEntity) || user.distanceToSqr(target) > inst.getConfigured().getConfig().range * inst.getConfigured().getConfig().range) {
                inst.remove(user);
                return;
            }
            double resistance = ShinsuStats.getNetResistance(user, target);
            ((LivingEntity) target).addEffect(new MobEffectInstance(EffectRegistry.REVERSE_FLOW.get(), 2, (int) (resistance * 2)));
        }
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        Codec.DOUBLE.fieldOf("range").forGetter(config -> config.range)
                ).apply(inst, Config::new));
        public final double range;

        public Config(Display display, Optional<Integer> duration, int cooldown, double range) {
            super(display, duration, cooldown);
            this.range = range;
        }
    }

    public static class Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id)
        ).apply(inst, Data::new));
        public final UUID id;

        public Data(UUID id) {
            this.id = id;
        }

    }
}
