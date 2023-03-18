package com.petrolpark.destroy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.util.PollutionHelper;

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
        int pollutionLevel = PollutionHelper.getPollution(source.getLevel(), pollutionType);
        source.sendSuccess(Component.translatable("commands.destroy.pollution.query", pollutionType.name(), pollutionLevel), true);
        return pollutionLevel;
    };

    private int setLevelPollution(CommandSourceStack source, PollutionType pollutionType, int value) {
        int pollutionLevel = PollutionHelper.setPollution(source.getLevel(), pollutionType, value);
        source.sendSuccess(Component.translatable("commands.destroy.pollution.set", pollutionType.name(), pollutionLevel), true);
        return pollutionLevel;
    };

    private int addLevelPollution(CommandSourceStack source, PollutionType pollutionType, int change) {
        int pollutionLevel = PollutionHelper.changePollution(source.getLevel(), pollutionType, change);
        source.sendSuccess(Component.translatable("commands.destroy.pollution.set", pollutionType.name(), pollutionLevel), true);
        return pollutionLevel;
    };
};
