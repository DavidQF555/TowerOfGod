package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.client.gui.RenderInfo;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum ShinsuTechnique {

    BODY_REINFORCEMENT(Repeat.DENY, false, true, new BodyReinforcement.Builder(), null, ShinsuIcons.RESISTANCE),
    BLACK_FISH(Repeat.DENY, false, true, new BlackFish.Builder(), null, ShinsuIcons.SWIRL),
    FLARE_WAVE_EXPLOSION(Repeat.ALLOW, false, true, new FlareWaveExplosion.Builder(), null, ShinsuIcons.TENSION),
    REVERSE_FLOW_CONTROL(Repeat.ALLOW, false, true, new ReverseFlowControl.Builder(), null, ShinsuIcons.REVERSE),
    SHINSU_BLAST(Repeat.ALLOW, false, true, new ShinsuBlast.Builder(), null, ShinsuIcons.BAANGS),
    MANIFEST(Repeat.DENY, true, true, new Manifest.Builder(), null, ShinsuIcons.PICKAXE),
    SHOOT_SHINSU_ARROW(Repeat.ALLOW, false, false, new ShootShinsuArrow.Builder(), null, ShinsuIcons.BAANGS),
    MOVE_DEVICES(Repeat.ALLOW, true, true, new MoveDevices.Builder(), BasicCommandTechnique.COLOR_TARGETING, ShinsuIcons.MOVE),
    LIGHTHOUSE_FLOW_CONTROL(Repeat.DENY, false, true, new LighthouseFlowControl.Builder(), BasicCommandTechnique.COLOR_TARGETING, ShinsuIcons.LIGHTHOUSE_FLOW_CONTROL),
    SCOUT(Repeat.ALLOW, false, true, new Scout.Builder(), BasicCommandTechnique.COLOR_TARGETING, ShinsuIcons.EYE),
    FOLLOW_OWNER(Repeat.TOGGLE, true, true, new FollowOwner.Builder(), BasicCommandTechnique.COLOR_TARGETING, ShinsuIcons.FOLLOW);

    private final Repeat repeat;
    private final boolean indefinite;
    private final boolean obtainable;
    private final IBuilder<? extends ShinsuTechniqueInstance> builder;
    private final TranslationTextComponent text;
    private final TechniqueSettings settings;
    private final RenderInfo icon;

    ShinsuTechnique(Repeat repeat, boolean indefinite, boolean obtainable, IBuilder<? extends ShinsuTechniqueInstance> builder, @Nullable TechniqueSettings settings, RenderInfo icon) {
        this.repeat = repeat;
        this.indefinite = indefinite;
        this.obtainable = obtainable;
        this.builder = builder;
        text = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + "." + name().toLowerCase());
        this.settings = settings == null ? TechniqueSettings.SINGLE : settings;
        this.icon = icon;
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

    public Repeat getRepeatEffect() {
        return repeat;
    }

    public boolean isObtainable() {
        return obtainable;
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public IBuilder<? extends ShinsuTechniqueInstance> getBuilder() {
        return builder;
    }

    public TranslationTextComponent getText() {
        return text;
    }

    public TechniqueSettings getSettings() {
        return settings;
    }

    public RenderInfo getIcon() {
        return icon;
    }

    public enum Repeat {
        DENY(),
        ALLOW(),
        TOGGLE()
    }

    public interface IBuilder<T extends ShinsuTechniqueInstance> {

        @Nullable
        T build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings);

        T emptyBuild();

        ShinsuTechnique getTechnique();

        @Nullable
        default T doBuild(LivingEntity user, int level, @Nullable Entity target, Vector3d dir, @Nullable String settings) {
            ShinsuTechnique technique = getTechnique();
            ShinsuStats stats = ShinsuStats.get(user);
            if ((!technique.isObtainable() || level > 0) && stats.getCooldown(technique) <= 0) {
                T instance = build(user, level, target, dir, settings);
                if (instance != null) {
                    List<ShinsuTechniqueInstance> conflicting = stats.getTechniques().stream().filter(instance::isConflicting).collect(Collectors.toList());
                    if (technique.getRepeatEffect() != Repeat.DENY || conflicting.isEmpty()) {
                        int netShinsuUse = instance.getShinsuUse();
                        int netBaangsUse = instance.getBaangsUse();
                        for (ShinsuTechniqueInstance inst : conflicting) {
                            netShinsuUse -= inst.getShinsuUse();
                            netBaangsUse -= inst.getBaangsUse();
                        }
                        if (stats.getShinsu() >= netShinsuUse && stats.getBaangs() >= netBaangsUse) {
                            return instance;
                        }
                    }
                }
            }
            return null;
        }
    }
}
