package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.suitability.StatSuitabilityCalculator;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.suitability.SuitabilityCalculator;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.block.IGrowable;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShinsuShapeRegistry {

    public static final DeferredRegister<ShinsuShape> SHAPES = DeferredRegister.create(ShinsuShape.class, TowerOfGod.MOD_ID);
    public static final RegistryObject<ShinsuShape> SHOVEL = register("shovel", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_SHOVEL.get().getDefaultInstance(), SuitabilityCalculator.sum(toList(Stats.BLOCK_MINED.iterator()).stream().filter(stat -> stat.getValue().getHarvestTool(stat.getValue().getDefaultState()) == ToolType.SHOVEL).map(StatSuitabilityCalculator::new).toArray(SuitabilityCalculator[]::new)).scale(0.1)));
    public static final RegistryObject<ShinsuShape> PICKAXE = register("pickaxe", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_PICKAXE.get().getDefaultInstance(), SuitabilityCalculator.sum(toList(Stats.BLOCK_MINED.iterator()).stream().filter(stat -> stat.getValue().getHarvestTool(stat.getValue().getDefaultState()) == ToolType.PICKAXE).map(StatSuitabilityCalculator::new).toArray(SuitabilityCalculator[]::new)).scale(0.1)));
    public static final RegistryObject<ShinsuShape> AXE = register("axe", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_AXE.get().getDefaultInstance(), SuitabilityCalculator.sum(toList(Stats.BLOCK_MINED.iterator()).stream().filter(stat -> stat.getValue().getHarvestTool(stat.getValue().getDefaultState()) == ToolType.AXE).map(StatSuitabilityCalculator::new).toArray(SuitabilityCalculator[]::new)).scale(0.1)));
    public static final RegistryObject<ShinsuShape> SWORD = register("sword", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_SWORD.get().getDefaultInstance(), new StatSuitabilityCalculator(Stats.CUSTOM.get(Stats.DAMAGE_DEALT)).scale(0.1)));
    public static final RegistryObject<ShinsuShape> HOE = register("hoe", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_HOE.get().getDefaultInstance(), SuitabilityCalculator.sum(toList(Stats.BLOCK_MINED.iterator()).stream().filter(stat -> stat.getValue() instanceof IGrowable).map(StatSuitabilityCalculator::new).toArray(SuitabilityCalculator[]::new)).scale(4)));
    public static final RegistryObject<ShinsuShape> BOW = register("bow", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_BOW.get().getDefaultInstance(), new StatSuitabilityCalculator(Stats.ITEM_USED.get(Items.BOW)).scale(2)));
    private static IForgeRegistry<ShinsuShape> registry = null;

    private ShinsuShapeRegistry() {
    }

    public static IForgeRegistry<ShinsuShape> getRegistry() {
        return registry;
    }

    private static RegistryObject<ShinsuShape> register(String name, Supplier<ShinsuShape> shape) {
        return SHAPES.register(name, shape);
    }

    private static <T> List<T> toList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);
        return list;
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ShinsuShape>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_shapes")).setType(ShinsuShape.class).create();
    }

}