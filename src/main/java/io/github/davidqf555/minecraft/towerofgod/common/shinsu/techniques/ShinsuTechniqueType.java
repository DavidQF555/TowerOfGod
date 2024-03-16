package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances.ShinsuTechniqueInstance;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.requirements.IRequirement;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuTechniqueTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public abstract class ShinsuTechniqueType<T extends ShinsuTechniqueConfig, S extends ShinsuTechniqueInstanceData> {

    public static final Codec<ShinsuTechniqueType<?, ?>> CODEC = ResourceLocation.CODEC.xmap(loc -> ShinsuTechniqueTypeRegistry.getRegistry().getValue(loc), ShinsuTechniqueType::getId);
    private final Codec<ConfiguredShinsuTechniqueType<T, S>> configured;
    private final Codec<S> data;
    private ResourceLocation id;

    public ShinsuTechniqueType(Codec<T> configured, Codec<S> data) {
        super();
        this.configured = configured.fieldOf("config").xmap(config -> new ConfiguredShinsuTechniqueType<>(this, config), ConfiguredShinsuTechniqueType::getConfig).codec();
        this.data = data;
    }

    public Codec<ConfiguredShinsuTechniqueType<T, S>> configuredCodec() {
        return configured;
    }

    public Codec<S> dataCodec() {
        return data;
    }

    public void onEnd(LivingEntity user, ShinsuTechniqueInstance<T, S> inst) {
    }

    @Nullable
    public abstract S onUse(LivingEntity user, T config, @Nullable LivingEntity target);

    public int getCooldown(T config) {
        return config.getCooldown();
    }

    public void tick(LivingEntity user, ShinsuTechniqueInstance<T, S> inst) {
    }

    public abstract IRequirement[] getRequirements();

    public ResourceLocation getId(){
        if(id == null) {
            id = ShinsuTechniqueTypeRegistry.getRegistry().getKey(this);
        }
        return id;
    }

}
