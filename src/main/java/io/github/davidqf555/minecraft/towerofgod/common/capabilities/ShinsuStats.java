package io.github.davidqf555.minecraft.towerofgod.common.capabilities;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuTypeData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateBaangsMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateShinsuMeterPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ShinsuStats implements INBTSerializable<CompoundTag> {

    public static final int ENTITY_RANGE = 32;
    public static final Capability<ShinsuStats> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static final String LEVEL_UP = "entity." + TowerOfGod.MOD_ID + ".level_up";
    private final Map<ShinsuTechniqueType, ShinsuTypeData> data;
    private final Map<ShinsuTechnique, Integer> cooldowns;
    private final List<ShinsuTechniqueInstance> techniques;
    private int level;
    private int shinsu;
    private int baangs;
    private double resistance;
    private double tension;
    private ShinsuAttribute attribute;
    private ShinsuShape shape;

    public ShinsuStats() {
        this(1, 0, 0, 1, 1, null, null);
    }

    private ShinsuStats(int level, int shinsu, int baangs, double resistance, double tension, ShinsuAttribute attribute, @Nullable ShinsuShape shape) {
        this.level = level;
        this.shinsu = shinsu;
        this.baangs = baangs;
        this.resistance = resistance;
        this.tension = tension;
        this.attribute = attribute;
        this.shape = shape;
        data = new EnumMap<>(ShinsuTechniqueType.class);
        cooldowns = new HashMap<>();
        techniques = new ArrayList<>();
    }

    @Nonnull
    public static ShinsuStats get(Entity user) {
        return user.getCapability(CAPABILITY).orElseGet(ShinsuStats::new);
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

    public int getLevel() {
        return level;
    }

    public void addLevel(int amount) {
        level = Math.max(1, level + amount);
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

    public void onKill(Entity owner, ShinsuStats killed) {
        addMaxShinsu(getGainedShinsu(killed.getMaxShinsu()));
        addMaxBaangs(getGainedBaangs(killed.getMaxBaangs()));
        multiplyBaseResistance(getGainedResistance(killed.getRawResistance()));
        multiplyBaseTension(getGainedTension(killed.getRawTension()));
        for (ShinsuTechniqueType type : ShinsuTechniqueType.values()) {
            ShinsuTypeData data = getData(type);
            int initial = data.getLevel();
            addExperience(type, killed.getData(type).getLevel());
            int after = data.getLevel();
            if (initial != after) {
                owner.sendMessage(new TranslatableComponent(LEVEL_UP, type.getText(), after), Util.NIL_UUID);
            }
        }
        if (owner instanceof ServerPlayer) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) owner), new UpdateShinsuMeterPacket(getShinsu(), getMaxShinsu()));
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) owner), new UpdateBaangsMeterPacket(getBaangs(), getMaxBaangs()));
        }
    }

    protected int getTechniqueLevelCap() {
        return getLevel() * 2;
    }

    protected int getGainedShinsu(int killed) {
        int cap = getLevel() * 15 + 15;
        int current = getMaxShinsu();
        if (current < cap) {
            int change = Math.min(cap - current, killed / 7);
            return current <= 0 ? Math.max(1, change) : change;
        }
        return 0;
    }

    protected int getGainedBaangs(int killed) {
        int current = getMaxBaangs();
        int cap = 2 + getLevel() / 2;
        if (current < cap) {
            int change = Math.min(cap - current, killed / 3);
            return current <= 0 ? Math.max(1, change) : change;
        }
        return 0;
    }

    protected double getGainedTension(double killed) {
        double cap = 1 + getLevel() / 4.0;
        double current = getRawTension();
        return current < cap ? Math.min(cap / current, 1 + killed / 20) : 1;
    }

    protected double getGainedResistance(double killed) {
        double cap = 1 + getLevel() / 4.0;
        double current = getRawResistance();
        return current < cap ? Math.min(cap / current, 1 + killed / 20) : 1;
    }

    public void addExperience(ShinsuTechniqueType type, int amount) {
        ShinsuTypeData data = getData(type);
        int exp = getExperience(type);
        int level = data.getLevel();
        int cap = getTechniqueLevelCap();
        while (amount > 0) {
            if (level >= cap) {
                exp = Math.max(exp - amount, 0);
                break;
            } else if (amount >= exp) {
                level++;
                exp = getLevelUpExperience(level);
                amount -= exp;
            } else {
                exp -= amount;
                break;
            }
        }
        data.setLevel(level);
        data.setExperience(exp);
    }

    private int getLevelUpExperience(int level) {
        return (int) Math.pow(2, level) * 2;
    }

    public int getExperience(ShinsuTechniqueType type) {
        return getData(type).getExperience();
    }

    public ShinsuTypeData getData(ShinsuTechniqueType type) {
        return data.computeIfAbsent(type, p -> new ShinsuTypeData());
    }

    public void setCooldown(ShinsuTechnique technique, int cooldown) {
        cooldowns.put(technique, cooldown);
    }

    public int getCooldown(ShinsuTechnique technique) {
        return cooldowns.getOrDefault(technique, 0);
    }

    public void tick(ServerLevel world) {
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Level", level);
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
        ListTag instances = new ListTag();
        for (ShinsuTechniqueInstance instance : techniques) {
            instances.add(instance.serializeNBT());
        }
        tag.put("Techniques", instances);
        CompoundTag data = new CompoundTag();
        this.data.forEach((type, value) -> data.put(type.name(), value.serializeNBT()));
        tag.put("Data", data);
        CompoundTag cooldowns = new CompoundTag();
        this.cooldowns.forEach((technique, cooldown) -> cooldowns.putInt(technique.getRegistryName().toString(), cooldown));
        tag.put("Cooldowns", cooldowns);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Level", Tag.TAG_INT)) {
            level = nbt.getInt("Level");
        }
        if (nbt.contains("Shinsu", Tag.TAG_INT)) {
            shinsu = nbt.getInt("Shinsu");
        }
        if (nbt.contains("Baangs", Tag.TAG_INT)) {
            baangs = nbt.getInt("Baangs");
        }
        if (nbt.contains("Resistance", Tag.TAG_DOUBLE)) {
            resistance = nbt.getDouble("Resistance");
        }
        if (nbt.contains("Tension", Tag.TAG_DOUBLE)) {
            tension = nbt.getDouble("Tension");
        }
        if (nbt.contains("Attribute", Tag.TAG_STRING)) {
            attribute = ShinsuAttributeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Attribute")));
        }
        if (nbt.contains("Shape", Tag.TAG_STRING)) {
            shape = ShinsuShapeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Shape")));
        }
        if (nbt.contains("Techniques", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("Techniques", Tag.TAG_COMPOUND);
            for (Tag data : list) {
                ShinsuTechnique type = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(((CompoundTag) data).getString("Technique")));
                ShinsuTechniqueInstance tech = type.getFactory().blankCreate();
                tech.deserializeNBT((CompoundTag) data);
                techniques.add(tech);
            }
        }
        if (nbt.contains("Data", Tag.TAG_COMPOUND)) {
            CompoundTag data = nbt.getCompound("Data");
            for (String key : data.getAllKeys()) {
                ShinsuTypeData d = new ShinsuTypeData();
                d.deserializeNBT(data.getCompound(key));
                this.data.put(ShinsuTechniqueType.valueOf(key), d);
            }
        }
        if (nbt.contains("Cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = nbt.getCompound("Cooldowns");
            for (String key : data.getAllKeys()) {
                setCooldown(ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(key)), data.getInt(key));
            }
        }
    }

}
