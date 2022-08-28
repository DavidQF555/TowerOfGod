package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TowerOfGod.MOD_ID);

    public static final RegistryObject<OreBlock> SUSPENDIUM_ORE = register("suspendium_ore", () -> new OreBlock(Block.Properties.of(Material.STONE).strength(5f, 5f).requiresCorrectToolForDrops()));
    public static final RegistryObject<SuspendiumBlock> SUSPENDIUM_BLOCK = register("suspendium_block", SuspendiumBlock::new);
    public static final RegistryObject<Block> LIGHT = register("light", () -> new Block(Block.Properties.of(Material.AIR).air().lightLevel(state -> 15)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
