package com.davidqf.minecraft.towerofgod.client.gui;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class ShinsuAdvancementProgress implements INBTSerializable<CompoundNBT> {

    private ShinsuAdvancement advancement;
    private int progress;
    private boolean complete;

    public ShinsuAdvancementProgress(ShinsuAdvancement advancement, int progress, boolean complete) {
        this.advancement = advancement;
        this.progress = progress;
        this.complete = complete;
    }

    public ShinsuAdvancement getAdvancement() {
        return advancement;
    }

    public int getProgress(){
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void complete() {
        complete = true;
    }

    public boolean isComplete(){
        return complete;
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Advancement", advancement.getName().getKey());
        nbt.putInt("Progress", progress);
        nbt.putBoolean("Complete", complete);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        advancement = ShinsuAdvancement.get(nbt.getString("Advancement"));
        progress = nbt.getInt("Progress");
        complete = nbt.getBoolean("Complete");
    }
}
