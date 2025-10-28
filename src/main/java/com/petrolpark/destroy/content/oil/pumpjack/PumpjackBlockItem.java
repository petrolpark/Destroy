package com.petrolpark.destroy.content.oil.pumpjack;

import static com.petrolpark.compat.create.CreateClient.OUTLINER;

import java.util.Set;

import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Pair;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class PumpjackBlockItem extends BlockItem {

    public PumpjackBlockItem(Block block, Properties properties) {
        super(block, properties);
    };

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result == InteractionResult.FAIL && context.getLevel().isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> showBounds(context));
        };
        return result;
    };

    @OnlyIn(Dist.CLIENT)
	public void showBounds(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Direction facing = context.getHorizontalDirection().getCounterClockWise(Axis.Y);
		if (!(context.getPlayer()instanceof LocalPlayer localPlayer)) return;
		OUTLINER.showCluster(Pair.of("pumpjack", pos), Set.of(pos, pos.relative(facing), pos.relative(facing.getOpposite()), pos.above()))
            .colored(0xFF_ff5d6c);;
		CreateLang.translate("large_water_wheel.not_enough_space")
			.color(0xFF_ff5d6c)
			.sendStatus(localPlayer);
	};
    
};
