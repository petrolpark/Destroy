package com.petrolpark.destroy.client.gui;

import java.util.function.Consumer;

import com.simibubi.create.AllKeys;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

/**
 * An improved {@link com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen screen for setting values} in Value Boxes.
 */
public class BetterValueSettingsScreen extends ValueSettingsScreen {

    private Direction sideAccessed;
    private InteractionHand interactionHand;
    private BlockPos pos; // Double reference because it's private

    public BetterValueSettingsScreen(BlockPos pos, Direction sideAccessed, InteractionHand interactionHand, ValueSettingsBoard board, ValueSettings valueSettings, Consumer<ValueSettings> onHover) {
        super(pos, board, valueSettings, onHover);
        this.pos = pos;
        this.sideAccessed = sideAccessed;
        this.interactionHand = interactionHand;
    };

    /**
     * Mostly copied from the {@link com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen Create source code},
     * with the information about the Interaction Hand and face accessed now included in the packet, allowing for finer control of
     * Value Boxes.
     */
    @Override
    protected void saveAndClose(double mouseX, double mouseY) {
		ValueSettings closest = getClosestCoordinate((int) mouseX, (int) mouseY);
		AllPackets.getChannel()
			.sendToServer(new ValueSettingsPacket(pos, closest.row(), closest.value(), interactionHand, sideAccessed,
				AllKeys.ctrlDown()));
		onClose();
	};
    
};
