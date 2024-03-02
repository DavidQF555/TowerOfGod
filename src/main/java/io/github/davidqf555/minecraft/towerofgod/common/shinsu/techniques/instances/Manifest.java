package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.Util;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuQualityData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class Manifest extends ShinsuTechniqueType<ShinsuTechniqueConfig, Manifest.Data> {

    public Manifest() {
        super(ShinsuTechniqueConfig.CODEC, Data.CODEC);
    }

    @Override
    public void tick(LivingEntity user, ShinsuTechniqueInstance<ShinsuTechniqueConfig, Data> inst) {
        boolean contains = false;
        IItemHandler inventory = user.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        UUID id = inst.getData().id;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            CompoundTag tag = slot.getTagElement(TowerOfGod.MOD_ID);
            if (!slot.isEmpty() && tag != null && tag.contains("Technique", Tag.TAG_INT_ARRAY) && id.equals(tag.getUUID("Technique"))) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            inst.remove(user);
        }
        super.tick(user, inst);
    }

    @Nullable
    @Override
    public Data onUse(LivingEntity user, ShinsuTechniqueConfig config, @Nullable LivingEntity target) {
        ShinsuQualityData quality = ShinsuQualityData.get(user);
        ShinsuShape shape = quality.getShape();
        if (shape == null) {
            return null;
        }
        UUID id = Mth.createInsecureUUID();
        ItemStack item = shape.getItem();
        item.getOrCreateTagElement(TowerOfGod.MOD_ID).putUUID("Technique", id);
        ShinsuAttribute.setAttribute(item, quality.getAttribute());
        IItemHandler inventory = user.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseGet(ItemStackHandler::new);
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.isItemValid(i, item)) {
                item = inventory.insertItem(i, item, false);
                if (item.isEmpty()) {
                    break;
                }
            }
        }
        return new Data(id);
    }

    @Override
    public IRequirement[] getRequirements() {
        return new IRequirement[0];
    }

    public static class Data {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Util.UUID_CODEC.fieldOf("id").forGetter(data -> data.id)
        ).apply(inst, Data::new));
        public final UUID id;

        public Data(UUID id) {
            this.id = id;
        }

    }

}
