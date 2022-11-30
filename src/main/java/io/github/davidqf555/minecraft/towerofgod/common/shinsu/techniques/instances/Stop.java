package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Stop extends ShinsuTechniqueInstance {

    private ShinsuTechnique target;

    public Stop(Entity user, ShinsuTechnique target) {
        super(user);
        this.target = target;
    }

    @Override
    public void onUse(ServerLevel world) {
        for (ShinsuTechniqueInstance inst : ShinsuTechniqueData.get(getUser(world)).getTechniques()) {
            if (inst.getTechnique().equals(target)) {
                inst.remove(world);
            }
        }
        super.onUse(world);
    }

    @Override
    public ShinsuTechnique getTechnique() {
        return ShinsuTechniqueRegistry.STOP.get();
    }

    @Override
    public int getShinsuUse() {
        return 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        if (target != null) {
            tag.putString("Stop", target.getRegistryName().toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Stop", Tag.TAG_STRING)) {
            target = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Stop")));
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<Stop> {

        @Override
        public Either<Stop, Component> create(Entity user, @Nullable Entity target, Vec3 dir) {
            return Either.right(TextComponent.EMPTY);
        }

        @Override
        public Stop blankCreate() {
            return new Stop(null, null);
        }

    }
}
