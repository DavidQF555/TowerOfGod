package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

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
    public Optional<ITextComponent> getCastError(T user, ShinsuTechniqueInstance instance) {
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
    public void tick(ServerWorld world) {
        super.tick(world);
        for (ShinsuTechnique technique : ShinsuTechnique.getObtainableTechniques()) {
            int cooldown = getCooldown(technique);
            if (cooldown > 0) {
                setCooldown(technique, cooldown - 1);
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        CompoundNBT cooldowns = new CompoundNBT();
        this.cooldowns.forEach((technique, cooldown) -> cooldowns.putInt(technique.getRegistryName().toString(), cooldown));
        tag.put("Cooldowns", cooldowns);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Cooldowns", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT data = nbt.getCompound("Cooldowns");
            for (String key : data.getAllKeys()) {
                setCooldown(ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(key)), data.getInt(key));
            }
        }
    }

}
