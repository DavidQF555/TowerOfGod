package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ShinsuStatsEventSubscriber {

    private ShinsuStatsEventSubscriber() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START) {
            ShinsuStats.get(event.player).tick((ServerLevel) event.player.level);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof IShinsuUser) {
            Entity source = event.getSource().getEntity();
            if (source instanceof IShinsuUser || source instanceof Player) {
                ShinsuStats.get(source).onKill(source, ShinsuStats.get(entity));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world instanceof ServerLevel && event.phase == TickEvent.Phase.START && event.world.getGameTime() % 100 == 0) {
            RegularTeamsSavedData.getOrCreate((ServerLevel) event.world).update((ServerLevel) event.world);
        }
    }

}
