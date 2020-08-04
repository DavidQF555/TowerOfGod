package com.davidqf.minecraft.towerofgod;

import com.davidqf.minecraft.towerofgod.entities.ObserverEntity;
import com.davidqf.minecraft.towerofgod.util.RegistryHandler;
import com.davidqf.minecraft.towerofgod.entities.LighthouseEntity;

import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("deprecation")
@Mod("towerofgod")
public class TowerOfGod {

    public static final String MOD_ID = "towerofgod";
    public static final ItemGroup TAB = new ItemGroup("towerofgodtab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.LIGHTHOUSE_ITEM.get());
        }
    };

    public TowerOfGod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseEntity.setAttributes().func_233813_a_());
            GlobalEntityTypeAttributes.put(RegistryHandler.OBSERVER_ENTITY.get(), ObserverEntity.setAttributes().func_233813_a_());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

}
