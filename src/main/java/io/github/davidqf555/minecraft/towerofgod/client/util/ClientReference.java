package io.github.davidqf555.minecraft.towerofgod.client.util;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;

import java.util.*;

public class ClientReference {

    public static final Map<ShinsuTechnique, Integer> initialCooldowns = new EnumMap<>(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
    public static List<Pair<ShinsuTechnique, String>> equipped = new ArrayList<>();
    public static Map<ShinsuTechnique, Integer> cooldowns = new EnumMap<>(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Set<String>> canCast = new EnumMap<>(ShinsuTechnique.class);

}
