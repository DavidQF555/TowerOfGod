package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ShootShinsuArrow extends ShinsuTechniqueType<ShootShinsuArrow.Config, ShootShinsuArrow.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public ShootShinsuArrow() {
        super(Config.CODEC, Data.CODEC);
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        ShinsuArrowEntity arrow = EntityRegistry.SHINSU_ARROW.get().create(user.level);
        if (arrow != null) {
            UUID id = Mth.createInsecureUUID();
            ShinsuAttribute attribute = ShinsuQualityData.get(user).getAttribute();
            arrow.setAttribute(attribute);
            arrow.setTechnique(id);
            Vec3 dir = user.getLookAngle();
            arrow.shoot(dir.x(), dir.y(), dir.z(), config.speed, 1);
            arrow.setOwner(user);
            arrow.setPos(user.getX(), user.getEyeY() - 0.1, user.getZ());
            user.level.addFreshEntity(arrow);
            return new Data(id, arrow.getUUID());
        }
        return null;
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<Config, Data> inst) {
        if (user.level instanceof ServerLevel && ((ServerLevel) user.level).getEntity(inst.getData().arrow) == null) {
            inst.remove(user);
        }
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    public static class Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id),
                Util.UUID_CODEC.fieldOf("arrow").forGetter(data -> data.arrow)
        ).apply(inst, Data::new));
        public final UUID id, arrow;

        public Data(UUID id, UUID arrow) {
            this.id = id;
            this.arrow = arrow;
        }

    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        Codec.FLOAT.fieldOf("speed").forGetter(config -> config.speed)
                ).apply(inst, Config::new));
        public final float speed;

        public Config(Display display, Optional<Integer> duration, int cooldown, float speed) {
            super(display, duration, cooldown);
            this.speed = speed;
        }

    }

}
