package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class GuideItem extends Item {

    private static final Component EMPTY = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_guide")) + ".empty").withStyle(ChatFormatting.RED);
    private static final Component LORE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_guide")) + ".lore").withStyle(ChatFormatting.DARK_AQUA);
    private final int color;

    public GuideItem(int color, Properties properties) {
        super(properties.stacksTo(1));
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> lines, TooltipFlag flag) {
        lines.add(LORE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack item = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayer) {
            ShinsuTechnique[] pages = PlayerTechniqueData.get(playerIn).getUnlocked().toArray(new ShinsuTechnique[0]);
            if (pages.length > 0) {
                TowerOfGod.CHANNEL.send(new OpenGuideScreenPacket(pages, color), PacketDistributor.PLAYER.with((ServerPlayer) playerIn));
                playerIn.awardStat(Stats.ITEM_USED.get(this));
            } else {
                playerIn.sendSystemMessage(EMPTY);
            }
        }
        return InteractionResultHolder.sidedSuccess(item, worldIn.isClientSide());
    }

}
