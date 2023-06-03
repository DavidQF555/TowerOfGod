package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
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
public final class ShinsuShapeRegistry {

    public static final ResourceLocation ADVANCEMENT = new ResourceLocation(TowerOfGod.MOD_ID, "shapes");
    public static final DeferredRegister<ShinsuShape> SHAPES = DeferredRegister.create(ShinsuShape.class, TowerOfGod.MOD_ID);
    public static final RegistryObject<ShinsuShape> SHOVEL = register("shovel", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_SHOVEL.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> PICKAXE = register("pickaxe", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_PICKAXE.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> AXE = register("axe", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_AXE.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> SWORD = register("sword", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_SWORD.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> HOE = register("hoe", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_HOE.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> BOW = register("bow", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_BOW.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> SPEAR = register("spear", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_SPEAR.get().getDefaultInstance()));
    public static final RegistryObject<ShinsuShape> HOOK = register("hook", () -> new ShinsuShape(() -> ItemRegistry.SHINSU_HOOK.get().getDefaultInstance()));

    private static IForgeRegistry<ShinsuShape> registry = null;

    private ShinsuShapeRegistry() {
    }

    public static IForgeRegistry<ShinsuShape> getRegistry() {
        return registry;
    }

    private static RegistryObject<ShinsuShape> register(String name, Supplier<ShinsuShape> shape) {
        return SHAPES.register(name, shape);
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ShinsuShape>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "shinsu_shapes")).setType(ShinsuShape.class).create();
    }

}