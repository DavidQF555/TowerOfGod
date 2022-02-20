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
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
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
    public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_shovel"), ForgeRegistries.ITEMS);
    public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_pickaxe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<AxeItem> SUSPENDIUM_AXE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_axe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_sword"), ForgeRegistries.ITEMS);
    public static final RegistryObject<HoeItem> SUSPENDIUM_HOE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_hoe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_helmet"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_chestplate"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_leggings"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_boots"), ForgeRegistries.ITEMS);
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
    public static final RegistryObject<DeviceItem> LIGHTHOUSE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse"), ForgeRegistries.ITEMS);
    public static final RegistryObject<DeviceItem> OBSERVER_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "observer"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ClickerItem> CLICKER_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "clicker"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuShovel> SHINSU_SHOVEL = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_shovel"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuPickaxe> SHINSU_PICKAXE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_pickaxe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuAxe> SHINSU_AXE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_axe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuSword> SHINSU_SWORD = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_sword"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuHoe> SHINSU_HOE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_hoe"), ForgeRegistries.ITEMS);
    public static final RegistryObject<ShinsuBow> SHINSU_BOW = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_bow"), ForgeRegistries.ITEMS);
    public static final RegistryObject<GuideItem> CONTROL_GUIDE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "control_guide"), ForgeRegistries.ITEMS);
    public static final RegistryObject<GuideItem> DISRUPTION_GUIDE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "disruption_guide"), ForgeRegistries.ITEMS);
    public static final RegistryObject<GuideItem> MANIFEST_GUIDE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "manifest_guide"), ForgeRegistries.ITEMS);
    public static final RegistryObject<GuideItem> DEVICE_CONTROL_GUIDE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "device_control_guide"), ForgeRegistries.ITEMS);
    public static final RegistryObject<GuideItem> LIGHTNING_CONTROL_GUIDE_ITEM = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lightning_control_guide"), ForgeRegistries.ITEMS);

    public static final RegistryObject<Block> SUSPENDIUM_ORE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_ore"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<SuspendiumBlock> SUSPENDIUM_BLOCK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_block"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<Block> LIGHT_BLOCK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "light"), ForgeRegistries.BLOCKS);

    public static final RegistryObject<FloorTeleportationTerminalBlock> FLOOR_TELEPORTATION_TERMINAL_BLOCK = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "floor_teleportation_terminal"), ForgeRegistries.BLOCKS);
    public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "observer"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<RegularEntity>> REGULAR_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "regular"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ShinsuEntity>> SHINSU_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ClickerEntity>> CLICKER_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "clicker"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<ShinsuArrowEntity>> SHINSU_ARROW_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_arrow"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<RankerEntity>> RANKER_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "ranker"), ForgeRegistries.ENTITIES);
    public static final RegistryObject<EntityType<DirectionalLightningBoltEntity>> DIRECTIONAL_LIGHTNING_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "directional_lightning"), ForgeRegistries.ENTITIES);

    public static final RegistryObject<TileEntityType<SuspendiumTileEntity>> SUSPENDIUM_TILE_ENTITY = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "suspendium_block"), ForgeRegistries.TILE_ENTITIES);

    public static final RegistryObject<ContainerType<LighthouseEntity.LighthouseContainer>> LIGHTHOUSE_CONTAINER = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse"), ForgeRegistries.CONTAINERS);

    public static final RegistryObject<ReverseFlowEffect> REVERSE_FLOW_EFFECT = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "reverse_flow"), ForgeRegistries.POTIONS);
    public static final RegistryObject<BodyReinforcementEffect> BODY_REINFORCEMENT_EFFECT = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "body_reinforcement"), ForgeRegistries.POTIONS);

    public static final RegistryObject<PointOfInterestType> FLOOR_TELEPORTATION_TERMINAL_POI = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "floor_teleportation_terminal"), ForgeRegistries.POI_TYPES);

    public static final RegistryObject<IRecipeSerializer<DeviceDyeRecipe>> DEVICE_DYE_RECIPE = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "device_dye"), ForgeRegistries.RECIPE_SERIALIZERS);

    public static final RegistryObject<ShinsuToolLootModifier.Serializer> SHINSU_TOOL_LOOT_MODIFIER_SERIALIZER = RegistryObject.of(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_tool"), ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);

    public static final List<RegistryObject<? extends Item>> SHINSU_ITEMS = ImmutableList.of(SHINSU_SHOVEL, SHINSU_PICKAXE, SHINSU_AXE, SHINSU_SWORD, SHINSU_HOE, SHINSU_BOW);
    public static final List<RegistryObject<? extends Item>> COLORED_DEVICE_ITEMS = ImmutableList.of(LIGHTHOUSE_ITEM, OBSERVER_ITEM);
    public static final List<RegistryObject<HookItem>> HOOK_ITEMS = ImmutableList.of(WOODEN_HOOK, STONE_HOOK, IRON_HOOK, GOLDEN_HOOK, DIAMOND_HOOK, NETHERITE_HOOK, SUSPENDIUM_HOOK);
    public static final List<RegistryObject<NeedleItem>> NEEDLE_ITEMS = ImmutableList.of(WOODEN_NEEDLE, STONE_NEEDLE, IRON_NEEDLE, GOLDEN_NEEDLE, DIAMOND_NEEDLE, NETHERITE_NEEDLE, SUSPENDIUM_NEEDLE);

    private RegistryHandler() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(5f, 5f).sound(SoundType.STONE).harvestLevel(1).harvestTool(ToolType.PICKAXE)).setRegistryName(SUSPENDIUM_ORE.getId()),
                new SuspendiumBlock().setRegistryName(SUSPENDIUM_BLOCK.getId()),
                new Block(Block.Properties.create(Material.AIR).setAir().setLightLevel(state -> 15)).setRegistryName(LIGHT_BLOCK.getId()),
                new FloorTeleportationTerminalBlock().setRegistryName(FLOOR_TELEPORTATION_TERMINAL_BLOCK.getId())
        );
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new Item(new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM.getId()),
                new BlockItem(SUSPENDIUM_ORE.get(), new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_ORE.getId()),
                new BlockItem(SUSPENDIUM_BLOCK.get(), new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_BLOCK.getId()),
                new BlockItem(FLOOR_TELEPORTATION_TERMINAL_BLOCK.get(), new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(FLOOR_TELEPORTATION_TERMINAL_BLOCK.getId()),
                new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_SHOVEL_ITEM.getId()),
                new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_PICKAXE_ITEM.getId()),
                new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_AXE_ITEM.getId()),
                new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_SWORD_ITEM.getId()),
                new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_HOE_ITEM.getId()),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.HEAD, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_HELMET_ITEM.getId()),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.CHEST, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_CHESTPLATE_ITEM.getId()),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.LEGS, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_LEGGINGS_ITEM.getId()),
                new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.FEET, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_BOOTS_ITEM.getId()),
                new NeedleItem(ItemTier.WOOD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(WOODEN_NEEDLE.getId()),
                new NeedleItem(ItemTier.STONE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(STONE_NEEDLE.getId()),
                new NeedleItem(ItemTier.IRON, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(IRON_NEEDLE.getId()),
                new NeedleItem(ItemTier.GOLD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(GOLDEN_NEEDLE.getId()),
                new NeedleItem(ItemTier.DIAMOND, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(DIAMOND_NEEDLE.getId()),
                new NeedleItem(ItemTier.NETHERITE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(NETHERITE_NEEDLE.getId()),
                new NeedleItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_NEEDLE.getId()),
                new HookItem(ItemTier.WOOD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(WOODEN_HOOK.getId()),
                new HookItem(ItemTier.STONE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(STONE_HOOK.getId()),
                new HookItem(ItemTier.IRON, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(IRON_HOOK.getId()),
                new HookItem(ItemTier.GOLD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(GOLDEN_HOOK.getId()),
                new HookItem(ItemTier.DIAMOND, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(DIAMOND_HOOK.getId()),
                new HookItem(ItemTier.NETHERITE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(NETHERITE_HOOK.getId()),
                new HookItem(ModToolTier.SUSPENDIUM, 3, -2.4f, new Item.Properties().group(TowerOfGod.TAB)).setRegistryName(SUSPENDIUM_HOOK.getId()),
                new DeviceItem((world, item) -> LIGHTHOUSE_ENTITY.get().create(world)).setRegistryName(LIGHTHOUSE_ITEM.getId()),
                new DeviceItem((world, item) -> OBSERVER_ENTITY.get().create(world)).setRegistryName(OBSERVER_ITEM.getId()),
                new ClickerItem().setRegistryName(CLICKER_ITEM.getId()),
                new ShinsuShovel(0.5f, -2).setRegistryName(SHINSU_SHOVEL.getId()),
                new ShinsuPickaxe(0, -1.6f).setRegistryName(SHINSU_PICKAXE.getId()),
                new ShinsuAxe(4, -2).setRegistryName(SHINSU_AXE.getId()),
                new ShinsuSword(2, -0.8f).setRegistryName(SHINSU_SWORD.getId()),
                new ShinsuHoe(-1, 4).setRegistryName(SHINSU_HOE.getId()),
                new ShinsuBow().setRegistryName(SHINSU_BOW.getId()),
                new GuideItem(new ShinsuTechnique[]{ShinsuTechnique.BODY_REINFORCEMENT, ShinsuTechnique.BLACK_FISH, ShinsuTechnique.SHINSU_BLAST}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".control_guide.author"), 0xFF7E79DF).setRegistryName(CONTROL_GUIDE_ITEM.getId()),
                new GuideItem(new ShinsuTechnique[]{ShinsuTechnique.FLARE_WAVE_EXPLOSION, ShinsuTechnique.REVERSE_FLOW_CONTROL}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".disruption_guide.author"), 0xFF3D3D3D).setRegistryName(DISRUPTION_GUIDE_ITEM.getId()),
                new GuideItem(new ShinsuTechnique[]{ShinsuTechnique.MANIFEST}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".manifest_guide.author"), 0xFF2D3771).setRegistryName(MANIFEST_GUIDE_ITEM.getId()),
                new GuideItem(new ShinsuTechnique[]{ShinsuTechnique.MOVE_DEVICES, ShinsuTechnique.LIGHTHOUSE_FLOW_CONTROL, ShinsuTechnique.SCOUT, ShinsuTechnique.FOLLOW_OWNER}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".device_control_guide.author"), 0xFFD8D8D8).setRegistryName(DEVICE_CONTROL_GUIDE_ITEM.getId()),
                new GuideItem(new ShinsuTechnique[]{ShinsuTechnique.CHANNEL_LIGHTNING, ShinsuTechnique.FLASH}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".lightning_control_guide.author"), 0xFFFFF96D).setRegistryName(LIGHTNING_CONTROL_GUIDE_ITEM.getId())
        );
    }

    @SubscribeEvent
    public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create(new LighthouseEntity.Factory(), EntityClassification.MISC).size(0.9f, 0.9f).build(LIGHTHOUSE_ENTITY.getId().toString()).setRegistryName(LIGHTHOUSE_ENTITY.getId()),
                EntityType.Builder.create(new ObserverEntity.Factory(), EntityClassification.MISC).size(0.4f, 0.4f).build(OBSERVER_ENTITY.getId().toString()).setRegistryName(OBSERVER_ENTITY.getId()),
                EntityType.Builder.create(new RegularEntity.Factory(), EntityClassification.CREATURE).size(0.6f, 1.8f).build(REGULAR_ENTITY.getId().toString()).setRegistryName(REGULAR_ENTITY.getId()),
                EntityType.Builder.create(new ShinsuEntity.Factory(), EntityClassification.MISC).size(1, 1).build(SHINSU_ENTITY.getId().toString()).setRegistryName(SHINSU_ENTITY.getId()),
                EntityType.Builder.create(new ClickerEntity.Factory(), EntityClassification.MISC).size(1, 1).build(CLICKER_ENTITY.getId().toString()).setRegistryName(CLICKER_ENTITY.getId()),
                EntityType.Builder.create(new ShinsuArrowEntity.Factory(), EntityClassification.MISC).size(0.4f, 0.4f).build(SHINSU_ARROW_ENTITY.getId().toString()).setRegistryName(SHINSU_ARROW_ENTITY.getId()),
                EntityType.Builder.create(new RankerEntity.Factory(), EntityClassification.CREATURE).size(0.6f, 1.8f).build(RANKER_ENTITY.getId().toString()).setRegistryName(RANKER_ENTITY.getId()),
                EntityType.Builder.create(new DirectionalLightningBoltEntity.Factory(), EntityClassification.MISC).size(1, 1).build(DIRECTIONAL_LIGHTNING_ENTITY.getId().toString()).setRegistryName(DIRECTIONAL_LIGHTNING_ENTITY.getId())
        );
    }

    @SubscribeEvent
    public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                IForgeContainerType.create(new LighthouseEntity.LighthouseContainer.Factory()).setRegistryName(LIGHTHOUSE_CONTAINER.getId())
        );
    }

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event) {
        event.getRegistry().registerAll(
                new ReverseFlowEffect().setRegistryName(REVERSE_FLOW_EFFECT.getId()),
                new BodyReinforcementEffect().setRegistryName(BODY_REINFORCEMENT_EFFECT.getId())
        );
    }

    @SubscribeEvent
    public static void registerLootModifierSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new ShinsuToolLootModifier.Serializer().setRegistryName(SHINSU_TOOL_LOOT_MODIFIER_SERIALIZER.getId())
        );
    }

    @SubscribeEvent
    public static void registerPOITypes(RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().registerAll(
                new PointOfInterestType(FLOOR_TELEPORTATION_TERMINAL_POI.getId().getPath(), new HashSet<>(FLOOR_TELEPORTATION_TERMINAL_BLOCK.get().getStateContainer().getValidStates()), 0, 1).setRegistryName(FLOOR_TELEPORTATION_TERMINAL_POI.getId())
        );
    }

    @SubscribeEvent
    public static void registryRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().registerAll(
                new SpecialRecipeSerializer<>(DeviceDyeRecipe::new).setRegistryName(DEVICE_DYE_RECIPE.getId())
        );
    }

    @SubscribeEvent
    public static void registryTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(SuspendiumTileEntity::new, SUSPENDIUM_BLOCK.get()).build(Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, SUSPENDIUM_TILE_ENTITY.getId().getPath())).setRegistryName(SUSPENDIUM_TILE_ENTITY.getId())
        );
    }
}
