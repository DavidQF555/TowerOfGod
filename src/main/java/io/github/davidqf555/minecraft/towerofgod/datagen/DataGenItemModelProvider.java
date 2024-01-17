package io.github.davidqf555.minecraft.towerofgod.datagen;

import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import io.github.davidqf555.minecraft.towerofgod.registration.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class DataGenItemModelProvider extends ItemModelProvider {

    public DataGenItemModelProvider(DataGenerator gen, ExistingFileHelper files) {
        super(gen, TowerOfGod.MOD_ID, files);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<NeedleItem> registry : ItemRegistry.NEEDLE_ITEMS) {
            ResourceLocation loc = registry.getId();
            withExistingParent(loc.toString(), "item/handheld")
                    .texture("layer0", modLoc("item/" + loc.getPath()));
        }
        ResourceLocation hookLoc = modLoc("item/hook");
        for (RegistryObject<? extends HookItem> registry : ItemRegistry.HOOK_ITEMS) {
            ResourceLocation loc = registry.getId();
            withExistingParent(loc.toString(), hookLoc)
                    .texture("layer0", modLoc("item/" + loc.getPath()));
        }
        ModelFile.ExistingModelFile throwing = getExistingFile(modLoc("item/spear_throwing"));
        for (RegistryObject<? extends SpearItem> registry : ItemRegistry.SPEARS) {
            ResourceLocation loc = registry.getId();
            withExistingParent(loc.toString(), modLoc("item/spear"))
                    .override()
                    .predicate(SpearItem.THROWING, 1)
                    .model(throwing)
                    .end();

        }
    }

}