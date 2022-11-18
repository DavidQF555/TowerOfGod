package io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity;

import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Messages;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShinsuTechniqueData<T extends Entity> implements INBTSerializable<CompoundNBT> {

    public static final double CAST_TARGET_RANGE = 32;
    private final List<ShinsuTechniqueInstance> technique = new ArrayList<>();

    public static <T extends Entity> ShinsuTechniqueData<T> get(T entity) {
        if (entity instanceof PlayerEntity) {
            return (ShinsuTechniqueData<T>) PlayerTechniqueData.get((PlayerEntity) entity);
        } else if (entity instanceof MobEntity) {
            return (ShinsuTechniqueData<T>) MobTechniqueData.get((MobEntity) entity);
        }
        return new ShinsuTechniqueData<>();
    }

    public Optional<ITextComponent> getCastError(T user, ShinsuTechniqueInstance instance) {
        int netShinsuUse = instance.getTechnique().getNetShinsuUse(user, instance);
        int netBaangsUse = instance.getTechnique().getNetBaangsUse(user, instance);
        if (ShinsuStats.getBaangs(user) < netBaangsUse) {
            return Optional.of(Messages.getRequiresBaangs(netBaangsUse));
        } else if (ShinsuStats.getShinsu(user) < netShinsuUse) {
            return Optional.of(Messages.getRequiresShinsu(netShinsuUse));
        }
        return Optional.empty();
    }

    public void onCast(T user, ShinsuTechniqueInstance instance) {
    }

    public List<ShinsuTechniqueInstance> getTechniques() {
        return technique;
    }

    public void addTechnique(ShinsuTechniqueInstance instance) {
        technique.add(instance);
    }

    public void removeTechnique(ShinsuTechniqueInstance instance) {
        technique.remove(instance);
    }

    public int getShinsuUsage() {
        int shinsu = 0;
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            shinsu += technique.getShinsuUse();
        }
        return shinsu;
    }

    public int getBaangsUsage() {
        int baangs = 0;
        for (ShinsuTechniqueInstance technique : getTechniques()) {
            baangs += technique.getBaangsUse();
        }
        return baangs;
    }

    public void tick(ServerWorld world) {
        List<ShinsuTechniqueInstance> techniques = new ArrayList<>(getTechniques());
        for (ShinsuTechniqueInstance technique : techniques) {
            technique.tick(world);
            if (technique.getTicks() >= technique.getDuration()) {
                technique.remove(world);
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        ListNBT instances = new ListNBT();
        for (ShinsuTechniqueInstance instance : getTechniques()) {
            instances.add(instance.serializeNBT());
        }
        tag.put("Techniques", instances);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Techniques", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("Techniques", Constants.NBT.TAG_COMPOUND);
            for (INBT data : list) {
                ShinsuTechnique type = ShinsuTechniqueRegistry.getRegistry().getValue(new ResourceLocation(((CompoundNBT) data).getString("Technique")));
                ShinsuTechniqueInstance tech = type.getFactory().blankCreate();
                tech.deserializeNBT((CompoundNBT) data);
                addTechnique(tech);
            }
        }
    }

}
