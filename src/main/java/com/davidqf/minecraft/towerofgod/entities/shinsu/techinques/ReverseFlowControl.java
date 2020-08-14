package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ReverseFlowControl extends ShinsuTechnique.Targetable {

    private static final double RANGE = 2;


    public ReverseFlowControl(LivingEntity user, int level, @Nonnull LivingEntity target) {
        super(ShinsuTechniques.REVERSE_FLOW_CONTROL, user, level, target);

    }

    @Override
    public boolean canUse(World world) {
        Entity u = getUser(world);
        Entity t = getTarget(world);
        if (u instanceof LivingEntity && t instanceof LivingEntity) {
            return super.canUse(world) && u.getDistanceSq(t) <= RANGE * RANGE;
        }
        return false;
    }

    @Override
    public void tick(World world) {
        Entity u = getUser(world);
        Entity t = getTarget(world);
        if (u instanceof LivingEntity && t instanceof LivingEntity) {
            LivingEntity user = (LivingEntity) u;
            LivingEntity target = (LivingEntity) t;
            double resistance = ShinsuTechnique.getTotalResistance(user, target);
            target.addPotionEffect(new EffectInstance(Effect.get(2), 2, (int) (5 * resistance) * getLevel() + 1, true, false, false));
        }
        super.tick(world);
    }
}
