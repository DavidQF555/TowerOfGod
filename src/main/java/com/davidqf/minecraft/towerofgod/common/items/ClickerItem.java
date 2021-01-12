package com.davidqf.minecraft.towerofgod.common.items;

import com.davidqf.minecraft.towerofgod.common.entities.ClickerEntity;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuShape;
import com.davidqf.minecraft.towerofgod.common.util.IShinsuStats;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClickerItem extends BasicItem {

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ClickerEntity entity = RegistryHandler.CLICKER_ENTITY.get().create(worldIn);
        if (entity != null) {
            Vector3d eye = playerIn.getEyePosition(1);
            Vector3d change = playerIn.getLookVec().mul(4, 4, 4);
            while (worldIn.getBlockState(new BlockPos(eye.add(change))).isSolid() && change.lengthSquared() > 0.625) {
                change = change.mul(0.9, 0.9, 0.9);
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
        double max = 0;
        List<ShinsuQuality> qualities = new ArrayList<>();
        for (ShinsuQuality quality : ShinsuQuality.values()) {
            double value = quality.getSuitability(player);
            if (value == max) {
                qualities.add(quality);
            } else if (value > max) {
                qualities.clear();
                qualities.add(quality);
                max = value;
            }
        }
        return qualities.get(player.getRNG().nextInt(qualities.size()));
    }

    private ShinsuShape getShape(ServerPlayerEntity player) {
        double max = 0;
        List<ShinsuShape> shapes = new ArrayList<>();
        for (ShinsuShape shape : ShinsuShape.values()) {
            double value = shape.getSuitability(player);
            if (value == max) {
                shapes.add(shape);
            } else if (value > max) {
                shapes.clear();
                shapes.add(shape);
                max = value;
            }
        }
        return shapes.get(player.getRNG().nextInt(shapes.size()));
    }
}
