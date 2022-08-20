package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.*;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.QualityRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.ShapeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.TypeLevelRequirement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShinsuTechniqueRegistry {

    public static final DeferredRegister<ShinsuTechnique> TECHNIQUES = DeferredRegister.create(ShinsuTechnique.class, TowerOfGod.MOD_ID);
    public static final RegistryObject<ShinsuTechnique> BODY_REINFORCEMENT = register("body_reinforcement", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.OVERRIDE, false, new BodyReinforcement.Factory(), ShinsuIcons.RESISTANCE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 1)}, ImmutableList.of(Direction.UP, Direction.UP, Direction.UP)));
    public static final RegistryObject<ShinsuTechnique> BLACK_FISH = register("black_fish", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.OVERRIDE, false, new BlackFish.Factory(), ShinsuIcons.SWIRL, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 5)}, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.DOWN)));
    public static final RegistryObject<ShinsuTechnique> SHINSU_BLAST = register("shinsu_blast", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, false, new ShinsuBlast.Factory(), ShinsuIcons.BAANGS, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 2), new QualityRequirement(null)}, ImmutableList.of(Direction.DOWN, Direction.UP)));
    public static final RegistryObject<ShinsuTechnique> FLARE_WAVE_EXPLOSION = register("flare_wave_explosion", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, false, new FlareWaveExplosion.Factory(), ShinsuIcons.TENSION, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DISRUPTION, 10)}, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.UP)));
    public static final RegistryObject<ShinsuTechnique> REVERSE_FLOW_CONTROL = register("reverse_flow_control", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, false, new ReverseFlowControl.Factory(), ShinsuIcons.REVERSE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DISRUPTION, 15)}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.RIGHT)));
    public static final RegistryObject<ShinsuTechnique> MANIFEST = register("manifest", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.OVERRIDE, true, new Manifest.Factory(), ShinsuIcons.PICKAXE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 5), new ShapeRequirement()}, ImmutableList.of(Direction.LEFT, Direction.RIGHT)));
    public static final RegistryObject<ShinsuTechnique> SHOOT_SHINSU_ARROW = register("shoot_shinsu_arrow", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, false, new ShootShinsuArrow.Factory(), ShinsuIcons.BAANGS, new IRequirement[0], ImmutableList.of()));
    public static final RegistryObject<ShinsuTechnique> MOVE_DEVICES = register("move_devices", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, true, new MoveDevices.Factory(), ShinsuIcons.MOVE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 5)}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.LEFT)));
    public static final RegistryObject<ShinsuTechnique> LIGHTHOUSE_FLOW_CONTROL = register("lighthouse_flow_control", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.OVERRIDE, true, new LighthouseFlowControl.Factory(), ShinsuIcons.LIGHTHOUSE_FLOW_CONTROL, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 5), new TypeLevelRequirement(ShinsuTechniqueType.DISRUPTION, 10)}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT)));
    public static final RegistryObject<ShinsuTechnique> SCOUT = register("scout", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, false, new Scout.Factory(), ShinsuIcons.EYE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 7)}, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)));
    public static final RegistryObject<ShinsuTechnique> FOLLOW_OWNER = register("follow_owner", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.TOGGLE, true, new FollowOwner.Factory(), ShinsuIcons.FOLLOW, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 1)}, ImmutableList.of(Direction.UP, Direction.LEFT)));
    public static final RegistryObject<ShinsuTechnique> CHANNEL_LIGHTNING = register("channel_lightning", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, true, new ChannelLightning.Factory(), ShinsuIcons.LIGHTNING, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 3), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 5), new QualityRequirement(ShinsuQualityRegistry.LIGHTNING.get())}, ImmutableList.of(Direction.DOWN, Direction.UP)));
    public static final RegistryObject<ShinsuTechnique> FLASH = register("flash", () -> new ShinsuTechnique(ShinsuTechnique.Repeat.ALLOW, false, new Flash.Factory(), ShinsuIcons.FLASH, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 10), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 10), new QualityRequirement(ShinsuQualityRegistry.LIGHTNING.get())}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)));
    private static IForgeRegistry<ShinsuTechnique> registry = null;

    private ShinsuTechniqueRegistry() {
    }

    public static IForgeRegistry<ShinsuTechnique> getRegistry() {
        return registry;
    }

    private static RegistryObject<ShinsuTechnique> register(String name, Supplier<ShinsuTechnique> technique) {
        return TECHNIQUES.register(name, technique);
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ShinsuTechnique>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_techniques")).setType(ShinsuTechnique.class).create();
    }

}