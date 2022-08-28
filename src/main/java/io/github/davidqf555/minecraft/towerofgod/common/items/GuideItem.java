package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class GuideItem extends Item {

    private final Supplier<ShinsuTechnique[]> pages;
    private final int color;

    public GuideItem(Supplier<ShinsuTechnique[]> pages, int color, Properties properties) {
        super(properties.stacksTo(1));
        this.pages = pages;
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("book.byAuthor", getAuthor()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayer) {
            TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new OpenGuideScreenPacket(pages.get(), color));
        }
        playerIn.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(item, worldIn.isClientSide());
    }

    protected Component getAuthor() {
        return new TranslatableComponent(getDescriptionId() + ".author");
    }

}
