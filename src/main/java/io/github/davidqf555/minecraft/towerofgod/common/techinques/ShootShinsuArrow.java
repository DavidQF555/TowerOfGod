package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ShinsuArrowEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class ShootShinsuArrow extends ShinsuTechniqueInstance.Direction {

    private UUID arrow;

    public ShootShinsuArrow(LivingEntity user, int level, Vector3d dir) {
        super(user, level, dir);
        arrow = null;
    }

    public static int getLevelForVelocity(float velocity, ShinsuQuality quality) {
        return (int) (MathHelper.sqrt(400 + 400 * velocity / quality.getSpeed()) - 19.5);
    }

    @Override
    public int getInitialDuration() {
        return 200;
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechnique.SHOOT_SHINSU_ARROW;
    }

    @Override
    public void onUse(ServerWorld world) {
        Entity user = getUser(world);
        if (user != null) {
            ShinsuArrowEntity arrow = RegistryHandler.SHINSU_ARROW_ENTITY.get().create(world);
            if (arrow != null) {
                ShinsuQuality quality = ShinsuStats.get(user).getQuality();
                arrow.setQuality(quality);
                arrow.setTechnique(getID());
                float speed = BowItem.getArrowVelocity(getLevel()) * 3 * (float) quality.getSpeed();
                Vector3d dir = getDirection();
                arrow.shoot(dir.x, dir.y, dir.z, speed, 1);
                arrow.setShooter(user);
                arrow.setPosition(user.getPosX(), user.getPosYEye() - 0.1, user.getPosZ());
                this.arrow = arrow.getUniqueID();
                world.addEntity(arrow);
            }
        }
        super.onUse(world);
    }

    @Override
    public int getShinsuUse() {
        return 3;
    }

    @Override
    public int getBaangsUse() {
        return 1;
    }

    @Override
    public void tick(ServerWorld world) {
        if (arrow == null || world.getEntityByUuid(arrow) == null) {
            remove(world);
        }
        super.tick(world);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (arrow != null) {
            nbt.putUniqueId("Arrow", arrow);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Arrow", Constants.NBT.TAG_INT_ARRAY)) {
            arrow = nbt.getUniqueId("Arrow");
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public static class Builder implements ShinsuTechnique.IBuilder<ShootShinsuArrow> {

        @Override
        public ShootShinsuArrow build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            return new ShootShinsuArrow(user, level, dir);
        }

        @Override
        public ShootShinsuArrow emptyBuild() {
            return new ShootShinsuArrow(null, 0, Vector3d.ZERO);
        }

        @Override
        public ShinsuTechnique getTechnique() {
            return ShinsuTechnique.SHOOT_SHINSU_ARROW;
        }
    }
}
