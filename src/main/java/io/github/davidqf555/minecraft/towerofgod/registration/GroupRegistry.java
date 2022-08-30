package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.entities.Group;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GroupRegistry {

    public static final DeferredRegister<Group> GROUPS = DeferredRegister.create(new ResourceLocation(TowerOfGod.MOD_ID, "groups"), TowerOfGod.MOD_ID);

    public static final RegistryObject<Group> ARIE = register("arie", () -> new Group(0xFFFFFFFF, () -> new ShinsuAttribute[0], () -> new ShinsuShape[]{ShinsuShapeRegistry.SWORD.get()}, () -> new ShinsuTechniqueType[0], item -> item instanceof SwordItem, 1, 1, 1, 1));
    public static final RegistryObject<Group> EURASIA = register("eurasia", () -> new Group(0xFF00FF7F, () -> new ShinsuAttribute[]{ShinsuAttributeRegistry.WIND.get()}, () -> new ShinsuShape[0], () -> new ShinsuTechniqueType[0], item -> false, 1, 1.5, 2, 2));
    public static final RegistryObject<Group> HA = register("ha", () -> new Group(0xFFDC143C, () -> new ShinsuAttribute[0], () -> new ShinsuShape[0], () -> new ShinsuTechniqueType[0], item -> item instanceof HookItem, 2, 1, 1, 1));
    public static final RegistryObject<Group> KHUN = register("khun", () -> new Group(0xFF6495ED, () -> new ShinsuAttribute[]{ShinsuAttributeRegistry.ICE.get(), ShinsuAttributeRegistry.LIGHTNING.get()}, () -> new ShinsuShape[0], () -> new ShinsuTechniqueType[0], item -> false, 1, 1, 1, 1));
    public static final RegistryObject<Group> YEON = register("yeon", () -> new Group(0xFFFF1493, () -> new ShinsuAttribute[]{ShinsuAttributeRegistry.FIRE.get()}, () -> new ShinsuShape[0], () -> new ShinsuTechniqueType[0], item -> false, 1, 1.2, 1.5, 1));
    private static Supplier<IForgeRegistry<Group>> registry = null;

    private GroupRegistry() {
    }

    private static RegistryObject<Group> register(String name, Supplier<Group> group) {
        return GROUPS.register(name, group);
    }

    public static IForgeRegistry<Group> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Group>().setName(new ResourceLocation(TowerOfGod.MOD_ID, "groups")));
    }

}
