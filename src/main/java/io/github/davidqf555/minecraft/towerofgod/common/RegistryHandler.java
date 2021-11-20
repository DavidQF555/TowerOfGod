package io.github.davidqf555.minecraft.towerofgod.common;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.FloorTeleportationTerminalBlock;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumBlock;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumTileEntity;
import io.github.davidqf555.minecraft.towerofgod.common.effects.BodyReinforcementEffect;
import io.github.davidqf555.minecraft.towerofgod.common.effects.ReverseFlowEffect;
import io.github.davidqf555.minecraft.towerofgod.common.entities.*;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.LighthouseEntity;
import io.github.davidqf555.minecraft.towerofgod.common.entities.devices.ObserverEntity;
import io.github.davidqf555.minecraft.towerofgod.common.items.*;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechniqueType;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RegistryHandler {

    public static final RegistryObject<Item> SUSPENDIUM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium"), ForgeRegistries.ITEMS);
    public static final RegistryObject<BlockItem> SUSPENDIUM_ORE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), ForgeRegistries.ITEMS);
    public static final RegistryObject<BlockItem> SUSPENDIUM_BLOCK_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_block"), ForgeRegistries.ITEMS);
    public static final RegistryObject<BlockItem> FLOOR_TELEPORTATION_TERMINAL_BLOCK_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "floor_teleportation_terminal_block"), ForgeRegistries.ITEMS);

    public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_shovel"), ForgeRegistries.ITEMS);
    public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_pickaxe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<AxeItem> SUSPENDIUM_AXE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_axe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_sword"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HoeItem> SUSPENDIUM_HOE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_hoe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_helmet"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_chestplate"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_leggings"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_boots"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> WOODEN_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "wooden_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> STONE_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "stone_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> IRON_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "iron_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> GOLDEN_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "golden_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> DIAMOND_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "diamond_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> NETHERITE_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "netherite_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<NeedleItem> SUSPENDIUM_NEEDLE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_needle"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> WOODEN_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "wooden_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> STONE_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "stone_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> IRON_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "iron_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> GOLDEN_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "golden_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> DIAMOND_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "diamond_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> NETHERITE_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "netherite_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HookItem> SUSPENDIUM_HOOK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_hook"), ForgeRegistries.ITEMS);
    public static final RegistryObject<DeviceItem> LIGHTHOUSE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<DeviceItem> OBSERVER_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "observer_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ClickerItem> CLICKER_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "clicker_item"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuShovel> SHINSU_SHOVEL = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_shovel"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuPickaxe> SHINSU_PICKAXE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_pickaxe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuAxe> SHINSU_AXE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_axe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuSword> SHINSU_SWORD = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_sword"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuHoe> SHINSU_HOE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_hoe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuBow> SHINSU_BOW = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_bow"), ForgeRegistries.ITEMS);

    public static final RegistryObject<Block> SUSPENDIUM_ORE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<SuspendiumBlock> SUSPENDIUM_BLOCK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_block"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<Block> LIGHT_BLOCK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "light_block"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<FloorTeleportationTerminalBlock> FLOOR_TELEPORTATION_TERMINAL_BLOCK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "floor_teleportation_terminal_block"), ForgeRegistries.BLOCKS);

    public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "observer_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<RegularEntity>> REGULAR_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "regular_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ShinsuEntity>> SHINSU_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ClickerEntity>> CLICKER_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "clicker_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ShinsuArrowEntity>> SHINSU_ARROW_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_arrow_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<RankerEntity>> RANKER_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "ranker_entity"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<TileEntityType<SuspendiumTileEntity>> SUSPENDIUM_TILE_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_block"), ForgeRegistries.TILE_ENTITIES);

    public static final RegistryObject<ContainerType<LighthouseEntity.LighthouseContainer>> LIGHTHOUSE_CONTAINER = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_container"), ForgeRegistries.CONTAINERS);

    public static final RegistryObject<ReverseFlowEffect> REVERSE_FLOW_EFFECT = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "reverse_flow_effect"), ForgeRegistries.POTIONS);
    public static final RegistryObject<BodyReinforcementEffect> BODY_REINFORCEMENT_EFFECT = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "body_reinforcement_effect"), ForgeRegistries.POTIONS);

    public static final RegistryObject<ShinsuToolLootModifier.Serializer> SHINSU_TOOL_LOOT_MODIFIER = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_tool_loot_modifier"), ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);

    public static final RegistryObject<PointOfInterestType> FLOOR_TELEPORTATION_TERMINAL_POI = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "floor_teleportation_terminal"), ForgeRegistries.POI_TYPES);

    public static final RegistryObject<IRecipeSerializer<DeviceDyeRecipe>> DEVICE_DYE_RECIPE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "device_dye_recipe"), ForgeRegistries.RECIPE_SERIALIZERS);

    public static final List<RegistryObject<? extends Item>> SHINSU_ITEMS = ImmutableList.of(SHINSU_SHOVEL, SHINSU_PICKAXE, SHINSU_AXE, SHINSU_SWORD, SHINSU_HOE, SHINSU_BOW);
    public static final List<RegistryObject<? extends Item>> COLORED_DEVICE_ITEMS = ImmutableList.of(LIGHTHOUSE_ITEM, OBSERVER_ITEM);
    public static final List<RegistryObject<HookItem>> HOOK_ITEMS = ImmutableList.of(WOODEN_HOOK, STONE_HOOK, IRON_HOOK, GOLDEN_HOOK, DIAMOND_HOOK, NETHERITE_HOOK, SUSPENDIUM_HOOK);
    public static final List<RegistryObject<NeedleItem>> NEEDLE_ITEMS = ImmutableList.of(WOODEN_NEEDLE, STONE_NEEDLE, IRON_NEEDLE, GOLDEN_NEEDLE, DIAMOND_NEEDLE, NETHERITE_NEEDLE, SUSPENDIUM_NEEDLE);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(5f, 5f).sound(SoundType.STONE).harvestLevel(1).harvestTool(ToolType.PICKAXE)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_ore"),
                new SuspendiumBlock().setRegistryName(TowerOfGod.MOD_ID, "suspendium_block"),
                new Block(Block.Properties.create(Material.AIR).setAir().setLightLevel(state -> 15)).setRegistryName(TowerOfGod.MOD_ID, "light_block"),
                new FloorTeleportationTerminalBlock().setRegistryName(TowerOfGod.MOD_ID, "floor_teleportation_terminal_block")
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new Item(new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium"),
                new BlockItem(SUSPENDIUM_ORE.get(), new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_ore"),
                new BlockItem(SUSPENDIUM_BLOCK.get(), new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_block"),
                new BlockItem(FLOOR_TELEPORTATION_TERMINAL_BLOCK.get(), new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "floor_teleportation_terminal_block"),
                new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_shovel"),
                new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_pickaxe"),
                new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_axe"),
                new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_sword"),
                new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_hoe"),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.HEAD, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_helmet"),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.CHEST, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_chestplate"),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.LEGS, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_leggings"),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.FEET, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_boots"),
                new NeedleItem(ItemTier.WOOD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "wooden_needle"),
                new NeedleItem(ItemTier.STONE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "stone_needle"),
                new NeedleItem(ItemTier.IRON, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "iron_needle"),
                new NeedleItem(ItemTier.GOLD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "golden_needle"),
                new NeedleItem(ItemTier.DIAMOND, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "diamond_needle"),
                new NeedleItem(ItemTier.NETHERITE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "netherite_needle"),
                new NeedleItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_needle"),
                new HookItem(ItemTier.WOOD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "wooden_hook"),
                new HookItem(ItemTier.STONE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "stone_hook"),
                new HookItem(ItemTier.IRON, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "iron_hook"),
                new HookItem(ItemTier.GOLD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "golden_hook"),
                new HookItem(ItemTier.DIAMOND, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "diamond_hook"),
                new HookItem(ItemTier.NETHERITE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "netherite_hook"),
                new HookItem(ModToolTier.SUSPENDIUM, 3, -2.4f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(TowerOfGod.MOD_ID, "suspendium_hook"),
                new DeviceItem((world, item) -> LIGHTHOUSE_ENTITY.get().create(world)).setRegistryName(TowerOfGod.MOD_ID, "lighthouse_item"),
                new DeviceItem((world, item) -> OBSERVER_ENTITY.get().create(world)).setRegistryName(TowerOfGod.MOD_ID, "observer_item"),
                new ClickerItem().setRegistryName(TowerOfGod.MOD_ID, "clicker_item"),
                new ShinsuShovel(0.5f, -2).setRegistryName(TowerOfGod.MOD_ID, "shinsu_shovel"),
                new ShinsuPickaxe(0, -1.6f).setRegistryName(TowerOfGod.MOD_ID, "shinsu_pickaxe"),
                new ShinsuAxe(4, -2).setRegistryName(TowerOfGod.MOD_ID, "shinsu_axe"),
                new ShinsuSword(2, -0.8f).setRegistryName(TowerOfGod.MOD_ID, "shinsu_sword"),
                new ShinsuHoe(-1, 4).setRegistryName(TowerOfGod.MOD_ID, "shinsu_hoe"),
                new ShinsuBow().setRegistryName(TowerOfGod.MOD_ID, "shinsu_bow"),
                new GuideItem(ShinsuTechniqueType.CONTROL, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".control_guide.author")).setRegistryName(TowerOfGod.MOD_ID, "control_guide"),
                new GuideItem(ShinsuTechniqueType.DISRUPTION, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".disruption_guide.author")).setRegistryName(TowerOfGod.MOD_ID, "disruption_guide")
        );
    }

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create(new LighthouseEntity.Factory(), EntityClassification.MISC).size(0.9f, 0.9f).build(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "lighthouse_entity"),
                EntityType.Builder.create(new ObserverEntity.Factory(), EntityClassification.MISC).size(0.4f, 0.4f).build(new ResourceLocation(TowerOfGod.MOD_ID, "observer_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "observer_entity"),
                EntityType.Builder.create(new RegularEntity.Factory(), EntityClassification.CREATURE).size(0.6f, 1.8f).build(new ResourceLocation(TowerOfGod.MOD_ID, "regular_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "regular_entity"),
                EntityType.Builder.create(new ShinsuEntity.Factory(), EntityClassification.MISC).size(1, 1).build(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "shinsu_entity"),
                EntityType.Builder.create(new ClickerEntity.Factory(), EntityClassification.MISC).size(1, 1).build(new ResourceLocation(TowerOfGod.MOD_ID, "clicker_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "clicker_entity"),
                EntityType.Builder.create(new ShinsuArrowEntity.Factory(), EntityClassification.MISC).size(0.4f, 0.4f).build(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_arrow_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "shinsu_arrow_entity"),
                EntityType.Builder.create(new RankerEntity.Factory(), EntityClassification.CREATURE).size(0.6f, 1.8f).build(new ResourceLocation(TowerOfGod.MOD_ID, "ranker_entity").toString()).setRegistryName(TowerOfGod.MOD_ID, "ranker_entity")
        );
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                IForgeContainerType.create(new LighthouseEntity.LighthouseContainer.Factory()).setRegistryName(TowerOfGod.MOD_ID, "lighthouse_container")
        );
    }

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event) {
        event.getRegistry().registerAll(
                new ReverseFlowEffect().setRegistryName(TowerOfGod.MOD_ID, "reverse_flow_effect"),
                new BodyReinforcementEffect().setRegistryName(TowerOfGod.MOD_ID, "body_reinforcement_effect")
        );
    }

    @SubscribeEvent
    public static void registerLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new ShinsuToolLootModifier.Serializer().setRegistryName(TowerOfGod.MOD_ID, "shinsu_tool_loot_modifier")
        );
    }

    @SubscribeEvent
    public static void registerPOITypes(RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().registerAll(
                new PointOfInterestType("floor_teleportation_terminal", new HashSet<>(FLOOR_TELEPORTATION_TERMINAL_BLOCK.get().getStateContainer().getValidStates()), 0, 1).setRegistryName(TowerOfGod.MOD_ID, "floor_teleportation_terminal")
        );
    }

    @SubscribeEvent
    public static void registryRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().registerAll(
                new SpecialRecipeSerializer<>(DeviceDyeRecipe::new).setRegistryName(TowerOfGod.MOD_ID, "device_dye_recipe")
        );
    }

    @SubscribeEvent
    public static void registryTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(SuspendiumTileEntity::new, SUSPENDIUM_BLOCK.get()).build(Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, "suspendium_block")).setRegistryName(TowerOfGod.MOD_ID, "suspendium_block")
        );
    }
}
