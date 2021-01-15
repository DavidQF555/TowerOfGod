package com.davidqf.minecraft.towerofgod.common.items;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.entities.FlyingDevice;
import com.davidqf.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.SpawnReason;
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

import javax.annotation.Nonnull;

public class ObserverItem extends Item {

    public ObserverItem() {
        super(new Properties().group(TowerOfGod.TAB));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        Vector3d eye = playerIn.getEyePosition(1);
        Vector3d change = playerIn.getLookVec().mul(4, 4, 4);
        while (worldIn.getBlockState(new BlockPos(eye.add(change))).isSolid() && change.lengthSquared() > 0.625) {
            change = change.mul(0.9, 0.9, 0.9);
        }
        FlyingDevice dev = RegistryHandler.OBSERVER_ENTITY.get().create(worldIn);
        if (dev != null && dev.canSpawn(worldIn, SpawnReason.MOB_SUMMONED)) {
            Vector3d spawn = eye.add(change);
            dev.setPosition(spawn.x, spawn.y, spawn.z);
            dev.setOwner(playerIn);
            worldIn.addEntity(dev);
            ItemStack item = playerIn.getHeldItem(handIn);
            item.setCount(item.getCount() - 1);
            if (playerIn instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
                serverPlayer.addStat(Stats.ITEM_USED.get(this));
            }
            return ActionResult.func_233538_a_(item, playerIn.world.isRemote());
        }
        return ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }
}
