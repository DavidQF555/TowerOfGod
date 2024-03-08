package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.entities.BaangEntity;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaangsTechniqueData<T extends LivingEntity> extends ShinsuTechniqueData<T> {

    private final List<Instance> baangs = new ArrayList<>();
    private int max;

    public int getMaxBaangs() {
        return max;
    }

    public void setMaxBaangs(int max) {
        this.max = max;
    }

    public void setBaangs(Map<ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>, Integer> baangs) {
        this.baangs.clear();
        baangs.forEach((type, count) -> {
            if (count > 0) {
                Instance inst = new Instance();
                inst.type = ConfiguredTechniqueTypeRegistry.getRegistry().getValue(type.location());
                this.baangs.add(inst);
            }
        });
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
            baang.setTechnique(type);
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
