package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.*;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TowerOfGod.MOD_ID);

    public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE = register("lighthouse", LighthouseEntity::new, MobCategory.MISC, 0.9f, 0.9f);
    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER = register("observer", ObserverEntity::new, MobCategory.MISC, 0.4f, 0.4f);
    public static final RegistryObject<EntityType<RegularEntity>> REGULAR = register("regular", RegularEntity::new, MobCategory.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<ShinsuEntity>> SHINSU = register("shinsu", ShinsuEntity::new, MobCategory.MISC, 1, 1);
    public static final RegistryObject<EntityType<ClickerEntity>> CLICKER = register("clicker", ClickerEntity::new, MobCategory.MISC, 1, 1);
    public static final RegistryObject<EntityType<ShinsuArrowEntity>> SHINSU_ARROW = register("shinsu_arrow", ShinsuArrowEntity::new, MobCategory.MISC, 0.4f, 0.4f);
    public static final RegistryObject<EntityType<ShinsuSpearEntity>> SHINSU_SPEAR = register("shinsu_spear", ShinsuSpearEntity::new, MobCategory.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<RankerEntity>> RANKER = register("ranker", RankerEntity::new, MobCategory.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<SpearEntity>> SPEAR = register("spear", SpearEntity::new, MobCategory.MISC, 0.5f, 0.5f);
    public static final RegistryObject<EntityType<DirectionalLightningBoltEntity>> DIRECTIONAL_LIGHTNING = register("directional_lightning", DirectionalLightningBoltEntity::new, MobCategory.MISC, 1, 1);
    public static final RegistryObject<EntityType<MentorEntity>> MENTOR = register("mentor", MentorEntity::new, MobCategory.CREATURE, 0.6f, 1.8f);
    public static final RegistryObject<EntityType<BaangEntity>> BAANG = register("baang", BaangEntity::new, MobCategory.MISC, 0.25f, 0.25f);

    private EntityRegistry() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory classification, float width, float height) {
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
            SpawnPlacements.register(EntityRegistry.REGULAR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesOfClass(RegularEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), 128, 128, 128)).size() < 10);
            SpawnPlacements.register(EntityRegistry.RANKER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesOfClass(RankerEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), 128, 128, 128)).size() < 1);
            SpawnPlacements.register(EntityRegistry.MENTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, world, reason, pos, rand) -> world.getEntitiesOfClass(RankerEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), 128, 128, 128)).size() < 1);
        });
    }

    @Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {

        @SubscribeEvent
        public static void onBiomeLoading(BiomeLoadingEvent event) {
            if (event.getCategory() != Biome.BiomeCategory.OCEAN && event.getCategory() != Biome.BiomeCategory.RIVER) {
                MobSpawnSettingsBuilder builder = event.getSpawns();
                builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityRegistry.REGULAR.get(), 4, 1, 1));
                builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityRegistry.RANKER.get(), 2, 1, 1));
                builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityRegistry.MENTOR.get(), 1, 1, 1));
            }
        }

    }

}
