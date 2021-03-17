package io.github.davidqf555.minecraft.towerofgod;

import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("towerofgod")
public class TowerOfGod {

    public static final String MOD_ID = "towerofgod";
    public static final ItemGroup TAB = new ItemGroup(MOD_ID + "tab") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.LIGHTHOUSE_ITEM.get());
        }
    };

    public TowerOfGod() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
