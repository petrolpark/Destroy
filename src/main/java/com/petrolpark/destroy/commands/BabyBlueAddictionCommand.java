package com.petrolpark.destroy.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.petrolpark.destroy.capability.babyblue.PlayerBabyBlueAddictionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BabyBlueAddictionCommand {
    
    public BabyBlueAddictionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("babyblueaddiction")
            .then(Commands.literal("query").then(Commands.argument("targets", EntityArgument.player()).executes((command) -> {
                return queryBabyBlueAddiction(command.getSource(), EntityArgument.getPlayer(command, "targets"));
            })))
            .then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer()).executes((command) -> {
                return setBabyBlueAddiction(command.getSource(), EntityArgument.getPlayers(command, "targets"), IntegerArgumentType.getInteger(command, "amount"));
            }))))
        );
    }

    private int queryBabyBlueAddiction(CommandSourceStack source, ServerPlayer player) {
        player.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(babyblueAddiction -> {
            source.sendSuccess(Component.translatable("commands.destroy.babyblueaddiction.query", player.getDisplayName(), babyblueAddiction.getBabyBlueAddiction()), true);
        });
        return 1;
    };

    private int setBabyBlueAddiction(CommandSourceStack source, Collection<? extends ServerPlayer> players, int amount) {
        for (ServerPlayer player : players) {
            player.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(babyblueAddiction -> {
                babyblueAddiction.setBabyBlueAddiction(amount);
            });
        };
        if (players.size() == 1) {
            source.sendSuccess(Component.translatable("commands.destroy.babyblueaddiction.set.single", amount, players.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.destroy.babyblueaddiction.set.multiple", amount, players.size()), true);
        };
        return 1;
    };
}
