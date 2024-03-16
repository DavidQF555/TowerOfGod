package io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.data.FullTextureRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.data.IRenderData;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public class ShinsuTechniqueConfig {

    public static final Codec<ShinsuTechniqueConfig> CODEC = RecordCodecBuilder.create(inst -> commonCodec(inst).apply(inst, ShinsuTechniqueConfig::new));
    private final Display display;
    private final Optional<Integer> duration;
    private final int cooldown;

    public ShinsuTechniqueConfig(Display display, Optional<Integer> duration, int cooldown) {
        this.display = display;
        this.duration = duration;
        this.cooldown = cooldown;
    }

    protected static <T extends ShinsuTechniqueConfig> Products.P3<RecordCodecBuilder.Mu<T>, Display, Optional<Integer>, Integer> commonCodec(RecordCodecBuilder.Instance<T> inst) {
        return inst.group(
                Display.CODEC.fieldOf("display").forGetter(ShinsuTechniqueConfig::getDisplay),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("duration").forGetter(ShinsuTechniqueConfig::getDuration),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown").forGetter(ShinsuTechniqueConfig::getCooldown)
        );
    }

    public Display getDisplay() {
        return display;
    }

    public Optional<Integer> getDuration() {
        return duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public record Display(String name, String desc, ResourceLocation icon, Optional<ShinsuAttribute> attribute) {
        public static final Codec<Display> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.STRING.fieldOf("name").forGetter(Display::name),
                Codec.STRING.fieldOf("description").forGetter(Display::desc),
                ResourceLocation.CODEC.fieldOf("icon").forGetter(Display::icon),
                ShinsuAttribute.CODEC.optionalFieldOf("attribute").forGetter(Display::attribute)
        ).apply(inst, Display::new));

        public TranslatableComponent getName() {
            return new TranslatableComponent(name());
        }

        public IRenderData getIcon() {
            return new FullTextureRenderData(icon());
        }

        public TranslatableComponent getDescription() {
            return new TranslatableComponent(desc());
        }

    }

}
