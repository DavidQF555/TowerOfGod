package io.github.davidqf555.minecraft.towerofgod.common.entities;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueInstance;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
        if (world instanceof ServerWorld) {
            IShinsuStats stats = IShinsuStats.get(this);
            stats.tick((ServerWorld) world);
        }
        super.livingTick();
    }

    public class CastShinsuGoal extends Goal {

        private final IShinsuStats stats;
        private ShinsuTechnique technique;

        private CastShinsuGoal() {
            stats = IShinsuStats.get(ShinsuUserEntity.this);
            technique = null;
        }

        @Override
        public boolean shouldExecute() {
            List<ShinsuTechnique> tech = new ArrayList<>();
            for (ShinsuTechnique technique : ShinsuTechnique.values()) {
                ShinsuTechnique.Builder<? extends ShinsuTechniqueInstance> builder = technique.getBuilder();
                LivingEntity target = getAttackTarget();
                Vector3d dir = (target != null && canEntityBeSeen(target)) ? target.getEyePosition(1).subtract(getEyePosition(1)).normalize() : getLookVec();
                if (stats.getCooldown(technique) <= 0 && builder.canCast(technique, ShinsuUserEntity.this, stats.getTechniqueLevel(technique), target, dir) && !isUsed(technique, target, dir)) {
                    tech.add(technique);
                }
            }
            if (tech.isEmpty()) {
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

        private boolean isUsed(ShinsuTechnique technique, @Nullable Entity target, @Nullable Vector3d dir) {
            for (ShinsuTechniqueInstance tech : stats.getTechniques()) {
                if (tech.getTechnique() == technique) {
                    if (tech instanceof ShinsuTechniqueInstance.Targetable) {
                        return target != null && ((ShinsuTechniqueInstance.Targetable) tech).getTargetUUID().equals(target.getUniqueID());
                    } else if (tech instanceof ShinsuTechniqueInstance.Direction) {
                        return ((ShinsuTechniqueInstance.Direction) tech).getDirection().equals(dir);
                    }
                    return true;
                }
            }
            return false;
        }
    }
}