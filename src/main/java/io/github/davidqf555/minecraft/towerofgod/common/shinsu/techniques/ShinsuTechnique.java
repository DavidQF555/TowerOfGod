package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
        int cooldown = ShinsuTechniqueData.get(user).getCooldown(this);
        if (!isUnlocked(user)) {
            return Either.right(Messages.LOCKED);
        } else if (cooldown > 0) {
            return Either.right(Messages.getOnCooldown(cooldown / 20.0));
        }
        Either<? extends ShinsuTechniqueInstance, ITextComponent> either = getFactory().create(user, target, dir);
        Optional<? extends ShinsuTechniqueInstance> op = either.left();
        if (op.isPresent()) {
            ShinsuTechniqueInstance instance = op.get();
            int netShinsuUse = getNetShinsuUse(user, instance);
            int netBaangsUse = getNetBaangsUse(user, instance);
            if (ShinsuStats.getBaangs(user) < netBaangsUse) {
                return Either.right(Messages.getRequiresBaangs(netBaangsUse));
            } else if (ShinsuStats.getShinsu(user) < netShinsuUse) {
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

    public void cast(LivingEntity user, @Nullable Entity target, Vector3d dir) {
        if (user.level instanceof ServerWorld) {
            ShinsuTechniqueData stats = ShinsuTechniqueData.get(user);
            if (stats.getCooldown(this) <= 0) {
                create(user, target, dir).ifLeft(instance -> cast(user, instance));
            }
        }
    }

    public void cast(LivingEntity user, ShinsuTechniqueInstance instance) {
        if (user.level instanceof ServerWorld) {
            ShinsuTechniqueData stats = ShinsuTechniqueData.get(user);
            stats.setCooldown(this, instance.getCooldown());
            stats.addTechnique(instance);
            instance.onUse((ServerWorld) user.level);
        }
    }

    public <T extends MobEntity & IShinsuUser> boolean shouldMobUse(T entity) {
        return mobUseCondition.shouldUse(entity);
    }

    public interface IFactory<T extends ShinsuTechniqueInstance> {

        Either<T, ITextComponent> create(LivingEntity user, @Nullable Entity target, Vector3d dir);

        T blankCreate();

    }

}
