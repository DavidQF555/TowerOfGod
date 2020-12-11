package com.davidqf.minecraft.towerofgod.common.techinques;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuIcons;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum ShinsuTechniques {

    BODY_REINFORCEMENT(new BodyReinforcement.Builder(10, 1), "body_reinforcement", ShinsuIcons.SWIRL),
    BLACK_FISH(new BlackFish.Builder(10, 1), "black_fish", ShinsuIcons.SWIRL),
    FLARE_WAVE_EXPLOSION(new FlareWaveExplosion.Builder(20, 1), "flare_wave_explosion", ShinsuIcons.SWIRL),
    REVERSE_FLOW_CONTROL(new ReverseFlowControl.Builder(10, 1), "reverse_flow_control", ShinsuIcons.REVERSE),
    SHINSU_BLAST(new ShinsuBlast.Builder(5, 1), "shinsu_blast", ShinsuIcons.SWIRL);

    private final Builder<? extends ShinsuTechnique> builder;
    private final TranslationTextComponent name;
    private final RenderInfo icon;

    ShinsuTechniques(Builder<? extends ShinsuTechnique> builder, String name, RenderInfo icon) {
        this.builder = builder;
        this.name = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + "." + name);
        this.icon = icon;
    }

    @Nullable
    public static ShinsuTechniques get(String name) {
        for (ShinsuTechniques tech : values()) {
            if (name.equals(tech.getName().getKey())) {
                return tech;
            }
        }
        return null;
    }

    @Nullable
    public static ShinsuTechniques get(ShinsuTechnique technique) {
        for (ShinsuTechniques tech : values()) {
            if (tech == technique.getTechnique()) {
                return tech;
            }
        }
        return null;
    }

    public Builder<? extends ShinsuTechnique> getBuilder() {
        return builder;
    }

    public TranslationTextComponent getName(){
        return name;
    }

    public int getShinsuUse() {
        return builder.getShinsuUse();
    }

    public int getBaangUse() {
        return builder.getBaangUse();
    }

    public RenderInfo getIcon(){
        return icon;
    }

    public interface Builder<T extends ShinsuTechnique> {

        @Nullable
        T build(LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir);

        T emptyBuild();

        int getShinsuUse();

        int getBaangUse();

        default boolean canCast(ShinsuTechniques technique, LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            IShinsuStats stats = IShinsuStats.get(user);
            return stats.getShinsu() >= technique.getShinsuUse() && stats.getBaangs() >= technique.getBaangUse();
        }
    }

}
