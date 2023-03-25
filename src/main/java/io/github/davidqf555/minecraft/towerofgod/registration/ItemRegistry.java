package io.github.davidqf555.minecraft.towerofgod.registration;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.*;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TowerOfGod.MOD_ID);
    private static final List<Pair<Supplier<CreativeModeTab>, Supplier<? extends Item>>> TABS = new ArrayList<>();
    public static final RegistryObject<ShinsuUserSpawnEggItem> REGULAR_SPAWN_EGG = register("regular_spawn_egg", () -> CreativeModeTabs.SPAWN_EGGS, () -> new ShinsuUserSpawnEggItem(EntityRegistry.REGULAR, new int[]{1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100}, 0x000000, 0xFF0000, new Item.Properties()));
    public static final RegistryObject<ShinsuUserSpawnEggItem> RANKER_SPAWN_EGG = register("ranker_spawn_egg", () -> CreativeModeTabs.SPAWN_EGGS, () -> new ShinsuUserSpawnEggItem(EntityRegistry.RANKER, new int[]{1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100}, 0xFF0000, 0x000000, new Item.Properties()));
    public static final RegistryObject<ShinsuShovel> SHINSU_SHOVEL = register("shinsu_shovel", null, () -> new ShinsuShovel(0.5f, -2));
    public static final RegistryObject<ShinsuPickaxe> SHINSU_PICKAXE = register("shinsu_pickaxe", null, () -> new ShinsuPickaxe(0, -1.6f));
    public static final RegistryObject<ShinsuAxe> SHINSU_AXE = register("shinsu_axe", null, () -> new ShinsuAxe(4, -2));
    public static final RegistryObject<ShinsuSword> SHINSU_SWORD = register("shinsu_sword", null, () -> new ShinsuSword(2, -0.8f));
    public static final RegistryObject<ShinsuHoe> SHINSU_HOE = register("shinsu_hoe", null, () -> new ShinsuHoe(-1, 4));
    public static final RegistryObject<ShinsuBow> SHINSU_BOW = register("shinsu_bow", null, ShinsuBow::new);
    public static final List<RegistryObject<? extends Item>> SHINSU_ITEMS = ImmutableList.of(SHINSU_SHOVEL, SHINSU_PICKAXE, SHINSU_AXE, SHINSU_SWORD, SHINSU_HOE, SHINSU_BOW);
    private static CreativeModeTab tab = null;
    public static final RegistryObject<Item> SUSPENDIUM = register("suspendium", ItemRegistry::getTab, () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockItem> SUSPENDIUM_ORE = register("suspendium_ore", ItemRegistry::getTab, () -> new BlockItem(BlockRegistry.SUSPENDIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> SUSPENDIUM_BLOCK = register("suspendium_block", ItemRegistry::getTab, () -> new BlockItem(BlockRegistry.SUSPENDIUM_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<ShovelItem> SUSPENDIUM_SHOVEL = register("suspendium_shovel", ItemRegistry::getTab, () -> new ShovelItem(ModToolTier.SUSPENDIUM, 0.5f, -2, new Item.Properties()));
    public static final RegistryObject<PickaxeItem> SUSPENDIUM_PICKAXE = register("suspendium_pickaxe", ItemRegistry::getTab, () -> new PickaxeItem(ModToolTier.SUSPENDIUM, 0, -1.6f, new Item.Properties()));
    public static final RegistryObject<AxeItem> SUSPENDIUM_AXE = register("suspendium_axe", ItemRegistry::getTab, () -> new AxeItem(ModToolTier.SUSPENDIUM, 4, -2, new Item.Properties()));
    public static final RegistryObject<SwordItem> SUSPENDIUM_SWORD = register("suspendium_sword", ItemRegistry::getTab, () -> new SwordItem(ModToolTier.SUSPENDIUM, 2, -0.8f, new Item.Properties()));
    public static final RegistryObject<HoeItem> SUSPENDIUM_HOE = register("suspendium_hoe", ItemRegistry::getTab, () -> new HoeItem(ModToolTier.SUSPENDIUM, -1, 4, new Item.Properties()));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_HELMET = register("suspendium_helmet", ItemRegistry::getTab, () -> new ArmorItem(ModArmorTier.SUSPENDIUM, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_CHESTPLATE = register("suspendium_chestplate", ItemRegistry::getTab, () -> new ArmorItem(ModArmorTier.SUSPENDIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_LEGGINGS = register("suspendium_leggings", ItemRegistry::getTab, () -> new ArmorItem(ModArmorTier.SUSPENDIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<ArmorItem> SUSPENDIUM_BOOTS = register("suspendium_boots", ItemRegistry::getTab, () -> new ArmorItem(ModArmorTier.SUSPENDIUM, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final RegistryObject<NeedleItem> WOODEN_NEEDLE = register("wooden_needle", ItemRegistry::getTab, () -> new NeedleItem(Tiers.WOOD, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<NeedleItem> STONE_NEEDLE = register("stone_needle", ItemRegistry::getTab, () -> new NeedleItem(Tiers.STONE, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<NeedleItem> IRON_NEEDLE = register("iron_needle", ItemRegistry::getTab, () -> new NeedleItem(Tiers.IRON, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<NeedleItem> GOLDEN_NEEDLE = register("golden_needle", ItemRegistry::getTab, () -> new NeedleItem(Tiers.GOLD, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<NeedleItem> DIAMOND_NEEDLE = register("diamond_needle", ItemRegistry::getTab, () -> new NeedleItem(Tiers.DIAMOND, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<NeedleItem> NETHERITE_NEEDLE = register("netherite_needle", ItemRegistry::getTab, () -> new NeedleItem(Tiers.NETHERITE, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<NeedleItem> SUSPENDIUM_NEEDLE = register("suspendium_needle", ItemRegistry::getTab, () -> new NeedleItem(ModToolTier.SUSPENDIUM, 1, 1.6f, new Item.Properties()));
    public static final List<RegistryObject<NeedleItem>> NEEDLE_ITEMS = ImmutableList.of(WOODEN_NEEDLE, STONE_NEEDLE, IRON_NEEDLE, GOLDEN_NEEDLE, DIAMOND_NEEDLE, NETHERITE_NEEDLE, SUSPENDIUM_NEEDLE);
    public static final RegistryObject<HookItem> WOODEN_HOOK = register("wooden_hook", ItemRegistry::getTab, () -> new HookItem(Tiers.WOOD, 3, -3.2f, new Item.Properties()));
    public static final RegistryObject<HookItem> STONE_HOOK = register("stone_hook", ItemRegistry::getTab, () -> new HookItem(Tiers.STONE, 3, -3.2f, new Item.Properties()));
    public static final RegistryObject<HookItem> IRON_HOOK = register("iron_hook", ItemRegistry::getTab, () -> new HookItem(Tiers.IRON, 3, -3.2f, new Item.Properties()));
    public static final RegistryObject<HookItem> GOLDEN_HOOK = register("golden_hook", ItemRegistry::getTab, () -> new HookItem(Tiers.GOLD, 3, -3.2f, new Item.Properties()));
    public static final RegistryObject<HookItem> DIAMOND_HOOK = register("diamond_hook", ItemRegistry::getTab, () -> new HookItem(Tiers.DIAMOND, 3, -3.2f, new Item.Properties()));
    public static final RegistryObject<HookItem> NETHERITE_HOOK = register("netherite_hook", ItemRegistry::getTab, () -> new HookItem(Tiers.NETHERITE, 3, -3.2f, new Item.Properties()));
    public static final RegistryObject<HookItem> SUSPENDIUM_HOOK = register("suspendium_hook", ItemRegistry::getTab, () -> new HookItem(ModToolTier.SUSPENDIUM, 3, -2.4f, new Item.Properties()));
    public static final List<RegistryObject<HookItem>> HOOK_ITEMS = ImmutableList.of(WOODEN_HOOK, STONE_HOOK, IRON_HOOK, GOLDEN_HOOK, DIAMOND_HOOK, NETHERITE_HOOK, SUSPENDIUM_HOOK);
    public static final RegistryObject<DeviceItem> LIGHTHOUSE = register("lighthouse", ItemRegistry::getTab, () -> new DeviceItem((world, item) -> EntityRegistry.LIGHTHOUSE.get().create(world), new Item.Properties()));
    public static final RegistryObject<DeviceItem> OBSERVER = register("observer", ItemRegistry::getTab, () -> new DeviceItem((world, item) -> EntityRegistry.OBSERVER.get().create(world), new Item.Properties()));
    public static final List<RegistryObject<? extends Item>> COLORED_DEVICE_ITEMS = ImmutableList.of(LIGHTHOUSE, OBSERVER);
    public static final RegistryObject<ClickerItem> CLICKER = register("clicker", ItemRegistry::getTab, () -> new ClickerItem(new Item.Properties()));
    public static final RegistryObject<GuideItem> SHINSU_GUIDE = register("shinsu_guide", ItemRegistry::getTab, () -> new GuideItem(0xFF7E79DF, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<SpearItem> WOODEN_SPEAR = register("wooden_spear", ItemRegistry::getTab, () -> new SpearItem(Tiers.WOOD, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<SpearItem> STONE_SPEAR = register("stone_spear", ItemRegistry::getTab, () -> new SpearItem(Tiers.STONE, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<SpearItem> IRON_SPEAR = register("iron_spear", ItemRegistry::getTab, () -> new SpearItem(Tiers.IRON, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<SpearItem> GOLDEN_SPEAR = register("golden_spear", ItemRegistry::getTab, () -> new SpearItem(Tiers.GOLD, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<SpearItem> DIAMOND_SPEAR = register("diamond_spear", ItemRegistry::getTab, () -> new SpearItem(Tiers.DIAMOND, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<SpearItem> NETHERITE_SPEAR = register("netherite_spear", ItemRegistry::getTab, () -> new SpearItem(Tiers.NETHERITE, 1, -1.2f, new Item.Properties()));
    public static final RegistryObject<SpearItem> SUSPENDIUM_SPEAR = register("suspendium_spear", ItemRegistry::getTab, () -> new SpearItem(ModToolTier.SUSPENDIUM, 1, -1.2f, new Item.Properties()));
    public static final List<RegistryObject<SpearItem>> SPEARS = ImmutableList.of(WOODEN_SPEAR, STONE_SPEAR, IRON_SPEAR, GOLDEN_SPEAR, DIAMOND_SPEAR, NETHERITE_SPEAR, SUSPENDIUM_SPEAR);

    private ItemRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterCreativeModeTab(CreativeModeTabEvent.Register event) {
        tab = event.registerCreativeModeTab(new ResourceLocation(TowerOfGod.MOD_ID, "main"), builder -> builder.icon(() -> SUSPENDIUM.get().getDefaultInstance()).title(Component.translatable(Util.makeDescriptionId("itemGroup", new ResourceLocation(TowerOfGod.MOD_ID, "main")))));
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(CreativeModeTabEvent.BuildContents event) {
        CreativeModeTab tab = event.getTab();
        TABS.forEach(pair -> {
            if (tab.equals(pair.getFirst().get())) {
                Item item = pair.getSecond().get();
                if (item instanceof ShinsuUserSpawnEggItem) {
                    event.acceptAll(Arrays.stream(((ShinsuUserSpawnEggItem) item).getCreativeModeTabLevels()).mapToObj(level -> {
                        ItemStack stack = item.getDefaultInstance();
                        ((ShinsuUserSpawnEggItem) item).setLevel(stack, level);
                        return stack;
                    }).toList());
                } else {
                    event.accept(item);
                }
            }
        });
    }

    private static <T extends Item> RegistryObject<T> register(String name, @Nullable Supplier<CreativeModeTab> tab, Supplier<T> item) {
        RegistryObject<T> out = ITEMS.register(name, item);
        if (tab != null) {
            TABS.add(Pair.of(tab, out));
        }
        return out;
    }

    public static CreativeModeTab getTab() {
        return tab;
    }

}
