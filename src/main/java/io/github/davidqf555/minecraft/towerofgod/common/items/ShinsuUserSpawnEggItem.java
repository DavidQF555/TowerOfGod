package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.BasicShinsuUserEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.Constants;

import java.util.function.Supplier;

public class ShinsuUserSpawnEggItem extends ForgeSpawnEggItem {

    private final int[] levels;

    public ShinsuUserSpawnEggItem(Supplier<? extends EntityType<? extends BasicShinsuUserEntity>> type, int[] levels, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
        this.levels = levels;
    }

    @Override
    public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> stacks) {
        if (allowdedIn(tab)) {
            for (int level : levels) {
                ItemStack stack = getDefaultInstance();
                setLevel(stack, level);
                stacks.add(stack);
            }
        }
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        return new TranslationTextComponent(getDescriptionId(), getLevel(stack));
    }

    protected int getLevel(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement(TowerOfGod.MOD_ID);
        return tag.contains("Level", Constants.NBT.TAG_INT) ? tag.getInt("Level") : 1;
    }

    protected void setLevel(ItemStack stack, int level) {
        stack.getOrCreateTagElement(TowerOfGod.MOD_ID).putInt("Level", level);
    }

}
