package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseFlowControlCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LighthouseFlowControl extends BasicCommandTechnique<LighthouseFlowControl.Config, BasicCommandTechnique.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public LighthouseFlowControl() {
        super(Config.CODEC, Data.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Override
    protected DeviceCommand createCommand(LivingEntity user, Config config, FlyingDevice entity, UUID id) {
        return new LighthouseFlowControlCommand(entity, id, config.range, config.getDuration().orElseThrow());
    }

    @Override
    protected Data createData(UUID id, List<UUID> devices) {
        return new Data(id, devices);
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

}
