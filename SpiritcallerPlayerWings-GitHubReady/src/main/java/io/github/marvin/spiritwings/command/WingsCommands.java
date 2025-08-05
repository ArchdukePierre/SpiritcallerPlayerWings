
package io.github.marvin.spiritwings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.marvin.spiritwings.server.WingData;
import io.github.marvin.spiritwings.server.WingData.FlightMode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class WingsCommands {

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("wings")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("give")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    Player p = EntityArgument.getPlayer(ctx, "player");
                                    WingData.setServer(p, true);
                                    // Default flight mode if none set
                                    if (WingData.getFlightMode(p) == FlightMode.OFF) {
                                        WingData.setFlightMode(p, FlightMode.ELYTRA);
                                    }
                                    ctx.getSource().sendSuccess(() -> Component.literal("Gave wings to " + p.getGameProfile().getName()), true);
                                    return 1;
                                })))
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    Player p = EntityArgument.getPlayer(ctx, "player");
                                    WingData.setServer(p, false);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Removed wings from " + p.getGameProfile().getName()), true);
                                    return 1;
                                })))
                .then(Commands.literal("toggle")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    Player p = EntityArgument.getPlayer(ctx, "player");
                                    boolean now = !WingData.getServer(p);
                                    WingData.setServer(p, now);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Set wings for " + p.getGameProfile().getName() + " -> " + now), true);
                                    return 1;
                                })))
                .then(Commands.literal("flight")
                        .then(Commands.argument("mode", StringArgumentType.word())
                                .suggests((c,b) -> {
                                    b.suggest("elytra");
                                    b.suggest("creative");
                                    b.suggest("off");
                                    return b.buildFuture();
                                })
                                .then(Commands.argument("player", EntityArgument.player())
                                    .executes(ctx -> {
                                        String m = StringArgumentType.getString(ctx, "mode");
                                        FlightMode mode = FlightMode.fromString(m);
                                        Player p = EntityArgument.getPlayer(ctx, "player");
                                        WingData.setFlightMode(p, mode);
                                        ctx.getSource().sendSuccess(() -> Component.literal("Set wings flight mode for " + p.getGameProfile().getName() + " -> " + mode), true);
                                        return 1;
                                    }))
                                .executes(ctx -> {
                                    // If player arg omitted, target command source if it is a player
                                    if (ctx.getSource().getEntity() instanceof Player p) {
                                        String m = StringArgumentType.getString(ctx, "mode");
                                        FlightMode mode = FlightMode.fromString(m);
                                        WingData.setFlightMode(p, mode);
                                        ctx.getSource().sendSuccess(() -> Component.literal("Set your wings flight mode -> " + mode), false);
                                        return 1;
                                    } else {
                                        ctx.getSource().sendFailure(Component.literal("Specify a player or run as a player."));
                                        return 0;
                                    }
                                })))
        );
    }
}
