package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuSpearEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ThrowSpear extends ShinsuTechniqueType<ShinsuTechniqueConfig, ThrowSpear.Data> {


    public ThrowSpear() {
        super(ShinsuTechniqueConfig.CODEC, Data.CODEC);
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<ShinsuTechniqueConfig, Data> inst) {
        UUID spear = inst.getData().spear;
        if (user.level instanceof ServerLevel && ((ServerLevel) user.level).getEntity(spear) == null) {
            inst.remove(user);
        }
        super.tick(user, inst);
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, ShinsuTechniqueConfig config, @Nullable LivingEntity target) {
        ShinsuSpearEntity proj = EntityRegistry.SHINSU_SPEAR.get().create(user.level);
        if (proj != null) {
            proj.setOwner(user);
            float speed = 2.5f;
            ShinsuAttribute attribute = ShinsuQualityData.get(user).getAttribute();
            if (attribute != null) {
                speed *= (float) attribute.getSpeed();
            }
            Vec3 direction = user.getLookAngle();
            proj.shoot(direction.x(), direction.y(), direction.z(), speed, 1);
            proj.setPos(user.getX(), user.getEyeY(), user.getZ());
            proj.setAttribute(attribute);
            UUID id = Mth.createInsecureUUID();
            proj.setTechnique(id);
            user.level.addFreshEntity(proj);
            user.level.playSound(null, proj, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1, 1);
            return new Data(id, proj.getUUID());
        }
        return null;
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<ShinsuBlast.Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(
                        Codec.FLOAT.fieldOf("speed").forGetter(config -> config.speed)
                ).apply(inst, ShinsuBlast.Config::new));
        public final float speed;

        public Config(Display display, Optional<Integer> duration, int cooldown, float speed) {
            super(display, duration, cooldown);
            this.speed = speed;
        }

    }

    public static class Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id),
                Util.UUID_CODEC.fieldOf("spear").forGetter(data -> data.spear)
        ).apply(inst, Data::new));
        public final UUID id, spear;

        public Data(UUID id, UUID spear) {
            this.id = id;
            this.spear = spear;
        }

    }

}
