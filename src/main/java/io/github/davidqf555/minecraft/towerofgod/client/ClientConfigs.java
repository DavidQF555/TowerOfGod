package io.github.davidqf555.minecraft.towerofgod.client;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ClientConfigs {

    public static final ClientConfigs INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ClientConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.IntValue shinsuCombinationResistivity;

    public ClientConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("Client configs for Tower of God mod");
        shinsuCombinationResistivity = builder.comment("This is the resistivity of the Shinsu technique combination gui. In other words, it is the inverse of sensitivity. Specifically, it is the minimum change in degrees of the pitch or yaw of the player to register an input. The default is 20 degrees per input. ")
                .defineInRange("Shinsu Combination GUI Resistivity", 20, 1, 100);
        builder.pop();
    }
}
