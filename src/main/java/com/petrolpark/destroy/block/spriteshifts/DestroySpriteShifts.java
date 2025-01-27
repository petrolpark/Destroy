package com.petrolpark.destroy.block.spriteshifts;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;

public class DestroySpriteShifts {

    public static final CTSpriteShiftEntry NETHER_CROCOITE_BLOCK = CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, Destroy.asResource("block/nether_crocoite_ore"), Destroy.asResource("block/nether_crocoite_ore_connected"));
    public static final CTSpriteShiftEntry STAINLESS_STEEL_BLOCK = CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, Destroy.asResource("block/stainless_steel_block"), Destroy.asResource("block/stainless_steel_block_connected"));
    public static final CTSpriteShiftEntry BOROSILICATE_GLASS = CTSpriteShifter.getCT(AllCTTypes.OMNIDIRECTIONAL, Destroy.asResource("block/borosilicate_glass"), Destroy.asResource("block/borosilicate_glass_connected"));
    
};
