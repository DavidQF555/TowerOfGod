package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuEntity;
import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.entities.shinsu.ShinsuUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ShinsuBlast extends ShinsuTechnique.Position {

    private static final double BASE_SPEED = 0.5;
    private static final double IDEAL_DISTANCE = 3;

    public ShinsuBlast(LivingEntity user, int level, @Nonnull Vector3d pos) {
        super(ShinsuTechniques.SHINSU_BLAST, user, level, pos);
    }

    @Override
    public boolean isIdeal(World world) {
        Entity u = getUser(world);
        if (u != null) {
            return u.getDistanceSq(getPosition()) >= IDEAL_DISTANCE * IDEAL_DISTANCE;
        }
        return false;
    }

    @Override
    public void onUse(World world) {
        Entity u = getUser(world);
        if (u instanceof ShinsuUser) {
            ShinsuUser user = (ShinsuUser) u;
            ShinsuQuality quality = user.getQuality();
            double speed = quality.getSpeed();
            speed *= BASE_SPEED * getLevel() / 2.0;
            ShinsuEntity shinsuEntity = new ShinsuEntity(world, user, quality, getLevel());
            shinsuEntity.setPosition(user.getPosX(), user.getPosYEye(), user.getPosZ());
            user.getEntityWorld().addEntity(shinsuEntity);
            Vector3d vec = getPosition().subtract(user.getPositionVec()).subtract(0, user.getEyeHeight(), 0).normalize().mul(speed, speed, speed);
            shinsuEntity.setMotion(vec);
        }
    }
}
