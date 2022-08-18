package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.QualityRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.TypeLevelRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class Flash extends ShinsuTechniqueInstance {

    private static final double RANGE = 64;
    private Vector3d direction;

    public Flash(LivingEntity user, Vector3d direction) {
        super(user);
        this.direction = direction;
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        DirectionalLightningBoltEntity lightning = EntityRegistry.DIRECTIONAL_LIGHTNING.get().create(world);
        if (lightning != null) {
            Vector3d start = new Vector3d(user.getPosX(), user.getPosYEye(), user.getPosZ());
            Vector3d end = start.add(direction.mul(RANGE, RANGE, RANGE));
            EntityRayTraceResult entity = ProjectileHelper.rayTraceEntities(world, lightning, start, end, AxisAlignedBB.withSizeAtOrigin(RANGE * 2, RANGE * 2, RANGE * 2).offset(start), null);
            Vector3d pos;
            if (entity != null) {
                pos = entity.getHitVec();
            } else {
                pos = world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, lightning)).getHitVec();
            }
            if (user instanceof ServerPlayerEntity) {
                lightning.setCaster((ServerPlayerEntity) user);
            }
            lightning.setEffectOnly(true);
            lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
            lightning.setStart(new Vector3f(start));
            world.addEntity(lightning);
            user.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.FLASH.get();
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("X", Constants.NBT.TAG_DOUBLE) && nbt.contains("Y", Constants.NBT.TAG_DOUBLE) && nbt.contains("Z", Constants.NBT.TAG_DOUBLE)) {
            direction = new Vector3d(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("X", direction.getX());
        nbt.putDouble("Y", direction.getY());
        nbt.putDouble("Z", direction.getZ());
        return nbt;
    }

    public static class Factory implements ShinsuTechnique.IFactory<Flash> {

        @Override
        public Either<Flash, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new Flash(user, dir));
        }

        @Override
        public Flash blankCreate() {
            return new Flash(null, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechniqueRegistry.FLASH.get();
        }

        @Override
        public IRequirement[] getRequirements() {
            return new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 10), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 10), new QualityRequirement(ShinsuQualityRegistry.LIGHTNING.get())};
        }
    }
}
