package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class Flash extends ShinsuTechniqueInstance.Direction {

    private static final double RANGE = 64;

    public Flash(LivingEntity user, int level, Vector3d dir) {
        super(user, level, dir);
    }

    @Override
    public int getCooldown() {
        return 200;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        DirectionalLightningBoltEntity lightning = RegistryHandler.LIGHTNING_PROJECTILE_ENTITY.get().create(world);
        if (lightning != null) {
            Vector3d start = new Vector3d(user.getPosX(), user.getPosYEye(), user.getPosZ());
            Vector3d end = start.add(getDirection().mul(RANGE, RANGE, RANGE));
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
        return ShinsuTechnique.FLASH;
    }

    @Override
    public int getShinsuUse() {
        return 15;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    public static class Factory implements ShinsuTechnique.IFactory<Flash> {

        @Override
        public Either<Flash, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return Either.left(new Flash(user, level, dir));
        }

        @Override
        public Flash emptyBuild() {
            return new Flash(null, 1, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.FLASH;
        }
    }
}
