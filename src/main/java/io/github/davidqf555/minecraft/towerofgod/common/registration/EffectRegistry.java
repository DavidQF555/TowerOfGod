package io.github.davidqf555.minecraft.towerofgod.common.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.effects.BodyReinforcementEffect;
import io.github.davidqf555.minecraft.towerofgod.common.effects.ReverseFlowEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class EffectRegistry {

    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, TowerOfGod.MOD_ID);

    public static final RegistryObject<ReverseFlowEffect> REVERSE_FLOW = register("reverse_flow", ReverseFlowEffect::new);
    public static final RegistryObject<BodyReinforcementEffect> BODY_REINFORCEMENT = register("body_reinforcement", BodyReinforcementEffect::new);

    private EffectRegistry() {
    }

    private static <T extends Effect> RegistryObject<T> register(String name, Supplier<T> effect) {
        return EFFECTS.register(name, effect);
    }

}
