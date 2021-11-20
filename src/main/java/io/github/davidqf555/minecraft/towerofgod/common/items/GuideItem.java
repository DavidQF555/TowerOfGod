package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class GuideItem extends Item {

    private final ITextComponent author;
    private final ShinsuTechniqueType type;

    public GuideItem(ShinsuTechniqueType type, ITextComponent author) {
        super(new Properties()
                .group(TowerOfGod.TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC)
        );
        this.type = type;
        this.author = author;
    }

    public ShinsuTechniqueType getType() {
        return type;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("book.byAuthor", author).mergeStyle(TextFormatting.GRAY));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        if (playerIn instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new OpenGuideScreenPacket(getType()));
        }
        playerIn.addStat(Stats.ITEM_USED.get(this));
        return ActionResult.func_233538_a_(item, worldIn.isRemote());
    }
}
