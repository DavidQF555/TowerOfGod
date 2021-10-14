package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
public class DeviceDyeRecipe extends SpecialRecipe {

    private static final List<Item> COLORABLE = RegistryHandler.COLORED_DEVICE_ITEMS.stream().map(RegistryObject::get).collect(Collectors.toList());

    public DeviceDyeRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, @Nonnull World worldIn) {
        ItemStack device = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (!item.isEmpty()) {
                if (device.isEmpty() && COLORABLE.contains(item.getItem())) {
                    device = item;
                } else if (dye.isEmpty() && item.getItem() instanceof DyeItem) {
                    dye = item;
                } else {
                    return false;
                }
            }
        }
        return !device.isEmpty() && !dye.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack device = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (!item.isEmpty()) {
                if (device.isEmpty() && COLORABLE.contains(item.getItem())) {
                    device = item;
                } else if (dye.isEmpty() && item.getItem() instanceof DyeItem) {
                    dye = item;
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }
        if (!device.isEmpty() && !dye.isEmpty()) {
            ItemStack result = device.copy();
            result.setCount(1);
            result.getOrCreateChildTag(TowerOfGod.MOD_ID).putInt("Color", ((DyeItem) dye.getItem()).getDyeColor().getId());
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RegistryHandler.DEVICE_DYE_RECIPE.get();
    }
}
