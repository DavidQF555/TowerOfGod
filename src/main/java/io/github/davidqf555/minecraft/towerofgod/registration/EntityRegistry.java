package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.*;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TowerOfGod.MOD_ID);

    public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE = register("lighthouse", LighthouseEntity::new, EntityClassification.MISC, 0.9f, 0.9f);
    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER = register("observer", ObserverEntity::new, EntityClassification.MISC, 0.4f, 0.4f);
    public static final RegistryObject<EntityType<RegularEntity>> REGULAR = register("regular", RegularEntity::new, EntityClassification.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<ShinsuEntity>> SHINSU = register("shinsu", ShinsuEntity::new, EntityClassification.MISC, 1, 1);
    public static final RegistryObject<EntityType<ClickerEntity>> CLICKER = register("clicker", ClickerEntity::new, EntityClassification.MISC, 1, 1);
    public static final RegistryObject<EntityType<ShinsuArrowEntity>> SHINSU_ARROW = register("shinsu_arrow", ShinsuArrowEntity::new, EntityClassification.MISC, 0.4f, 0.4f);
    public static final RegistryObject<EntityType<ShinsuSpearEntity>> SHINSU_SPEAR = register("shinsu_spear", ShinsuSpearEntity::new, EntityClassification.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<RankerEntity>> RANKER = register("ranker", RankerEntity::new, EntityClassification.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = register("spear", SpearEntity::new, EntityClassification.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<DirectionalLightningBoltEntity>> DIRECTIONAL_LIGHTNING = register("directional_lightning", DirectionalLightningBoltEntity::new, EntityClassification.MISC, 1, 1);
    public static final RegistryObject<EntityType<MentorEntity>> MENTOR = register("mentor", MentorEntity::new, EntityClassification.CREATURE, 0.6f, 1.8f);

    private EntityRegistry() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.IFactory<T> factory, EntityClassification classification, float width, float height) {
        return register(name, () -> EntityType.Builder.of(factory, classification).sized(width, height).build(name));
    }

    private static <T extends EntityType<?>> RegistryObject<T> register(String name, Supplier<T> entity) {
        return TYPES.register(name, entity);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(LIGHTHOUSE.get(), LighthouseEntity.setAttributes().build());
        event.put(OBSERVER.get(), ObserverEntity.setAttributes().build());
        event.put(REGULAR.get(), RegularEntity.setAttributes().build());
        event.put(RANKER.get(), RankerEntity.setAttributes().build());
        event.put(MENTOR.get(), MentorEntity.setAttributes().build());
    }


    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            EntitySpawnPlacementRegistry.register(EntityRegistry.REGULAR.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesOfClass(RegularEntity.class, AxisAlignedBB.ofSize(128, 128, 128).move(pos)).size() < 10);
            EntitySpawnPlacementRegistry.register(EntityRegistry.RANKER.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesOfClass(RankerEntity.class, AxisAlignedBB.ofSize(128, 128, 128).move(pos)).size() < 1);
            EntitySpawnPlacementRegistry.register(EntityRegistry.MENTOR.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesOfClass(RankerEntity.class, AxisAlignedBB.ofSize(128, 128, 128).move(pos)).size() < 1);
        });
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            if (event.getCategory() != Biome.Category.OCEAN && event.getCategory() != Biome.Category.RIVER) {
                MobSpawnInfoBuilder builder = event.getSpawns();
                builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityRegistry.REGULAR.get(), 4, 1, 1));
                builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityRegistry.RANKER.get(), 2, 1, 1));
                builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityRegistry.MENTOR.get(), 1, 1, 1));
            }
        }

    }

}
