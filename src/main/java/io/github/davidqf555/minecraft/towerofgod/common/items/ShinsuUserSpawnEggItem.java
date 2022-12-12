package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.BasicShinsuUserEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class ShinsuUserSpawnEggItem extends ForgeSpawnEggItem {

    private final int[] levels;

    public ShinsuUserSpawnEggItem(Supplier<? extends EntityType<? extends BasicShinsuUserEntity>> type, int[] levels, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
        this.levels = levels;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(), getLevel(stack));
    }

    public int getLevel(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement(TowerOfGod.MOD_ID);
        return tag.contains("Level", Tag.TAG_INT) ? tag.getInt("Level") : 1;
    }

    public void setLevel(ItemStack stack, int level) {
        stack.getOrCreateTagElement(TowerOfGod.MOD_ID).putInt("Level", level);
    }

    public int[] getCreativeModeTabLevels() {
        return levels;
    }

}
