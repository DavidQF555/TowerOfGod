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
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum ShinsuTechnique {

    BODY_REINFORCEMENT(true, new BodyReinforcement.Builder(10, 1), "body_reinforcement", ShinsuIcons.RESISTANCE),
    BLACK_FISH(true, new BlackFish.Builder(10, 1), "black_fish", ShinsuIcons.SWIRL),
    FLARE_WAVE_EXPLOSION(true, new FlareWaveExplosion.Builder(20, 1), "flare_wave_explosion", ShinsuIcons.TENSION),
    REVERSE_FLOW_CONTROL(true, new ReverseFlowControl.Builder(10, 1), "reverse_flow_control", ShinsuIcons.REVERSE),
    SHINSU_BLAST(true, new ShinsuBlast.Builder(5, 1), "shinsu_blast", ShinsuIcons.BAANGS),
    MANIFEST(true, new Manifest.Builder(10, 1), "manifest", ShinsuIcons.SWIRL),
    SHOOT_SHINSU_ARROW(false, new ShootShinsuArrow.Builder(3, 1), "shoot_shinsu_arrow", ShinsuIcons.BAANGS);

    private final boolean obtainable;
    private final Builder<? extends ShinsuTechniqueInstance> builder;
    private final String name;
    private final TranslationTextComponent text;
    private final RenderInfo icon;

    ShinsuTechnique(boolean obtainable, Builder<? extends ShinsuTechniqueInstance> builder, String name, RenderInfo icon) {
        this.obtainable = obtainable;
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

    public static List<ShinsuTechnique> getObtainableTechniques() {
        List<ShinsuTechnique> obtainable = new ArrayList<>();
        for (ShinsuTechnique technique : values()) {
            if (technique.obtainable) {
                obtainable.add(technique);
            }
        }
        return obtainable;
    }

    public boolean isObtainable() {
        return obtainable;
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
