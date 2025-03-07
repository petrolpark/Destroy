package com.petrolpark.destroy.core.chemistry.storage.measuringcylinder;

import com.mojang.blaze3d.platform.Window;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.core.bettervaluesettings.BetterValueSettingsScreen;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

public class TransferFluidScreen extends BetterValueSettingsScreen {

    protected final boolean blockToItem; // True if filling the Item, false if Emptying

    public TransferFluidScreen(BlockPos pos, Direction sideAccessed, InteractionHand interactionHand, ValueSettingsBoard board, ValueSettings valueSettings, boolean blockToItem, int netId) {
        super(pos, sideAccessed, interactionHand, board, valueSettings, s -> {}, netId);
        this.blockToItem = blockToItem;
    };

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (blockToItem && minecraft.options.keyAttack.matches(keyCode, scanCode)) {
            Window window = minecraft.getWindow();
			double x = minecraft.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
			double y = minecraft.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();
			saveAndClose(x, y);
			return true;
        };
        return super.keyReleased(keyCode, scanCode, modifiers);
    };

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (blockToItem && minecraft.options.keyAttack.matchesMouse(button)) {
            saveAndClose(mouseX, mouseY);
            return true;
        };
        return super.mouseReleased(mouseX, mouseY, button);
    };

    @Override
    protected void saveAndClose(double mouseX, double mouseY) {
        ValueSettings closest = getClosestCoordinate((int) mouseX, (int) mouseY);
		DestroyMessages.sendToServer(new TransferFluidC2SPacket(pos, sideAccessed, hand, blockToItem, closest.value()));
		onClose();
    };
    
};
