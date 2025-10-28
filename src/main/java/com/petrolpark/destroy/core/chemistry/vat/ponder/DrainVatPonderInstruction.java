package com.petrolpark.destroy.core.chemistry.vat.ponder;

import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.core.chemistry.storage.IMixtureStorageItem.SinglePhaseVatExtraction;

import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class DrainVatPonderInstruction extends PonderInstruction {
    
    public final BlockPos vatControllerPos;
    public final boolean liquid;
    public final int drainAmount;

    public DrainVatPonderInstruction(BlockPos vatControllerPos, int drainAmount) {
        this(vatControllerPos, true, drainAmount);
    };

    public DrainVatPonderInstruction(BlockPos vatControllerPos, boolean liquid, int drainAmount) {
        this.vatControllerPos = vatControllerPos;
        this.liquid = liquid;
        this.drainAmount = drainAmount;
    };

    @Override
    public boolean isComplete() {
        return true;
    };

    @Override
    public void tick(PonderScene scene) {
        scene.getWorld().getBlockEntity(vatControllerPos, DestroyBlockEntityTypes.VAT_CONTROLLER.get()).ifPresent(vat -> {
            new SinglePhaseVatExtraction(vat, !liquid).drain(drainAmount, FluidAction.EXECUTE);
        });
    };
};
