package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, TowerOfGod.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block[]> valid) {
        return TYPES.register(name, () -> BlockEntityType.Builder.of(supplier, valid.get()).build(null));
    }

    public static final RegistryObject<BlockEntityType<SuspendiumTileEntity>> SUSPENDIUM = register("suspendium", SuspendiumTileEntity::new, () -> new Block[]{BlockRegistry.SUSPENDIUM_BLOCK.get()});

}
