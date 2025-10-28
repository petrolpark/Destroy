package com.petrolpark.destroy.content.oil.seismology;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.AnimatedOverlayElementBase;
import net.createmod.ponder.foundation.instruction.FadeInOutInstruction;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class ShowSeismographPonderInstruction extends FadeInOutInstruction {

    private final Minecraft mc;
    private final Set<Outline> outlines;

    private final Vec3 sceneSpace;
    private final Pointing direction;
    private final Seismograph seismograph;
    
    private final SeismographElement element;

	private ShowSeismographPonderInstruction(Pointing direction, Vec3 sceneSpace, Seismograph seismograph) {
		super(100000);

        mc = Minecraft.getInstance();
        outlines = new HashSet<>();

        this.sceneSpace = sceneSpace;
        this.direction = direction;
        this.seismograph = seismograph;

		element = new SeismographElement();
	};

    public static final SeismographElement add(SceneBuilder scene, Pointing direction, Vec3 sceneSpace, Seismograph seismograph) {
        ShowSeismographPonderInstruction instruction = new ShowSeismographPonderInstruction(direction, sceneSpace, seismograph);
        scene.addInstruction(instruction);
        return instruction.element;
    };

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        outlines.clear();
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        Iterator<Outline> iterator = outlines.iterator();
        while (iterator.hasNext()) {
            Outline outline = iterator.next();
            outline.ttl--;
            if (outline.ttl <= 0) iterator.remove();
            if (outline.ttl <= 10) outline.fade.chase(0f, 0.2f, LerpedFloat.Chaser.LINEAR);
            else if (outline.lifetime - outline.ttl <= 10) outline.fade.chase(1f, 0.2f, LerpedFloat.Chaser.LINEAR);
            outline.fade.tickChaser();
        };
    };

	@Override
	protected void show(PonderScene scene) {
		scene.addElement(element);
		element.setVisible(true);
	};

	@Override
	protected void hide(PonderScene scene) {
		element.setVisible(false);
	};

	@Override
	protected void applyFade(PonderScene scene, float fade) {
		element.setFade(fade);
	};

    public static class Outline {
        public final int x;
        public final int y;
        public final DestroyGuiTextures texture;
        public final PonderPalette color;
        public final int lifetime;
        public int ttl;

        LerpedFloat fade;

        public Outline(int x, int y, DestroyGuiTextures texture, PonderPalette color, int ttl) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.color = color;
            lifetime = ttl + 15;
            this.ttl = ttl + 15;

            fade = LerpedFloat.linear().startWithValue(0f);
        };
    };

    public class SeismographElement extends AnimatedOverlayElementBase {

        public void close() {
            remainingTicks = 10;
        };

        public void highlightRow(SceneBuilder scene, PonderPalette color, int row, int ticks) {
            scene.addInstruction(s -> outlines.add(new Outline(4, 12 + 6 * row, DestroyGuiTextures.SEISMOGRAPH_HIGHLIGHT_ROW, color, ticks)));
        };

        public void highlightColumn(SceneBuilder scene, PonderPalette color, int column, int ticks) {
            scene.addInstruction(s -> outlines.add(new Outline(12 + 6 * column, 4, DestroyGuiTextures.SEISMOGRAPH_HIGHLIGHT_COLUMMN, color, ticks)));
        };

        public void highlightPlus(SceneBuilder scene, PonderPalette color, int x, int z, int ticks) {
            scene.addInstruction(s -> outlines.add(new Outline(6 + 6 * x, 6 + 6 * z, DestroyGuiTextures.SEISMOGRAPH_HIGHLIGHT_CROSS, color, ticks)));
        };

        public void highlightCell(SceneBuilder scene, PonderPalette color, int x, int z, int ticks) {
            scene.addInstruction(s -> outlines.add(new Outline(12 + 6 * x, 12 + 6 * z, DestroyGuiTextures.SEISMOGRAPH_HIGHLIGHT_CELL, color, ticks)));
        };

        /**
         * Largely copied from {@link com.simibubi.create.foundation.ponder.element.InputWindowElement Create 0.5.1 source code}.
         */
        @Override
        public void render(PonderScene scene, PonderUI screen, GuiGraphics graphics, float partialTicks, float fade) {

            float xFade = direction == Pointing.RIGHT ? -1 : direction == Pointing.LEFT ? 1 : 0;
            float yFade = direction == Pointing.DOWN ? -1 : direction == Pointing.UP ? 1 : 0;
            xFade *= 10 * (1 - fade);
            yFade *= 10 * (1 - fade);

            if (fade < 1 / 16f) return;
            Vec2 sceneToScreen = scene.getTransform().sceneToScreen(sceneSpace, partialTicks);

            RenderSystem.disableDepthTest();
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(sceneToScreen.x + xFade, sceneToScreen.y + yFade, 400f);
            PonderUI.renderSpeechBox(graphics, 0, 0, 96, 96, false, direction, true);
            ms.pushPose();
            ms.scale(1.5f, 1.5f, 1.5f);
            SeismographItemRenderer.renderSeismograph(ms, graphics.bufferSource(), 0, null, null, seismograph, mc, (t, x, y) -> t.render(graphics, (int)x, (int)y), false);
            ms.translate(0f, 0f, 100f);
            for (Outline outline : outlines) {
                outline.texture.render(graphics, outline.x, outline.y, outline.color.getColorObject().setAlpha(1f).scaleAlpha(outline.fade.getValue(partialTicks)));
            };
            ms.popPose();
            ms.popPose();
            RenderSystem.enableDepthTest();
        };
        
    };
    
};
