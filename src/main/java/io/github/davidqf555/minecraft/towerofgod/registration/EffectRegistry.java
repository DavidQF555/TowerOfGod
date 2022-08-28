package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.effects.BodyReinforcementEffect;
import io.github.davidqf555.minecraft.towerofgod.common.effects.ReverseFlowEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class EffectRegistry {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TowerOfGod.MOD_ID);

    public static final RegistryObject<ReverseFlowEffect> REVERSE_FLOW = register("reverse_flow", ReverseFlowEffect::new);
    public static final RegistryObject<BodyReinforcementEffect> BODY_REINFORCEMENT = register("body_reinforcement", BodyReinforcementEffect::new);

    private EffectRegistry() {
    }

    private static <T extends MobEffect> RegistryObject<T> register(String name, Supplier<T> effect) {
        return EFFECTS.register(name, effect);
    }

}
