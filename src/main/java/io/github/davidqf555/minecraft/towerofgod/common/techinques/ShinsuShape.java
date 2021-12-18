package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public enum ShinsuShape implements IShinsuTechniqueProvider {

    NONE(() -> ItemStack.EMPTY, player -> -1.0),
    SHOVEL(() -> RegistryHandler.SHINSU_SHOVEL.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Stat<Block> stat : Stats.BLOCK_MINED) {
            Block block = stat.getValue();
            if (block.getHarvestTool(block.getDefaultState()) == ToolType.SHOVEL) {
                amount += stats.getValue(stat) * 0.1;
            }
        }
        return amount;
    }, ShinsuTechnique.MANIFEST),
    PICKAXE(() -> RegistryHandler.SHINSU_PICKAXE.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Stat<Block> stat : Stats.BLOCK_MINED) {
            Block block = stat.getValue();
            if (block.getHarvestTool(block.getDefaultState()) == ToolType.PICKAXE) {
                amount += stats.getValue(stat) * 0.1;
            }
        }
        return amount;
    }, ShinsuTechnique.MANIFEST),
    AXE(() -> RegistryHandler.SHINSU_AXE.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Stat<Block> stat : Stats.BLOCK_MINED) {
            Block block = stat.getValue();
            if (block.getHarvestTool(block.getDefaultState()) == ToolType.AXE) {
                amount += stats.getValue(stat) * 0.1;
            }
        }
        return amount;
    }, ShinsuTechnique.MANIFEST),
    SWORD(() -> RegistryHandler.SHINSU_SWORD.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.CUSTOM.get(Stats.DAMAGE_DEALT)) * 0.1, ShinsuTechnique.MANIFEST),
    HOE(() -> RegistryHandler.SHINSU_HOE.get().getDefaultInstance(), player -> {
        StatisticsManager stats = player.getStats();
        double amount = 0;
        for (Block crop : BlockTags.CROPS.getAllElements()) {
            amount += stats.getValue(Stats.BLOCK_MINED.get(crop)) * 4;
        }
        return amount;
    }, ShinsuTechnique.MANIFEST),
    BOW(() -> RegistryHandler.SHINSU_BOW.get().getDefaultInstance(), player -> player.getStats().getValue(Stats.ITEM_USED.get(Items.BOW)) * 2.0, ShinsuTechnique.MANIFEST);

    private final NonNullSupplier<ItemStack> item;
    private final Function<ServerPlayerEntity, Double> suitability;
    private final ShinsuTechnique[] obtainable;

    ShinsuShape(NonNullSupplier<ItemStack> item, Function<ServerPlayerEntity, Double> suitability, ShinsuTechnique... obtainable) {
        this.item = item;
        this.suitability = suitability;
        this.obtainable = obtainable;
    }

    @Nullable
    public ShinsuTechnique getTechnique(List<Direction> combination) {
        for (ShinsuTechnique technique : obtainable) {
            if (technique.matches(combination)) {
                return technique;
            }
        }
        return null;
    }

    public ItemStack createItem() {
        return item.get();
    }

    public double getSuitability(ServerPlayerEntity player) {
        return suitability.apply(player);
    }

    @Override
    public Set<ShinsuTechnique> getTechniques() {
        return EnumSet.copyOf(Arrays.asList(obtainable));
    }
}
