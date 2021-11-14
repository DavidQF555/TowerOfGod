package io.github.davidqf555.minecraft.towerofgod.client;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.event.KeyEvent;

public final class KeyBindingsList {

    public static final KeyBinding SHINSU_TECHNIQUE_GUI = new KeyBinding("key." + TowerOfGod.MOD_ID + ".shinsu_technique_gui", KeyEvent.VK_G, "category." + TowerOfGod.MOD_ID);

    public static void register() {
        ClientRegistry.registerKeyBinding(SHINSU_TECHNIQUE_GUI);
    }
}
