package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.instances.*;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.requirements.IRequirement;
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

    BODY_REINFORCEMENT(Repeat.OVERRIDE, false, new BodyReinforcement.Factory(), ShinsuIcons.RESISTANCE, ImmutableList.of(Direction.UP, Direction.UP, Direction.UP)),
    BLACK_FISH(Repeat.OVERRIDE, false, new BlackFish.Factory(), ShinsuIcons.SWIRL, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.DOWN)),
    SHINSU_BLAST(Repeat.ALLOW, false, new ShinsuBlast.Factory(), ShinsuIcons.BAANGS, ImmutableList.of(Direction.DOWN, Direction.UP)),
    FLARE_WAVE_EXPLOSION(Repeat.ALLOW, false, new FlareWaveExplosion.Factory(), ShinsuIcons.TENSION, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.UP)),
    REVERSE_FLOW_CONTROL(Repeat.ALLOW, false, new ReverseFlowControl.Factory(), ShinsuIcons.REVERSE, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.RIGHT)),
    MANIFEST(Repeat.OVERRIDE, true, new Manifest.Factory(), ShinsuIcons.PICKAXE, ImmutableList.of(Direction.LEFT, Direction.RIGHT)),
    SHOOT_SHINSU_ARROW(Repeat.ALLOW, false, new ShootShinsuArrow.Factory(), ShinsuIcons.BAANGS, ImmutableList.of()),
    MOVE_DEVICES(Repeat.ALLOW, true, new MoveDevices.Factory(), ShinsuIcons.MOVE, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.LEFT)),
    LIGHTHOUSE_FLOW_CONTROL(Repeat.OVERRIDE, true, new LighthouseFlowControl.Factory(), ShinsuIcons.LIGHTHOUSE_FLOW_CONTROL, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT)),
    SCOUT(Repeat.ALLOW, false, new Scout.Factory(), ShinsuIcons.EYE, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)),
    FOLLOW_OWNER(Repeat.TOGGLE, true, new FollowOwner.Factory(), ShinsuIcons.FOLLOW, ImmutableList.of(Direction.UP, Direction.LEFT)),
    CHANNEL_LIGHTNING(Repeat.ALLOW, true, new ChannelLightning.Factory(), ShinsuIcons.LIGHTNING, ImmutableList.of(Direction.DOWN, Direction.UP)),
    FLASH(Repeat.ALLOW, false, new Flash.Factory(), ShinsuIcons.FLASH, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT));

    private final Repeat repeat;
    private final boolean indefinite;
    private final IFactory<? extends ShinsuTechniqueInstance> factory;
    private final TranslationTextComponent text;
    private final TranslationTextComponent description;
    private final IRenderData icon;
    private final List<Direction> combination;

    ShinsuTechnique(Repeat repeat, boolean indefinite, IFactory<? extends ShinsuTechniqueInstance> factory, IRenderData icon, List<Direction> combination) {
        this.repeat = repeat;
        this.indefinite = indefinite;
        this.factory = factory;
        String name = name().toLowerCase();
        text = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + "." + name);
        description = new TranslationTextComponent("technique." + TowerOfGod.MOD_ID + "." + name + ".description");
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
            if (technique.isObtainable()) {
                obtainable.add(technique);
            }
        }
        return obtainable;
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

    public Repeat getRepeatEffect() {
        return repeat;
    }

    public boolean isObtainable() {
        return !combination.isEmpty();
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public IFactory<? extends ShinsuTechniqueInstance> getFactory() {
        return factory;
    }

    public TranslationTextComponent getText() {
        return text;
    }

    public TranslationTextComponent getDescription() {
        return description;
    }

    public IRenderData getIcon() {
        return icon;
    }

    public enum Repeat {
        OVERRIDE(),
        ALLOW(),
        TOGGLE()
    }

    public interface IFactory<T extends ShinsuTechniqueInstance> {

        Either<T, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir);

        T blankCreate();

        ShinsuTechnique getTechnique();

        default Either<T, ITextComponent> doCreate(LivingEntity user, @Nullable Entity target, Vector3d dir) {
            ShinsuTechnique technique = getTechnique();
            ShinsuStats stats = ShinsuStats.get(user);
            List<ShinsuTechniqueInstance> same = stats.getTechniques().stream().filter(inst -> inst.getTechnique() == technique).collect(Collectors.toList());
            Repeat repeat = technique.getRepeatEffect();
            int cooldown = stats.getCooldown(technique);
            if (!isUnlocked(user)) {
                return Either.right(Messages.LOCKED);
            } else if (repeat == Repeat.TOGGLE && !same.isEmpty()) {
                return Either.left(blankCreate());
            } else if (cooldown > 0) {
                return Either.right(Messages.ON_COOLDOWN.apply(cooldown / 20.0));
            }
            Either<T, ITextComponent> either = create(user, target, dir);
            Optional<T> op = either.left();
            if (op.isPresent()) {
                T instance = op.get();
                int netShinsuUse = instance.getShinsuUse();
                int netBaangsUse = instance.getBaangsUse();
                if (repeat == Repeat.OVERRIDE) {
                    for (ShinsuTechniqueInstance inst : same) {
                        netShinsuUse -= inst.getShinsuUse();
                        netBaangsUse -= inst.getBaangsUse();
                    }
                }
                if (stats.getBaangs() < netBaangsUse) {
                    return Either.right(Messages.REQUIRES_BAANGS.apply(netBaangsUse));
                } else if (stats.getShinsu() < netShinsuUse) {
                    return Either.right(Messages.REQUIRES_SHINSU.apply(netShinsuUse));
                }
                return Either.left(instance);
            }
            return either;
        }

        default boolean isUnlocked(LivingEntity user) {
            for (IRequirement requirement : getRequirements()) {
                if (!requirement.isUnlocked(user)) {
                    return false;
                }
            }
            return true;
        }

        IRequirement[] getRequirements();

    }

}
