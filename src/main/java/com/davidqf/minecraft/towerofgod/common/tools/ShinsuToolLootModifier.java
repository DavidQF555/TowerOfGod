package com.davidqf.minecraft.towerofgod.common.tools;

import com.davidqf.minecraft.towerofgod.TowerOfGod;
import com.davidqf.minecraft.towerofgod.common.techinques.ShinsuQuality;
import com.google.gson.JsonObject;
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
        ShinsuQuality quality = ShinsuQuality.get(tool.getOrCreateChildTag(TowerOfGod.MOD_ID).getString("Quality"));
        return quality.filterDrops(generatedLoot, context);
    }

    public static class Serializer extends GlobalLootModifierSerializer<ShinsuToolLootModifier> {

        @Override
        public ShinsuToolLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] condition) {
            return new ShinsuToolLootModifier(condition);
        }
    }
}