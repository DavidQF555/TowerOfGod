package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Thunderstorm extends AreaTechnique<AreaTechnique.Config, NoData> {

    public Thunderstorm() {
        super(Config.CODEC, NoData.CODEC);
    }

    @Nullable
    @Override
    public NoData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return NoData.INSTANCE;
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    @Override
    protected void doEffect(LivingEntity user, ShinsuTechniqueInstance<Config, NoData> inst, Vec3 pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(user.level);
        if (lightning != null) {
            lightning.setPos(pos.x(), pos.y() + 0.5, pos.z());
            user.level.addFreshEntity(lightning);
        }
    }

}
