package io.github.davidqf555.minecraft.towerofgod.common.util;

import io.github.davidqf555.minecraft.towerofgod.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.LightBlock;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumBlock;
import io.github.davidqf555.minecraft.towerofgod.common.blocks.SuspendiumOre;
import io.github.davidqf555.minecraft.towerofgod.common.entities.*;
import io.github.davidqf555.minecraft.towerofgod.common.items.*;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ReverseFlowEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistryHandler {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TowerOfGod.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TowerOfGod.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, TowerOfGod.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TowerOfGod.MOD_ID);
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, TowerOfGod.MOD_ID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, TowerOfGod.MOD_ID);

    public static final RegistryObject<Item> SUSPENDIUM = ITEMS.register("suspendium", () -> new Item(new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<Block> SUSPENDIUM_ORE = BLOCKS.register("suspendium_ore", SuspendiumOre::new);
    public static final RegistryObject<Item> SUSPENDIUM_ORE_ITEM = ITEMS.register("suspendium_ore", () -> new BlockItem(SUSPENDIUM_ORE.get(), new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<Block> SUSPENDIUM_BLOCK = BLOCKS.register("suspendium_block", SuspendiumBlock::new);
    public static final RegistryObject<Item> SUSPENDIUM_BLOCK_ITEM = ITEMS.register("suspendium_block", () -> new BlockItem(SUSPENDIUM_BLOCK.get(), new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL = ITEMS.register("suspendium_shovel", () -> new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE = ITEMS.register("suspendium_pickaxe", () -> new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<AxeItem> SUSPENDIUM_AXE = ITEMS.register("suspendium_axe", () -> new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD = ITEMS.register("suspendium_sword", () -> new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HoeItem> SUSPENDIUM_HOE = ITEMS.register("suspendium_hoe", () -> new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET = ITEMS.register("suspendium_helmet", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.HEAD, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE = ITEMS.register("suspendium_chestplate", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.CHEST, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS = ITEMS.register("suspendium_leggings", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.LEGS, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS = ITEMS.register("suspendium_boots", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.FEET, new Item.Properties().group(TowerOfGod.TAB)));

    public static final RegistryObject<NeedleItem> WOODEN_NEEDLE = ITEMS.register("wooden_needle", () -> new NeedleItem(ItemTier.WOOD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> STONE_NEEDLE = ITEMS.register("stone_needle", () -> new NeedleItem(ItemTier.STONE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> IRON_NEEDLE = ITEMS.register("iron_needle", () -> new NeedleItem(ItemTier.IRON, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> GOLDEN_NEEDLE = ITEMS.register("golden_needle", () -> new NeedleItem(ItemTier.GOLD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> DIAMOND_NEEDLE = ITEMS.register("diamond_needle", () -> new NeedleItem(ItemTier.DIAMOND, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> NETHERITE_NEEDLE = ITEMS.register("netherite_needle", () -> new NeedleItem(ItemTier.NETHERITE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> SUSPENDIUM_NEEDLE = ITEMS.register("suspendium_needle", () -> new NeedleItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties().group(TowerOfGod.TAB)));

    public static final RegistryObject<HookItem> WOODEN_HOOK = ITEMS.register("wooden_hook", () -> new HookItem(ItemTier.WOOD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> STONE_HOOK = ITEMS.register("stone_hook", () -> new HookItem(ItemTier.STONE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> IRON_HOOK = ITEMS.register("iron_hook", () -> new HookItem(ItemTier.IRON, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> GOLDEN_HOOK = ITEMS.register("golden_hook", () -> new HookItem(ItemTier.GOLD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> DIAMOND_HOOK = ITEMS.register("diamond_hook", () -> new HookItem(ItemTier.DIAMOND, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> NETHERITE_HOOK = ITEMS.register("netherite_hook", () -> new HookItem(ItemTier.NETHERITE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> SUSPENDIUM_HOOK = ITEMS.register("suspendium_hook", () -> new HookItem(ModToolTier.SUSPENDIUM, 3, -2.4f, new Item.Properties().group(TowerOfGod.TAB)));

    public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE_ENTITY = ENTITY_TYPES.register("lighthouse_entity", () -> EntityType.Builder.create(new LighthouseEntity.Factory(), EntityClassification.AMBIENT).size(0.9f, 0.9f).build(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_entity").toString()));
    public static final RegistryObject<Block> LIGHT_BLOCK = BLOCKS.register("light_block", LightBlock::new);
    public static final RegistryObject<Item> LIGHTHOUSE_ITEM = ITEMS.register("lighthouse_item", LighthouseItem::new);
    public static final RegistryObject<ContainerType<LighthouseEntity.LighthouseContainer>> LIGHTHOUSE_CONTAINER = CONTAINER_TYPES.register("lighthouse_container", () -> IForgeContainerType.create(new LighthouseEntity.LighthouseContainer.Factory()));

    public static final RegistryObject<EntityType<ObserverEntity>> OBSERVER_ENTITY = ENTITY_TYPES.register("observer_entity", () -> EntityType.Builder.create(new ObserverEntity.Factory(), EntityClassification.AMBIENT).size(0.4f, 0.4f).build(new ResourceLocation(TowerOfGod.MOD_ID, "observer_entity").toString()));
    public static final RegistryObject<Item> OBSERVER_ITEM = ITEMS.register("observer_item", ObserverItem::new);

    public static final RegistryObject<EntityType<RegularEntity>> REGULAR_ENTITY = ENTITY_TYPES.register("regular_entity", () -> EntityType.Builder.create(new RegularEntity.Factory(), EntityClassification.CREATURE).size(0.6f, 1.8f).build(new ResourceLocation(TowerOfGod.MOD_ID, "regular_entity").toString()));

    public static final RegistryObject<EntityType<ShinsuEntity>> SHINSU_ENTITY = ENTITY_TYPES.register("shinsu_entity", () -> EntityType.Builder.create(new ShinsuEntity.Factory(), EntityClassification.MISC).size(0.4f, 0.4f).build(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_entity").toString()));

    public static final RegistryObject<ReverseFlowEffect> REVERSE_FLOW_EFFECT = EFFECTS.register("reverse_flow_effect", ReverseFlowEffect::new);

    public static final RegistryObject<EntityType<ClickerEntity>> CLICKER_ENTITY = ENTITY_TYPES.register("clicker_entity", () -> EntityType.Builder.create(new ClickerEntity.Factory(), EntityClassification.MISC).size(1, 1).build(new ResourceLocation(TowerOfGod.MOD_ID, "clicker_entity").toString()));
    public static final RegistryObject<ClickerItem> CLICKER_ITEM = ITEMS.register("clicker_item", ClickerItem::new);

    public static final RegistryObject<ShinsuShovel> SHINSU_SHOVEL = ITEMS.register("shinsu_shovel", () -> new ShinsuShovel(0.5f, -2));
    public static final RegistryObject<ShinsuPickaxe> SHINSU_PICKAXE = ITEMS.register("shinsu_pickaxe", () -> new ShinsuPickaxe(0, -1.6f));
    public static final RegistryObject<ShinsuAxe> SHINSU_AXE = ITEMS.register("shinsu_axe", () -> new ShinsuAxe(4, -2));
    public static final RegistryObject<ShinsuSword> SHINSU_SWORD = ITEMS.register("shinsu_sword", () -> new ShinsuSword(2, -0.8f));
    public static final RegistryObject<ShinsuHoe> SHINSU_HOE = ITEMS.register("shinsu_hoe", () -> new ShinsuHoe(-1, 4));
    public static final RegistryObject<ShinsuBow> SHINSU_BOW = ITEMS.register("shinsu_bow", ShinsuBow::new);
    public static final RegistryObject<EntityType<ShinsuArrowEntity>> SHINSU_ARROW_ENTITY = ENTITY_TYPES.register("shinsu_arrow_entity", () -> EntityType.Builder.create(new ShinsuArrowEntity.Factory(), EntityClassification.MISC).size(0.4f, 0.4f).build(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_arrow_entity").toString()));
    public static final RegistryObject<ShinsuToolLootModifier.Serializer> SHINSU_TOOL_LOOT_MODIFIER = LOOT_MODIFIERS.register("shinsu_tool_loot_modifier", ShinsuToolLootModifier.Serializer::new);

    public static final List<RegistryObject<? extends Item>> SHINSU_ITEMS = new ArrayList<>(Arrays.asList(SHINSU_SHOVEL, SHINSU_PICKAXE, SHINSU_AXE, SHINSU_SWORD, SHINSU_HOE, SHINSU_BOW));

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        CONTAINER_TYPES.register(bus);
        ENTITY_TYPES.register(bus);
        EFFECTS.register(bus);
        LOOT_MODIFIERS.register(bus);
    }
}
