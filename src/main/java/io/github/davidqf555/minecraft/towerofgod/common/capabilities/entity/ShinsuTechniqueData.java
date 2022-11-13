package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShinsuTechniqueData implements INBTSerializable<CompoundNBT> {

    public static final double CAST_TARGET_RANGE = 32;
    @CapabilityInject(ShinsuTechniqueData.class)
    public static Capability<ShinsuTechniqueData> capability = null;
    private final List<ShinsuTechniqueInstance> technique = new ArrayList<>();
    private final Map<ShinsuTechnique, Integer> cooldowns = new HashMap<>();

    public static ShinsuTechniqueData get(Entity entity) {
        return entity.getCapability(capability).orElseGet(ShinsuTechniqueData::new);
    }

    public void setCooldown(ShinsuTechnique technique, int cooldown) {
        cooldowns.put(technique, cooldown);
    }

    public int getCooldown(ShinsuTechnique technique) {
        return cooldowns.getOrDefault(technique, 0);
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

    public int getBaangsUsage() {
        int baangs = 0;
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            baangs += technique.getBaangsUse();
        }
        return baangs;
    }

    public void tick(ServerWorld world) {
        List<ShinsuTechniqueInstance> techniques = new ArrayList<>(getTechniques());
        for (ShinsuTechniqueInstance technique : techniques) {
            technique.tick(world);
            if (technique.getTicks() >= technique.getDuration()) {
                technique.remove(world);
            }
        }
        for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
            int cooldown = getCooldown(technique);
            if (cooldown > 0) {
                setCooldown(technique, cooldown - 1);
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        ListNBT instances = new ListNBT();
        for (ShinsuTechniqueInstance instance : getTechniques()) {
            instances.add(instance.serializeNBT());
        }
        tag.put("Techniques", instances);
        CompoundNBT cooldowns = new CompoundNBT();
        this.cooldowns.forEach((technique, cooldown) -> cooldowns.putInt(technique.getRegistryName().toString(), cooldown));
        tag.put("Cooldowns", cooldowns);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Techniques", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("Techniques", Constants.NBT.TAG_COMPOUND);
            for (INBT data : list) {
                ShinsuTechnique type = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(((CompoundNBT) data).getString("Technique")));
                ShinsuTechniqueInstance tech = type.getFactory().blankCreate();
                tech.deserializeNBT((CompoundNBT) data);
                addTechnique(tech);
            }
        }
        if (nbt.contains("Cooldowns", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = nbt.getCompound("Cooldowns");
            for (String key : data.getAllKeys()) {
                setCooldown(ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(key)), data.getInt(key));
            }
        }
    }

}
