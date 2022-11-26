package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.Group;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
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
    private final UsageData usage;
    private final MobUseCondition mobUseCondition;

    public ShinsuTechnique(boolean indefinite, ShinsuTechnique.IFactory<?> factory, IRenderData icon, IRequirement[] requirements, @Nullable UsageData usage, MobUseCondition mobUseCondition) {
        this.indefinite = indefinite;
        this.factory = factory;
        this.icon = icon;
        this.requirements = requirements;
        this.usage = usage;
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

    public boolean shouldMobUse(MobEntity mob) {
        return mobUseCondition.shouldUse(mob);
    }

    public boolean matches(List<Direction> combination) {
        List<Direction> current = getCombination();
        if (combination.size() == current.size()) {
            for (int i = 0; i < combination.size(); i++) {
                if (!combination.get(i).equals(current.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public List<Direction> getCombination() {
        UsageData data = getUsageData();
        return data == null ? ImmutableList.of() : data.getCombination();
    }

    public boolean isObtainable() {
        return getUsageData() != null;
    }

    @Nullable
    public UsageData getUsageData() {
        return usage;
    }

    public boolean isIndefinite() {
        return indefinite;
    }

    public IFactory<? extends ShinsuTechniqueInstance> getFactory() {
        return factory;
    }

    public IRequirement[] getRequirements() {
        return requirements;
    }

    public TranslationTextComponent getText() {
        return new TranslationTextComponent(Util.makeDescriptionId("technique", getRegistryName()));
    }

    public TranslationTextComponent getDescription() {
        return new TranslationTextComponent(Util.makeDescriptionId("technique", getRegistryName()) + ".description");
    }

    public IRenderData getIcon() {
        return icon;
    }

    public Either<? extends ShinsuTechniqueInstance, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
        Either<? extends ShinsuTechniqueInstance, ITextComponent> either = getFactory().create(user, target, dir);
        Optional<? extends ShinsuTechniqueInstance> op = either.left();
        if (op.isPresent()) {
            ShinsuTechniqueInstance instance = op.get();
            ShinsuTechniqueData<Entity> data = ShinsuTechniqueData.get(user);
            Optional<ITextComponent> error = data.getCastError(user, instance);
            if (error.isPresent()) {
                return Either.right(error.get());
            }
        }
        return either;
    }

    public int getNetShinsuUse(Entity user, ShinsuTechniqueInstance instance) {
        return instance.getShinsuUse();
    }

    public void cast(Entity user, @Nullable Entity target, Vector3d dir) {
        create(user, target, dir).ifLeft(instance -> cast(user, instance));
    }

    public void cast(Entity user, ShinsuTechniqueInstance instance) {
        ShinsuTechniqueData<Entity> stats = ShinsuTechniqueData.get(user);
        stats.addTechnique(instance);
        stats.onCast(user, instance);
        instance.onUse((ServerWorld) user.level);
    }

    public interface IFactory<T extends ShinsuTechniqueInstance> {

        Either<T, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir);

        T blankCreate();

    }

    public static class UsageData {

        private final List<Direction> combination;
        private final List<ShinsuAttribute> attributes;
        private final List<ShinsuShape> shapes;
        private final List<Group> groups;

        public UsageData(List<Direction> combination, List<ShinsuAttribute> attributes, List<ShinsuShape> shapes, List<Group> group) {
            this.combination = combination;
            this.attributes = attributes;
            this.shapes = shapes;
            this.groups = group;
        }

        public static UsageData all(List<Direction> combination) {
            List<ShinsuAttribute> attributes = new ArrayList<>(ShinsuAttributeRegistry.getRegistry().getValues());
            List<ShinsuShape> shapes = new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues());
            List<Group> groups = new ArrayList<>(GroupRegistry.getRegistry().getValues());
            return new UsageData(combination, attributes, shapes, groups);
        }

        public List<Direction> getCombination() {
            return combination;
        }

        public List<ShinsuAttribute> getMentorAttributes() {
            return attributes;
        }

        public List<ShinsuShape> getMentorShapes() {
            return shapes;
        }

        public List<Group> getMentorGroups() {
            return groups;
        }

    }

}
