package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

    //TODO implement into PlayerTechniqueData
    public boolean isUnlocked(PlayerEntity player) {
        return true;
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

    public Either<? extends ShinsuTechniqueInstance, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir) {
        Either<? extends ShinsuTechniqueInstance, ITextComponent> either = getFactory().create(user, target, dir);
        Optional<? extends ShinsuTechniqueInstance> op = either.left();
        if (op.isPresent()) {
            ShinsuTechniqueInstance instance = op.get();
            ShinsuTechniqueData data = ShinsuTechniqueData.get(user);
            Optional<ITextComponent> error = data.getCastError(user, instance);
            if (error.isPresent()) {
                return Either.right(error.get());
            }
        }
        return either;
    }

    public int getNetShinsuUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        return instance.getShinsuUse();
    }

    public int getNetBaangsUse(LivingEntity user, ShinsuTechniqueInstance instance) {
        return instance.getBaangsUse();
    }

    public void cast(LivingEntity user, @Nullable Entity target, Vector3d dir) {
        create(user, target, dir).ifLeft(instance -> cast(user, instance));
    }

    public void cast(LivingEntity user, ShinsuTechniqueInstance instance) {
        ShinsuTechniqueData stats = ShinsuTechniqueData.get(user);
        stats.addTechnique(instance);
        stats.onCast(user, instance);
        instance.onUse((ServerWorld) user.level);
    }

    public MobUseCondition getMobUseCondition() {
        return mobUseCondition;
    }

    public interface IFactory<T extends ShinsuTechniqueInstance> {

        Either<T, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir);

        T blankCreate();

    }

}
