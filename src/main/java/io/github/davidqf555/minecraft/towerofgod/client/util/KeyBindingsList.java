package io.github.davidqf555.minecraft.towerofgod.client.util;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.event.KeyEvent;

public class KeyBindingsList {

    public static final KeyBinding OPEN_WHEEL = new KeyBinding("key." + TowerOfGod.MOD_ID + ".open_shinsu_wheel", KeyEvent.VK_G, "category." + TowerOfGod.MOD_ID);

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_WHEEL);
    }
}
