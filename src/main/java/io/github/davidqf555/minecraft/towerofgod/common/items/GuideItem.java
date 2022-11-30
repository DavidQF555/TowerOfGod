package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuideItem extends Item {

    private static final ITextComponent EMPTY = new TranslationTextComponent(Util.makeDescriptionId("item", new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_guide")) + ".empty").withStyle(TextFormatting.RED);
    private static final ITextComponent LORE = new TranslationTextComponent(Util.makeDescriptionId("item", new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_guide")) + ".lore").withStyle(TextFormatting.DARK_AQUA);
    private final int color;

    public GuideItem(int color, Properties properties) {
        super(properties.stacksTo(1));
        this.color = color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> lines, ITooltipFlag flag) {
        lines.add(LORE);
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
            } else {
                playerIn.sendMessage(EMPTY, Util.NIL_UUID);
            }
        }
        return ActionResult.sidedSuccess(item, worldIn.isClientSide());
    }

}
