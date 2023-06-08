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
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShinsuTechnique {

    private final boolean indefinite;
    private final IFactory<?> factory;
    private final IRenderData icon;
    private final IRequirement[] requirements;
    private final UsageData usage;
    private final MobUseCondition mobUseCondition;
    private ResourceLocation id;

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

    public boolean shouldMobUse(Mob mob) {
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

    public MutableComponent getText() {
        return Component.translatable(Util.makeDescriptionId("technique", getId()));
    }

    public MutableComponent getDescription() {
        return Component.translatable(Util.makeDescriptionId("technique", getId()) + ".description");
    }

    public ResourceLocation getId() {
        if (id == null) {
            id = ShinsuTechniqueRegistry.getRegistry().getKey(this);
        }
        return id;
    }

    public IRenderData getIcon() {
        return icon;
    }

    public Either<? extends ShinsuTechniqueInstance, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
        Either<? extends ShinsuTechniqueInstance, Component> either = getFactory().create(user, target, dir);
        Optional<? extends ShinsuTechniqueInstance> op = either.left();
        if (op.isPresent()) {
            ShinsuTechniqueInstance instance = op.get();
            ShinsuTechniqueData<Entity> data = ShinsuTechniqueData.get(user);
            Optional<Component> error = data.getCastError(user, instance);
            if (error.isPresent()) {
                return Either.right(error.get());
            }
        }
        return either;
    }

    public int getNetShinsuUse(Entity user, ShinsuTechniqueInstance instance) {
        return instance.getShinsuUse();
    }

    public void cast(Entity user, @Nullable Entity target, Vec3 dir) {
        create(user, target, dir).ifLeft(instance -> cast(user, instance));
    }

    public void cast(Entity user, ShinsuTechniqueInstance instance) {
        ShinsuTechniqueData<Entity> stats = ShinsuTechniqueData.get(user);
        stats.addTechnique(instance);
        stats.onCast(user, instance);
        instance.onUse((ServerLevel) user.level());
    }

    public interface IFactory<T extends ShinsuTechniqueInstance> {

        Either<T, Component> create(Entity user, @Nullable Entity target, Vec3 dir);

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
