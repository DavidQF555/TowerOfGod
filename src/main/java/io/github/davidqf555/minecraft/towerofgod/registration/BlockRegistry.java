package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.LevitationBlock;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.TemporaryBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TowerOfGod.MOD_ID);

    public static final RegistryObject<DropExperienceBlock> SUSPENDIUM_ORE = register("suspendium_ore", () -> new DropExperienceBlock(Block.Properties.of(Material.STONE).strength(3f, 3f).requiresCorrectToolForDrops()));
    public static final RegistryObject<LevitationBlock> SUSPENDIUM_BLOCK = register("suspendium_block", () -> new LevitationBlock(Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLUE).strength(5f, 6f).requiresCorrectToolForDrops()));
    public static final RegistryObject<TemporaryBlock> LIGHT = register("light", () -> new TemporaryBlock(Block.Properties.of(Material.AIR).noCollission().noLootTable().air().lightLevel(state -> 15)));

    private BlockRegistry() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
