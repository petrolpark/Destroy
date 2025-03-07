package com.petrolpark.destroy.content.oil.seismology;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class SeismographScreen extends AbstractSimiScreen {

    private final Minecraft mc;

    private final Seismograph seismograph;
    private final Integer mapId;
    private final MapItemSavedData mapData;
    private final InteractionHand hand;

    public static final int SCALE = 3;

    public SeismographScreen(ItemStack stack, InteractionHand hand) {
        mc = Minecraft.getInstance();

        mapId = SeismographItem.getMapId(stack);
        mapData = SeismographItem.getSavedData(mapId, mc.level);
        seismograph = SeismographItem.readSeismograph(stack);

        this.hand = hand;
    };

    @Override
    protected void init() {
        setWindowSize(64 * SCALE, 64 * SCALE);
        super.init();
    };

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double x = (mouseX - guiLeft) / (double)SCALE;
        double y = (mouseY - guiTop) / (double)SCALE;
        if (seismograph != null && x > 13 && y > 13 && x < 60 && y < 60) {
            int gridX = (int)(x - 13) / 6;
            int gridY = (int)(y - 13) / 6;
            Seismograph.Mark mark = seismograph.getMark(gridX, gridY);
            Seismograph.Mark newMark = null;
            switch (mark) {
                case TICK:
                case CROSS:
                    break;
                case NONE: {
                    newMark = Seismograph.Mark.GUESSED_TICK;
                    break;
                } case GUESSED_TICK: {
                    newMark = Seismograph.Mark.GUESSED_CROSS;
                    break;
                } case GUESSED_CROSS: {
                    newMark = Seismograph.Mark.NONE;
                    break;
                }
            };
            if (newMark != null) {
                seismograph.mark(gridX, gridY, newMark);
                seismograph.triggerSolveSeismographAdvancement(null, null); // Won't actually trigger the advancement, but will fill in the grid if player is correct
                DestroyMessages.sendToServer(new MarkSeismographC2SPacket((byte)gridX, (byte)gridY, newMark, hand == InteractionHand.MAIN_HAND));
            };
        };
        return super.mouseClicked(mouseX, mouseY, button);
    };

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(guiLeft, guiTop, 0f);
        ms.scale(3f, 3f, 3f);
        SeismographItemRenderer.renderSeismograph(ms, graphics.bufferSource(), 0xFFFFFF, mapId, mapData, seismograph, mc, (t, x, y) -> t.render(ms, x, y), false);
        ms.popPose();
    };
    
};
