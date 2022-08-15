package io.github.davidqf555.minecraft.towerofgod.common.entities.devices;

import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public abstract class DeviceCommand extends Goal implements INBTSerializable<CompoundNBT> {

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
    public boolean shouldExecute() {
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
            if (owner == null || ShinsuTechniqueInstance.get(owner, technique) == null) {
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Type", getType().name());
        nbt.putInt("Ticks", ticks);
        nbt.putInt("Duration", duration);
        nbt.putBoolean("Remove", remove);
        nbt.putUniqueId("Technique", technique);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Ticks", Constants.NBT.TAG_INT)) {
            ticks = nbt.getInt("Ticks");
        }
        if (nbt.contains("Duration", Constants.NBT.TAG_INT)) {
            duration = nbt.getInt("Duration");
        }
        if (nbt.contains("Remove", Constants.NBT.TAG_BYTE)) {
            remove = nbt.getBoolean("Remove");
        }
        if (nbt.contains("Technique", Constants.NBT.TAG_INT_ARRAY)) {
            technique = nbt.getUniqueId("Technique");
        }
    }
}
