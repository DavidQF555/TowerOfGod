package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Either;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class Stop extends ShinsuTechniqueInstance {

    private ShinsuTechnique target;

    public Stop(Entity user, ShinsuTechnique target) {
        super(user);
        this.target = target;
    }

    @Override
    public void onUse(ServerWorld world) {
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        if (target != null) {
            tag.putString("Stop", target.getRegistryName().toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Stop", Constants.NBT.TAG_STRING)) {
            target = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(nbt.getString("Stop")));
        }
    }

    public static class Factory implements ShinsuTechnique.IFactory<Stop> {

        @Override
        public Either<Stop, ITextComponent> create(Entity user, @Nullable Entity target, Vector3d dir) {
            return Either.right(StringTextComponent.EMPTY);
        }

        @Override
        public Stop blankCreate() {
            return new Stop(null, null);
        }

    }
}
