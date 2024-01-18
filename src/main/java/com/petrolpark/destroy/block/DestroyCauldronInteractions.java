package com.petrolpark.destroy.block;

import java.util.Map;

import com.petrolpark.destroy.item.DestroyItems;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

public class DestroyCauldronInteractions {

    static Map<Item, CauldronInteraction> URINE = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
        map.defaultReturnValue((blockState, level, blockPos, player, interactionHand, itemStack) -> {
            return InteractionResult.PASS;
        });
    });

    static {
        URINE.put(Items.GLASS_BOTTLE, (blockState, level, blockPos, player, interactionHand, itemStack) -> {
            if (!level.isClientSide) {
               Item item = itemStack.getItem();
               player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, DestroyItems.URINE_BOTTLE.asStack()));
               player.awardStat(Stats.USE_CAULDRON);
               player.awardStat(Stats.ITEM_USED.get(item));
               level.setBlockAndUpdate(blockPos, Blocks.CAULDRON.defaultBlockState());
               level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.gameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
            };
   
            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    };
};
