package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.data.ShinsuIcons;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.Direction;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.OverridingShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ReplacementShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ToggleableShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.GearCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.HasTargetCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.MobUseCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.TargetDistanceCondition;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.*;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.AttributeRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.ShapeRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.GroupRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShinsuTechniqueRegistry {

    public static final DeferredRegister<ShinsuTechnique> TECHNIQUES = DeferredRegister.create(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_techniques"), TowerOfGod.MOD_ID);

    public static final RegistryObject<OverridingShinsuTechnique> BODY_REINFORCEMENT = register("body_reinforcement", () -> new OverridingShinsuTechnique(false, new BodyReinforcement.Factory(), ShinsuIcons.RESISTANCE, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.UP, Direction.UP)), MobUseCondition.TRUE));
    public static final RegistryObject<OverridingShinsuTechnique> BLACK_FISH = register("black_fish", () -> new OverridingShinsuTechnique(false, new BlackFish.Factory(), ShinsuIcons.SWIRL, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.DOWN, Direction.DOWN)), MobUseCondition.and(new GearCondition(EquipmentSlot.HEAD, ItemStack::isEmpty), new GearCondition(EquipmentSlot.CHEST, ItemStack::isEmpty), new GearCondition(EquipmentSlot.LEGS, ItemStack::isEmpty), new GearCondition(EquipmentSlot.FEET, ItemStack::isEmpty))));
    public static final RegistryObject<ShinsuTechnique> SHINSU_BLAST = register("shinsu_blast", () -> new ShinsuTechnique(false, new ShinsuBlast.Factory(), ShinsuIcons.BAANGS, new IRequirement[]{new AttributeRequirement(() -> null)}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.DOWN, Direction.UP)), TargetDistanceCondition.above(8)));
    public static final RegistryObject<ShinsuTechnique> FLARE_WAVE_EXPLOSION = register("flare_wave_explosion", () -> new ShinsuTechnique(false, new FlareWaveExplosion.Factory(), ShinsuIcons.TENSION, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.DOWN, Direction.UP)), TargetDistanceCondition.below(4)));
    public static final RegistryObject<ShinsuTechnique> REVERSE_FLOW_CONTROL = register("reverse_flow_control", () -> new ShinsuTechnique(false, new ReverseFlowControl.Factory(), ShinsuIcons.REVERSE, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.RIGHT)), TargetDistanceCondition.below(4)));
    public static final RegistryObject<OverridingShinsuTechnique> MANIFEST = register("manifest", () -> new OverridingShinsuTechnique(true, new Manifest.Factory(), ShinsuIcons.PICKAXE, new IRequirement[]{new ShapeRequirement()}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.LEFT, Direction.RIGHT)), MobUseCondition.TRUE));
    public static final RegistryObject<ReplacementShinsuTechnique> THROW_SPEAR = register("throw_spear", () -> new ReplacementShinsuTechnique(inst -> inst.getTechnique().equals(MANIFEST.get()), false, new ThrowSpear.Factory(), ShinsuIcons.BAANGS, new IRequirement[0], null, MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> SHOOT_SHINSU_ARROW = register("shoot_shinsu_arrow", () -> new ShinsuTechnique(false, new ShootShinsuArrow.Factory(), ShinsuIcons.BAANGS, new IRequirement[0], null, MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> MOVE_DEVICES = register("move_devices", () -> new ShinsuTechnique(true, new MoveDevices.Factory(), ShinsuIcons.MOVE, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.LEFT)), MobUseCondition.TRUE));
    public static final RegistryObject<OverridingShinsuTechnique> LIGHTHOUSE_FLOW_CONTROL = register("lighthouse_flow_control", () -> new OverridingShinsuTechnique(true, new LighthouseFlowControl.Factory(), ShinsuIcons.LIGHTHOUSE_FLOW_CONTROL, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.UP, Direction.LEFT)), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> SCOUT = register("scout", () -> new ShinsuTechnique(false, new Scout.Factory(), ShinsuIcons.EYE, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)), MobUseCondition.TRUE));
    public static final RegistryObject<ToggleableShinsuTechnique> FOLLOW_OWNER = register("follow_owner", () -> new ToggleableShinsuTechnique(true, new FollowOwner.Factory(), ShinsuIcons.FOLLOW, new IRequirement[]{}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.LEFT)), MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> CHANNEL_LIGHTNING = register("channel_lightning", () -> new ShinsuTechnique(true, new ChannelLightning.Factory(), ShinsuIcons.LIGHTNING, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING)}, new ShinsuTechnique.UsageData(ImmutableList.of(Direction.DOWN, Direction.UP), ImmutableList.of(ShinsuAttributeRegistry.LIGHTNING.get()), new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues()), ImmutableList.of(GroupRegistry.KHUN.get())), TargetDistanceCondition.above(8)));
    public static final RegistryObject<ShinsuTechnique> FLASH = register("flash", () -> new ShinsuTechnique(false, new Flash.Factory(), ShinsuIcons.FLASH, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING)}, new ShinsuTechnique.UsageData(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), ImmutableList.of(ShinsuAttributeRegistry.LIGHTNING.get()), new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues()), ImmutableList.of(GroupRegistry.KHUN.get())), TargetDistanceCondition.above(8)));
    public static final RegistryObject<ShinsuTechnique> BOOST = register("boost", () -> new ShinsuTechnique(false, new Boost.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.FIRE)}, new ShinsuTechnique.UsageData(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), ImmutableList.of(ShinsuAttributeRegistry.FIRE.get()), new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues()), ImmutableList.of(GroupRegistry.YEON.get())), TargetDistanceCondition.above(8)));
    public static final RegistryObject<OverridingShinsuTechnique> FLAMETHROWER = register("flamethrower", () -> new OverridingShinsuTechnique(false, new Flamethrower.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.FIRE)}, new ShinsuTechnique.UsageData(ImmutableList.of(Direction.DOWN, Direction.UP), ImmutableList.of(ShinsuAttributeRegistry.FIRE.get()), new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues()), ImmutableList.of(GroupRegistry.YEON.get())), new HasTargetCondition(true)));
    public static final RegistryObject<ShinsuTechnique> STOP = register("stop", () -> new ShinsuTechnique(false, new Stop.Factory(), ShinsuIcons.SHINSU, new IRequirement[0], null, MobUseCondition.TRUE));
    public static final RegistryObject<ShinsuTechnique> ERUPTION = register("eruption", () -> new ShinsuTechnique(false, new FireExplosion.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.FIRE)}, new ShinsuTechnique.UsageData(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.RIGHT), ImmutableList.of(ShinsuAttributeRegistry.FIRE.get()), new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues()), ImmutableList.of(GroupRegistry.YEON.get())), MobUseCondition.and(new HasTargetCondition(true), TargetDistanceCondition.above(8))));
    public static final RegistryObject<ShinsuTechnique> EARTH_SHATTER = register("earth_shatter", () -> new ShinsuTechnique(false, new EarthShatter.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.STONE)}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.LEFT)), new HasTargetCondition(true)));
    public static final RegistryObject<ShinsuTechnique> TREE_WALL = register("tree_wall", () -> new ShinsuTechnique(false, new TreeWall.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.PLANT)}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.UP, Direction.RIGHT, Direction.LEFT)), new HasTargetCondition(true)));
    public static final RegistryObject<ShinsuTechnique> GROW_TREE = register("grow_tree", () -> new ShinsuTechnique(false, new GrowTree.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.PLANT)}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.DOWN, Direction.UP)), new HasTargetCondition(true)));
    public static final RegistryObject<ShinsuTechnique> THROW_ROCK = register("throw_rock", () -> new ShinsuTechnique(false, new ThrowRock.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.STONE)}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.DOWN, Direction.UP)), new HasTargetCondition(true)));
    public static final RegistryObject<OverridingShinsuTechnique> THUNDERSTORM = register("thunderstorm", () -> new OverridingShinsuTechnique(false, new Thunderstorm.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.LIGHTNING)}, new ShinsuTechnique.UsageData(ImmutableList.of(Direction.DOWN, Direction.LEFT, Direction.UP), ImmutableList.of(ShinsuAttributeRegistry.LIGHTNING.get()), new ArrayList<>(ShinsuShapeRegistry.getRegistry().getValues()), ImmutableList.of(GroupRegistry.KHUN.get())), new HasTargetCondition(true)));
    public static final RegistryObject<OverridingShinsuTechnique> FOREST = register("forest", () -> new OverridingShinsuTechnique(false, new Forest.Factory(), ShinsuIcons.SHINSU, new IRequirement[]{new AttributeRequirement(ShinsuAttributeRegistry.PLANT)}, ShinsuTechnique.UsageData.all(ImmutableList.of(Direction.DOWN, Direction.LEFT, Direction.UP)), new HasTargetCondition(true)));
    private static Supplier<IForgeRegistry<ShinsuTechnique>> registry = null;

    private ShinsuTechniqueRegistry() {
    }

    public static IForgeRegistry<ShinsuTechnique> getRegistry() {
        return registry.get();
    }

    private static <T extends ShinsuTechnique> RegistryObject<T> register(String name, Supplier<T> technique) {
        return TECHNIQUES.register(name, technique);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<ShinsuTechnique>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_techniques")).setType(ShinsuTechnique.class));
    }

}
