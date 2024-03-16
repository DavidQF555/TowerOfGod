package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
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

public class ShinsuBlast extends ShinsuTechniqueType<ShinsuBlast.Config, ShinsuBlast.Data> {

    private static final IRequirement[] EMPTY = new IRequirement[0];

    public ShinsuBlast() {
        super(Config.CODEC, Data.CODEC);
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<Config, Data> inst) {
        if (user.level instanceof ServerLevel && ((ServerLevel) user.level).getEntity(inst.getData().blast) == null) {
            inst.remove(user);
        }
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        ShinsuEntity shinsu = EntityRegistry.SHINSU.get().create(user.level);
            if (shinsu != null) {
                UUID id = Mth.createInsecureUUID();
                Vec3 dir = user.getLookAngle();
                shinsu.setOwner(user);
                shinsu.setTechnique(id);
                shinsu.setPos(user.getX(), user.getEyeY() - shinsu.getBoundingBox().getYsize() / 2, user.getZ());
                shinsu.shoot(dir.x(), dir.y(), dir.z(), config.speed, 0);
                user.level.addFreshEntity(shinsu);
                return new Data(id, shinsu.getUUID());
            }
        return null;
    }

    @Override
    public IRequirement[] getRequirements() {
        return EMPTY;
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

    public static class Data extends ShinsuTechniqueInstanceData {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id),
                Util.UUID_CODEC.fieldOf("blast").forGetter(data -> data.blast)
        ).apply(inst, Data::new));
        public UUID blast;

        public Data(UUID id, UUID blast) {
            super(id);
            this.blast = blast;
        }

    }

}
