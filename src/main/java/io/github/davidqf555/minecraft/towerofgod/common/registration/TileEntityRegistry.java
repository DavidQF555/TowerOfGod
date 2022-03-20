package io.github.davidqf555.minecraft.towerofgod.common.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TowerOfGod.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> supplier, Supplier<Block[]> valid) {
        return TYPES.register(name, () -> TileEntityType.Builder.create(supplier, valid.get()).build(null));
    }

    public static final RegistryObject<TileEntityType<SuspendiumTileEntity>> SUSPENDIUM = register("suspendium", SuspendiumTileEntity::new, () -> new Block[]{BlockRegistry.SUSPENDIUM_BLOCK.get()});

}
