package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class Flamethrower extends ShinsuTechniqueType<Flamethrower.Config, NoData> {

    public Flamethrower() {
        super(Config.CODEC, NoData.CODEC);
    }

    @Nullable
    @Override
    public NoData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return NoData.INSTANCE;
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<Config, NoData> inst) {
        Random rand = user.getRandom();
        Vec3 center = user.getLookAngle();
        int count = (int) (ShinsuStats.get(user).getTension() * 2) + 1;
        for (int i = 0; i < count; i++) {
            SmallFireball fire = EntityType.SMALL_FIREBALL.create(user.level);
            if (fire != null) {
                Flamethrower.Config config = inst.getConfigured().getConfig();
                float angle = config.spread * i / count - config.spread / 2;
                Vec3 dir = center.yRot(angle * (float) Math.PI / 180).add(rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2 - 0.1, rand.nextDouble() * 0.2 - 0.1);
                fire.setPos(user.getX(), user.getEyeY(), user.getZ());
                fire.setDeltaMovement(dir.scale(config.magnitude));
                fire.yPower = -0.05;
                fire.setOwner(user);
                user.level.addFreshEntity(fire);
            }
        }
        super.tick(user, inst);
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    public static class Config extends ShinsuTechniqueConfig {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(inst ->
                commonCodec(inst).and(inst.group(
                                Codec.FLOAT.fieldOf("spread").forGetter(config -> config.spread),
                                Codec.DOUBLE.fieldOf("magnitude").forGetter(config -> config.magnitude)
                        )
                ).apply(inst, Config::new));
        public final float spread;
        public final double magnitude;

        public Config(Display display, Optional<Integer> duration, int cooldown, float spread, double magnitude) {
            super(display, duration, cooldown);
            this.spread = spread;
            this.magnitude = magnitude;
        }

    }

}
