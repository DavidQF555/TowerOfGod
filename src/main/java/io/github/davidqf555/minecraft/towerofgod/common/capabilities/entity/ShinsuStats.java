package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShinsuStats implements INBTSerializable<CompoundNBT> {

    public static final int ENTITY_RANGE = 32;
    @CapabilityInject(ShinsuStats.class)
    public static Capability<ShinsuStats> capability = null;
    private final Map<ShinsuTechnique, Integer> cooldowns;
    private final List<ShinsuTechniqueInstance> techniques;
    private int shinsu;
    private int baangs;
    private double resistance;
    private double tension;
    private ShinsuAttribute attribute;
    private ShinsuShape shape;

    public ShinsuStats() {
        this(0, 0, 1, 1, null, null);
    }

    private ShinsuStats(int shinsu, int baangs, double resistance, double tension, ShinsuAttribute attribute, @Nullable ShinsuShape shape) {
        this.shinsu = shinsu;
        this.baangs = baangs;
        this.resistance = resistance;
        this.tension = tension;
        this.attribute = attribute;
        this.shape = shape;
        cooldowns = new HashMap<>();
        techniques = new ArrayList<>();
    }

    @Nonnull
    public static ShinsuStats get(Entity user) {
        return user.getCapability(capability).orElseGet(ShinsuStats::new);
    }

    public static double getNetResistance(Entity user, Entity target) {
        ShinsuStats targetStats = get(target);
        ShinsuStats userStats = get(user);
        return targetStats.getRawResistance() / userStats.getRawTension();
    }

    public List<ShinsuTechniqueInstance> getTechniques() {
        return techniques;
    }

    public void addTechnique(ShinsuTechniqueInstance technique) {
        techniques.add(technique);
    }

    public void removeTechnique(ShinsuTechniqueInstance technique) {
        techniques.remove(technique);
    }

    public int getShinsu() {
        int shinsu = getMaxShinsu();
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            shinsu -= technique.getShinsuUse();
        }
        return shinsu;
    }

    public int getMaxShinsu() {
        return shinsu;
    }

    public void addMaxShinsu(int amount) {
        shinsu = Math.max(0, shinsu + amount);
    }

    public int getBaangs() {
        int baangs = getMaxBaangs();
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            baangs -= technique.getBaangsUse();
        }
        return baangs;
    }

    public int getMaxBaangs() {
        return baangs;
    }

    public void addMaxBaangs(int amount) {
        baangs = Math.max(0, baangs + amount);
    }

    public double getRawResistance() {
        return resistance;
    }

    public double getRawTension() {
        return tension;
    }

    public void multiplyBaseResistance(double factor) {
        resistance *= factor;
    }

    public void multiplyBaseTension(double factor) {
        tension *= factor;
    }

    @Nullable
    public ShinsuAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(@Nullable ShinsuAttribute attribute) {
        this.attribute = attribute;
    }

    @Nullable
    public ShinsuShape getShape() {
        return shape;
    }

    public void setShape(@Nullable ShinsuShape shape) {
        this.shape = shape;
    }

    public void setCooldown(ShinsuTechnique technique, int cooldown) {
        cooldowns.put(technique, cooldown);
    }

    public int getCooldown(ShinsuTechnique technique) {
        return cooldowns.getOrDefault(technique, 0);
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
        tag.putInt("Shinsu", shinsu);
        tag.putInt("Baangs", baangs);
        tag.putDouble("Resistance", resistance);
        tag.putDouble("Tension", tension);
        ShinsuAttribute attribute = getAttribute();
        if (attribute != null) {
            tag.putString("Attribute", attribute.getRegistryName().toString());
        }
        ShinsuShape shape = getShape();
        if (shape != null) {
            tag.putString("Shape", shape.getRegistryName().toString());
        }
        ListNBT instances = new ListNBT();
        for (ShinsuTechniqueInstance instance : techniques) {
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
        if (nbt.contains("Shinsu", Constants.NBT.TAG_INT)) {
            shinsu = nbt.getInt("Shinsu");
        }
        if (nbt.contains("Baangs", Constants.NBT.TAG_INT)) {
            baangs = nbt.getInt("Baangs");
        }
        if (nbt.contains("Resistance", Constants.NBT.TAG_DOUBLE)) {
            resistance = nbt.getDouble("Resistance");
        }
        if (nbt.contains("Tension", Constants.NBT.TAG_DOUBLE)) {
            tension = nbt.getDouble("Tension");
        }
        if (nbt.contains("Attribute", Constants.NBT.TAG_STRING)) {
            attribute = ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute")));
        }
        if (nbt.contains("Shape", Constants.NBT.TAG_STRING)) {
            shape = ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape")));
        }
        if (nbt.contains("Techniques", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("Techniques", Constants.NBT.TAG_COMPOUND);
            for (INBT data : list) {
                ShinsuTechnique type = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(((CompoundNBT) data).getString("Technique")));
                ShinsuTechniqueInstance tech = type.getFactory().blankCreate();
                tech.deserializeNBT((CompoundNBT) data);
                techniques.add(tech);
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
