package com.davidqf.minecraft.towerofgod.entities.shinsu.techinques;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public enum ShinsuTechniques {

    BODY_REINFORCEMENT(BodyReinforcement.class, 2, 5, 600, 0, 10, 1, false),
    FLARE_WAVE_EXPLOSION(FlareWaveExplosion.class, 10, 3, 0, 80, 20, 1, true),
    REVERSE_FLOW_CONTROL(ReverseFlowControl.class, 10, 3, 60, 80, 10, 1, false),
    SHINSU_BLAST(ShinsuBlast.class, 1, 5, 60, 80, 5, 1, true);

    private final Class<? extends ShinsuTechnique> clazz;
    private final int levelRequirement;
    private final int maxLevel;
    private final int duration;
    private final int cooldown;
    private final int shinsuUse;
    private final int baangUse;
    private final boolean canStack;

    ShinsuTechniques(Class<? extends ShinsuTechnique> clazz, int levelRequirement, int maxLevel, int duration, int cooldown, int shinsuUse, int baangUse, boolean canStack) {
        this.clazz = clazz;
        this.levelRequirement = levelRequirement;
        this.maxLevel = maxLevel;
        this.cooldown = cooldown;
        this.duration = duration;
        this.shinsuUse = shinsuUse;
        this.baangUse = baangUse;
        this.canStack = canStack;
    }

    public static ShinsuTechniques get(String name) {
        for (ShinsuTechniques tech : values()) {
            if (tech.name().equals(name)) {
                return tech;
            }
        }
        return null;
    }

    public static ShinsuTechniques get(ShinsuTechnique technique) {
        for (ShinsuTechniques tech : values()) {
            if (tech.getTechniqueClass().equals(technique.getClass())) {
                return tech;
            }
        }
        return null;
    }

    public ShinsuTechnique newEmptyInstance() {
        try {
            return clazz.getConstructor(LivingEntity.class, int.class).newInstance(null, 0);
        } catch (NoSuchMethodException noSuchMethodException) {
            try {
                return clazz.getConstructor(LivingEntity.class, int.class, LivingEntity.class).newInstance(null, 0, null);
            } catch (NoSuchMethodException ex) {
                try {
                    return clazz.getConstructor(LivingEntity.class, int.class, Vector3d.class).newInstance(null, 0, null);
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public Class<? extends ShinsuTechnique> getTechniqueClass() {
        return clazz;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getDuration() {
        return duration;
    }

    public int getShinsuUse() {
        return shinsuUse;
    }

    public int getBaangUse() {
        return baangUse;
    }

    public boolean canStack() {
        return canStack;
    }

}
