package com.davidqf.towerofgodmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("towerofgodmod")
public class TowerOfGod {

	public static final String MOD_ID = "towerofgodmod";
	public static final ItemGroup TAB = new ItemGroup("towerofgodmodtab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(RegistryHandler.SUSPENDIUM.get());
		}
	};

	public TowerOfGod() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		RegistryHandler.init();

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {}

	private void doClientStuff(final FMLClientSetupEvent event) {}

}
