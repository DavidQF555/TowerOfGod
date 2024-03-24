package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class RayTraceTechnique<C extends RayTraceTechnique.Config, S extends ShinsuTechniqueInstanceData> extends ShinsuTechniqueType<C, S> {

    protected RayTraceTechnique(Codec<C> config, Codec<S> data) {
        super(config, data);
    }

    @Nullable
    @Override
    public S onUse(LivingEntity user, C config, @Nullable LivingEntity target) {
        Vec3 start = new Vec3(user.getX(), user.getEyeY(), user.getZ());
        Vec3 end = start.add(user.getLookAngle().scale(config.range));
        if (config.entityCollision) {
            EntityHitResult entity = ProjectileUtil.getEntityHitResult(user.level, null, start, end, AABB.ofSize(start, config.range * 2, config.range * 2, config.range * 2), e -> true);
            if (entity != null) {
                S effect = doEffect(user, config, target, entity);
                S data = doEntityEffect(user, config, target, entity);
                return data == null ? effect : data;
            }
        }
        return doEffect(user, config, target, user.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)));
    }

    @Nullable
    protected abstract S doEffect(LivingEntity user, C config, @Nullable LivingEntity target, HitResult result);

    @Nullable
    protected S doEntityEffect(LivingEntity user, C config, @Nullable LivingEntity target, EntityHitResult result) {
        return null;
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst -> rayTraceCommonCodec(inst).apply(inst, Config::new));
        public final boolean entityCollision;
        public final double range;

        public Config(Display display, MobUseCondition condition, Optional<Integer> duration, int cooldown, boolean entityCollision, double range) {
            super(display, condition, duration, cooldown);
            this.entityCollision = entityCollision;
            this.range = range;
        }

        protected static <T extends Config> Products.P6<RecordCodecBuilder.Mu<T>, Display, MobUseCondition, Optional<Integer>, Integer, Boolean, Double> rayTraceCommonCodec(RecordCodecBuilder.Instance<T> inst) {
            return commonCodec(inst).and(inst.group(
                    Codec.BOOL.optionalFieldOf("entity_collision", true).forGetter(config -> config.entityCollision),
                    Codec.DOUBLE.optionalFieldOf("range", 64.0).forGetter(config -> config.range)
            ));
        }

    }

}
