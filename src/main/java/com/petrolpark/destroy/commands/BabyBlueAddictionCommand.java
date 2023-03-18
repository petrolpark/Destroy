package com.petrolpark.destroy.commands;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddictionProvider;
import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BabyBlueAddictionCommand {
    
    public BabyBlueAddictionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("babyblueaddiction")
            .then(Commands.literal("query").then(Commands.argument("targets", EntityArgument.player()).executes(context -> {
                return queryBabyBlueAddiction(context.getSource(), EntityArgument.getPlayer(context, "targets"));
            })))
            .then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("amount", IntegerArgumentType.integer(0, DestroyAllConfigs.COMMON.substances.maxAddictionLevel.get())).executes(context -> {
                return setBabyBlueAddiction(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"));
            }))))
        );
    }

    private int queryBabyBlueAddiction(CommandSourceStack source, ServerPlayer player) {
        return player.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).map(babyblueAddiction -> {
            int addictionLevel = babyblueAddiction.getBabyBlueAddiction();
            source.sendSuccess(Component.translatable("commands.destroy.babyblueaddiction.query", player.getDisplayName(), addictionLevel), true);
            return addictionLevel;
        }).orElse(0);
    };

    // Returns the number of Players for whom the Baby Blue addiction was set
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
        return players.size();
    };
}
