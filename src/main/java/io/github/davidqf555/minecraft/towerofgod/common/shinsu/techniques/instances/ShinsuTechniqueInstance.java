package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.instances;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.davidqf555.minecraft.towerofgod.common.capabilities.entity.ShinsuTechniqueData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueConfig;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ShinsuTechniqueType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

public class ShinsuTechniqueInstance<C extends ShinsuTechniqueConfig, S> {

    public static final Codec<ShinsuTechniqueInstance<?, ?>> CODEC = Codec.of(new InstanceEncoder(), new InstanceDecoder());
    private final ConfiguredShinsuTechniqueType<C, S> type;
    private final S data;
    private int ticks;

    public ShinsuTechniqueInstance(ConfiguredShinsuTechniqueType<C, S> type, S data) {
        this(type, data, 0);
    }

    protected ShinsuTechniqueInstance(ConfiguredShinsuTechniqueType<C, S> type, S data, int ticks) {
        this.type = type;
        this.data = data;
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    public ConfiguredShinsuTechniqueType<C, S> getConfigured() {
        return type;
    }

    public S getData() {
        return data;
    }

    public void remove(LivingEntity user) {
        ShinsuTechniqueData<?> stats = ShinsuTechniqueData.get(user);
        stats.removeTechnique(this);
        getConfigured().onEnd(user, this);
    }

    public void tick(LivingEntity user) {
        getConfigured().tick(user, this);
        ticks++;
    }

    public void onEnd(LivingEntity user) {
        getConfigured().onEnd(user, this);
    }

    public void onUse(LivingEntity user) {
        getConfigured().onUse(user, this);
    }

    public boolean shouldRemove() {
        return getConfigured().getConfig().getDuration()
                .map(duration -> ticks >= duration)
                .orElse(false);
    }

    private static class InstanceEncoder implements Encoder<ShinsuTechniqueInstance<?, ?>> {

        @Override
        public <T> DataResult<T> encode(ShinsuTechniqueInstance<?, ?> input, DynamicOps<T> ops, T prefix) {
            RecordBuilder<T> builder = ops.mapBuilder();

            builder.add("Type", ConfiguredShinsuTechniqueType.CODEC.encodeStart(ops, Holder.direct(input.getConfigured())));
            Codec<Object> codec = (Codec<Object>) input.getConfigured().getType().dataCodec();
            builder.add("Data", codec.encodeStart(ops, input.getData()));
            builder.add("Ticks", Codec.INT.encodeStart(ops, input.ticks));

            return builder.build(prefix);
        }
    }

    private static class InstanceDecoder implements Decoder<ShinsuTechniqueInstance<?, ?>> {

        @Override
        public <T> DataResult<Pair<ShinsuTechniqueInstance<?, ?>, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Integer> ticks = ops.get(input, "Ticks")
                    .flatMap(val -> Codec.INT.parse(ops, val));
            DataResult<ConfiguredShinsuTechniqueType<?, ?>> type = ops.get(input, "Type")
                    .flatMap(val -> ConfiguredShinsuTechniqueType.CODEC.parse(ops, val))
                    .map(Holder::value);
            DataResult<?> data = type
                    .map(ConfiguredShinsuTechniqueType::getType)
                    .map(ShinsuTechniqueType::dataCodec)
                    .flatMap(codec -> ops.get(input, "Data")
                            .flatMap(val -> codec.parse(ops, val))
                    );
            return type.apply3((t, d, ti) -> new ShinsuTechniqueInstance<>((ConfiguredShinsuTechniqueType<ShinsuTechniqueConfig, Object>) t, d, ti), data, ticks)
                    .map(inst -> Pair.of(inst, input));
        }
    }

}
