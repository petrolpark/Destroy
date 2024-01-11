package com.petrolpark.destroy.util;

import com.petrolpark.destroy.block.IChainableBlock;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.ChainCogwheelsC2SPacket;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// Client-only
public class CogwheelChainingHandler {
    
    private static BlockPos firstPos;
    private static Axis firstAxis;

    public static boolean tryConnect(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return false;
        //Player player = minecraft.player;
        BlockState clickedState = level.getBlockState(pos);
        if (!IChainableBlock.isStateChainable(clickedState)) return false;
        Axis axis = clickedState.getValue(CogWheelBlock.AXIS);
        if (axis == null) return false; // Should never be the case
        if (firstPos == null) {
            firstPos = pos;
            firstAxis = axis;
            return true;
        } else {
            if (canConnect(pos, axis)) {
                DestroyMessages.sendToServer(new ChainCogwheelsC2SPacket(firstPos, pos));
                cancel();
                return true;
            } else {
                return false;
            }
        }
    };

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Items.CHAIN)) cancel();
    };

    public static void cancel() {
        firstPos = null;
        firstAxis = null;
    };

    private static boolean canConnect(BlockPos secondPos, Axis secondAxis) {
        return canConnect(firstPos, firstAxis, secondPos, secondAxis);
    };

    public static boolean canConnect(BlockPos pos1, Axis axis1, BlockPos pos2, Axis axis2) {
        if (pos1 == null || axis1 == null || pos2 == null || axis2 == null) return false;
        return axis1 == axis2 && !pos1.equals(pos2) && pos1.get(axis1) == pos2.get(axis1) && pos1.distToCenterSqr(pos2.getCenter()) <= Math.pow(DestroyAllConfigs.SERVER.contraptions.maxChainLength.get(), 2);
    };
};
