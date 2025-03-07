package com.petrolpark.destroy.core.chemistry.vat.ponder;

import com.petrolpark.destroy.core.chemistry.vat.VatSideBlockEntity;
import com.petrolpark.destroy.core.chemistry.vat.VatSideBlockEntity.DisplayType;

import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;

public class SetVatSideTypePonderInstruction extends PonderInstruction {

    public final BlockPos blockPos;
    public final VatSideBlockEntity.DisplayType displayType;

    public SetVatSideTypePonderInstruction(BlockPos blockPos, DisplayType displayType) {
        this.blockPos = blockPos;
        this.displayType = displayType;
    };

    @Override
    public boolean isComplete() {
        return true;
    };

    @Override
    public void tick(PonderScene scene) {
        if (scene.getWorld().getBlockEntity(blockPos) instanceof VatSideBlockEntity vatSide)
            vatSide.setDisplayType(displayType);
    };
    
};
