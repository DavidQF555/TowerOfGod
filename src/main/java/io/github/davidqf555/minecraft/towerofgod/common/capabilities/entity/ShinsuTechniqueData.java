package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class ShinsuTechniqueData<T extends Entity> implements INBTSerializable<CompoundTag> {

    public static final Capability<ShinsuTechniqueData<?>> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final double CAST_TARGET_RANGE = 32;
    private final List<ShinsuTechniqueInstance<?, ?>> technique = new ArrayList<>();

    public static <T extends Entity> ShinsuTechniqueData<T> get(T entity) {
        return entity.getCapability(CAPABILITY).<ShinsuTechniqueData<T>>cast().orElseGet(ShinsuTechniqueData::new);
    }

    public List<ShinsuTechniqueInstance<?, ?>> getTechniques() {
        return technique;
    }

    public void addTechnique(ShinsuTechniqueInstance<?, ?> inst) {
        technique.add(inst);
    }

    public void removeTechnique(ShinsuTechniqueInstance<?, ?> inst) {
        technique.remove(inst);
    }

    public void tick(LivingEntity user) {
        List<ShinsuTechniqueInstance<?, ?>> techniques = new ArrayList<>(getTechniques());
        for (ShinsuTechniqueInstance<?, ?> technique : techniques) {
            technique.tick(user);
            if (technique.shouldRemove()) {
                technique.remove(user);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag instances = new ListTag();
        for (ShinsuTechniqueInstance<?, ?> instance : getTechniques()) {
            ShinsuTechniqueInstance.CODEC.encodeStart(NbtOps.INSTANCE, instance)
                    .result()
                    .ifPresent(instances::add);
        }
        tag.put("Techniques", instances);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Techniques", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("Techniques", Tag.TAG_COMPOUND);
            for (Tag data : list) {
                ShinsuTechniqueInstance.CODEC.parse(NbtOps.INSTANCE, data)
                        .result()
                        .ifPresent(this::addTechnique);
            }
        }
    }

}
