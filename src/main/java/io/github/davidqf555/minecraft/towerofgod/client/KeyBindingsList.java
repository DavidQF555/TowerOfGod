package io.github.davidqf555.minecraft.towerofgod.client;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.client.KeyMapping;

import java.awt.event.KeyEvent;

public final class KeyBindingsList {

    public static final KeyMapping SHINSU_TECHNIQUE_GUI = new KeyMapping("key." + TowerOfGod.MOD_ID + ".shinsu_technique_gui", KeyEvent.VK_G, "category." + TowerOfGod.MOD_ID);

    private KeyBindingsList() {
    }

}
