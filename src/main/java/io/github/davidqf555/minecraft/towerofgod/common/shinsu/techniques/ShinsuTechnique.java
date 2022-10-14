package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShinsuTechnique extends ForgeRegistryEntry<ShinsuTechnique> {

    private final boolean indefinite;
    private final IFactory<?> factory;
    private final IRenderData icon;
    private final IRequirement[] requirements;
    private final List<Direction> combination;
    private final MobUseCondition mobUseCondition;

    public ShinsuTechnique(boolean indefinite, ShinsuTechnique.IFactory<?> factory, IRenderData icon, IRequirement[] requirements, List<Direction> combination, MobUseCondition mobUseCondition) {
        this.indefinite = indefinite;
        this.factory = factory;
        this.icon = icon;
        this.requirements = requirements;
        this.combination = combination;
        this.mobUseCondition = mobUseCondition;
    }

    @Nullable
    public static ShinsuTechnique get(ShinsuTechniqueInstance technique) {
        for (ShinsuTechnique tech : ShinsuTechniqueRegistry.getRegistry()) {
            if (tech == technique.getTechnique()) {
                return tech;
            }
        }
        return null;
    }

    public static List<ShinsuTechnique> getObtainableTechniques() {
        List<ShinsuTechnique> obtainable = new ArrayList<>();
        for (ShinsuTechnique technique : ShinsuTechniqueRegistry.getRegistry()) {
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

    public boolean isObtainable() {
        return !combination.isEmpty();
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public IFactory<? extends ShinsuTechniqueInstance> getFactory() {
        return factory;
    }

    public boolean isUnlocked(LivingEntity user) {
        for (IRequirement requirement : getRequirements()) {
            if (!requirement.isUnlocked(user)) {
                return false;
            }
        }
        return true;
    }

    public IRequirement[] getRequirements() {
        return requirements;
    }

    public TranslatableComponent getText() {
        return new TranslatableComponent(Util.makeDescriptionId("technique", getRegistryName()));
    }

    public TranslatableComponent getDescription() {
        return new TranslatableComponent(Util.makeDescriptionId("technique", getRegistryName()) + ".description");
    }

    public IRenderData getIcon() {
        return icon;
    }

    public Either<? extends ShinsuTechniqueInstance, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir) {
        ShinsuStats stats = ShinsuStats.get(user);
        int cooldown = stats.getCooldown(this);
        if (!isUnlocked(user)) {
            return Either.right(Messages.LOCKED);
        } else if (cooldown > 0) {
            return Either.right(Messages.getOnCooldown(cooldown / 20.0));
        }
        Either<? extends ShinsuTechniqueInstance, Component> either = getFactory().create(user, target, dir);
        Optional<? extends ShinsuTechniqueInstance> op = either.left();
        if (op.isPresent()) {
            ShinsuTechniqueInstance instance = op.get();
            int netShinsuUse = getNetShinsuUse(user, instance);
            int netBaangsUse = getNetBaangsUse(user, instance);
            if (stats.getBaangs() < netBaangsUse) {
                return Either.right(Messages.getRequiresBaangs(netBaangsUse));
            } else if (stats.getShinsu() < netShinsuUse) {
                return Either.right(Messages.getRequiresShinsu(netShinsuUse));
            }
            return Either.left(instance);
        }
        return either;
    }

    protected int getNetShinsuUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        return instance.getShinsuUse();
    }

    protected int getNetBaangsUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        return instance.getBaangsUse();
    }

    public void cast(LivingEntity user, @Nullable Entity target, Vec3 dir) {
        if (user.level instanceof ServerLevel) {
            ShinsuStats stats = ShinsuStats.get(user);
            if (stats.getCooldown(this) <= 0) {
                create(user, target, dir).ifLeft(instance -> cast(user, instance));
            }
        }
    }

    public void cast(LivingEntity user, ShinsuTechniqueInstance instance) {
        if (user.level instanceof ServerLevel) {
            ShinsuStats stats = ShinsuStats.get(user);
            stats.setCooldown(this, instance.getCooldown());
            stats.addTechnique(instance);
            instance.onUse((ServerLevel) user.level);
        }
    }

    public <T extends Mob & IShinsuUser> boolean shouldMobUse(T entity) {
        return mobUseCondition.shouldUse(entity);
    }

    public interface IFactory<T extends ShinsuTechniqueInstance> {

        Either<T, Component> create(LivingEntity user, @Nullable Entity target, Vec3 dir);

        T blankCreate();

    }

}
