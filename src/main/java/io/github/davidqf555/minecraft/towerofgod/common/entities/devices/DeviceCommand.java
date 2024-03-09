package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.BasicCommandTechnique;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public abstract class DeviceCommand extends Goal implements INBTSerializable<CompoundTag> {

    private final FlyingDevice entity;
    private UUID technique;
    private boolean remove;
    private int duration;
    private int ticks;

    public DeviceCommand(FlyingDevice entity, UUID technique, int duration) {
        this.entity = entity;
        this.duration = duration;
        ticks = 0;
        this.technique = technique;
        remove = false;
    }

    public int ticksLeft() {
        return duration - ticks;
    }

    public FlyingDevice getEntity() {
        return entity;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void passiveTick() {
        if (!getType().isIndefinite()) {
            ticks++;
        }
        if (ticksLeft() <= 0 || technique == null) {
            remove();
        } else {
            Entity owner = getEntity().getOwner();
            if (!(owner instanceof LivingEntity) || ShinsuTechniqueData.get((LivingEntity) owner).getTechniques().stream()
                    .filter(inst -> inst.getConfigured().getType() instanceof BasicCommandTechnique<?, ?>)
                    .map(inst -> (BasicCommandTechnique.Data) inst.getData())
                    .map(data -> data.id)
                    .noneMatch(technique::equals)) {
                remove();
            }
        }
    }

    @Override
    public void tick() {

    }

    public UUID getTechniqueID() {
        return technique;
    }

    public boolean shouldRemove() {
        return remove;
    }

    public void remove() {
        remove = true;
    }

    public abstract CommandType getType();

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Type", getType().name());
        nbt.putInt("Ticks", ticks);
        nbt.putInt("Duration", duration);
        nbt.putBoolean("Remove", remove);
        nbt.putUUID("Technique", technique);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Ticks", Tag.TAG_INT)) {
            ticks = nbt.getInt("Ticks");
        }
        if (nbt.contains("Duration", Tag.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Remove", Tag.TAG_BYTE)) {
            remove = nbt.getBoolean("Remove");
        }
        if (nbt.contains("Technique", Tag.TAG_INT_ARRAY)) {
            technique = nbt.getUUID("Technique");
        }
    }
}
