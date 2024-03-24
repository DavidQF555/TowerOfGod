package io.github.davidqf555.minecraft.towerofgod.common.items;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.player.PlayerTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.packets.OpenGuideScreenPacket;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GuideItem extends Item {

    private static final Component EMPTY = new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_guide")) + ".empty").withStyle(ChatFormatting.RED);
    private static final Component LORE = new TranslatableComponent(Util.makeDescriptionId("item", new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_guide")) + ".lore").withStyle(ChatFormatting.DARK_AQUA);
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
            Registry<ConfiguredShinsuTechniqueType<?, ?>> registry = ConfiguredTechniqueTypeRegistry.getRegistry(playerIn.getServer().registryAccess());
            ConfiguredShinsuTechniqueType<?, ?>[] pages = PlayerTechniqueData.get(playerIn).getUnlocked().stream().map(registry::getOrThrow).toArray(ConfiguredShinsuTechniqueType[]::new);
            if (pages.length > 0) {
                Arrays.sort(pages, Comparator.comparing(technique -> technique.getConfig().getDisplay().name()));
                ResourceKey<ConfiguredShinsuTechniqueType<?, ?>>[] keys = Arrays.stream(pages)
                        .map(registry::getKey)
                        .map(loc -> ResourceKey.create(ConfiguredTechniqueTypeRegistry.REGISTRY, loc))
                        .toArray(ResourceKey[]::new);
                TowerOfGod.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new OpenGuideScreenPacket(keys, color));
                playerIn.awardStat(Stats.ITEM_USED.get(this));
            } else {
                playerIn.sendMessage(EMPTY, Util.NIL_UUID);
            }
        }
        return InteractionResultHolder.sidedSuccess(item, worldIn.isClientSide());
    }

}
