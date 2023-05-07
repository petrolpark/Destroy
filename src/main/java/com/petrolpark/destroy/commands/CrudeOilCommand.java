package com.petrolpark.destroy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.petrolpark.destroy.capability.chunk.ChunkCrudeOil;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;

public class CrudeOilCommand {
  
    public CrudeOilCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("crudeoil")
            .then(Commands.argument("position", BlockPosArgument.blockPos())
                .executes(CrudeOilCommand::generateCrudeOil)
            )
        );
    };

    private static int generateCrudeOil(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer(); // May be null
        BlockPos pos = context.getArgument("position", Coordinates.class).getBlockPos(source);
        LevelChunk chunk = source.getLevel().getChunkAt(pos);
        int amount = chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL).map(crudeOil -> {
            crudeOil.generate(chunk, player);
            return crudeOil.getAmount();
        }).orElse(0);
        source.sendSuccess(Component.translatable("commands.destroy.crudeoil", amount, pos.getX(), pos.getY(), pos.getZ()), true);
        return amount;
    };
};
