package io.github.davidqf555.minecraft.towerofgod.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {

    public static final ServerConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ServerConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.ConfigValue<Integer> shinsuUpdatePeriod;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Server config for Tower of God mod");
        shinsuUpdatePeriod = builder.comment("This is the period that Shinsu techniques update. The greater this is, the less often Shinsu Techniques update, improving game performance but also decreasing consistency in game logic. The default is 10 ticks per update. ")
                .defineInRange("Shinsu Tick Period", 10, 1, 200);
        builder.pop();
    }
}
