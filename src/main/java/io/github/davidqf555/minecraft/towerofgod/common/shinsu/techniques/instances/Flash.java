package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.math.Vector3f;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueInstanceData;
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

public class Flash extends RayTraceTechnique<RayTraceTechnique.Config, ShinsuTechniqueInstanceData> {

    private final IRequirement[] requirements = new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING)};

    public Flash() {
        super(Config.CODEC, ShinsuTechniqueInstanceData.CODEC);
    }

    @Override
    public IRequirement[] getRequirements() {
        return requirements;
    }

    @Nullable
    @Override
    protected ShinsuTechniqueInstanceData doEffect(LivingEntity user, RayTraceTechnique.Config config, @Nullable LivingEntity target, HitResult result) {
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(user.level);
        if (lightning != null) {
            Vec3 start = new Vec3(user.getX(), user.getEyeY(), user.getZ());
            Vec3 end = result.getLocation();
            if (user instanceof ServerPlayer) {
                lightning.setCause((ServerPlayer) user);
            }
            lightning.setVisualOnly(true);
            lightning.setPos(end.x(), end.y(), end.z());
            lightning.setStart(new Vector3f(start));
            user.level.addFreshEntity(lightning);
            user.teleportTo(end.x(), end.y(), end.z());
            return new ShinsuTechniqueInstanceData(Mth.createInsecureUUID());
        }
        return null;
    }

}
