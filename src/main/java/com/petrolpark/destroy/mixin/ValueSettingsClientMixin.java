package com.petrolpark.destroy.mixin;

import net.createmod.catnip.gui.ScreenOpener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.core.bettervaluesettings.BetterValueSettingsScreen;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ValueSettingsClient.class)
public class ValueSettingsClientMixin {

    Minecraft mc = Minecraft.getInstance();

    /**
     * Replace that {@link com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen screen} that opens to edit a value
     * with an {@link com.petrolpark.destroy.core.bettervaluesettings.BetterValueSettingsScreen improved version}.
     * @param ci The callback information
     */
    @Inject(
        method = "Lcom/simibubi/create/foundation/blockEntity/behaviour/ValueSettingsClient;tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/createmod/catnip/gui/ScreenOpener;open(Lnet/minecraft/client/gui/screens/Screen;)V"
        ),
        cancellable = true,
        remap = false
    )
    public void inTick(CallbackInfo ci) {

        // Get the Value Box Behaviour
        BlockEntityBehaviour behaviour = BlockEntityBehaviour.get(mc.level, thisValueSettingsClient().interactHeldPos, ((ValueSettingsClient)(Object)this).interactHeldBehaviour);
        if (!(behaviour instanceof ValueSettingsBehaviour valueSettingBehaviour)) return;

        // Get the Block hit result
        if (!(mc.hitResult instanceof BlockHitResult blockHitResult)) return;

        // Open the screen
        ScreenOpener.open(new BetterValueSettingsScreen(
            thisValueSettingsClient().interactHeldPos,
            thisValueSettingsClient().interactHeldFace,
            thisValueSettingsClient().interactHeldHand,
            valueSettingBehaviour.createBoard(mc.player, blockHitResult),
            valueSettingBehaviour.getValueSettings(),
            valueSettingBehaviour::newSettingHovered,
            valueSettingBehaviour.netId())
        );
        
        // Replace the cancelled code
		thisValueSettingsClient().interactHeldTicks = -1;

        // Cancel the rest of the method
        ci.cancel();
    };

    private ValueSettingsClient thisValueSettingsClient() {
        return (ValueSettingsClient)(Object)this;
    };
};
