package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FireExplosion extends GroundTechniqueInstance<GroundTechniqueInstance.Config, GroundTechniqueInstance.Data> {

    private final IRequirement[] requirements = new IRequirement[0];

    public FireExplosion() {
        super(Config.CODEC, Data.CODEC);
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return new Data(Mth.createInsecureUUID(), user.getX(), user.getZ(), user.getLookAngle().x(), user.getLookAngle().z(), user.getBlockY());
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Override
    public void doEffect(LivingEntity user, ShinsuTechniqueInstance<Config, Data> inst, Vec3 pos) {
        if (user.level instanceof ServerLevel) {
            user.level.explode(user, pos.x(), pos.y(), pos.z(), 2, true, Explosion.BlockInteraction.NONE);
            ((ServerLevel) user.level).sendParticles(ParticleTypes.FLAME, pos.x(), pos.y(), pos.z(), 100, 2, 2, 2, 0.2);
        }
    }

}
