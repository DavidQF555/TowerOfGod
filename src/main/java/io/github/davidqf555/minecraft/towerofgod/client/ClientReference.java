package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.gui.IRenderInfo;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ItemStackRenderInfo;
import io.github.davidqf555.minecraft.towerofgod.client.gui.RenderInfo;
import io.github.davidqf555.minecraft.towerofgod.common.TowerOfGod;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class ClientReference {

    public static final Map<ShinsuTechnique, Integer> initialCooldowns = new EnumMap<>(ShinsuTechnique.class);
    private static final Map<String, IRenderInfo> SHINSU_ICONS = new HashMap<>();
    private static final ResourceLocation ICONS_LOCATION = new ResourceLocation(TowerOfGod.MOD_ID, "textures/gui/shinsu/shinsu_icons.png");
    public static Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
    public static List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
    public static Map<ShinsuTechnique, Integer> cooldowns = new EnumMap<>(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Set<String>> canCast = new EnumMap<>(ShinsuTechnique.class);

    static {
        SHINSU_ICONS.put("swirl", new RenderInfo(ICONS_LOCATION, 64, 64, 0, 0, 16, 16));
        SHINSU_ICONS.put("resistance", new RenderInfo(ICONS_LOCATION, 64, 64, 16, 0, 16, 16));
        SHINSU_ICONS.put("baangs", new RenderInfo(ICONS_LOCATION, 64, 64, 32, 0, 16, 16));
        SHINSU_ICONS.put("tension", new RenderInfo(ICONS_LOCATION, 64, 64, 48, 0, 16, 16));
        SHINSU_ICONS.put("reverse", new RenderInfo(ICONS_LOCATION, 64, 64, 0, 16, 16, 16));
        SHINSU_ICONS.put("shinsu", new RenderInfo(ICONS_LOCATION, 64, 64, 16, 16, 16, 14));
        SHINSU_ICONS.put("pickaxe", new RenderInfo(ICONS_LOCATION, 64, 64, 32, 16, 16, 16));
        SHINSU_ICONS.put("move", new RenderInfo(ICONS_LOCATION, 64, 64, 48, 16, 16, 16));
        SHINSU_ICONS.put("lighthouse_flow_control", new RenderInfo(ICONS_LOCATION, 64, 64, 0, 32, 16, 16));
        SHINSU_ICONS.put("eye", new RenderInfo(ICONS_LOCATION, 64, 64, 16, 32, 16, 16));
        SHINSU_ICONS.put("follow", new RenderInfo(ICONS_LOCATION, 64, 64, 32, 32, 16, 16));
    }

    @Nullable
    public static IRenderInfo getShinsuIcon(String name) {
        return SHINSU_ICONS.get(name);
    }

    public static IRenderInfo getShinsuIcon(Supplier<ItemStack> item) {
        return new ItemStackRenderInfo(item);
    }

    @Nullable
    public static IRenderInfo getShinsuIcon(Either<String, Supplier<ItemStack>> icon) {
        return icon.right().map(ClientReference::getShinsuIcon).orElseGet(() -> getShinsuIcon(icon.left().get()));
    }

}
