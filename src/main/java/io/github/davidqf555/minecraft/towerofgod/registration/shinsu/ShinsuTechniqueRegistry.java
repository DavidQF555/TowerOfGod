package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.OverridingShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ToggleableShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.GearCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.*;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.AttributeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.ShapeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.TypeLevelRequirement;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
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
    public static final RegistryObject<OverridingShinsuTechnique> BODY_REINFORCEMENT = register("body_reinforcement", () -> new OverridingShinsuTechnique(false, new BodyReinforcement.Factory(), ShinsuIcons.RESISTANCE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 1)}, ImmutableList.of(Direction.UP, Direction.UP, Direction.UP), MobUseCondition.TRUE));
    public static final RegistryObject<OverridingShinsuTechnique> BLACK_FISH = register("black_fish", () -> new OverridingShinsuTechnique(false, new BlackFish.Factory(), ShinsuIcons.SWIRL, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 5)}, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.DOWN), MobUseCondition.and(new GearCondition(EquipmentSlotType.HEAD, ItemStack::isEmpty), new GearCondition(EquipmentSlotType.CHEST, ItemStack::isEmpty), new GearCondition(EquipmentSlotType.LEGS, ItemStack::isEmpty), new GearCondition(EquipmentSlotType.FEET, ItemStack::isEmpty))));
    public static final RegistryObject<ShinsuTechnique> SHINSU_BLAST = register("shinsu_blast", () -> new ShinsuTechnique(false, new ShinsuBlast.Factory(), ShinsuIcons.BAANGS, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 2), new AttributeRequirement(null)}, ImmutableList.of(Direction.DOWN, Direction.UP), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> FLARE_WAVE_EXPLOSION = register("flare_wave_explosion", () -> new ShinsuTechnique(false, new FlareWaveExplosion.Factory(), ShinsuIcons.TENSION, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DISRUPTION, 10)}, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.UP), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> REVERSE_FLOW_CONTROL = register("reverse_flow_control", () -> new ShinsuTechnique(false, new ReverseFlowControl.Factory(), ShinsuIcons.REVERSE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DISRUPTION, 15)}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.RIGHT), MobUseCondition.TRUE));
    public static final RegistryObject<OverridingShinsuTechnique> MANIFEST = register("manifest", () -> new OverridingShinsuTechnique(true, new Manifest.Factory(), ShinsuIcons.PICKAXE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 5), new ShapeRequirement()}, ImmutableList.of(Direction.LEFT, Direction.RIGHT), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> SHOOT_SHINSU_ARROW = register("shoot_shinsu_arrow", () -> new ShinsuTechnique(false, new ShootShinsuArrow.Factory(), ShinsuIcons.BAANGS, new IRequirement[0], ImmutableList.of(), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> MOVE_DEVICES = register("move_devices", () -> new ShinsuTechnique(true, new MoveDevices.Factory(), ShinsuIcons.MOVE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 5)}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.LEFT), MobUseCondition.TRUE));
    public static final RegistryObject<OverridingShinsuTechnique> LIGHTHOUSE_FLOW_CONTROL = register("lighthouse_flow_control", () -> new OverridingShinsuTechnique(true, new LighthouseFlowControl.Factory(), ShinsuIcons.LIGHTHOUSE_FLOW_CONTROL, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 5), new TypeLevelRequirement(ShinsuTechniqueType.DISRUPTION, 10)}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> SCOUT = register("scout", () -> new ShinsuTechnique(false, new Scout.Factory(), ShinsuIcons.EYE, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 7)}, ImmutableList.of(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT), MobUseCondition.TRUE));
    public static final RegistryObject<ToggleableShinsuTechnique> FOLLOW_OWNER = register("follow_owner", () -> new ToggleableShinsuTechnique(true, new FollowOwner.Factory(), ShinsuIcons.FOLLOW, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.DEVICE_CONTROL, 1)}, ImmutableList.of(Direction.UP, Direction.LEFT), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> CHANNEL_LIGHTNING = register("channel_lightning", () -> new ShinsuTechnique(true, new ChannelLightning.Factory(), ShinsuIcons.LIGHTNING, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 3), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 5), new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING.get())}, ImmutableList.of(Direction.DOWN, Direction.UP), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> FLASH = register("flash", () -> new ShinsuTechnique(false, new Flash.Factory(), ShinsuIcons.FLASH, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 10), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 10), new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING.get())}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> BOOST = register("boost", () -> new ShinsuTechnique(false, new Boost.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 10), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 10), new AttributeRequirement(ShinsuAttributeRegistry.FIRE.get())}, ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), MobUseCondition.TRUE));
    public static final RegistryObject<OverridingShinsuTechnique> FLAMETHROWER = register("flamethrower", () -> new OverridingShinsuTechnique(false, new Flamethrower.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new TypeLevelRequirement(ShinsuTechniqueType.CONTROL, 3), new TypeLevelRequirement(ShinsuTechniqueType.MANIFEST, 5), new AttributeRequirement(ShinsuAttributeRegistry.FIRE.get())}, ImmutableList.of(Direction.DOWN, Direction.UP), MobUseCondition.TRUE));
    private static IForgeRegistry<ShinsuTechnique> registry = null;

    private ShinsuTechniqueRegistry() {
    }

    public static IForgeRegistry<ShinsuTechnique> getRegistry() {
        return registry;
    }

    private static <T extends ShinsuTechnique> RegistryObject<T> register(String name, Supplier<T> technique) {
        return TECHNIQUES.register(name, technique);
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ShinsuTechnique>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_techniques")).setType(ShinsuTechnique.class).create();
    }

}
