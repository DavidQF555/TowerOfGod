package io.github.davidqf555.minecraft.towerofgod.client;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.client.gui.ShinsuTechniqueBarGui;
import io.github.davidqf555.minecraft.towerofgod.client.gui.StatsMeterGui;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;

import java.util.*;

public class ClientReference {

    public static final Map<ShinsuTechnique, Integer> INITIAL_COOLDOWNS = new EnumMap<>(ShinsuTechnique.class);
    public static StatsMeterGui.Shinsu shinsu = null;
    public static StatsMeterGui.Baangs baangs = null;
    public static ShinsuTechniqueBarGui bar = null;
    public static Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
    public static List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
    public static Map<ShinsuTechnique, Integer> cooldowns = new EnumMap<>(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Set<String>> canCast = new EnumMap<>(ShinsuTechnique.class);

}
