package com.davidqf.minecraft.towerofgod.common.techinques;

import net.minecraft.block.Blocks;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;

public enum ShinsuQuality {

    NONE(ParticleTypes.DRIPPING_WATER, 1, 0xAA24a6d1),
    LIGHTNING(ParticleTypes.INSTANT_EFFECT, 1.5, 0xFFfbff85),
    FIRE(ParticleTypes.FLAME, 1, 0xFFff8119),
    ICE(ParticleTypes.POOF, 1, 0xFFa8fbff),
    STONE(new BlockParticleData(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()), 0.8, 0xFF999999),
    WIND(ParticleTypes.AMBIENT_ENTITY_EFFECT, 1.4, 0xAAabffac),
    CRYSTAL(new BlockParticleData(ParticleTypes.BLOCK, Blocks.GLASS.getDefaultState()), 0.9, 0xFFf7f7f7),
    PLANT(ParticleTypes.COMPOSTER, 1, 0xFF03ff2d);

    private final IParticleData particleType;
    private final double speed;
    private final int color;

    ShinsuQuality(IParticleData particleType, double speed, int color) {
        this.particleType = particleType;
        this.speed = speed;
        this.color = color;
    }

    public static ShinsuQuality get(String name) {
        for (ShinsuQuality quality : values()) {
            if (quality.name().equals(name)) {
                return quality;
            }
        }
        return null;
    }

    public IParticleData getParticleType() {
        return particleType;
    }

    public double getSpeed() {
        return speed;
    }

    public int getColor() {
        return color;
    }

}
