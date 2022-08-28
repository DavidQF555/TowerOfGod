package io.github.davidqf555.minecraft.towerofgod.registration;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class TagRegistry {

    public static final TagKey<Block> MINEABLE_WITH_SPEAR = BlockTags.create(new ResourceLocation(TowerOfGod.MOD_ID, "mineable/spear"));
    public static final TagKey<Block> MINEABLE_WITH_NEEDLE = BlockTags.create(new ResourceLocation(TowerOfGod.MOD_ID, "mineable/needle"));
    public static final TagKey<Block> MINEABLE_WITH_HOOK = BlockTags.create(new ResourceLocation(TowerOfGod.MOD_ID, "mineable/hook"));

    private TagRegistry() {
    }

}
