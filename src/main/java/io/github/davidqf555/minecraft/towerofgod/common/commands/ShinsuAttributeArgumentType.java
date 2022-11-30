package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuAttributeRegistry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class ShinsuAttributeArgumentType implements ArgumentType<ShinsuAttribute> {

    private final DynamicCommandExceptionType exception = new DynamicCommandExceptionType(loc -> Component.translatable("commands." + TowerOfGod.MOD_ID + ".shinsu.unknown_Attribute", loc));

    @Override
    public ShinsuAttribute parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = ResourceLocation.read(reader);
        ShinsuAttribute attribute = ShinsuAttributeRegistry.getRegistry().getValue(loc);
        if (attribute == null) {
            throw exception.create(loc);
        }
        return attribute;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(ShinsuAttributeRegistry.getRegistry().getKeys(), builder);
    }

}