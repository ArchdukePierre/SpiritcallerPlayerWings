
package io.github.marvin.spiritwings.server;

import io.github.marvin.spiritwings.network.NetworkHandler;
import io.github.marvin.spiritwings.network.msg.S2CSetWingState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class WingData {
    private static final String KEY_ENABLED = "spiritwings_enabled";
    private static final String KEY_MODE = "spiritwings_flight_mode"; // "elytra", "creative", "off"

    // SERVER: set state and sync visuals to clients
    public static void setServer(Player player, boolean enabled) {
        CompoundTag persistent = player.getPersistentData();
        persistent.putBoolean(KEY_ENABLED, enabled);
        if (!player.level().isClientSide) {
            NetworkHandler.CHANNEL.send(net.minecraftforge.network.PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new S2CSetWingState(player.getUUID(), enabled));
        }
    }

    public static boolean getServer(Player player) {
        return player.getPersistentData().getBoolean(KEY_ENABLED);
    }

    public static void copy(Player from, Player to) {
        setServer(to, getServer(from));
        setFlightMode(to, getFlightMode(from));
    }

    // CLIENT local cache for visuals (boolean only)
    public static boolean clientHasWings(Player player) {
        return player.getPersistentData().getBoolean(KEY_ENABLED);
    }

    public static void setClient(Player player, boolean enabled) {
        player.getPersistentData().putBoolean(KEY_ENABLED, enabled);
    }

    // ===== Flight mode handling (server-side authoritative) =====
    public enum FlightMode {
        ELYTRA, CREATIVE, OFF;

        public static FlightMode fromString(String s) {
            if (s == null) return OFF;
            s = s.toLowerCase();
            return switch (s) {
                case "elytra" -> ELYTRA;
                case "creative" -> CREATIVE;
                default -> OFF;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case ELYTRA -> "elytra";
                case CREATIVE -> "creative";
                default -> "off";
            };
        }
    }

    public static void setFlightMode(Player player, FlightMode mode) {
        player.getPersistentData().putString(KEY_MODE, mode.toString());
    }

    public static FlightMode getFlightMode(Player player) {
        return FlightMode.fromString(player.getPersistentData().getString(KEY_MODE));
    }

    public static boolean isEnabled(Player p) {
        return getServer(p);
    }
}
