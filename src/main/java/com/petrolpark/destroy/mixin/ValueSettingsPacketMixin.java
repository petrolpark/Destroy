package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.behaviour.SmartValueSettingsBehaviour;
import com.petrolpark.destroy.mixin.accessor.ValueSettingsPacketAccessor;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;

import net.minecraft.server.level.ServerPlayer;

@Mixin(ValueSettingsPacket.class)
public class ValueSettingsPacketMixin {
    
    /**
     * Pass Block Entities with {@link com.petrolpark.destroy.behaviour.SmartValueSettingsBehaviour Smart Value Settings Behaviours} additional information.
     * Copied and minorly adjusted from the {@link com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket Create source code}.
     */
    @Overwrite
	protected void applySettings(ServerPlayer player, SmartBlockEntity be) {
		for (BlockEntityBehaviour behaviour : be.getAllBehaviours()) {
			if (!(behaviour instanceof ValueSettingsBehaviour valueSettingsBehaviour))
				continue;
			if (!valueSettingsBehaviour.acceptsValueSettings())
				continue;
            if (valueSettingsBehaviour instanceof SmartValueSettingsBehaviour smartValueSettingsBehaviour) {
                smartValueSettingsBehaviour.acceptAccessInformation(((ValueSettingsPacketAccessor)this).getInteractHand(), ((ValueSettingsPacketAccessor)this).getSide());
            };
			valueSettingsBehaviour.setValueSettings(player, new ValueSettings(((ValueSettingsPacketAccessor)this).getRow(), ((ValueSettingsPacketAccessor)this).getValue()), ((ValueSettingsPacketAccessor)this).getCtrlDown());
			return;
		}
	}
};
