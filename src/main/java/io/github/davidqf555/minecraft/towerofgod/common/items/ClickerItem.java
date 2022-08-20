package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ClickerEntity;
import io.github.davidqf555.minecraft.towerofgod.common.packets.UpdateClientQualityPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.quality.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.EntityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuQualityRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClickerItem extends Item {

    public ClickerItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ClickerEntity entity = EntityRegistry.CLICKER.get().create(worldIn);
        if (entity != null) {
            Vector3d eye = playerIn.getEyePosition(1);
            BlockRayTraceResult result = worldIn.rayTraceBlocks(new RayTraceContext(eye, eye.add(playerIn.getLookVec().scale(4)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
            Vector3d spawn = result.getHitVec();
            entity.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
            ItemStack item = playerIn.getHeldItem(handIn);
            if (!playerIn.isCreative()) {
                item.setCount(item.getCount() - 1);
            }
            if (playerIn instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                ShinsuQuality quality = getQuality(serverPlayer);
                ShinsuShape shape = getShape(serverPlayer);
                ShinsuStats stats = ShinsuStats.get(serverPlayer);
                stats.setQuality(quality);
                stats.setShape(shape);
                entity.setQuality(quality);
                entity.setShape(shape);
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                serverPlayer.addStat(Stats.ITEM_USED.get(this));
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new UpdateClientQualityPacket(quality));
            }
            worldIn.addEntity(entity);
            return ActionResult.func_233538_a_(item, playerIn.world.isRemote());
        }
        return ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }

    private ShinsuQuality getQuality(ServerPlayerEntity player) {
        double total = 0;
        Map<ShinsuQuality, Double> suitabilities = new HashMap<>();
        for (ShinsuQuality quality : ShinsuQualityRegistry.getRegistry()) {
            double suitability = quality.getSuitability(player);
            if (suitability > 0) {
                total += suitability;
                suitabilities.put(quality, suitability);
            }
        }
        double current = 0;
        double random = player.getRNG().nextDouble() * total;
        for (ShinsuQuality quality : suitabilities.keySet()) {
            current += suitabilities.get(quality);
            if (random < current) {
                return quality;
            }
        }
        List<ShinsuQuality> all = new ArrayList<>(ShinsuQualityRegistry.getRegistry().getValues());
        return all.get(player.getRNG().nextInt(all.size()));
    }

    private ShinsuShape getShape(ServerPlayerEntity player) {
        double total = 0;
        Map<ShinsuShape, Double> suitabilities = new HashMap<>();
        for (ShinsuShape shape : ShinsuShapeRegistry.getRegistry()) {
            double suitability = shape.getSuitability(player);
            if (suitability > 0) {
                total += suitability;
                suitabilities.put(shape, suitability);
            }
        }
        double current = 0;
        double random = player.getRNG().nextDouble() * total;
        for (ShinsuShape shape : suitabilities.keySet()) {
            current += suitabilities.get(shape);
            if (random < current) {
                return shape;
            }
        }
        ShinsuShape[] all = ShinsuShapeRegistry.getRegistry().getValues().toArray(new ShinsuShape[0]);
        return all[player.getRNG().nextInt(all.length)];
    }
}
