package io.github.davidqf555.minecraft.towerofgod.registration;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.*;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechnique;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueRegistry;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TowerOfGod.MOD_ID);

    public static final RegistryObject<Item> SUSPENDIUM = register("suspendium", () -> new Item(new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<BlockItem> SUSPENDIUM_ORE = register("suspendium_ore", () -> new BlockItem(BlockRegistry.SUSPENDIUM_ORE.get(), new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<BlockItem> SUSPENDIUM_BLOCK = register("suspendium_block", () -> new BlockItem(BlockRegistry.SUSPENDIUM_BLOCK.get(), new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<BlockItem> FLOOR_TELEPORTATION_TERMINAL = register("floor_teleportation_terminal", () -> new BlockItem(BlockRegistry.FLOOR_TELEPORTATION_TERMINAL.get(), new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL = register("suspendium_shovel", () -> new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE = register("suspendium_pickaxe", () -> new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<AxeItem> SUSPENDIUM_AXE = register("suspendium_axe", () -> new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD = register("suspendium_sword", () -> new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HoeItem> SUSPENDIUM_HOE = register("suspendium_hoe", () -> new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET = register("suspendium_helmet", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.HEAD, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE = register("suspendium_chestplate", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.CHEST, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS = register("suspendium_leggings", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.LEGS, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS = register("suspendium_boots", () -> new ArmorItem(ModArmorTier.SUSPENDIUM, EquipmentSlotType.FEET, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> WOODEN_NEEDLE = register("wooden_needle", () -> new NeedleItem(ItemTier.WOOD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> STONE_NEEDLE = register("stone_needle", () -> new NeedleItem(ItemTier.STONE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> IRON_NEEDLE = register("iron_needle", () -> new NeedleItem(ItemTier.IRON, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> GOLDEN_NEEDLE = register("golden_needle", () -> new NeedleItem(ItemTier.GOLD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> DIAMOND_NEEDLE = register("diamond_needle", () -> new NeedleItem(ItemTier.DIAMOND, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> NETHERITE_NEEDLE = register("netherite_needle", () -> new NeedleItem(ItemTier.NETHERITE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<NeedleItem> SUSPENDIUM_NEEDLE = register("suspendium_needle", () -> new NeedleItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final List<RegistryObject<NeedleItem>> NEEDLE_ITEMS = ImmutableList.of(WOODEN_NEEDLE, STONE_NEEDLE, IRON_NEEDLE, GOLDEN_NEEDLE, DIAMOND_NEEDLE, NETHERITE_NEEDLE, SUSPENDIUM_NEEDLE);
    public static final RegistryObject<HookItem> WOODEN_HOOK = register("wooden_hook", () -> new HookItem(ItemTier.WOOD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> STONE_HOOK = register("stone_hook", () -> new HookItem(ItemTier.STONE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> IRON_HOOK = register("iron_hook", () -> new HookItem(ItemTier.IRON, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> GOLDEN_HOOK = register("golden_hook", () -> new HookItem(ItemTier.GOLD, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> DIAMOND_HOOK = register("diamond_hook", () -> new HookItem(ItemTier.DIAMOND, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> NETHERITE_HOOK = register("netherite_hook", () -> new HookItem(ItemTier.NETHERITE, 3, -3.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<HookItem> SUSPENDIUM_HOOK = register("suspendium_hook", () -> new HookItem(ModToolTier.SUSPENDIUM, 3, -2.4f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final List<RegistryObject<HookItem>> HOOK_ITEMS = ImmutableList.of(WOODEN_HOOK, STONE_HOOK, IRON_HOOK, GOLDEN_HOOK, DIAMOND_HOOK, NETHERITE_HOOK, SUSPENDIUM_HOOK);
    public static final RegistryObject<DeviceItem> LIGHTHOUSE = register("lighthouse", () -> new DeviceItem((world, item) -> EntityRegistry.LIGHTHOUSE.get().create(world), new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<DeviceItem> OBSERVER = register("observer", () -> new DeviceItem((world, item) -> EntityRegistry.OBSERVER.get().create(world), new Item.Properties().group(TowerOfGod.TAB)));
    public static final List<RegistryObject<? extends Item>> COLORED_DEVICE_ITEMS = ImmutableList.of(LIGHTHOUSE, OBSERVER);
    public static final RegistryObject<ClickerItem> CLICKER = register("clicker", () -> new ClickerItem(new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<ShinsuShovel> SHINSU_SHOVEL = register("shinsu_shovel", () -> new ShinsuShovel(0.5f, -2));
    public static final RegistryObject<ShinsuPickaxe> SHINSU_PICKAXE = register("shinsu_pickaxe", () -> new ShinsuPickaxe(0, -1.6f));
    public static final RegistryObject<ShinsuAxe> SHINSU_AXE = register("shinsu_axe", () -> new ShinsuAxe(4, -2));
    public static final RegistryObject<ShinsuSword> SHINSU_SWORD = register("shinsu_sword", () -> new ShinsuSword(2, -0.8f));
    public static final RegistryObject<ShinsuHoe> SHINSU_HOE = register("shinsu_hoe", () -> new ShinsuHoe(-1, 4));
    public static final RegistryObject<ShinsuBow> SHINSU_BOW = register("shinsu_bow", ShinsuBow::new);
    public static final List<RegistryObject<? extends Item>> SHINSU_ITEMS = ImmutableList.of(SHINSU_SHOVEL, SHINSU_PICKAXE, SHINSU_AXE, SHINSU_SWORD, SHINSU_HOE, SHINSU_BOW);
    public static final RegistryObject<GuideItem> CONTROL_GUIDE = register("control_guide", () -> new GuideItem(() -> new ShinsuTechnique[]{ShinsuTechniqueRegistry.BODY_REINFORCEMENT.get(), ShinsuTechniqueRegistry.BLACK_FISH.get(), ShinsuTechniqueRegistry.SHINSU_BLAST.get()}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".control_guide.author"), 0xFF7E79DF));
    public static final RegistryObject<GuideItem> DISRUPTION_GUIDE = register("disruption_guide", () -> new GuideItem(() -> new ShinsuTechnique[]{ShinsuTechniqueRegistry.FLARE_WAVE_EXPLOSION.get(), ShinsuTechniqueRegistry.REVERSE_FLOW_CONTROL.get()}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".disruption_guide.author"), 0xFF3D3D3D));
    public static final RegistryObject<GuideItem> MANIFEST_GUIDE = register("manifest_guide", () -> new GuideItem(() -> new ShinsuTechnique[]{ShinsuTechniqueRegistry.MANIFEST.get()}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".manifest_guide.author"), 0xFF2D3771));
    public static final RegistryObject<GuideItem> DEVICE_CONTROL_GUIDE = register("device_control_guide", () -> new GuideItem(() -> new ShinsuTechnique[]{ShinsuTechniqueRegistry.MOVE_DEVICES.get(), ShinsuTechniqueRegistry.LIGHTHOUSE_FLOW_CONTROL.get(), ShinsuTechniqueRegistry.SCOUT.get(), ShinsuTechniqueRegistry.FOLLOW_OWNER.get()}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".device_control_guide.author"), 0xFFD8D8D8));
    public static final RegistryObject<GuideItem> LIGHTNING_CONTROL_GUIDE = register("lightning_control_guide", () -> new GuideItem(() -> new ShinsuTechnique[]{ShinsuTechniqueRegistry.CHANNEL_LIGHTNING.get(), ShinsuTechniqueRegistry.FLASH.get()}, new TranslationTextComponent("item." + TowerOfGod.MOD_ID + ".lightning_control_guide.author"), 0xFFFFF96D));
    public static final RegistryObject<SpearItem> WOODEN_SPEAR = register("wooden_spear", () -> new SpearItem(ItemTier.WOOD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> STONE_SPEAR = register("stone_spear", () -> new SpearItem(ItemTier.STONE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> IRON_SPEAR = register("iron_spear", () -> new SpearItem(ItemTier.IRON, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> GOLDEN_SPEAR = register("golden_spear", () -> new SpearItem(ItemTier.GOLD, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> DIAMOND_SPEAR = register("diamond_spear", () -> new SpearItem(ItemTier.DIAMOND, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> NETHERITE_SPEAR = register("netherite_spear", () -> new SpearItem(ItemTier.NETHERITE, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final RegistryObject<SpearItem> SUSPENDIUM_SPEAR = register("suspendium_spear", () -> new SpearItem(ModToolTier.SUSPENDIUM, 1, -1.2f, new Item.Properties().group(TowerOfGod.TAB)));
    public static final List<RegistryObject<SpearItem>> SPEARS = ImmutableList.of(WOODEN_SPEAR, STONE_SPEAR, IRON_SPEAR, GOLDEN_SPEAR, DIAMOND_SPEAR, NETHERITE_SPEAR, SUSPENDIUM_SPEAR);

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

}
