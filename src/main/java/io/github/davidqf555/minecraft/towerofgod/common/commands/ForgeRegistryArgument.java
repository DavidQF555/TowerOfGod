package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.concurrent.CompletableFuture;

public class ForgeRegistryArgument<T extends IForgeRegistryEntry<T>> implements ArgumentType<T> {

    private final IForgeRegistry<T> registry;
    private final DynamicCommandExceptionType error;

    public ForgeRegistryArgument(IForgeRegistry<T> registry, String error) {
        this.registry = registry;
        this.error = new DynamicCommandExceptionType(loc -> new TranslatableComponent(error, loc));
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = ResourceLocation.read(reader);
        T technique = registry.getValue(loc);
        if (technique == null) {
            throw error.create(loc);
        }
        return technique;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(registry.getKeys(), builder);
    }

}
