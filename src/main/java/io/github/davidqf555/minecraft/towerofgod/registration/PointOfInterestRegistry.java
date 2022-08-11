package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.block.BlockState;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public final class PointOfInterestRegistry {

    public static final DeferredRegister<PointOfInterestType> TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, TowerOfGod.MOD_ID);

    public static final RegistryObject<PointOfInterestType> FLOOR_TELEPORTATION_TERMINAL = register("floor_teleportation_terminal", () -> new HashSet<>(BlockRegistry.FLOOR_TELEPORTATION_TERMINAL.get().getStateContainer().getValidStates()), 0, 1);

    private PointOfInterestRegistry() {
    }

    private static RegistryObject<PointOfInterestType> register(String name, Supplier<Set<BlockState>> states, int max, int range) {
        return TYPES.register(name, () -> new PointOfInterestType(name, states.get(), max, range));
    }
}
