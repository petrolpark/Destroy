package com.petrolpark.destroy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.petrolpark.destroy.capability.level.pollution.LevelPollutionProvider;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.server.command.EnumArgument;

public class PollutionCommand {

    public PollutionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pollution")
            .then(Commands.argument("type", EnumArgument.enumArgument(PollutionType.class))
                .then((Commands.literal("query").executes(context -> {
                    return queryLevelPollution(context.getSource(), context.getArgument("type", PollutionType.class));
                }))).then((Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes(context -> {
                    return setLevelPollution(context.getSource(), context.getArgument("type", PollutionType.class), IntegerArgumentType.getInteger(context, "value"));
                })))).then((Commands.literal("add").then(Commands.argument("change", IntegerArgumentType.integer()).executes(context -> {
                    return addLevelPollution(context.getSource(), context.getArgument("type", PollutionType.class), IntegerArgumentType.getInteger(context, "change"));
                }))))
            )
        );
    };

    private int queryLevelPollution(CommandSourceStack source, PollutionType pollutionType) {
        return source.getLevel().getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> {
            int pollutionLevel = levelPollution.get(pollutionType);
            source.sendSuccess(Component.translatable("commands.destroy.pollution.query", pollutionType.name(), pollutionLevel), true);
            return pollutionLevel;
        }).orElse(0);
    };

    private int setLevelPollution(CommandSourceStack source, PollutionType pollutionType, int value) {
        return source.getLevel().getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> {
            int pollutionLevel = levelPollution.set(pollutionType, value);
            source.sendSuccess(Component.translatable("commands.destroy.pollution.set", pollutionType.name(), pollutionLevel), true);
            return pollutionLevel;
        }).orElse(0);
    };

    private int addLevelPollution(CommandSourceStack source, PollutionType pollutionType, int change) {
        return source.getLevel().getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> {
            int pollutionLevel = levelPollution.change(pollutionType, change);
            source.sendSuccess(Component.translatable("commands.destroy.pollution.set", pollutionType.name(), pollutionLevel), true);
            return pollutionLevel;
        }).orElse(0);
    };
};
