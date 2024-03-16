package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.techniques.ConfiguredShinsuTechniqueType;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ConfiguredTechniqueTypeRegistry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class ShinsuTechniqueArgumentType implements ArgumentType<ConfiguredShinsuTechniqueType<?, ?>> {

    private final DynamicCommandExceptionType exception = new DynamicCommandExceptionType(loc -> Component.translatable("commands." + TowerOfGod.MOD_ID + ".shinsu.unknown_technique", loc));

    @Override
    public ConfiguredShinsuTechniqueType<?, ?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = ResourceLocation.read(reader);
        ConfiguredShinsuTechniqueType<?, ?> technique = ConfiguredTechniqueTypeRegistry.getRegistry().getValue(loc);
        if (technique == null) {
            throw exception.create(loc);
        }
        return technique;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(ConfiguredTechniqueTypeRegistry.getRegistry().getKeys(), builder);
    }

}
