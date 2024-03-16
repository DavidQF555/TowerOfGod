package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Thunderstorm extends AreaTechnique<AreaTechnique.Config, ShinsuTechniqueInstanceData> {

    public Thunderstorm() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Nullable
    @Override
    public ShinsuTechniqueInstanceData onUse(LivingEntity user, Config config, @Nullable LivingEntity target) {
        return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    @Override
    protected void doEffect(LivingEntity user, ShinsuTechniqueInstance<Config, ShinsuTechniqueInstanceData> inst, Vec3 pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(user.level);
        if (lightning != null) {
            lightning.setPos(pos.x(), pos.y() + 0.5, pos.z());
            user.level.addFreshEntity(lightning);
        }
    }

}
