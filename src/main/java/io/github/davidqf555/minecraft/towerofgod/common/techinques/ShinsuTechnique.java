package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum ShinsuTechnique {

    BODY_REINFORCEMENT(ShinsuTechniqueType.CONTROL, Repeat.OVERRIDE, false, true, 1, new BodyReinforcement.Builder(), ShinsuIcons.RESISTANCE, ImmutableList.of(Direction.UP, Direction.UP, Direction.UP)),
    BLACK_FISH(ShinsuTechniqueType.CONTROL, Repeat.OVERRIDE, false, true, 5, new BlackFish.Builder(), ShinsuIcons.SWIRL, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.DOWN)),
    SHINSU_BLAST(ShinsuTechniqueType.CONTROL, Repeat.ALLOW, false, true, 2, new ShinsuBlast.Builder(), ShinsuIcons.BAANGS, ImmutableList.of(Direction.DOWN, Direction.UP)),
    FLARE_WAVE_EXPLOSION(ShinsuTechniqueType.DISRUPTION, Repeat.ALLOW, false, true, 10, new FlareWaveExplosion.Builder(), ShinsuIcons.TENSION, ImmutableList.of(Direction.UP, Direction.LEFT, Direction.LEFT)),
    REVERSE_FLOW_CONTROL(ShinsuTechniqueType.DISRUPTION, Repeat.ALLOW, false, true, 15, new ReverseFlowControl.Builder(), ShinsuIcons.REVERSE, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.RIGHT)),
    MANIFEST(ShinsuTechniqueType.MANIFEST, Repeat.OVERRIDE, true, true, 10, new Manifest.Builder(), ShinsuIcons.PICKAXE, ImmutableList.of(Direction.LEFT, Direction.RIGHT)),
    SHOOT_SHINSU_ARROW(ShinsuTechniqueType.MANIFEST, Repeat.ALLOW, false, false, 0, new ShootShinsuArrow.Builder(), ShinsuIcons.BAANGS, ImmutableList.of()),
    MOVE_DEVICES(ShinsuTechniqueType.DEVICE_CONTROL, Repeat.ALLOW, true, true, 5, new MoveDevices.Builder(), ShinsuIcons.MOVE, ImmutableList.of(Direction.UP, Direction.RIGHT)),
    LIGHTHOUSE_FLOW_CONTROL(ShinsuTechniqueType.DEVICE_CONTROL, Repeat.OVERRIDE, false, true, 10, new LighthouseFlowControl.Builder(), ShinsuIcons.LIGHTHOUSE_FLOW_CONTROL, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)),
    SCOUT(ShinsuTechniqueType.DEVICE_CONTROL, Repeat.ALLOW, false, true, 7, new Scout.Builder(), ShinsuIcons.EYE, ImmutableList.of(Direction.UP, Direction.DOWN)),
    FOLLOW_OWNER(ShinsuTechniqueType.DEVICE_CONTROL, Repeat.TOGGLE, true, true, 1, new FollowOwner.Builder(), ShinsuIcons.FOLLOW, ImmutableList.of(Direction.UP, Direction.LEFT));

    private final ShinsuTechniqueType type;
    private final Repeat repeat;
    private final boolean indefinite;
    private final boolean obtainable;
    private final int level;
    private final IBuilder<? extends ShinsuTechniqueInstance> builder;
    private final TranslationTextComponent text;
    private final IRenderData icon;
    private final List<Direction> combination;

    ShinsuTechnique(ShinsuTechniqueType type, Repeat repeat, boolean indefinite, boolean obtainable, int level, IBuilder<? extends ShinsuTechniqueInstance> builder, IRenderData icon, List<Direction> combination) {
        this.type = type;
        this.repeat = repeat;
        this.indefinite = indefinite;
        this.obtainable = obtainable;
        this.level = level;
        this.builder = builder;
        text = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + "." + name().toLowerCase());
        this.icon = icon;
        this.combination = combination;
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

    public ShinsuTechniqueType getType() {
        return type;
    }

    public boolean matches(List<Direction> combination) {
        if (combination.size() == this.combination.size()) {
            for (int i = 0; i < combination.size(); i++) {
                if (!combination.get(i).equals(this.combination.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public List<Direction> getCombination() {
        return combination;
    }

    public int getLevelRequirement() {
        return level;
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

    public IRenderData getIcon() {
        return icon;
    }

    public enum Repeat {
        OVERRIDE(),
        ALLOW(),
        TOGGLE()
    }

    public interface IBuilder<T extends ShinsuTechniqueInstance> {

        Either<T, ITextComponent> build(LivingEntity user, int level, @Nullable Entity target, Vector3d dir);

        T emptyBuild();

        ShinsuTechnique getTechnique();

        default Either<T, ITextComponent> doBuild(LivingEntity user, int level, @Nullable Entity target, Vector3d dir) {
            ShinsuTechnique technique = getTechnique();
            ShinsuStats stats = ShinsuStats.get(user);
            List<ShinsuTechniqueInstance> same = stats.getTechniques().stream().filter(inst -> inst.getTechnique() == technique).collect(Collectors.toList());
            int cooldown = stats.getData(technique.getType()).getCooldown();
            if (technique.getRepeatEffect() == Repeat.TOGGLE && !same.isEmpty()) {
                return Either.left(emptyBuild());
            } else if (cooldown > 0) {
                return Either.right(ErrorMessages.ON_COOLDOWN.apply(cooldown));
            } else if (technique.isObtainable() && level <= technique.getLevelRequirement()) {
                return Either.right(ErrorMessages.REQUIRES_LEVEL.apply(technique.getType(), technique.getLevelRequirement()));
            }
            Either<T, ITextComponent> either = build(user, level, target, dir);
            Optional<T> op = either.left();
            if (op.isPresent()) {
                T instance = op.get();
                int netShinsuUse = instance.getShinsuUse();
                int netBaangsUse = instance.getBaangsUse();
                for (ShinsuTechniqueInstance inst : same) {
                    netShinsuUse -= inst.getShinsuUse();
                    netBaangsUse -= inst.getBaangsUse();
                }
                if (stats.getBaangs() < netBaangsUse) {
                    return Either.right(ErrorMessages.REQUIRES_BAANGS.apply(netBaangsUse));
                } else if (stats.getShinsu() < netShinsuUse) {
                    return Either.right(ErrorMessages.REQUIRES_SHINSU.apply(netShinsuUse));
                }
                return Either.left(instance);
            }
            return either;
        }
    }
}
