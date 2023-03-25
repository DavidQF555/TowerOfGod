package io.github.davidqf555.minecraft.towerofgod.common.items;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.RecipeRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;

@MethodsReturnNonnullByDefault
public class DeviceDyeRecipe extends CustomRecipe {

    private static final List<? extends Item> COLORABLE = ItemRegistry.COLORED_DEVICE_ITEMS.stream().map(RegistryObject::get).toList();

    public DeviceDyeRecipe(ResourceLocation idIn, CraftingBookCategory category) {
        super(idIn, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, @Nonnull Level worldIn) {
        ItemStack device = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack item = inv.getItem(i);
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack device = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack item = inv.getItem(i);
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
            result.getOrCreateTagElement(TowerOfGod.MOD_ID).putInt("Color", ((DyeItem) dye.getItem()).getDyeColor().getId());
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DEVICE_DYE.get();
    }
}
