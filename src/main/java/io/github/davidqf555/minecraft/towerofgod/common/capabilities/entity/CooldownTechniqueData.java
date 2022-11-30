package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CooldownTechniqueData<T extends Entity> extends ShinsuTechniqueData<T> {

    private final Map<ShinsuTechnique, Integer> cooldowns = new HashMap<>();

    public void setCooldown(ShinsuTechnique technique, int cooldown) {
        cooldowns.put(technique, cooldown);
    }

    public int getCooldown(ShinsuTechnique technique) {
        return cooldowns.getOrDefault(technique, 0);
    }

    @Override
    public Optional<Component> getCastError(T user, ShinsuTechniqueInstance instance) {
        int cooldown = getCooldown(instance.getTechnique());
        if (cooldown > 0) {
            return Optional.of(Messages.getOnCooldown(cooldown / 20.0));
        }
        return super.getCastError(user, instance);
    }

    @Override
    public void onCast(T user, ShinsuTechniqueInstance instance) {
        setCooldown(instance.getTechnique(), instance.getCooldown());
        super.onCast(user, instance);
    }

    @Override
    public void tick(ServerLevel world) {
        super.tick(world);
        for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
            int cooldown = getCooldown(technique);
            if (cooldown > 0) {
                setCooldown(technique, cooldown - 1);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        CompoundTag cooldowns = new CompoundTag();
        this.cooldowns.forEach((technique, cooldown) -> cooldowns.putInt(technique.getId().toString(), cooldown));
        tag.put("Cooldowns", cooldowns);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Cooldowns", Tag.TAG_COMPOUND)) {
            CompoundTag data = nbt.getCompound("Cooldowns");
            for (String key : data.getAllKeys()) {
                setCooldown(ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(key)), data.getInt(key));
            }
        }
    }

}
