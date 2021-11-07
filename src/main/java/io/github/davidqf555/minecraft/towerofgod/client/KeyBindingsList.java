package io.github.davidqf555.minecraft.towerofgod.client;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.event.KeyEvent;

public final class KeyBindingsList {

    public static final KeyBinding OPEN_SHINSU_TECHNIQUES_GUI = new KeyBinding("key." + TowerOfGod.MOD_ID + ".open_shinsu_techniques_gui", KeyEvent.VK_G, "category." + TowerOfGod.MOD_ID);

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_SHINSU_TECHNIQUES_GUI);
    }
}
