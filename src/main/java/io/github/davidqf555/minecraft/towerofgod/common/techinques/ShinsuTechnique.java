package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum ShinsuTechnique {

    BODY_REINFORCEMENT(Repeat.DENY, false, true, new BodyReinforcement.Builder(), null, Either.left("resistance")),
    BLACK_FISH(Repeat.DENY, false, true, new BlackFish.Builder(), null, Either.left("swirl")),
    FLARE_WAVE_EXPLOSION(Repeat.ALLOW, false, true, new FlareWaveExplosion.Builder(), null, Either.left("tension")),
    REVERSE_FLOW_CONTROL(Repeat.ALLOW, false, true, new ReverseFlowControl.Builder(), null, Either.left("reverse")),
    SHINSU_BLAST(Repeat.ALLOW, false, true, new ShinsuBlast.Builder(), null, Either.left("baangs")),
    MANIFEST(Repeat.DENY, true, true, new Manifest.Builder(), null, Either.left("pickaxe")),
    SHOOT_SHINSU_ARROW(Repeat.ALLOW, false, false, new ShootShinsuArrow.Builder(), null, Either.left("baangs")),
    MOVE_DEVICES(Repeat.ALLOW, true, true, new MoveDevices.Builder(), BasicCommandTechnique.COLOR_TARGETING, Either.left("move")),
    LIGHTHOUSE_FLOW_CONTROL(Repeat.DENY, false, true, new LighthouseFlowControl.Builder(), BasicCommandTechnique.COLOR_TARGETING, Either.left("lighthouse_flow_control")),
    SCOUT(Repeat.ALLOW, false, true, new Scout.Builder(), BasicCommandTechnique.COLOR_TARGETING, Either.left("eye")),
    FOLLOW_OWNER(Repeat.TOGGLE, true, true, new FollowOwner.Builder(), BasicCommandTechnique.COLOR_TARGETING, Either.left("follow"));

    private final Repeat repeat;
    private final boolean indefinite;
    private final boolean obtainable;
    private final IBuilder<? extends ShinsuTechniqueInstance> builder;
    private final TranslationTextComponent text;
    private final TechniqueSettings settings;
    private final Either<String, Supplier<ItemStack>> icon;

    ShinsuTechnique(Repeat repeat, boolean indefinite, boolean obtainable, IBuilder<? extends ShinsuTechniqueInstance> builder, @Nullable TechniqueSettings settings, Either<String, Supplier<ItemStack>> icon) {
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

    public Either<String, Supplier<ItemStack>> getIcon() {
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
