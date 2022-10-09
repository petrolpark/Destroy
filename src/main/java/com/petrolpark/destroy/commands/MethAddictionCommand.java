package com.petrolpark.destroy.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.petrolpark.destroy.capability.methaddiction.PlayerMethAddictionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MethAddictionCommand {
    public MethAddictionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("methaddiction")
            .then(Commands.literal("query").then(Commands.argument("targets", EntityArgument.player()).executes((command) -> {
                return queryMethAddiction(command.getSource(), EntityArgument.getPlayer(command, "targets"));
            })))
            .then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer()).executes((command) -> {
                return setMethAddiction(command.getSource(), EntityArgument.getPlayers(command, "targets"), IntegerArgumentType.getInteger(command, "amount"));
            }))))
        );
    }

    private int queryMethAddiction(CommandSourceStack source, ServerPlayer player) {
        player.getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).ifPresent(methAddiction -> {
            source.sendSuccess(Component.translatable("commands.destroy.methaddiction.query", player.getDisplayName(), methAddiction.getMethAddiction()), true);
        });
        return 1;
    };

    private int setMethAddiction(CommandSourceStack source, Collection<? extends ServerPlayer> players, int amount) {
        for (ServerPlayer player : players) {
            player.getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).ifPresent(methAddiction -> {
                methAddiction.setMethAddiction(amount);
            });
        };
        if (players.size() == 1) {
            source.sendSuccess(Component.translatable("commands.destroy.methaddiction.set.single", amount, players.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.destroy.methaddiction.set.multiple", amount, players.size()), true);
        };
        return 1;
    };
}
