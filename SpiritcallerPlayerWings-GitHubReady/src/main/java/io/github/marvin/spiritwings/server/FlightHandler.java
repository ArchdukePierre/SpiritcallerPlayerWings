
package io.github.marvin.spiritwings.server;

import io.github.marvin.spiritwings.server.WingData.FlightMode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FlightHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent evt) {
        if (evt.phase != TickEvent.Phase.END) return;
        if (!(evt.player instanceof ServerPlayer player)) return;

        boolean enabled = WingData.isEnabled(player);
        var mode = WingData.getFlightMode(player);

        switch (mode) {
            case CREATIVE -> handleCreativeFlight(player, enabled);
            case ELYTRA -> handleElytraFlight(player, enabled);
            case OFF -> {
                // Ensure creative flight is revoked if previously granted
                if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
            }
        }
    }

    private static void handleCreativeFlight(ServerPlayer player, boolean enabled) {
        if (enabled) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else {
            // revoke if not creative/spectator
            if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    private static void handleElytraFlight(ServerPlayer player, boolean enabled) {
        if (!enabled) return;

        // If player is in conditions similar to Elytra, force and maintain fall flying.
        boolean grounded = player.onGround();
        boolean inFluid = player.isInWaterOrBubble();
        boolean passenger = player.isPassenger();
        if (grounded || inFluid || passenger) return;

        // Start/maintain fall flying without needing a real Elytra item.
        // Vanilla will normally turn it off when no Elytra is equipped; re-enable each tick.
        if (!player.isFallFlying()) {
            player.startFallFlying();
        }
        // Optional: if a real Elytra is equipped and broken, vanilla might stop flight; we just keep it alive.
    }
}
