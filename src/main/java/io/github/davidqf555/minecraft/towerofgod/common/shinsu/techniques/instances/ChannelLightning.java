package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.AttributeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChannelLightning extends RayTraceTechnique<ChannelLightning.Config, ShinsuTechniqueInstanceData> {

    private final IRequirement[] requirements = new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING)};

    public ChannelLightning() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Nullable
    @Override
    protected ShinsuTechniqueInstanceData doEffect(LivingEntity user, Config config, @Nullable LivingEntity target, HitResult result) {
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(user.level);
        if (lightning != null) {
            if (user instanceof ServerPlayer) {
                lightning.setCause((ServerPlayer) user);
            }
            lightning.setDamage((float) (ShinsuStats.get(user).getTension() * 2));
            Vec3 pos = result.getLocation();
            lightning.setPos(pos.x(), pos.y(), pos.z());
            lightning.setStart(new Vector3f(user.getEyePosition(1)));
            user.level.addFreshEntity(lightning);
            return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
        }
        return null;
    }

    public static class Config extends RayTraceTechnique.Config {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                rayTraceCommonCodec(inst).and(
                        Codec.FLOAT.fieldOf("damage").forGetter(config -> config.damage)
                ).apply(inst, Config::new));
        public final float damage;

        public Config(Display display, MobUseCondition condition, Optional<Integer> duration, int cooldown, boolean entityCollision, double range, float damage) {
            super(display, condition, duration, cooldown, entityCollision, range);
            this.damage = damage;
        }

    }

}
