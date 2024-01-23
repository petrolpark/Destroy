package com.petrolpark.destroy.client.gui.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.client.gui.DestroyIcons;
import com.petrolpark.destroy.util.RedstoneProgram;
import com.petrolpark.destroy.util.RedstoneProgram.Channel;
import com.petrolpark.destroy.util.RedstoneProgram.PlayMode;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;

import net.minecraft.client.gui.GuiGraphics;

public abstract class RedstoneProgrammerScreen extends AbstractSimiScreen {

    protected final RedstoneProgram program;
    protected boolean changed; // Whether to send an update next tick

    private DestroyGuiTextures background;

    // Scroll values
    private LerpedFloat verticalScroll = LerpedFloat.linear().startWithValue(0d);
    private LerpedFloat horizontalScroll = LerpedFloat.linear().startWithValue(0d);

    // Buttons
    private IconButton playPauseButton;
    private IconButton confirmButton;
    private IconButton clearButton;
    private Map<PlayMode, IconButton> modeButtons;

    public RedstoneProgrammerScreen(RedstoneProgram program) {
        this.program = program;

        modeButtons = new HashMap<>(PlayMode.values().length);
    };

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        clearWidgets();

        playPauseButton = new IconButton(guiLeft + 10,  guiTop + 20, AllIcons.I_PLAY);
        playPauseButton.withCallback(change(() -> {
            program.paused = !program.paused;
            program.mode = PlayMode.MANUAL;
            playPauseButton.active = !program.paused;
        }));
        addRenderableWidget(playPauseButton);

        confirmButton = new IconButton(guiLeft + background.width - 33, guiTop + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
            if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer(); // It thinks minecraft and player might be null
        }); 
		addRenderableWidget(confirmButton);

        clearButton = new IconButton(guiLeft + background.width - 33 - 18, guiTop + background.height - 24, AllIcons.I_TRASH);
        addRenderableWidget(clearButton);

        modeButtons.clear();
        for (PlayMode mode : PlayMode.values()) {
            IconButton button = new IconButton(guiLeft + 16 + mode.ordinal() * 18, guiTop + background.height - 24, DestroyIcons.get(mode));
            button.setToolTip(mode.description);
            button.withCallback(change(() -> program.mode = mode));
            modeButtons.put(mode, button);
            addRenderableWidget(button);
        };
    };

    @Override
    public void tick() {
        super.tick();

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
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float xOffset = horizontalScroll.getValue(partialTicks);
        float yOffset = -verticalScroll.getValue(partialTicks);

        for (Channel channel : program.getChannels()) {
            
        };
        
    };

    protected Runnable change(Runnable runnable) {
        return () -> {
            runnable.run();
            changed = true;
        };
    };
    
};
