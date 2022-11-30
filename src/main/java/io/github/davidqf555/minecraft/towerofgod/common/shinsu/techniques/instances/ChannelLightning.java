package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
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

public class ChannelLightning extends ShinsuTechniqueInstance {

    private static final double RANGE = 64;
    private Vector3d direction;

    public ChannelLightning(Entity user, Vector3d direction) {
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
            Vector3d start = new Vector3d(user.getX(), user.getEyeY(), user.getZ());
            Vector3d end = start.add(direction.multiply(RANGE, RANGE, RANGE));
            EntityRayTraceResult entity = ProjectileHelper.getEntityHitResult(world, lightning, start, end, AxisAlignedBB.ofSize(RANGE * 2, RANGE * 2, RANGE * 2).move(start), null);
            Vector3d pos;
            if (entity != null) {
                pos = entity.getLocation();
            } else {
                pos = world.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, lightning)).getLocation();
            }
            if (user instanceof ServerPlayerEntity) {
                lightning.setCause((ServerPlayerEntity) user);
            }
            lightning.setDamage((float) (ShinsuStats.get(user).getTension() * 2));
            lightning.setPos(pos.x(), pos.y(), pos.z());
            lightning.setStart(new Vector3f(start));
            world.addFreshEntity(lightning);
        }
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.CHANNEL_LIGHTNING.get();
    }

    @Override
    public int getShinsuUse() {
        return 25;
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
        nbt.putDouble("X", direction.x());
        nbt.putDouble("Y", direction.y());
        nbt.putDouble("Z", direction.z());
        return nbt;
    }

    public static class Factory implements ShinsuTechnique.IFactory<ChannelLightning> {

        @Override
        public Either<ChannelLightning, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.left(new ChannelLightning(user, dir));
        }

        @Override
        public ChannelLightning blankCreate() {
            return new ChannelLightning(null, Vector3d.ZERO);
        }

    }
}
