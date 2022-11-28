package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Arrays;
import java.util.Comparator;

public class GuideItem extends Item {

    private final int color;

    public GuideItem(int color, Properties properties) {
        super(properties.stacksTo(1));
        this.color = color;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayerEntity) {
            ShinsuTechnique[] pages = PlayerTechniqueData.get(playerIn).getUnlocked().toArray(new ShinsuTechnique[0]);
            if (pages.length > 0) {
                Arrays.sort(pages, Comparator.comparing(technique -> technique.getText().getKey()));
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new OpenGuideScreenPacket(pages, color));
                playerIn.awardStat(Stats.ITEM_USED.get(this));
            }
        }
        return ActionResult.sidedSuccess(item, worldIn.isClientSide());
    }

}
