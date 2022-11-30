package io.github.davidqf555.minecraft.towerofgod.common.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.shape.ShinsuShape;
import io.github.davidqf555.minecraft.towerofgod.registration.shinsu.ShinsuShapeRegistry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class ShinsuShapeArgumentType implements ArgumentType<ShinsuShape> {

    private final DynamicCommandExceptionType exception = new DynamicCommandExceptionType(loc -> Component.translatable("commands." + TowerOfGod.MOD_ID + ".shinsu.unknown_shape", loc));

    @Override
    public ShinsuShape parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation loc = ResourceLocation.read(reader);
        ShinsuShape shape = ShinsuShapeRegistry.getRegistry().getValue(loc);
        if (shape == null) {
            throw exception.create(loc);
        }
        return shape;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(ShinsuShapeRegistry.getRegistry().getKeys(), builder);
    }

}