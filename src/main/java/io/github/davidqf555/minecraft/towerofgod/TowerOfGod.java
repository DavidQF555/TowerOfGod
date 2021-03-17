package io.github.davidqf555.minecraft.towerofgod;

import io.github.davidqf555.minecraft.towerofgod.client.util.KeyBindingsList;
import io.github.davidqf555.minecraft.towerofgod.common.entities.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ObserverEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.RegularEntity;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        KeyBindingsList.register();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Deprecated
    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(RegistryHandler.LIGHTHOUSE_ENTITY.get(), LighthouseEntity.setAttributes().create());
            GlobalEntityTypeAttributes.put(RegistryHandler.OBSERVER_ENTITY.get(), ObserverEntity.setAttributes().create());
            GlobalEntityTypeAttributes.put(RegistryHandler.REGULAR_ENTITY.get(), RegularEntity.setAttributes().create());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }

}
