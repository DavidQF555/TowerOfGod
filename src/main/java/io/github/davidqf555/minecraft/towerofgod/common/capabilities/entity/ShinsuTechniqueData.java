package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShinsuTechniqueData<T extends Entity> implements INBTSerializable<CompoundTag> {

    public static final Capability<ShinsuTechniqueData<?>> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final double CAST_TARGET_RANGE = 32;
    private final List<ShinsuTechniqueInstance> technique = new ArrayList<>();

    public static <T extends Entity> ShinsuTechniqueData<T> get(T entity) {
        return entity.getCapability(CAPABILITY).<ShinsuTechniqueData<T>>cast().orElseGet(ShinsuTechniqueData::new);
    }

    public Optional<Component> getCastError(T user, ShinsuTechniqueInstance instance) {
        int netShinsuUse = instance.getTechnique().getNetShinsuUse(user, instance);
        if (ShinsuStats.getShinsu(user) < netShinsuUse) {
            return Optional.of(Messages.getRequiresShinsu(netShinsuUse));
        }
        return Optional.empty();
    }

    public void onCast(T user, ShinsuTechniqueInstance instance) {
    }

    public List<ShinsuTechniqueInstance> getTechniques() {
        return technique;
    }

    public void addTechnique(ShinsuTechniqueInstance instance) {
        technique.add(instance);
    }

    public void removeTechnique(ShinsuTechniqueInstance instance) {
        technique.remove(instance);
    }

    public int getShinsuUsage() {
        int shinsu = 0;
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            shinsu += technique.getShinsuUse();
        }
        return shinsu;
    }

    public void tick(ServerLevel world) {
        List<ShinsuTechniqueInstance> techniques = new ArrayList<>(getTechniques());
        for (ShinsuTechniqueInstance technique : techniques) {
            technique.tick(world);
            if (technique.getTicks() >= technique.getDuration()) {
                technique.remove(world);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag instances = new ListTag();
        for (ShinsuTechniqueInstance instance : getTechniques()) {
            instances.add(instance.serializeNBT());
        }
        tag.put("Techniques", instances);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Techniques", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("Techniques", Tag.TAG_COMPOUND);
            for (Tag data : list) {
                ShinsuTechnique type = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(((CompoundTag) data).getString("Technique")));
                ShinsuTechniqueInstance tech = type.getFactory().blankCreate();
                tech.deserializeNBT((CompoundTag) data);
                addTechnique(tech);
            }
        }
    }

}
