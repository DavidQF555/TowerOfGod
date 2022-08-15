package io.github.davidqf555.minecraft.towerofgod.common.items;

import com.google.gson.JsonObject;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.ShinsuQuality;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class ShinsuToolLootModifier extends LootModifier {

    public ShinsuToolLootModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.get(LootParameters.TOOL);
        ShinsuQuality quality = ShinsuQuality.getQuality(tool);
        return quality.filterDrops(generatedLoot, context);
    }

    public static class Serializer extends GlobalLootModifierSerializer<ShinsuToolLootModifier> {

        @Override
        public ShinsuToolLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] condition) {
            return new ShinsuToolLootModifier(condition);
        }

        @Override
        public JsonObject write(ShinsuToolLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
