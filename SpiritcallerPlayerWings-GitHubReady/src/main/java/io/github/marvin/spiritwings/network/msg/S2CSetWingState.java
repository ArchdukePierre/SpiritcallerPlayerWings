
package io.github.marvin.spiritwings.network.msg;

import java.util.UUID;
import java.util.function.Supplier;

import io.github.marvin.spiritwings.server.WingData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class S2CSetWingState {
    private final UUID playerId;
    private final boolean enabled;

    public S2CSetWingState(UUID playerId, boolean enabled) {
        this.playerId = playerId;
        this.enabled = enabled;
    }

    public static void encode(S2CSetWingState msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerId);
        buf.writeBoolean(msg.enabled);
    }

    public static S2CSetWingState decode(FriendlyByteBuf buf) {
        return new S2CSetWingState(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(S2CSetWingState msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var mc = Minecraft.getInstance();
                if (mc.level == null) return;
                Entity e = mc.level.getPlayerByUUID(msg.playerId);
                if (e instanceof net.minecraft.world.entity.player.Player p) {
                    WingData.setClient(p, msg.enabled);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
