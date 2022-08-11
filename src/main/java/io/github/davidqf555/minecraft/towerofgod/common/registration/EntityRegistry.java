package io.github.davidqf555.minecraft.towerofgod.common.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.*;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TowerOfGod.MOD_ID);

    public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE = register("lighthouse", new LighthouseEntity.Factory(), EntityClassification.MISC, 0.9f, 0.9f);
    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER = register("observer", new ObserverEntity.Factory(), EntityClassification.MISC, 0.4f, 0.4f);
    public static final RegistryObject<EntityType<RegularEntity>> REGULAR = register("regular", new RegularEntity.Factory(), EntityClassification.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<ShinsuEntity>> SHINSU = register("shinsu", new ShinsuEntity.Factory(), EntityClassification.MISC, 1, 1);
    public static final RegistryObject<EntityType<ClickerEntity>> CLICKER = register("clicker", new ClickerEntity.Factory(), EntityClassification.MISC, 1, 1);
    public static final RegistryObject<EntityType<ShinsuArrowEntity>> SHINSU_ARROW = register("shinsu_arrow", new ShinsuArrowEntity.Factory(), EntityClassification.MISC, 0.4f, 0.4f);
    public static final RegistryObject<EntityType<RankerEntity>> RANKER = register("ranker", new RankerEntity.Factory(), EntityClassification.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = register("spear", new SpearEntity.Factory(), EntityClassification.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<DirectionalLightningBoltEntity>> DIRECTIONAL_LIGHTNING = register("directional_lightning", new DirectionalLightningBoltEntity.Factory(), EntityClassification.MISC, 1, 1);

    private EntityRegistry() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.IFactory<T> factory, EntityClassification classification, float width, float height) {
        return register(name, () -> EntityType.Builder.create(factory, classification).size(width, height).build(name));
    }

    private static <T extends EntityType<?>> RegistryObject<T> register(String name, Supplier<T> entity) {
        return TYPES.register(name, entity);
    }
}
