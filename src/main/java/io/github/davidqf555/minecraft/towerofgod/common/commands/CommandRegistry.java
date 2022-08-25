package io.github.davidqf555.minecraft.towerofgod.common.commands;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommandRegistry {

    private CommandRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ShinsuCommand.register(event.getDispatcher());
    }
}
