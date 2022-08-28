package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ContainerRegistry {

    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<MenuType<LighthouseEntity.LighthouseContainer>> LIGHTHOUSE = register("lighthouse", new LighthouseEntity.LighthouseContainer.Factory());

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory) {
        return TYPES.register(name, () -> IForgeMenuType.create(factory));
    }
}
