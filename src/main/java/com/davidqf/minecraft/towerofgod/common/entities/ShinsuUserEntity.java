package com.davidqf.minecraft.towerofgod.common.entities;

import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuTechniques;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public abstract class ShinsuUserEntity extends CreatureEntity {

    public ShinsuUserEntity(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new CastShinsuGoal());
    }

    @Override
    public void livingTick() {
        IShinsuStats stats = IShinsuStats.get(this);
        List<ShinsuTechnique> techniques = stats.getTechniques();
        for (int i = techniques.size() - 1; i >= 0; i--) {
            ShinsuTechnique attack = techniques.get(i);
            attack.tick(world);
            if (attack.ticksLeft() <= 0) {
                attack.onEnd(world);
                techniques.remove(i);
            }
        }
        Map<ShinsuTechniques, Integer> cooldowns = stats.getCooldowns();
        List<ShinsuTechniques> keys = new ArrayList<>(cooldowns.keySet());
        for (ShinsuTechniques key : keys) {
            cooldowns.put(key, Math.max(0, cooldowns.get(key) - 1));
        }
        super.livingTick();
    }

    public class CastShinsuGoal extends Goal {

        private final IShinsuStats stats;
        private ShinsuTechniques technique;

        private CastShinsuGoal(){
            stats = IShinsuStats.get(ShinsuUserEntity.this);
            technique = null;
        }

        @Override
        public boolean shouldExecute() {
            List<ShinsuTechniques> tech = new ArrayList<>();
            for(ShinsuTechniques technique : stats.getKnownTechniques().keySet()){
                ShinsuTechniques.Builder<? extends ShinsuTechnique> builder = technique.getBuilder();
                LivingEntity target = getAttackTarget();
                Vector3d dir = (target != null && canEntityBeSeen(target)) ? target.getEyePosition(1).subtract(getEyePosition(1)).normalize() : getLookVec();
                if((!stats.getCooldowns().containsKey(technique) || stats.getCooldowns().get(technique) <= 0) && builder.canCast(technique, ShinsuUserEntity.this, stats.getTechniqueLevel(technique), target, dir) && !isUsed(technique, target, dir)) {
                    tech.add(technique);
                }
            }
            if(tech.isEmpty()){
                return false;
            }
            int index = (int) (Math.random() * tech.size());
            technique = tech.get(index);
            return true;
        }

        @Override
        public void startExecuting() {
            LivingEntity target = getAttackTarget();
            Vector3d dir = (target != null && canEntityBeSeen(target)) ? target.getEyePosition(1).subtract(getEyePosition(1)).normalize() : getLookVec();
            stats.cast(ShinsuUserEntity.this, technique, target, dir);
        }

        @Override
        public void resetTask() {
            technique = null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return false;
        }

        private boolean isUsed(ShinsuTechniques technique, @Nullable Entity target, @Nullable Vector3d dir){
            for(ShinsuTechnique tech : stats.getTechniques()){
                if(tech.getTechnique() == technique) {
                    if (tech instanceof ShinsuTechnique.Targetable) {
                        return target != null && ((ShinsuTechnique.Targetable) tech).getTargetUUID().equals(target.getUniqueID());
                    } else if (tech instanceof ShinsuTechnique.Direction) {
                        return ((ShinsuTechnique.Direction) tech).getDirection().equals(dir);
                    }
                    return true;
                }
            }
            return false;
        }
    }
}