package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.DeviceCommand;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FlyingDevice;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.FollowOwnerCommand;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FollowOwner extends BasicCommandTechnique<FollowOwner.Config, BasicCommandTechnique.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public FollowOwner() {
        super(Config.CODEC, Data.CODEC);
    }

    @Override
    protected DeviceCommand createCommand(LivingEntity user, Config config, FlyingDevice entity, UUID id) {
        return new FollowOwnerCommand(entity, id, 1);
    }

    @Override
    protected Data createData(UUID id, List<UUID> devices) {
        return new Data(id, devices);
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        Codec.FLOAT.fieldOf("speed").forGetter(config -> config.speed)
                ).apply(inst, Config::new));
        public final float speed;

        public Config(Display display, MobUseCondition condition, Optional<Integer> duration, int cooldown, float speed) {
            super(display, condition, duration, cooldown);
            this.speed = speed;
        }

    }

}
