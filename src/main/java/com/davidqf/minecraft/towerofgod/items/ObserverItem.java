package com.davidqf.minecraft.towerofgod.items;

import com.davidqf.minecraft.towerofgod.entities.ObserverEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ObserverItem extends BasicItem {

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        Vector3d spawn = playerIn.getEyePosition(1).add(playerIn.getLookVec().mul(2, 2, 2));
        ObserverEntity ob = new ObserverEntity(worldIn, playerIn);
        if(ob.canSpawn(worldIn, SpawnReason.MOB_SUMMONED)) {
            ob.setPosition(spawn.x, spawn.y, spawn.z);
            worldIn.addEntity(ob);
            ItemStack item = playerIn.getHeldItem(handIn);
            item.setCount(item.getCount() - 1);
            playerIn.setHeldItem(handIn, item);
        }
        return ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }
}
