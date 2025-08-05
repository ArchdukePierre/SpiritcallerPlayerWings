
package io.github.marvin.spiritwings.network;

import io.github.marvin.spiritwings.SpiritWingsMod;
import io.github.marvin.spiritwings.network.msg.S2CSetWingState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(SpiritWingsMod.MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals
        );
        int id = 0;
        CHANNEL.registerMessage(id++, S2CSetWingState.class, S2CSetWingState::encode, S2CSetWingState::decode, S2CSetWingState::handle);
    }
}
