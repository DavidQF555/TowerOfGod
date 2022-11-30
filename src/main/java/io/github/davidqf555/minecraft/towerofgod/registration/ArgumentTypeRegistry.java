package io.github.davidqf555.minecraft.towerofgod.registration;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.commands.ShinsuAttributeArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.commands.ShinsuShapeArgumentType;
import io.github.davidqf555.minecraft.towerofgod.common.commands.ShinsuTechniqueArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = TowerOfGod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ArgumentTypeRegistry {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> INFO = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, TowerOfGod.MOD_ID);

    public static final RegistryObject<SingletonArgumentInfo<ShinsuTechniqueArgumentType>> SHINSU_TECHNIQUE = register("shinsu_technique", () -> SingletonArgumentInfo.contextFree(ShinsuTechniqueArgumentType::new));
    public static final RegistryObject<SingletonArgumentInfo<ShinsuShapeArgumentType>> SHINSU_SHAPE = register("shinsu_shape", () -> SingletonArgumentInfo.contextFree(ShinsuShapeArgumentType::new));
    public static final RegistryObject<SingletonArgumentInfo<ShinsuAttributeArgumentType>> SHINSU_ATTRIBUTE = register("shinsu_attribute", () -> SingletonArgumentInfo.contextFree(ShinsuAttributeArgumentType::new));

    private ArgumentTypeRegistry() {
    }

    private static <T extends ArgumentType<?>, M extends ArgumentTypeInfo.Template<T>, A extends ArgumentTypeInfo<T, M>> RegistryObject<A> register(String name, Supplier<A> info) {
        return INFO.register(name, info);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        ArgumentTypeInfos.registerByClass(ShinsuTechniqueArgumentType.class, SHINSU_TECHNIQUE.get());
        ArgumentTypeInfos.registerByClass(ShinsuAttributeArgumentType.class, SHINSU_ATTRIBUTE.get());
        ArgumentTypeInfos.registerByClass(ShinsuShapeArgumentType.class, SHINSU_SHAPE.get());
    }
}
