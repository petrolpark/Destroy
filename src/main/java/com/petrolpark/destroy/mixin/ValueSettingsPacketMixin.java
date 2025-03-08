package com.petrolpark.destroy.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.petrolpark.destroy.core.bettervaluesettings.BetterValueSettingsBehaviour;
import com.petrolpark.destroy.mixin.accessor.ValueSettingsPacketAccessor;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

@Mixin(value = ValueSettingsPacket.class, remap = false)
public class ValueSettingsPacketMixin {

	/**
     * Pass Block Entities with {@link com.petrolpark.destroy.core.bettervaluesettings.BetterValueSettingsBehaviour Smart Value Settings Behaviours} additional information.
     * Copied and minorly adjusted from the {@link com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket Create source code}.
     */
    @Overwrite(remap = false)
	protected void applySettings(ServerPlayer player, SmartBlockEntity be) {
		InteractionHand hand = ((ValueSettingsPacketAccessor)this).getInteractHand();
		Direction side = ((ValueSettingsPacketAccessor)this).getSide();
		for (BlockEntityBehaviour behaviour : be.getAllBehaviours()) {
			if (!(behaviour instanceof ValueSettingsBehaviour valueSettingsBehaviour))
				continue;
			if (!valueSettingsBehaviour.acceptsValueSettings())
				continue;
			if (hand != null) {
				valueSettingsBehaviour.onShortInteract(player, hand, side, ((ValueSettingsPacketAccessor) this).getHitResult());
			};
            if (valueSettingsBehaviour instanceof BetterValueSettingsBehaviour smartValueSettingsBehaviour) {
                smartValueSettingsBehaviour.acceptAccessInformation(hand, side);
            };
			valueSettingsBehaviour.setValueSettings(player, new ValueSettings(((ValueSettingsPacketAccessor)this).getRow(), ((ValueSettingsPacketAccessor)this).getValue()), ((ValueSettingsPacketAccessor)this).getCtrlDown());
			return;
		}
	}

	@Redirect(method = "writeSettings",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeBlockHitResult(Lnet/minecraft/world/phys/BlockHitResult;)V"),
			remap = true) // Uses mc class, so remap required
	protected void writeSafeBlockHitResult(FriendlyByteBuf buffer, BlockHitResult hitResult) {
		buffer.writeBoolean(hitResult != null);
		if (hitResult != null) buffer.writeBlockHitResult(hitResult);
	}

	@Redirect(method = "readSettings",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readBlockHitResult()Lnet/minecraft/world/phys/BlockHitResult;"),
			remap = true) // Uses mc class, so remap required
	protected BlockHitResult readSafeBlockHitResult(FriendlyByteBuf buffer) {
		if(buffer.readBoolean()) return buffer.readBlockHitResult();
		return null;
	}
};
