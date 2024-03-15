package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.entities.BaangEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;

public class BaangsTechniqueData<T extends LivingEntity> extends ShinsuTechniqueData<T> {

    private final List<Instance> baangs = new ArrayList<>();
    private int max;

    public int getMaxBaangs() {
        return max;
    }

    public void setMaxBaangs(int max) {
        this.max = max;
    }

    public Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> getBaangSettings() {
        Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> map = new HashMap<>();
        baangs.forEach(inst -> map.put(inst.type, map.getOrDefault(inst.type, 0) + inst.cooldown));
        return map;
    }

    public BaangEntity[] getBaangs(ServerLevel world) {
        return baangs.stream()
                .filter(inst -> inst.id != null)
                .map(inst -> world.getEntity(inst.id))
                .filter(entity -> entity instanceof BaangEntity)
                .map(entity -> (BaangEntity) entity)
                .toArray(BaangEntity[]::new);
    }

    public void setBaangs(Map<ConfiguredShinsuTechniqueType<?, ?>, Integer> baangs) {
        this.baangs.clear();
        int total = 0;
        for (ConfiguredShinsuTechniqueType<?, ?> type : baangs.keySet()) {
            int count = baangs.get(type);
            for (int i = 0; i < count; i++) {
                if (total < getMaxBaangs()) {
                    Instance inst = new Instance();
                    inst.type = type;
                    this.baangs.add(inst);
                    total++;
                }
            }
        }
    }

    @Override
    public void tick(T user) {
        super.tick(user);
        if (user.level instanceof ServerLevel) {
            UUID id = user.getUUID();
            baangs.forEach(instance -> {
                if (instance.id != null) {
                    Entity baang = ((ServerLevel) user.level).getEntity(instance.id);
                    if (!(baang instanceof BaangEntity) || !id.equals(((BaangEntity) baang).getUserID())) {
                        instance.id = null;
                    }
                } else if (--instance.cooldown <= 0) {
                    if ((instance.id = spawnBaang(user, instance.type)) != null) {
                        instance.cooldown = instance.type.getConfig().getCooldown();
                    }
                }
            });
        }
    }

    @Nullable
    protected UUID spawnBaang(T user, ConfiguredShinsuTechniqueType<?, ?> type) {
        BaangEntity baang = EntityRegistry.BAANG.get().create(user.level);
        if (baang != null) {
            baang.setUserID(user.getUUID());
            baang.setTechniqueType(type);
            user.level.addFreshEntity(baang);
            return baang.getUUID();
        }
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt("MaxBaangs", getMaxBaangs());
        ListTag baangs = new ListTag();
        this.baangs.stream().map(Instance::serializeNBT).forEach(baangs::add);
        tag.put("Baangs", baangs);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Baangs", Tag.TAG_LIST)) {
            for (Tag tag : nbt.getList("Baangs", Tag.TAG_COMPOUND)) {
                Instance inst = new Instance();
                inst.deserializeNBT((CompoundTag) tag);
                baangs.add(inst);
            }
        }
        if (nbt.contains("MaxBaangs", Tag.TAG_INT)) {
            setMaxBaangs(nbt.getInt("MaxBaangs"));
        }
    }

    private static class Instance implements INBTSerializable<CompoundTag> {

        private ConfiguredShinsuTechniqueType<?, ?> type;
        private int cooldown;
        private UUID id;

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Type", ConfiguredTechniqueTypeRegistry.getRegistry().getKey(type).toString());
            tag.putInt("Cooldown", cooldown);
            if (id != null) {
                tag.putUUID("ID", id);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            if (nbt.contains("Type", Tag.TAG_STRING)) {
                type = ConfiguredTechniqueTypeRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Type")));
            }
            if (nbt.contains("Cooldown", Tag.TAG_INT)) {
                cooldown = nbt.getInt("Cooldown");
            }
            if (nbt.contains("ID", Tag.TAG_INT_ARRAY)) {
                id = nbt.getUUID("ID");
            }
        }
    }

}