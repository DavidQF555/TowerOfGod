package com.davidqf.towerofgodmod.util;

import com.davidqf.towerofgodmod.TowerOfGod;
import com.davidqf.towerofgodmod.armor.ModArmorTier;
import com.davidqf.towerofgodmod.blocks.*;
import com.davidqf.towerofgodmod.entities.LighthouseEntity;
import com.davidqf.towerofgodmod.items.*;
import com.davidqf.towerofgodmod.tools.ModToolTier;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class RegistryHandler {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TowerOfGod.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TowerOfGod.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, TowerOfGod.MOD_ID);

	public static final RegistryObject<Item> SUSPENDIUM = ITEMS.register("suspendium", BasicItem::new);
	public static final RegistryObject<Block> SUSPENDIUM_ORE = BLOCKS.register("suspendium_ore", SuspendiumOre::new);
	public static final RegistryObject<Item> SUSPENDIUM_ORE_ITEM = ITEMS.register("suspendium_ore", () -> new BlockItemBase(SUSPENDIUM_ORE.get()));
	public static final RegistryObject<Block> SUSPENDIUM_BLOCK = BLOCKS.register("suspendium_block", SuspendiumBlock::new);
	public static final RegistryObject<Item> SUSPENDIUM_BLOCK_ITEM = ITEMS.register("suspendium_block", () -> new BlockItemBase(SUSPENDIUM_BLOCK.get()));
	public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL = ITEMS.register("suspendium_shovel", () -> new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE = ITEMS.register("suspendium_pickaxe", () -> new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<AxeItem> SUSPENDIUM_AXE = ITEMS.register("suspendium_axe", () -> new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD = ITEMS.register("suspendium_sword", () -> new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<HoeItem> SUSPENDIUM_HOE = ITEMS.register("suspendium_hoe", () -> new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET = ITEMS.register("suspendium_helmet", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.HEAD, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE = ITEMS.register("suspendium_chestplate", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.CHEST, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS = ITEMS.register("suspendium_leggings", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.LEGS, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS = ITEMS.register("suspendium_boots", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.FEET, new Item.Properties().group(TowerOfGod.TAB)));

	public static final RegistryObject<SwordItem> WOODEN_NEEDLE = ITEMS.register("wooden_needle", () -> new SwordItem(ItemTier.WOOD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> STONE_NEEDLE = ITEMS.register("stone_needle", () -> new SwordItem(ItemTier.STONE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> IRON_NEEDLE = ITEMS.register("iron_needle", () -> new SwordItem(ItemTier.IRON, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> GOLDEN_NEEDLE = ITEMS.register("golden_needle", () -> new SwordItem(ItemTier.GOLD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> DIAMOND_NEEDLE = ITEMS.register("diamond_needle", () -> new SwordItem(ItemTier.DIAMOND, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> NETHERITE_NEEDLE = ITEMS.register("netherite_needle", () -> new SwordItem(ItemTier.NETHERITE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
	public static final RegistryObject<SwordItem> SUSPENDIUM_NEEDLE = ITEMS.register("suspendium_needle", () -> new SwordItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties().group(TowerOfGod.TAB)));

	public static final RegistryObject<EntityType<LighthouseEntity>> LIGHTHOUSE_ENTITY = ENTITY_TYPES.register("lighthouse_entity", () -> EntityType.Builder.create(new LighthouseEntity.Factory(), EntityClassification.AMBIENT).size(1, 1).build(new ResourceLocation(TowerOfGod.MOD_ID, "lighthouse_entity").toString()));
	
	public static final RegistryObject<Item> LIGHTHOUSE_ITEM = ITEMS.register("lighthouse_item", LighthouseItem::new);

	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		BLOCKS.register(bus);
		ENTITY_TYPES.register(bus);
	}
}
