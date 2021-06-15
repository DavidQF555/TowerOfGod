package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.entities.ClickerEntity;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuQuality;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClickerItem extends Item {

    public ClickerItem() {
        super(new Properties().group(TowerOfGod.TAB));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ClickerEntity entity = RegistryHandler.CLICKER_ENTITY.get().create(worldIn);
        if (entity != null) {
            Vector3d eye = playerIn.getEyePosition(1);
            Vector3d change = playerIn.getLookVec().scale(4);
            while (worldIn.getBlockState(new BlockPos(eye.add(change))).isSolid() && change.lengthSquared() > 0.625) {
                change = change.scale(0.9);
            }
            Vector3d spawn = eye.add(change);
            entity.setPosition(spawn.x, spawn.y, spawn.z);
            worldIn.addEntity(entity);
            ItemStack item = playerIn.getHeldItem(handIn);
            item.setCount(item.getCount() - 1);
            if (playerIn instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                ShinsuQuality quality = getQuality(serverPlayer);
                ShinsuShape shape = getShape(serverPlayer);
                IShinsuStats stats = IShinsuStats.get(serverPlayer);
                stats.setQuality(quality);
                stats.setShape(shape);
                entity.setQuality(quality);
                entity.setShape(shape);
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                serverPlayer.addStat(Stats.ITEM_USED.get(this));
            }
            return ActionResult.func_233538_a_(item, playerIn.world.isRemote());
        }
        return ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }

    private ShinsuQuality getQuality(ServerPlayerEntity player) {
        double total = 0;
        Map<ShinsuQuality, Double> suitabilities = new EnumMap<>(ShinsuQuality.class);
        for (ShinsuQuality quality : ShinsuQuality.values()) {
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
        ShinsuQuality[] all = ShinsuQuality.values();
        return all[player.getRNG().nextInt(all.length)];
    }

    private ShinsuShape getShape(ServerPlayerEntity player) {
        double total = 0;
        Map<ShinsuShape, Double> suitabilities = new EnumMap<>(ShinsuShape.class);
        for (ShinsuShape shape : ShinsuShape.values()) {
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
        ShinsuShape[] all = ShinsuShape.values();
        return all[player.getRNG().nextInt(all.length)];
    }
}
