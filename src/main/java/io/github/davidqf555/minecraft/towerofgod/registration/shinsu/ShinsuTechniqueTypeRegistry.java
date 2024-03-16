package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShinsuTechniqueTypeRegistry {

    public static final ResourceKey<Registry<ShinsuTechniqueType<?, ?>>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_technique"));
    public static final DeferredRegister<ShinsuTechniqueType<?, ?>> TYPES = DeferredRegister.create(REGISTRY, TowerOfGod.MOD_ID);

    public static final RegistryObject<BodyReinforcement> BODY_REINFORCEMENT = register("body_reinforcement", BodyReinforcement::new);
    public static final RegistryObject<BlackFish> BLACK_FISH = register("black_fish", BlackFish::new);
    public static final RegistryObject<ShinsuBlast> SHINSU_BLAST = register("shinsu_blast", ShinsuBlast::new);
    public static final RegistryObject<FlareWaveExplosion> FLARE_WAVE_EXPLOSION = register("flare_wave_explosion", FlareWaveExplosion::new);
    public static final RegistryObject<ReverseFlowControl> REVERSE_FLOW_CONTROL = register("reverse_flow_control", ReverseFlowControl::new);
    public static final RegistryObject<Manifest> MANIFEST = register("manifest", Manifest::new);
    public static final RegistryObject<MoveDevices> MOVE_DEVICES = register("move_devices", MoveDevices::new);
    public static final RegistryObject<LighthouseFlowControl> LIGHTHOUSE_FLOW_CONTROL = register("lighthouse_flow_control", LighthouseFlowControl::new);
    public static final RegistryObject<Scout> SCOUT = register("scout", Scout::new);
    public static final RegistryObject<FollowOwner> FOLLOW_OWNER = register("follow_owner", FollowOwner::new);
    public static final RegistryObject<ChannelLightning> CHANNEL_LIGHTNING = register("channel_lightning", ChannelLightning::new);
    public static final RegistryObject<Flash> FLASH = register("flash", Flash::new);
    public static final RegistryObject<Boost> BOOST = register("boost", Boost::new);
    public static final RegistryObject<Flamethrower> FLAMETHROWER = register("flamethrower", Flamethrower::new);
    public static final RegistryObject<FireExplosion> ERUPTION = register("eruption", FireExplosion::new);
    public static final RegistryObject<EarthShatter> EARTH_SHATTER = register("earth_shatter", EarthShatter::new);
    public static final RegistryObject<TreeWall> TREE_WALL = register("tree_wall", TreeWall::new);
    public static final RegistryObject<GrowTree> GROW_TREE = register("grow_tree", GrowTree::new);
    public static final RegistryObject<ThrowRock> THROW_ROCK = register("throw_rock", ThrowRock::new);
    public static final RegistryObject<Thunderstorm> THUNDERSTORM = register("thunderstorm", Thunderstorm::new);
    public static final RegistryObject<Forest> FOREST = register("forest", Forest::new);
    private static Supplier<IForgeRegistry<ShinsuTechniqueType<?, ?>>> registry = null;

    private ShinsuTechniqueTypeRegistry() {
    }

    public static IForgeRegistry<ShinsuTechniqueType<?, ?>> getRegistry() {
        return registry.get();
    }

    private static <T extends ShinsuTechniqueType<?, ?>> RegistryObject<T> register(String name, Supplier<T> technique) {
        return TYPES.register(name, technique);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<ShinsuTechniqueType<?, ?>>().setName(REGISTRY.location()));
    }

}
