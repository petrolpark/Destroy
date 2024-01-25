package com.petrolpark.destroy.client.gui.screen;

import java.util.Map;
import java.util.Map.Entry;

import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.client.gui.DestroyIcons;
import com.petrolpark.destroy.client.gui.menu.RedstoneProgrammerMenu;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.RedstoneProgramSyncC2SPacket;
import com.petrolpark.destroy.util.RedstoneProgram;
import com.petrolpark.destroy.util.RedstoneProgram.Channel;
import com.petrolpark.destroy.util.RedstoneProgram.PlayMode;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneProgrammerScreen extends AbstractSimiContainerScreen<RedstoneProgrammerMenu> {

    protected final RedstoneProgram program;

    private DestroyGuiTextures background;

    // Scroll values
    private LerpedFloat verticalScroll = LerpedFloat.linear().startWithValue(0d);
    private LerpedFloat horizontalScroll = LerpedFloat.linear().startWithValue(0d);

    // Buttons
    private IconButton playPauseButton;
    private IconButton confirmButton;
    private IconButton clearButton;
    private Map<PlayMode, IconButton> modeButtons;

    
    public RedstoneProgrammerScreen(RedstoneProgrammerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        program = container.contentHolder;

        background = DestroyGuiTextures.REDSTONE_PROGRAMMER;
        modeButtons = Map.of();
    };

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        clearWidgets();

        playPauseButton = new IconButton(leftPos + 10,  topPos + 20, AllIcons.I_PLAY);
        playPauseButton.withCallback(change(() -> {
            program.paused = !program.paused;
            program.mode = PlayMode.MANUAL;
            playPauseButton.active = !program.paused;
        }));
        addRenderableWidget(playPauseButton);

        confirmButton = new IconButton(leftPos + background.width - 33, topPos + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
            if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer(); // It thinks minecraft and player might be null
        }); 
		addRenderableWidget(confirmButton);

        clearButton = new IconButton(leftPos + background.width - 33 - 18, topPos + background.height - 24, AllIcons.I_TRASH);
        addRenderableWidget(clearButton);

        modeButtons.clear();
        for (PlayMode mode : PlayMode.values()) {
            IconButton button = new IconButton(leftPos + 16 + mode.ordinal() * 18, topPos + background.height - 24, DestroyIcons.get(mode));
            button.setToolTip(mode.description);
            button.withCallback(change(() -> program.mode = mode));
            modeButtons.put(mode, button);
            addRenderableWidget(button);
        };
    };

    @Override
    public void containerTick() {
        super.containerTick();

        // Tick chasers
        verticalScroll.tickChaser();
        horizontalScroll.tickChaser();

        // Set the mode of play
        for (Entry<PlayMode, IconButton> entry : modeButtons.entrySet()) {
            if (program.mode == entry.getKey()) {
                entry.getValue().active = false;
            } else {
                entry.getValue().active = true;
            };
        };
    };

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {

        // Background
        background.render(graphics, leftPos, topPos);

        float xOffset = horizontalScroll.getValue(partialTicks);
        float yOffset = -verticalScroll.getValue(partialTicks);

        for (Channel channel : program.getChannels()) {
            
        };
        
    };

    protected Runnable change(Runnable runnable) {
        return () -> {
            runnable.run();
            DestroyMessages.sendToServer(new RedstoneProgramSyncC2SPacket(program));
        };
    };
    
};
