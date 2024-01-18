package com.petrolpark.destroy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.block.entity.behaviour.ExtendedBasinBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.GeniusFluidTankBehaviour;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Mixin(BasinBlockEntity.class)
public abstract class BasinBlockEntityMixin implements IHaveHoveringInformation {

    /**
     * Replace the Basin's Smart Fluid Tanks with Genius ones, which can hold Mixtures.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("RETURN"),
        remap = false
    )
    public void inAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        behaviours.remove(getInputTank());
        behaviours.remove(getOutputTank());
        setInputTank(
            new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, (BasinBlockEntity)(Object)this, 2, 1000, true)
                .whenFluidUpdates(() -> setContentsChanged(true))
        );
        setOutputTank(
            new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, (BasinBlockEntity)(Object)this, 2, 1000, true)
                .whenFluidUpdates(() -> setContentsChanged(true))
                .forbidInsertion()
        );
        behaviours.add(getInputTank());
        behaviours.add(getOutputTank());
    };
    
    /**
     * Add the 'Basin too full' pop-up if a Basin will not be able to react.
     */
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ExtendedBasinBehaviour behaviour = ((BasinBlockEntity)(Object)this).getBehaviour(ExtendedBasinBehaviour.TYPE);
        if (behaviour != null && behaviour.tooFullToReact) {
            DestroyLang.translate("tooltip.basin.too_full.title").style(ChatFormatting.GOLD).forGoggles(tooltip);
            TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.basin.too_full").component(), TooltipHelper.Palette.GRAY_AND_WHITE).forEach(component -> {
                DestroyLang.builder().add(component.copy()).forGoggles(tooltip);
            });
            return true;
        };
        tooltip.add(Component.literal(""));
        return false;
    };

    @Accessor("inputTank")
    public abstract SmartFluidTankBehaviour getInputTank();

    @Accessor("inputTank")
    public abstract void setInputTank(SmartFluidTankBehaviour behaviour);

    @Accessor("outputTank")
    public abstract SmartFluidTankBehaviour getOutputTank();

    @Accessor("outputTank")
    public abstract void setOutputTank(SmartFluidTankBehaviour behaviour);

    @Accessor("contentsChanged")
    public abstract void setContentsChanged(boolean contentsChanged);
};
