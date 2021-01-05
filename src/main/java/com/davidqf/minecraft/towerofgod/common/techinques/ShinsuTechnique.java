package com.davidqf.minecraft.towerofgod.common.techinques;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.client.gui.ShinsuIcons;
import com.davidqf.minecraft.towerofgod.client.render.RenderInfo;
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
public enum ShinsuTechnique {

    BODY_REINFORCEMENT(new BodyReinforcement.Builder(10, 1), "body_reinforcement", ShinsuIcons.SWIRL),
    BLACK_FISH(new BlackFish.Builder(10, 1), "black_fish", ShinsuIcons.SWIRL),
    FLARE_WAVE_EXPLOSION(new FlareWaveExplosion.Builder(20, 1), "flare_wave_explosion", ShinsuIcons.SWIRL),
    REVERSE_FLOW_CONTROL(new ReverseFlowControl.Builder(10, 1), "reverse_flow_control", ShinsuIcons.REVERSE),
    SHINSU_BLAST(new ShinsuBlast.Builder(5, 1), "shinsu_blast", ShinsuIcons.SWIRL);

    private final Builder<? extends ShinsuTechniqueInstance> builder;
    private final String name;
    private final TranslationTextComponent text;
    private final RenderInfo icon;

    ShinsuTechnique(Builder<? extends ShinsuTechniqueInstance> builder, String name, RenderInfo icon) {
        this.builder = builder;
        this.name = name;
        text = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + "." + name);
        this.icon = icon;
    }

    @Nullable
    public static ShinsuTechnique get(String name) {
        for (ShinsuTechnique tech : values()) {
            if (tech.getName().equals(name)) {
                return tech;
            }
        }
        return null;
    }

    @Nullable
    public static ShinsuTechnique get(ShinsuTechniqueInstance technique) {
        for (ShinsuTechnique tech : values()) {
            if (tech == technique.getTechnique()) {
                return tech;
            }
        }
        return null;
    }

    public Builder<? extends ShinsuTechniqueInstance> getBuilder() {
        return builder;
    }

    public String getName() {
        return name;
    }

    public TranslationTextComponent getText() {
        return text;
    }

    public int getShinsuUse() {
        return builder.getShinsuUse();
    }

    public int getBaangUse() {
        return builder.getBaangUse();
    }

    public RenderInfo getIcon() {
        return icon;
    }

    public interface Builder<T extends ShinsuTechniqueInstance> {

        @Nullable
        T build(LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir);

        T emptyBuild();

        int getShinsuUse();

        int getBaangUse();

        default boolean canCast(ShinsuTechnique technique, LivingEntity user, int level, @Nullable Entity target, @Nullable Vector3d dir) {
            IShinsuStats stats = IShinsuStats.get(user);
            return level > 0 && stats.getCooldown(technique) <= 0 && stats.getShinsu() >= technique.getShinsuUse() && stats.getBaangs() >= technique.getBaangUse();
        }
    }

}
