package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.util.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Function;

public enum ShinsuShape {

    NONE(() -> ItemStack.EMPTY, player -> -1.0),
    SHOVEL(() -> RegistryHandler.SHINSU_SHOVEL.get().getDefaultInstance(), player -> {
        double amount = 0;
        StatisticsManager stats = player.getStats();
        for (Block block : Tags.Blocks.DIRT.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        for (Block block : Tags.Blocks.SAND.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        for (Block block : Tags.Blocks.GRAVEL.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        return amount / 8;
    }),
    PICKAXE(() -> RegistryHandler.SHINSU_PICKAXE.get().getDefaultInstance(), player -> {
        double amount = 0;
        StatisticsManager stats = player.getStats();
        for (Block block : Tags.Blocks.STONE.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        for (Block block : Tags.Blocks.ORES.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        for (Block block : Tags.Blocks.NETHERRACK.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        return amount / 8;
    }),
    AXE(() -> RegistryHandler.SHINSU_AXE.get().getDefaultInstance(), player -> {
        double amount = 0;
        StatisticsManager stats = player.getStats();
        for (Block block : Tags.Blocks.FENCES_WOODEN.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        for (Block block : Tags.Blocks.FENCE_GATES_WOODEN.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        for (Block block : Tags.Blocks.CHESTS_WOODEN.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED, block);
        }
        return (amount + stats.getValue(Stats.CUSTOM, Stats.PLAYER_KILLS)) * 2;
    }),
    SWORD(() -> RegistryHandler.SHINSU_SWORD.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.CUSTOM, Stats.MOB_KILLS) * 1.0),
    HOE(() -> RegistryHandler.SHINSU_HOE.get().getDefaultInstance(), player -> {
        double amount = 0;
        StatisticsManager stats = player.getStats();
        for (Item item : Tags.Items.CROPS.getAllElements()) {
            amount += stats.getValue(Stats.ITEM_USED, item);
        }
        return amount;
    }),
    BOW(() -> RegistryHandler.SHINSU_BOW.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.ITEM_USED, Items.BOW) * 1.0);

    private final NonNullSupplier<ItemStack> item;
    private final Function<ServerPlayerEntity, Double> suitability;

    ShinsuShape(NonNullSupplier<ItemStack> item, Function<ServerPlayerEntity, Double> suitability) {
        this.item = item;
        this.suitability = suitability;
    }

    public static ShinsuShape get(String name) {
        for (ShinsuShape shape : values()) {
            if (shape.name().equals(name)) {
                return shape;
            }
        }
        return NONE;
    }

    public ItemStack createItem() {
        return item.get();
    }

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.apply(player);
    }

}
