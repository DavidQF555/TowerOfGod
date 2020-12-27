package com.davidqf.minecraft.towerofgod.common.util;

import com.davidqf.minecraft.towerofgod.TowerOfGod;

import com.davidqf.minecraft.towerofgod.client.util.IPlayerShinsuEquips;
import com.davidqf.minecraft.towerofgod.common.packets.*;
import com.davidqf.minecraft.towerofgod.common.entities.ShinsuUserEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    private static final ResourceLocation SHINSU_STATS = new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_stats");
    private static final ResourceLocation PLAYER_EQUIPS = new ResourceLocation(TowerOfGod.MOD_ID, "player_equips");
    private static int index = 0;

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof ShinsuUserEntity || entity instanceof PlayerEntity) {
            event.addCapability(SHINSU_STATS, new IShinsuStats.Provider());
        }
        if (entity instanceof PlayerEntity) {
            event.addCapability(PLAYER_EQUIPS, new IPlayerShinsuEquips.Provider());
        }
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModBus {
        @SubscribeEvent
        public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
            CapabilityManager.INSTANCE.register(IShinsuStats.class, new IShinsuStats.Storage(), () -> IShinsuStats.Type.ADVANCEMENT.getSupplier().get());
            CapabilityManager.INSTANCE.register(IPlayerShinsuEquips.class, new IPlayerShinsuEquips.Storage(), new IPlayerShinsuEquips.PlayerShinsuEquips.Factory());
            ShinsuStatsSyncMessage.register(index++);
            PlayerEquipsSyncMessage.register(index++);
            ShinsuTechniqueMessage.register(index++);
            ShinsuCriteriaCompletionMessage.register(index++);
        }
    }
}
