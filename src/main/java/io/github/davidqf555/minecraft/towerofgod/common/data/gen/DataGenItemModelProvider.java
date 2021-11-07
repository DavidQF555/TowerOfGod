package io.github.davidqf555.minecraft.towerofgod.common.data.gen;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class DataGenItemModelProvider extends ItemModelProvider {

    public DataGenItemModelProvider(DataGenerator generator, ExistingFileHelper files) {
        super(generator, TowerOfGod.MOD_ID, files);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<NeedleItem> registry : RegistryHandler.NEEDLE_ITEMS) {
            ResourceLocation loc = registry.getId();
            generatedModels.put(loc, withExistingParent(loc.toString(), mcLoc("item/handheld"))
                    .texture("layer0", modLoc("item/" + loc.getPath()))
            );
        }
        ResourceLocation hookLoc = modLoc("item/hook");
        ItemModelBuilder hook = withExistingParent(hookLoc.toString(), mcLoc("item/handheld"))
                .transforms()
                .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                .rotation(-45, -90, 0)
                .translation(0, 19, 3)
                .scale(2, 2, 2)
                .end()
                .transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
                .rotation(-45, 90, 0)
                .translation(0, 19, 3)
                .scale(2, 2, 2)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                .rotation(-45, -90, 0)
                .translation(0, 8, 0)
                .scale(2, 2, 2)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                .rotation(-45, 90, 0)
                .translation(0, 8, 0)
                .scale(2, 2, 2)
                .end()
                .end();
        generatedModels.put(hookLoc, hook);
        for (RegistryObject<HookItem> registry : RegistryHandler.HOOK_ITEMS) {
            ResourceLocation loc = registry.getId();
            generatedModels.put(loc, withExistingParent(loc.toString(), hookLoc)
                    .texture("layer0", modLoc("item/" + loc.getPath()))
            );
        }
    }
}