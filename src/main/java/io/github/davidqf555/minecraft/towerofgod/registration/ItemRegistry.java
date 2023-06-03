package io.github.davidqf555.minecraft.towerofgod.registration;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.*;
import io.github.davidqf555.minecraft.towerofgod.common.items.shinsu.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TowerOfGod.MOD_ID);

    public static final RegistryObject<Item> SUSPENDIUM = register("suspendium", () -> new Item(new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<BlockItem> SUSPENDIUM_ORE = register("suspendium_ore", () -> new BlockItem(BlockRegistry.SUSPENDIUM_ORE.get(), new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<BlockItem> SUSPENDIUM_BLOCK = register("suspendium_block", () -> new BlockItem(BlockRegistry.SUSPENDIUM_BLOCK.get(), new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL = register("suspendium_shovel", () -> new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE = register("suspendium_pickaxe", () -> new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<AxeItem> SUSPENDIUM_AXE = register("suspendium_axe", () -> new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD = register("suspendium_sword", () -> new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HoeItem> SUSPENDIUM_HOE = register("suspendium_hoe", () -> new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET = register("suspendium_helmet", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlot.HEAD, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE = register("suspendium_chestplate", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlot.CHEST, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS = register("suspendium_leggings", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlot.LEGS, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS = register("suspendium_boots", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlot.FEET, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> WOODEN_NEEDLE = register("wooden_needle", () -> new NeedleItem(Tiers.WOOD, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> STONE_NEEDLE = register("stone_needle", () -> new NeedleItem(Tiers.STONE, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> IRON_NEEDLE = register("iron_needle", () -> new NeedleItem(Tiers.IRON, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> GOLDEN_NEEDLE = register("golden_needle", () -> new NeedleItem(Tiers.GOLD, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> DIAMOND_NEEDLE = register("diamond_needle", () -> new NeedleItem(Tiers.DIAMOND, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> NETHERITE_NEEDLE = register("netherite_needle", () -> new NeedleItem(Tiers.NETHERITE, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> SUSPENDIUM_NEEDLE = register("suspendium_needle", () -> new NeedleItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final List<RegistryObject<NeedleItem>> NEEDLE_ITEMS = ImmutableList.of(WOODEN_NEEDLE, STONE_NEEDLE, IRON_NEEDLE, GOLDEN_NEEDLE, DIAMOND_NEEDLE, NETHERITE_NEEDLE, SUSPENDIUM_NEEDLE);
    public static final RegistryObject<HookItem> WOODEN_HOOK = register("wooden_hook", () -> new HookItem(Tiers.WOOD, 3, -3.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> STONE_HOOK = register("stone_hook", () -> new HookItem(Tiers.STONE, 3, -3.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> IRON_HOOK = register("iron_hook", () -> new HookItem(Tiers.IRON, 3, -3.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> GOLDEN_HOOK = register("golden_hook", () -> new HookItem(Tiers.GOLD, 3, -3.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> DIAMOND_HOOK = register("diamond_hook", () -> new HookItem(Tiers.DIAMOND, 3, -3.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> NETHERITE_HOOK = register("netherite_hook", () -> new HookItem(Tiers.NETHERITE, 3, -3.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> SUSPENDIUM_HOOK = register("suspendium_hook", () -> new HookItem(ModToolTier.SUSPENDIUM, 3, -2.4f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final List<RegistryObject<? extends HookItem>> CRAFTABLE_HOOKS = ImmutableList.of(WOODEN_HOOK, STONE_HOOK, IRON_HOOK, GOLDEN_HOOK, DIAMOND_HOOK, NETHERITE_HOOK, SUSPENDIUM_HOOK);
    public static final RegistryObject<ShinsuHook> SHINSU_HOOK = register("shinsu_hook", () -> new ShinsuHook(3, -3.2f));
    public static final List<RegistryObject<? extends HookItem>> HOOK_ITEMS = ImmutableList.<RegistryObject<? extends HookItem>>builder().addAll(CRAFTABLE_HOOKS).add(SHINSU_HOOK).build();
    public static final RegistryObject<DeviceItem> LIGHTHOUSE = register("lighthouse", () -> new DeviceItem((world, item) -> EntityRegistry.LIGHTHOUSE.get().create(world), new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<DeviceItem> OBSERVER = register("observer", () -> new DeviceItem((world, item) -> EntityRegistry.OBSERVER.get().create(world), new Item.Properties().tab(TowerOfGod.TAB)));
    public static final List<RegistryObject<? extends Item>> COLORED_DEVICE_ITEMS = ImmutableList.of(LIGHTHOUSE, OBSERVER);
    public static final RegistryObject<ClickerItem> CLICKER = register("clicker", () -> new ClickerItem(new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ShinsuShovel> SHINSU_SHOVEL = register("shinsu_shovel", () -> new ShinsuShovel(0.5f, -2));
    public static final RegistryObject<ShinsuPickaxe> SHINSU_PICKAXE = register("shinsu_pickaxe", () -> new ShinsuPickaxe(0, -1.6f));
    public static final RegistryObject<ShinsuAxe> SHINSU_AXE = register("shinsu_axe", () -> new ShinsuAxe(4, -2));
    public static final RegistryObject<ShinsuSword> SHINSU_SWORD = register("shinsu_sword", () -> new ShinsuSword(2, -0.8f));
    public static final RegistryObject<ShinsuHoe> SHINSU_HOE = register("shinsu_hoe", () -> new ShinsuHoe(-1, 4));
    public static final RegistryObject<ShinsuBow> SHINSU_BOW = register("shinsu_bow", ShinsuBow::new);
    public static final RegistryObject<ShinsuSpear> SHINSU_SPEAR = register("shinsu_spear", ShinsuSpear::new);
    public static final List<RegistryObject<? extends Item>> SHINSU_ITEMS = ImmutableList.of(SHINSU_SHOVEL, SHINSU_PICKAXE, SHINSU_AXE, SHINSU_SWORD, SHINSU_HOE, SHINSU_BOW, SHINSU_SPEAR, SHINSU_HOOK);
    public static final RegistryObject<GuideItem> SHINSU_GUIDE = register("shinsu_guide", () -> new GuideItem(0xFF7E79DF, new Item.Properties().tab(TowerOfGod.TAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SpearItem> WOODEN_SPEAR = register("wooden_spear", () -> new SpearItem(Tiers.WOOD, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> STONE_SPEAR = register("stone_spear", () -> new SpearItem(Tiers.STONE, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> IRON_SPEAR = register("iron_spear", () -> new SpearItem(Tiers.IRON, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> GOLDEN_SPEAR = register("golden_spear", () -> new SpearItem(Tiers.GOLD, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> DIAMOND_SPEAR = register("diamond_spear", () -> new SpearItem(Tiers.DIAMOND, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> NETHERITE_SPEAR = register("netherite_spear", () -> new SpearItem(Tiers.NETHERITE, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> SUSPENDIUM_SPEAR = register("suspendium_spear", () -> new SpearItem(ModToolTier.SUSPENDIUM, 1, -1.2f, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final List<RegistryObject<? extends SpearItem>> CRAFTABLE_SPEARS = ImmutableList.of(WOODEN_SPEAR, STONE_SPEAR, IRON_SPEAR, GOLDEN_SPEAR, DIAMOND_SPEAR, NETHERITE_SPEAR, SUSPENDIUM_SPEAR);
    public static final List<RegistryObject<? extends SpearItem>> SPEARS = ImmutableList.<RegistryObject<? extends SpearItem>>builder().addAll(CRAFTABLE_SPEARS).add(SHINSU_SPEAR).build();
    public static final RegistryObject<ShinsuUserSpawnEggItem> REGULAR_SPAWN_EGG = register("regular_spawn_egg", () -> new ShinsuUserSpawnEggItem(EntityRegistry.REGULAR, new int[]{1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100}, 0x000000, 0xFF0000, new Item.Properties().tab(TowerOfGod.TAB)));
    public static final RegistryObject<ShinsuUserSpawnEggItem> RANKER_SPAWN_EGG = register("ranker_spawn_egg", () -> new ShinsuUserSpawnEggItem(EntityRegistry.RANKER, new int[]{1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100}, 0xFF0000, 0x000000, new Item.Properties().tab(TowerOfGod.TAB)));

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

}
