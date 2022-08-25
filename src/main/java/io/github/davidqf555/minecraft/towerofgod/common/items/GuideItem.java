package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
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
import java.util.function.Supplier;

public class GuideItem extends Item {

    private final ITextComponent author;
    private final Supplier<ShinsuTechnique[]> pages;
    private final int color;

    public GuideItem(Supplier<ShinsuTechnique[]> pages, ITextComponent author, int color) {
        super(new Properties()
                .tab(TowerOfGod.TAB)
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
        );
        this.pages = pages;
        this.author = author;
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("book.byAuthor", author).withStyle(TextFormatting.GRAY));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayerEntity) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerIn), new OpenGuideScreenPacket(pages.get(), color));
        }
        playerIn.awardStat(Stats.ITEM_USED.get(this));
        return ActionResult.sidedSuccess(item, worldIn.isClientSide());
    }
}
