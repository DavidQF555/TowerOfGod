package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TowerOfGod.MOD_ID);

    public static final RegistryObject<OreBlock> SUSPENDIUM_ORE = register("suspendium_ore", () -> new OreBlock(Block.Properties.of(Material.STONE).strength(5f, 5f).sound(SoundType.STONE).harvestLevel(1).harvestTool(ToolType.PICKAXE)));
    public static final RegistryObject<SuspendiumBlock> SUSPENDIUM_BLOCK = register("suspendium_block", SuspendiumBlock::new);
    public static final RegistryObject<Block> LIGHT = register("light", () -> new Block(Block.Properties.of(Material.AIR).air().lightLevel(state -> 15)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
