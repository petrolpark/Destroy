package com.petrolpark.destroy.content.redstone.programmer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.client.DestroyIcons;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgram.Channel;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgram.PlayMode;
import com.petrolpark.destroy.util.GuiHelper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class RedstoneProgrammerScreen extends AbstractSimiContainerScreen<RedstoneProgrammerMenu> {

    protected final RedstoneProgrammerMenu menu;
    public final RedstoneProgram program;

    private DestroyGuiTextures background;
    private int width;

    // Areas
    public static final Rect2i NOTE_AREA = new Rect2i(77, 31, 168, 154);
    public static final Rect2i ITEM_AREA = new Rect2i(RedstoneProgrammerMenu.SCREEN_ITEM_AREA_X, RedstoneProgrammerMenu.SCREEN_ITEM_AREA_Y, RedstoneProgrammerMenu.SCREEN_ITEM_AREA_WIDTH, RedstoneProgrammerMenu.SCREEN_ITEM_AREA_HEIGHT);

    // Spacing
    public static final int DISTANCE_BETWEEN_CHANNELS = RedstoneProgrammerMenu.SCREEN_DISTANCE_BETWEEN_CHANNELS;
    private static final int noteWidth = 4;

    // Scroll values
    private int verticalScroll;
    private LerpedFloat horizontalScroll = LerpedFloat.linear().startWithValue(0d);
    private LerpedFloat playhead = LerpedFloat.linear().startWithValue(0d);
    private boolean followPlayHead = false;

    // Mouse dragging information
    private boolean dragging;
    private int draggingChannel; // Which channel is being clicked
    private boolean draggingDeleting; // Whether clicking is deleting NOTE_AREA

    // Buttons
    private IconButton playPauseButton;
    private IconButton restartButton;
    private IconButton confirmButton;
    private Map<PlayMode, IconButton> modeButtons;
    private IconButton followPlayheadButton;

    // Scroll inputs
    private ScrollInput ticksPerBeatScroller;
    private ScrollInput beatsPerLineScroller;
    private ScrollInput linesPerBarScroller;

    // Syncing
    private boolean shouldSend;
    
    public RedstoneProgrammerScreen(RedstoneProgrammerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        menu = container;
        program = container.contentHolder;

        background = DestroyGuiTextures.REDSTONE_PROGRAMMER;
        modeButtons = new HashMap<>(PlayMode.values().length);
        playhead.setValue((float)noteWidth * (float)program.getAbsolutePlaytime());
    };

    @Override
    protected void init() {
        width = background.width;
        setWindowSize(width, background.height);
        super.init();
        clearWidgets();

        playPauseButton = new IconButton(leftPos + 7, topPos + 6, AllIcons.I_PLAY)
            .withCallback(() -> {
                program.paused = !program.paused;
                if (program.mode.powerRequired || program.mode == PlayMode.LOOP) program.mode = PlayMode.MANUAL;
                setPlayPauseButtonIcon();
                shouldSend = true;
            });
        setPlayPauseButtonIcon();
        addRenderableWidget(playPauseButton);

        restartButton = new IconButton(leftPos + 29, topPos + 6, AllIcons.I_REFRESH)
            .withCallback(() -> {
                program.restart();
                playhead.setValue(0f);
                if (!followPlayHead) {
                    horizontalScroll.setValue(0d);
                    horizontalScroll.chase(0d, 1d, LerpedFloat.Chaser.LINEAR);
                };
                shouldSend = true;
            });
        addRenderableWidgets(restartButton);

        confirmButton = new IconButton(leftPos + width - 33, topPos + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
            if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer(); // It thinks minecraft and player might be null
        }); 
		addRenderableWidget(confirmButton);

        modeButtons.clear();
        for (PlayMode mode : PlayMode.values()) {
            IconButton button = new IconButton(leftPos + 27 + mode.ordinal() * 18, topPos + background.height - 24, DestroyIcons.get(mode));
            button.setToolTip(mode.description);
            button.withCallback(() -> {
                shouldSend = program.mode != mode;
                program.mode = mode;
                if (shouldSend && mode == PlayMode.LOOP) program.paused = false;
            });
            button.active = program.mode != mode;
            modeButtons.put(mode, button);
            addRenderableWidget(button);
        };

        ticksPerBeatScroller = new ScrollInput(leftPos + 6, topPos + background.height - 24, 18, 9)
            .setState(program.getTicksPerBeat())
            .calling(i -> {
                program.setTicksPerBeat(i);
                shouldSend = true;
            })
            .titled(DestroyLang.translate("tooltip.redstone_programmer.ticks_per_beat").component())
            .addHint(DestroyLang.translate("tooltip.redstone_programmer.ticks_per_beat.hint").component())
            .withRange(DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMinTicksPerBeat.get(), 81);
        ticksPerBeatScroller.setState(program.getTicksPerBeat());
        addRenderableWidget(ticksPerBeatScroller);

        beatsPerLineScroller = new ScrollInput(leftPos + 156, topPos + background.height - 24, 18, 9)
            .setState(program.beatsPerLine)
            .calling(i -> {
                program.beatsPerLine = i;
                shouldSend = true;
            })
            .titled(DestroyLang.translate("tooltip.redstone_programmer.beats_per_line").component())
            .addHint(DestroyLang.translate("tooltip.redstone_programmer.beats_per_line.hint").component())
            .withRange(0, 32);
        beatsPerLineScroller.setState(program.beatsPerLine);
        addRenderableWidget(beatsPerLineScroller);

        linesPerBarScroller = new ScrollInput(leftPos + 176, topPos + background.height - 24, 18, 9)
            .setState(program.linesPerBar)
            .calling(i -> {
                program.linesPerBar = i;
                shouldSend = true;
            })
            .titled(DestroyLang.translate("tooltip.redstone_programmer.lines_per_bar").component())
            .addHint(DestroyLang.translate("tooltip.redstone_programmer.lines_per_bar.hint").component())
            .withRange(0, 32);
        linesPerBarScroller.setState(program.linesPerBar);
        addRenderableWidget(linesPerBarScroller);

        followPlayheadButton = new IconButton(leftPos + 196, topPos + background.height - 24, AllIcons.I_CONFIG_OPEN)
            .withCallback(() -> followPlayHead = true);
        followPlayheadButton.setToolTip(DestroyLang.translate("tooltip.redstone_programmer.follow_playhead").component());
        addRenderableWidget(followPlayheadButton);
    };

    @Override
    public void containerTick() {
        super.containerTick();

        // Tick chasers
        float speed = (float)noteWidth / program.getTicksPerBeat();
        playhead.chase((float)noteWidth * (float)program.getAbsolutePlaytime() / program.getTicksPerBeat(), speed, LerpedFloat.Chaser.LINEAR);
        if (followPlayHead) clampHorizontalScroll(playhead.getChaseTarget() - 20d, speed);
        if (Math.abs(playhead.getValue() - playhead.getChaseTarget()) > speed * 2f) playhead.setValue(playhead.getChaseTarget());
        if (followPlayHead && Math.abs(horizontalScroll.getValue() - horizontalScroll.getChaseTarget()) > speed * 2f) horizontalScroll.setValue(horizontalScroll.getChaseTarget());
        horizontalScroll.tickChaser();
        playhead.tickChaser();

        // Set the mode of play
        for (Entry<PlayMode, IconButton> entry : modeButtons.entrySet()) {
            entry.getValue().active = program.mode != entry.getKey();
        };

        // Advance playhead
        program.tick();

        // Update play button icon to reflect whether the program is playing
        setPlayPauseButtonIcon();
        
        // Sync to server
        if (shouldSend) {
            DestroyMessages.sendToServer(new RedstoneProgramSyncC2SPacket(program));
            shouldSend = false;
        };
    };

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        int mX = (int)mouseX - getGuiLeft();
        int mY = (int)mouseY - getGuiTop();
        boolean inNoteArea = NOTE_AREA.contains(mX, mY);
        boolean inItemArea = ITEM_AREA.contains(mX, mY);

        // Adding/deleting Notes and Channels
        if (inItemArea || inNoteArea) {
            double relativeMouseX = mX - NOTE_AREA.getX() + horizontalScroll.getChaseTarget();
            
            // Altering length of program
            if (inNoteArea && mY > NOTE_AREA.getY() + 70 && mY < NOTE_AREA.getY() + 82) {
                int programWidth = program.getLength() * noteWidth;
                if (relativeMouseX > programWidth + 10 && relativeMouseX < programWidth + 22) {
                    if (program.getLength() <= 1) return super.mouseClicked(mouseX, mouseY, button);
                    if (program.beatsPerLine == 0 || program.linesPerBar == 0 || Screen.hasShiftDown()) {
                        program.setDuration(program.getLength() - 1);
                    } else {
                        program.setDuration(Math.max(1, program.beatsPerLine * program.linesPerBar * ((program.getLength() - 1) / (program.beatsPerLine * program.linesPerBar)))); // Shorten the program to one fewer whole or partial bars
                    };
                } else if (relativeMouseX > programWidth + 26 && relativeMouseX < programWidth + 38) {
                    if (program.beatsPerLine == 0 || program.linesPerBar == 0 || Screen.hasShiftDown()) {
                        program.setDuration(program.getLength() + 1);
                    } else {
                        program.setDuration(program.beatsPerLine * program.linesPerBar * ((program.getLength() / (program.beatsPerLine * program.linesPerBar)) + 1));
                    };
                };
                shouldSend = true;
                return true;
            };

            // Determine the channel on which we are clicking
            ImmutableList<Channel> channels = program.getChannels();
            double posInList = mY - NOTE_AREA.getY() + verticalScroll;
            int channelNo = (int)(posInList / DISTANCE_BETWEEN_CHANNELS);
            if (channelNo < 0 || channelNo >= channels.size()) return super.mouseClicked(mouseX, mouseY, button);
            Channel channel = channels.get(channelNo);

            // Adding/removing notes
            if (inNoteArea) {
                int note = (int)(relativeMouseX / noteWidth);
                if (note < 0 || note >= program.getLength()) return super.mouseClicked(mouseX, mouseY, button);
                dragging = true;
                draggingChannel = channelNo;
                draggingDeleting = channel.getStrength(note) != 0;
                channel.setStrength(note, draggingDeleting ? 0 : 15);
                followPlayHead = false;
                shouldSend = true;
                return true;

            // Deleting and reordering channels
            } else if (inItemArea) {
                boolean success = false;
                if (mX > ITEM_AREA.getX() + 4) {
                    if (mX < ITEM_AREA.getX() + 16) {
                        program.remove(channel);
                        clampVerticalScroll(verticalScroll);
                        success = true;
                    } else if (mX < ITEM_AREA.getX() + 32 && channelNo < channels.size() - 1) {
                        program.swap(channel, channels.get(channelNo + 1));
                        success = true;
                    };
                };
                if (success) {
                    menu.refreshSlots();
                    shouldSend = true;
                    return true;
                };
            };
        };

        dragging = false;

        return super.mouseClicked(mouseX, mouseY, button);
    };

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        double mX = mouseX - getGuiLeft();
        double mY = mouseY - getGuiTop();

        if (dragging && NOTE_AREA.contains((int)mX, (int)mY)) {
            ImmutableList<Channel> channels = program.getChannels();
            if (draggingChannel < 0 || draggingChannel >= channels.size()) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            Channel channel = channels.get(draggingChannel);
            int leftNote = (int)((mX - NOTE_AREA.getX() + horizontalScroll.getChaseTarget()) / noteWidth);
            for (int i = 0; i <= Math.abs(dragX) / noteWidth; i++) {
                int note = leftNote + i * (int)Math.signum(dragX);
                if (note < 0 || note >= program.getLength()) continue;
                channel.setStrength(note, draggingDeleting ? 0 : 15);
            };
            shouldSend = true;
            return true;
        };

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    };

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    };

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int mX = (int)mouseX - getGuiLeft();
        int mY = (int)mouseY - getGuiTop();
        if (ITEM_AREA.contains(mX, mY)) {
            int oldScroll = verticalScroll;
            clampVerticalScroll(oldScroll + (int)delta * 5);
            if (oldScroll != verticalScroll) {
                menu.refreshSlots(verticalScroll);
                shouldSend = true;
            };
            return true;
        } else if (NOTE_AREA.contains(mX, mY)) {
            followPlayHead = false;

            // Changing strengths of notes
            ImmutableList<Channel> channels = program.getChannels();
            double posInList = mY - NOTE_AREA.getY() + verticalScroll;
            int channelNo = (int)(posInList / DISTANCE_BETWEEN_CHANNELS);
            if (channelNo >= 0 && channelNo < channels.size()) { // If we're scrolling on a valid channel
                Channel channel = channels.get(channelNo);
                int note = (int)((mX - NOTE_AREA.getX() + horizontalScroll.getChaseTarget()) / noteWidth);
                if (note >= 0 && note < program.getLength()) { // If we're scrolling on a valid note
                    int strength = channel.getStrength(note);
                    if (strength != 0) {
                        int minSelectedNote = note;
                        int maxSelectedNote = note;
                        if (!Screen.hasShiftDown()) {
                            while (minSelectedNote > 0) {
                                if (channel.getStrength(minSelectedNote) != strength) {
                                    minSelectedNote++;
                                    break;
                                };
                                minSelectedNote--;
                            };
                            while (maxSelectedNote < program.getLength() - 1) {
                                if (channel.getStrength(maxSelectedNote) != strength) {
                                    maxSelectedNote--;
                                    break;
                                };
                                maxSelectedNote++;
                            };
                        };
                        for (int i = minSelectedNote; i <= maxSelectedNote; i++) {
                            channel.setStrength(i, Mth.clamp(strength + (int)delta, 1, 15));
                        };
                        shouldSend = true;
                        return true;
                    };
                };
            };

            // Scrolling horizontally
            clampHorizontalScroll(horizontalScroll.getChaseTarget() + delta * 5, 10d);
        };
        return super.mouseScrolled(mouseX, mouseY, delta);
    };

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {

        PoseStack ms = graphics.pose();
        ms.pushPose();

        // Background
        background.render(graphics, leftPos, topPos);

        ms.translate(leftPos, topPos, 0d);

        float xOffset = horizontalScroll.getValue(partialTicks);
        float yOffset = -verticalScroll;

        // Labels
        graphics.drawString(font, DestroyLang.translate("tooltip.redstone_programmer.playback").component(), 6, background.height - 34, AllGuiTextures.FONT_COLOR, false);
        graphics.drawString(font, DestroyLang.translate("tooltip.redstone_programmer.editor").component(), 156, background.height - 34, AllGuiTextures.FONT_COLOR, false);
        // Play time
        int playtimeSeconds = program.getAbsolutePlaytime() / 20;
        int durationSeconds = program.getLength() * program.getTicksPerBeat() / 20;
        graphics.drawString(font, String.format("%02d:%02d / %02d:%02d", playtimeSeconds / 60, playtimeSeconds % 60, durationSeconds / 60, durationSeconds % 60), 128, 6, AllGuiTextures.FONT_COLOR, false);

        // Scroll values
        graphics.drawString(font, Component.literal(""+program.getTicksPerBeat()), 9, background.height - 19, 0xE0E0E0, true);
        graphics.drawString(font, Component.literal(""+program.beatsPerLine), 159, background.height - 19, 0xE0E0E0, true);
        graphics.drawString(font, Component.literal(""+program.linesPerBar), 179, background.height - 19, 0xE0E0E0, true);

        UIRenderHelper.swapAndBlitColor(minecraft.getMainRenderTarget(), UIRenderHelper.framebuffer);

        // Lines
        GuiHelper.startStencil(graphics, NOTE_AREA.getX(), NOTE_AREA.getY() - 11, NOTE_AREA.getWidth(), NOTE_AREA.getHeight() + 11);
        ms.pushPose();
        ms.translate(NOTE_AREA.getX(), NOTE_AREA.getY() - 10, 0f);
        if (program.beatsPerLine > 0) {
            int time = 0;
            while (time < program.getLength()) {
                float horizontalOffset = -xOffset + time * noteWidth;
                if (horizontalOffset < 0f || horizontalOffset > NOTE_AREA.getWidth()) {
                    time += program.beatsPerLine;
                    continue;
                };
                ms.pushPose();
                ms.translate(horizontalOffset, 0f, 0f);
                DestroyGuiTextures line = program.linesPerBar > 0 && time % (program.linesPerBar * program.beatsPerLine) == 0 ? DestroyGuiTextures.REDSTONE_PROGRAMMER_BARLINE : DestroyGuiTextures.REDSTONE_PROGRAMMER_LINE;
                line.render(graphics, 0, 0);
                ms.popPose();
                time += program.beatsPerLine;
            };
        };
        ms.pushPose();
        ms.translate(-xOffset + program.getLength() * noteWidth, 0f, 0f);
        DestroyGuiTextures.REDSTONE_PROGRAMMER_BARLINE.render(graphics, 0, 0);
        ms.popPose();
        ms.popPose();
        GuiHelper.endStencil();
        
        // Channels
        int channelNo = 0;
        for (Channel channel : program.getChannels()) {
            float verticalOffset = yOffset + channelNo * DISTANCE_BETWEEN_CHANNELS;
            if (verticalOffset < -DISTANCE_BETWEEN_CHANNELS || verticalOffset > ITEM_AREA.getHeight()) {
                channelNo++;
                continue;
            };

            // Items and buttons
            GuiHelper.startStencil(graphics, ITEM_AREA.getX(), ITEM_AREA.getY(), ITEM_AREA.getWidth(), ITEM_AREA.getHeight());
            ms.pushPose();
            ms.translate(ITEM_AREA.getX(), ITEM_AREA.getY() + verticalOffset, 0d);
            DestroyGuiTextures.REDSTONE_PROGRAMMER_ITEM_SLOTS.render(graphics, 31, 3);
            DestroyGuiTextures.REDSTONE_PROGRAMMER_DELETE_CHANNEL.render(graphics, 2, 3);
            if (channelNo < program.getChannels().size() - 1) DestroyGuiTextures.REDSTONE_PROGRAMMER_MOVE_CHANNEL_DOWN.render(graphics, 16, 8);
            graphics.renderItem(channel.getNetworkKey().getFirst().getStack(), 32, 4);
            graphics.renderItem(channel.getNetworkKey().getSecond().getStack(), 50, 4);
            ms.popPose();
            GuiHelper.endStencil();

            // Sequence
            GuiHelper.startStencil(graphics, NOTE_AREA.getX(), NOTE_AREA.getY(), NOTE_AREA.getWidth(), NOTE_AREA.getHeight());
            renderNotes: for (int i = 0; i < program.getLength(); i++) {
                float horizontalOffset = -xOffset + i * noteWidth;
                if (horizontalOffset < -5) continue;
                if (horizontalOffset > NOTE_AREA.getWidth()) break renderNotes;
                int strength = channel.getStrength(i);
                if (strength == 0) continue;

                boolean previousMatches = i == 0 ? false : channel.getStrength(i - 1) == strength;
                boolean nextMatches = i == program.getLength() ? false : channel.getStrength(i + 1) == strength;

                DestroyGuiTextures border = previousMatches ? (nextMatches ? DestroyGuiTextures.REDSTONE_PROGRAMMER_NOTE_BORDER_MIDDLE : DestroyGuiTextures.REDSTONE_PROGRAMMER_NOTE_BORDER_RIGHT) : (nextMatches ? DestroyGuiTextures.REDSTONE_PROGRAMMER_NOTE_BORDER_LEFT : DestroyGuiTextures.REDSTONE_PROGRAMMER_NOTE_BORDER_LONE);
                
                ms.pushPose();
                ms.translate(NOTE_AREA.getX() + horizontalOffset, NOTE_AREA.getY() + verticalOffset, 0);
                DestroyGuiTextures.getRedstoneProgrammerNote(strength).render(graphics, 0, 2);
                border.render(graphics, 0, 2);
                ms.popPose();
            };
            GuiHelper.endStencil();
            channelNo++;
        };

        // Additional item slots for adding a new channel
        GuiHelper.startStencil(graphics, ITEM_AREA.getX(), ITEM_AREA.getY(), ITEM_AREA.getWidth(), ITEM_AREA.getHeight());
        if (channelNo < DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMaxChannels.get()) {
            ms.pushPose();
            ms.translate(ITEM_AREA.getX(), NOTE_AREA.getY() + yOffset + channelNo * DISTANCE_BETWEEN_CHANNELS, 0f);
            DestroyGuiTextures.REDSTONE_PROGRAMMER_ITEM_SLOTS.render(graphics, 31, 3);
            ms.popPose();
        };
        GuiHelper.endStencil();

        // Playhead and length buttons
        GuiHelper.startStencil(graphics, NOTE_AREA.getX(), NOTE_AREA.getY() - 11, NOTE_AREA.getWidth(), NOTE_AREA.getHeight() + 11);
        ms.pushPose();
        ms.translate(NOTE_AREA.getX() - xOffset, NOTE_AREA.getY() - 10, channelNo);

        int programWidth = program.getLength() * noteWidth;
        if (program.getLength() > 1) DestroyGuiTextures.REDSTONE_PROGRAMMER_REMOVE_BAR.render(graphics, programWidth + 10, 80);
        DestroyGuiTextures.REDSTONE_PROGRAMMER_ADD_BAR.render(graphics, programWidth + 26, 80);

        double relativeMouseX = mouseX - getGuiLeft() - NOTE_AREA.getX() + xOffset;
        double relativeMouseY = mouseY - getGuiTop() - NOTE_AREA.getY();

        // Deciding on tooltip for adding/removing bars
        String renderedTooltip = "none";
        if (relativeMouseY > 70 && relativeMouseY < 82) {
            if (program.getLength() > 1 && relativeMouseX > programWidth + 10 && relativeMouseX < programWidth + 22) {
                renderedTooltip = "remove";
            } else if (relativeMouseX > programWidth + 26 && relativeMouseX < programWidth + 38) {
                renderedTooltip = "add";
            };
        };

        ms.pushPose();
        ms.translate(program.paused ? playhead.getValue() : playhead.getValue(partialTicks), 0f, 0f);
        DestroyGuiTextures.REDSTONE_PROGRAMMER_PLAYHEAD.render(graphics, -3, 0);
        ms.popPose();

        ms.popPose();

        GuiHelper.endStencil();

        // Shadows
        graphics.fillGradient(ITEM_AREA.getX(), ITEM_AREA.getY(), ITEM_AREA.getX() + ITEM_AREA.getWidth(), ITEM_AREA.getY() + 10, 200, 0x77000000, 0x00000000);
        graphics.fillGradient(ITEM_AREA.getX(), ITEM_AREA.getY() + ITEM_AREA.getHeight() - 10, ITEM_AREA.getX() + ITEM_AREA.getWidth(), ITEM_AREA.getY() + ITEM_AREA.getHeight(), 200, 0x00000000, 0x70000000);
        
        UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());

        ms.popPose();

        // Aforementioned tooltips for adding/removing bars
        if ("remove".equals(renderedTooltip)) {
            graphics.renderTooltip(font, List.of(DestroyLang.translate("tooltip.redstone_programmer.remove_bar").component(), DestroyLang.translate("tooltip.redstone_programmer.remove_bar.hint").component()), Optional.empty(), mouseX, mouseY);
        } else if ("add".equals(renderedTooltip)) {
            graphics.renderTooltip(font, List.of(DestroyLang.translate("tooltip.redstone_programmer.add_bar").component(), DestroyLang.translate("tooltip.redstone_programmer.add_bar.hint").component()), Optional.empty(), mouseX, mouseY);
        };
    };

    public void clampVerticalScroll(int newScroll) {
        verticalScroll = Mth.clamp(newScroll, 0, Math.max(0, 6 + Math.min(program.getChannels().size() + 1, DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMaxChannels.get()) * DISTANCE_BETWEEN_CHANNELS - ITEM_AREA.getHeight()));
    };

    public void clampHorizontalScroll(double newScroll, double speed) {
        horizontalScroll.chase(Mth.clamp(newScroll, 0d, Math.max(46d - NOTE_AREA.getWidth() + program.getLength() * noteWidth, 0d)), speed, LerpedFloat.Chaser.LINEAR);
    };

    public void setPlayPauseButtonIcon() {
        playPauseButton.setIcon(program.paused ? AllIcons.I_PLAY : AllIcons.I_PAUSE);
    };
    
};
