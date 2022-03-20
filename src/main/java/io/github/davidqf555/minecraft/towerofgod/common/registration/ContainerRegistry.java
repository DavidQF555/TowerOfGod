package io.github.davidqf555.minecraft.towerofgod.common.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContainerRegistry {

    public static final DeferredRegister<ContainerType<?>> TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<ContainerType<LighthouseEntity.LighthouseContainer>> LIGHTHOUSE = register("lighthouse", new LighthouseEntity.LighthouseContainer.Factory());

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String name, IContainerFactory<T> factory) {
        return TYPES.register(name, () -> IForgeContainerType.create(factory));
    }
}
