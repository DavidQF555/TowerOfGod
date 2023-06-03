package io.github.davidqf555.minecraft.towerofgod.common.items.shinsu;

import com.google.gson.JsonObject;
import io.github.davidqf555.minecraft.towerofgod.common.shinsu.attributes.ShinsuAttribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class ShinsuToolLootModifier extends LootModifier {

    public ShinsuToolLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        ShinsuAttribute attribute = ShinsuAttribute.getAttribute(tool);
        if (attribute != null) {
            return attribute.filterDrops(generatedLoot, context);
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ShinsuToolLootModifier> {

        @Override
        public ShinsuToolLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] condition) {
            return new ShinsuToolLootModifier(condition);
        }

        @Override
        public JsonObject write(ShinsuToolLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
