package io.github.davidqf555.minecraft.towerofgod.client.util;

import io.github.davidqf555.minecraft.towerofgod.common.techinques.ShinsuTechnique;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ClientReference {

    public static Map<ShinsuTechnique, Integer> known = new EnumMap<>(ShinsuTechnique.class);
    public static ShinsuTechnique[] equipped = new ShinsuTechnique[0];
    public static String[] settings = new String[0];
    public static Map<ShinsuTechnique, Integer> cooldowns = new EnumMap<>(ShinsuTechnique.class);
    public static Map<ShinsuTechnique, Set<String>> canCast = new EnumMap<>(ShinsuTechnique.class);

}
