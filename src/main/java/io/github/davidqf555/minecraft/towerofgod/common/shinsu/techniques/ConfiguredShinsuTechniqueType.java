package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class ConfiguredShinsuTechniqueType<C extends ShinsuTechniqueConfig, S> extends ForgeRegistryEntry<ConfiguredShinsuTechniqueType<?, ?>> {

    public static final Codec<ConfiguredShinsuTechniqueType<?, ?>> DIRECT_CODEC = ShinsuTechniqueType.CODEC.dispatch(ConfiguredShinsuTechniqueType::getType, ShinsuTechniqueType::configuredCodec);
    public static final RegistryFileCodec<ConfiguredShinsuTechniqueType<?, ?>> CODEC = RegistryFileCodec.create(ConfiguredTechniqueTypeRegistry.REGISTRY, DIRECT_CODEC);
    private final ShinsuTechniqueType<C, S> type;
    private final C config;

    protected ConfiguredShinsuTechniqueType(ShinsuTechniqueType<C, S> type, C config) {
        super();
        this.type = type;
        this.config = config;
    }

    public void tick(LivingEntity user, ShinsuTechniqueInstance<C, S> inst) {
        getType().tick(user, inst);
    }

    public void onEnd(LivingEntity user, ShinsuTechniqueInstance<C, S> inst) {
        getType().onEnd(user, inst);
    }

    public ShinsuTechniqueType<C, S> getType() {
        return type;
    }

    public C getConfig() {
        return config;
    }

    public boolean cast(LivingEntity user, @Nullable LivingEntity target) {
        S data = getType().onUse(user, getConfig(), target);
        if (data == null) {
            return false;
        }
        ShinsuTechniqueInstance<C, S> inst = new ShinsuTechniqueInstance<>(this, data);
        ShinsuTechniqueData.get(user).addTechnique(inst);
        return true;
    }

}
