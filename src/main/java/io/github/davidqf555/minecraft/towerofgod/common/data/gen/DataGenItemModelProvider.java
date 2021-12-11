package io.github.davidqf555.minecraft.towerofgod.common.data.gen;

import io.github.davidqf555.minecraft.towerofgod.common.RegistryHandler;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.items.HookItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.NeedleItem;
import io.github.davidqf555.minecraft.towerofgod.common.items.SpearItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
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
            withExistingParent(loc.toString(), "item/handheld")
                    .texture("layer0", modLoc("item/" + loc.getPath()));
        }
        ResourceLocation hookLoc = modLoc("item/hook");
        withExistingParent(hookLoc.toString(), "item/handheld")
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
                .rotation(0, 90, -25)
                .translation(13, 17, 1)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.GUI)
                .rotation(15, -25, -5)
                .translation(2, 3, 0)
                .scale(0.65f, 0.65f, 0.65f)
                .end()
                .transform(ModelBuilder.Perspective.FIXED)
                .rotation(0, 180, 0)
                .translation(-2, 4, -5)
                .scale(0.5f, 0.5f, 0.5f)
                .end()
                .transform(ModelBuilder.Perspective.GROUND)
                .rotation(0, 0, 0)
                .translation(8, 0, 8)
                .scale(0.25f, 0.25f, 0.25f)
                .end()
                .end();
        for (RegistryObject<HookItem> registry : RegistryHandler.HOOK_ITEMS) {
            ResourceLocation loc = registry.getId();
            withExistingParent(loc.toString(), hookLoc)
                    .texture("layer0", modLoc("item/" + loc.getPath()));
        }
        ResourceLocation spearLoc = modLoc("item/spear");
        ItemModelBuilder spear = getBuilder(spearLoc.toString())
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .transforms()
                .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                .rotation(0, 60, 0)
                .translation(11, 17, -2)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
                .rotation(0, 60, 0)
                .translation(3, 17, 12)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                .rotation(0, -90, 25)
                .translation(-3, 17, 1)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                .rotation(0, 90, -25)
                .translation(13, 17, 1)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.GUI)
                .rotation(0, 0, -45)
                .translation(8, 8, 0)
                .scale(0.5f, 0.5f, 0.5f)
                .end()
                .transform(ModelBuilder.Perspective.FIXED)
                .rotation(0, 180, 0)
                .translation(0, 0, 0)
                .scale(0.5f, 0.5f, 0.5f)
                .end()
                .transform(ModelBuilder.Perspective.GROUND)
                .rotation(0, 0, 0)
                .translation(2, 8, 2)
                .scale(0.25f, 0.25f, 0.25f)
                .end()
                .end();
        ResourceLocation throwingLoc = modLoc("item/spear_throwing");
        ItemModelBuilder throwing = withExistingParent(throwingLoc.toString(), spearLoc)
                .transforms()
                .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                .rotation(0, 90, 180)
                .translation(8, -17, 9)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.THIRDPERSON_LEFT)
                .rotation(0, 90, 180)
                .translation(8, -17, -7)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                .rotation(0, -90, 25)
                .translation(-3, 17, 1)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                .rotation(0, 90, -25)
                .translation(13, 17, 1)
                .scale(1, 1, 1)
                .end()
                .transform(ModelBuilder.Perspective.GUI)
                .rotation(15, -25, -5)
                .translation(2, 3, 0)
                .scale(0.65f, 0.65f, 0.65f)
                .end()
                .transform(ModelBuilder.Perspective.FIXED)
                .rotation(0, 180, 0)
                .translation(-2, 4, -5)
                .scale(0.5f, 0.5f, 0.5f)
                .end()
                .transform(ModelBuilder.Perspective.GROUND)
                .rotation(0, 0, 0)
                .translation(0, 10, 0)
                .scale(0.25f, 0.25f, 0.25f)
                .end()
                .end();
        for (RegistryObject<SpearItem> registry : RegistryHandler.SPEAR_ITEMS) {
            ResourceLocation loc = registry.getId();
            withExistingParent(loc.toString(), spearLoc)
                    .texture("all", modLoc("entity/spear/" + loc.getPath()))
                    .override()
                    .predicate(SpearItem.THROWING, 1)
                    .model(throwing)
                    .end();
        }
    }

}