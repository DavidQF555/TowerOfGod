package io.github.davidqf555.minecraft.towerofgod.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod("towerofgod")
public class TowerOfGod {

    public static final String MOD_ID = "towerofgod";
    public static final ItemGroup TAB = new ItemGroup(MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return RegistryHandler.SUSPENDIUM.get().getDefaultInstance();
        }
    };
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TowerOfGod.MOD_ID, TowerOfGod.MOD_ID),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public TowerOfGod() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
