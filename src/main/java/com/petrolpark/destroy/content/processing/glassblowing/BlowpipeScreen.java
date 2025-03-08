package com.petrolpark.destroy.content.processing.glassblowing;

import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.client.DestroyFluidRenderer;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.lang.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BlowpipeScreen extends AbstractSimiScreen {

    private static final Object recipeCacheKey = new Object();

    protected DestroyGuiTextures background;

    protected final InteractionHand hand;
    protected List<GlassblowingRecipe> recipes;

    protected int scroll;

    public BlowpipeScreen(InteractionHand hand) {
        super(DestroyLang.translate("tooltip.blowpipe.select_recipe").component());
        this.hand = hand;
        background = DestroyGuiTextures.BLOWPIPE_BACKGROUND;
    };

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        recipes = RecipeFinder.get(recipeCacheKey, minecraft.level, r -> r instanceof GlassblowingRecipe).stream().map(r -> (GlassblowingRecipe)r).toList();
        refreshRecipeButtons();
    };

    public void refreshRecipeButtons() {
        clearWidgets();
        for (int i = 0; i < 3; i++) {
            if (scroll + i >= recipes.size()) return;
            addRenderableWidget(new RecipeButton(scroll + i, guiLeft + 8, 15 + guiTop + (i * 18)));
        };
    };

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!super.mouseScrolled(mouseX, mouseY, delta)) {
            if (recipes.size() <= 3) return false;
            scroll = Mth.clamp((int)(scroll - delta), 0, recipes.size() - 3);
            refreshRecipeButtons();
            return true;
        };
        return true;
    };

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        background.render(graphics, guiLeft, guiTop);
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(guiLeft, guiTop, 100);
        mouseX -= guiLeft;
        mouseY -= guiTop;
        graphics.drawString(font, title, 6, 5, 0x8B8B8B, false);

        // Recipes
        for (int i = 0; i < 3; i++) {
            if (scroll + i >= recipes.size()) continue;
            GlassblowingRecipe recipe = recipes.get(scroll + i);
            int y = 16 + i * 18;

            // Item
            ItemStack stack = recipes.get(scroll + i).getResultItem(minecraft.level.registryAccess());
            graphics.renderItem(stack, 56, y, i);

            // Fluid
            FluidIngredient ingredient  = recipe.getFluidIngredients().get(0);
            List<FluidStack> ingredients = ingredient.getMatchingFluidStacks();
            FluidStack fluidStack = ingredients.get((AnimationTickHolder.getTicks() / 20) % ingredients.size());
            DestroyFluidRenderer.renderFluidSquare(graphics, 9, y, fluidStack);

            // Tooltips
            if (mouseY >= y && mouseY <= y + 16) {
                if (mouseX >= 9 && mouseX <= 27) {
                    graphics.renderTooltip(font, List.of(fluidStack.getDisplayName(), DestroyLang.builder().add(Component.literal(""+ingredient.getRequiredAmount())).add(CreateLang.translate("generic.unit.millibuckets")).style(ChatFormatting.GRAY).component()), Optional.empty(), mouseX, mouseY);
                };
                if (mouseX >= 56 && mouseX <= 72) {
                    graphics.renderTooltip(font, stack, mouseX, mouseY);
                };
            };
        };

        // Scroll bar
        if (recipes.size() <= 3) {
            DestroyGuiTextures.BLOWPIPE_SCROLL_LOCKED.render(graphics, 75, 15);
        } else {
            DestroyGuiTextures.BLOWPIPE_SCROLL.render(graphics, 75, 15 + (int)(39f * (float)scroll / (float)(recipes.size() - 3f)));
        };

        ms.popPose();
    };

    public class RecipeButton extends AbstractSimiWidget {

        protected RecipeButton(int recipeNo, int x, int y) {
            super(x, y, 64, 18);
            withCallback(() -> {
                if (recipeNo >= recipes.size()) return;
                DestroyMessages.sendToServer(new SelectGlassblowingRecipeC2SPacket(hand, recipes.get(recipeNo).getId()));
                if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer();
            });
        };

        @Override
        protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            (isMouseOver(mouseX, mouseY) ? DestroyGuiTextures.BLOWPIPE_RECIPE_SELECTED : DestroyGuiTextures.BLOWPIPE_RECIPE).render(graphics, getX(), getY());
        };

    };
    
};
