package io.github.davidqf555.minecraft.towerofgod.common.techinques;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TechniqueSettings {

    public static final TechniqueSettings SINGLE = new TechniqueSettings(StringTextComponent.EMPTY, ImmutableMap.of("", Pair.of(null, null)), "");
    private final ITextComponent title;
    private final Map<String, Pair<ITextComponent, Either<String, Supplier<ItemStack>>>> options;
    private final String def;

    public TechniqueSettings(ITextComponent title, Map<String, Pair<ITextComponent, Either<String, Supplier<ItemStack>>>> options, String def) {
        this.title = title;
        this.options = options;
        this.def = def;
    }

    public ITextComponent getTitle() {
        return title;
    }

    public Set<String> getOptions() {
        return options.keySet();
    }

    public ITextComponent getText(String option) {
        return options.get(option).getFirst();
    }

    public Either<String, Supplier<ItemStack>> getIcon(String option) {
        return options.get(option).getSecond();
    }

    public String getDefault() {
        return def;
    }
}
