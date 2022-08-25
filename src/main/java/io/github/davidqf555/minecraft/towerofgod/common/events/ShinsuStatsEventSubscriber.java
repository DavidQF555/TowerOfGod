package io.github.davidqf555.minecraft.towerofgod.common.events;

import io.github.davidqf555.minecraft.towerofgod.common.ServerConfigs;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.IShinsuUser;
import io.github.davidqf555.minecraft.towerofgod.common.world.RegularTeamsSavedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
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
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && event.player.level.getGameTime() % ServerConfigs.INSTANCE.shinsuUpdatePeriod.get() == 0) {
            ShinsuStats.get(event.player).periodicTick((ServerWorld) event.player.level, ServerConfigs.INSTANCE.shinsuUpdatePeriod.get());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof IShinsuUser) {
            Entity source = event.getSource().getEntity();
            if (source instanceof IShinsuUser || source instanceof PlayerEntity) {
                ShinsuStats.get(source).onKill(source, ShinsuStats.get(entity));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world instanceof ServerWorld && event.phase == TickEvent.Phase.START && event.world.getGameTime() % 100 == 0) {
            RegularTeamsSavedData.getOrCreate((ServerWorld) event.world).update((ServerWorld) event.world);
        }
    }

}
