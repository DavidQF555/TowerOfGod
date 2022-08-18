package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.DirectionalLightningBoltEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
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

public class ChannelLightning extends ShinsuTechniqueInstance {

    private static final double RANGE = 64;
    private Vector3d direction;
    private float damage;

    public ChannelLightning(LivingEntity user, Vector3d direction, float damage) {
        super(user);
        this.direction = direction;
        this.damage = damage;
    }

    @Override
    public int getCooldown() {
        return 160;
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
            lightning.setDamage(damage);
            lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
            lightning.setStart(new Vector3f(start));
            world.addEntity(lightning);
        }
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.CHANNEL_LIGHTNING.get();
    }

    @Override
    public int getShinsuUse() {
        return 10;
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
        if (nbt.contains("Damage", Constants.NBT.TAG_FLOAT)) {
            damage = nbt.getFloat("Damage");
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putDouble("X", direction.getX());
        nbt.putDouble("Y", direction.getY());
        nbt.putDouble("Z", direction.getZ());
        nbt.putFloat("Damage", damage);
        return nbt;
    }

    public static class Factory implements ShinsuTechnique.IFactory<ChannelLightning> {

        @Override
        public Either<ChannelLightning, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            int level = ShinsuStats.get(user).getData(ShinsuTechniqueType.CONTROL).getLevel();
            return Either.left(new ChannelLightning(user, dir, level - 4));
        }

        @Override
        public ChannelLightning blankCreate() {
            return new ChannelLightning(null, Vector3d.ZERO, 0);
        }

    }
}
