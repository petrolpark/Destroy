package com.petrolpark.destroy.core.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.petrolpark.destroy.client.DestroyLang;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.createmod.catnip.data.Iterate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class AttachedCheckCommand {
    
     public AttachedCheckCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("checkattached")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("position", BlockPosArgument.blockPos())
                .executes(AttachedCheckCommand::attachedCheck)
            )
        );
    };

    private static int attachedCheck(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        BlockPos pos = context.getArgument("position", Coordinates.class).getBlockPos(source);
        ServerLevel level = source.getLevel();
        int matches = 0;
        source.sendSuccess(() -> Component.translatable("commands.destroy.attachedcheck", pos.getX(), pos.getY(), pos.getZ()), true);
        for (Direction d : Iterate.directions) {
            boolean attached = BlockMovementChecks.isBlockAttachedTowards(level.getBlockState(pos), level, pos, d);
            source.sendSuccess(DestroyLang.direction(d).add(Component.literal(": "+attached))::component, true);
            matches++;
        };
        return matches;
    };
};
