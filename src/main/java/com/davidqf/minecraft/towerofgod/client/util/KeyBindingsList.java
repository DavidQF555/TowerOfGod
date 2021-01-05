package com.davidqf.minecraft.towerofgod.client.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingsList {

    public static final KeyBinding OPEN_WHEEL = new KeyBinding("key." + TowerOfGod.MOD_ID + ".openshinsuwheel", 71, "key." + TowerOfGod.MOD_ID + ".categpry");

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_WHEEL);
    }
}
