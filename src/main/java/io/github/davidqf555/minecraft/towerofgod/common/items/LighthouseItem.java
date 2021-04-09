package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.IShinsuStats;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class LighthouseItem extends Item {

    public LighthouseItem() {
        super(new Properties().group(TowerOfGod.TAB));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        if (ShinsuTechnique.USE_LIGHTHOUSE.getBuilder().canCast(ShinsuTechnique.USE_LIGHTHOUSE, playerIn, 1, null, playerIn.getLookVec())) {
            IShinsuStats stats = IShinsuStats.get(playerIn);
            stats.cast(playerIn, ShinsuTechnique.USE_LIGHTHOUSE, 1, null, playerIn.getLookVec());
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
