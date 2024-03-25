package io.github.davidqf555.minecraft.towerofgod.registration.shinsu;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.conditions.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MobUseConditionRegistry {

    public static final ResourceKey<Registry<MobUseConditionType>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(TowerOfGod.MOD_ID, "mob_use_conditions"));
    public static final DeferredRegister<MobUseConditionType> CONDITIONS = DeferredRegister.create(REGISTRY, TowerOfGod.MOD_ID);
    public static final RegistryObject<MobUseConditionType> ALWAYS = register("always", () -> new MobUseConditionType(Codec.unit(MobUseCondition.ALWAYS)));
    public static final RegistryObject<MobUseConditionType> COMBINATION = register("combination", () -> new MobUseConditionType(CombinationCondition.CODEC.get()));
    public static final RegistryObject<MobUseConditionType> GEAR = register("gear", () -> new MobUseConditionType(GearCondition.CODEC));
    public static final RegistryObject<MobUseConditionType> HAS_TARGET = register("target", () -> new MobUseConditionType(HasTargetCondition.CODEC));
    public static final RegistryObject<MobUseConditionType> TARGET_DISTANCE = register("target_distance", () -> new MobUseConditionType(TargetDistanceCondition.CODEC));
    private static Supplier<IForgeRegistry<MobUseConditionType>> registry = null;

    private MobUseConditionRegistry() {
    }

    private static RegistryObject<MobUseConditionType> register(String name, Supplier<MobUseConditionType> condition) {
        return CONDITIONS.register(name, condition);
    }

    public static IForgeRegistry<MobUseConditionType> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<MobUseConditionType>().setType(MobUseConditionType.class).setName(REGISTRY.location()));
    }

}
