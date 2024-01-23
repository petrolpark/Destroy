package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2i;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.IItemReactant;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.petrolpark.destroy.chemistry.reactionresult.CombinedReactionResult;
import com.petrolpark.destroy.chemistry.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.client.gui.stackedtextbox.AbstractStackedTextBox;
import com.petrolpark.destroy.client.gui.stackedtextbox.AbstractStackedTextBox.LinesAndActivationAreas;
import com.petrolpark.destroy.compat.jei.MoleculeJEIIngredient;
import com.petrolpark.destroy.compat.jei.ReactionTooltipHelper;
import com.petrolpark.destroy.compat.jei.animation.DestroyGuiTextureDrawable;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.ReactionRecipe;
import com.petrolpark.destroy.recipe.ReactionRecipe.GenericReactionRecipe;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ReactionCategory<T extends ReactionRecipe> extends HoverableTextCategory<T> {

    private static final int yOffset = 32;

    public static final Palette DARK_GRAY_AND_BLUE = Palette.ofColors(ChatFormatting.DARK_GRAY, ChatFormatting.BLUE);
    public static final Palette WHITE_AND_AQUA = Palette.ofColors(ChatFormatting.WHITE, ChatFormatting.AQUA);
    public static final Palette WHITE_AND_WHITE = Palette.ofColors(ChatFormatting.WHITE, ChatFormatting.WHITE);

    public static RecipeType<? extends ReactionRecipe> TYPE;

    public static final Map<Reaction, ReactionRecipe> RECIPES = new HashMap<>();

    public ReactionCategory(Info<T> info, IJeiHelpers helpers) {
        super(info, helpers);
        if (TYPE == null) TYPE = info.recipeType();
    };

    /**
     * Generate every Reaction's recipe to go in JEI.
     */
    static {
        for (Reaction reaction : Reaction.REACTIONS.values()) {
            ReactionRecipe recipe = ReactionRecipe.create(reaction);
            reaction.getReverseReactionForDisplay().ifPresent(reverseReaction -> {
                RECIPES.put(reverseReaction, recipe);
            });
            if (reaction.includeInJei()) RECIPES.put(reaction, recipe);
        };
    };

    @Override
    public Collection<LinesAndActivationAreas> getHoverableTexts(ReactionRecipe recipe) {
        Reaction reaction = recipe.getReaction();
        if (reaction.getId() == null && !(recipe instanceof GenericReactionRecipe)) return List.of();

        Minecraft minecraft = Minecraft.getInstance();
        List<LinesAndActivationAreas> paragraphs = new ArrayList<>(2);
        paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(Component.translatable(getTranslationKey(recipe)).getString(), 2, 2, 176, minecraft.screen, minecraft.font, DARK_GRAY_AND_BLUE, false));
        paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(Component.translatable(getTranslationKey(recipe) + ".description").getString(), 2, 90, 176, minecraft.screen, minecraft.font, DARK_GRAY_AND_BLUE, false));
        if (reaction.needsUV()) {
            Vector2i pos = getCatalystRenderPosition(0, getNumberOfCatalysts(reaction));
            paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(DestroyLang.translate("tooltip.reaction.ultraviolet").string(), pos.x + 3, pos.y + 4, 100, minecraft.screen, minecraft.font, WHITE_AND_WHITE, false));
        };
        return paragraphs;
    };

    protected String getTranslationKey(ReactionRecipe recipe) {
        Reaction reaction = recipe.getReaction();
        return reaction.getNameSpace() + ".reaction." + reaction.getId();
    };

    @Override
    public Palette getPaletteForBoxes() {
        return WHITE_AND_AQUA;
    };

    private static void tooManyMoleculesWarning(boolean reactants, Reaction reaction) {
        Destroy.LOGGER.warn("Reaction '"+reaction.getFullId()+"' has too many " + (reactants ? "reactants" : "products") + " to fit on JEI.");
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        Reaction reaction = recipe.getReaction();

        int i = 0;

        int numberOfReactants = reaction.getReactants().size() + reaction.getItemReactants().stream().filter(ir -> !ir.isCatalyst()).toList().size();
        if (numberOfReactants >= 6) tooManyMoleculesWarning(true, reaction);
        int reactantsXOffset = 5;
        if (numberOfReactants >= 5) {
        } else if (numberOfReactants % 2 == 0) {
            reactantsXOffset += 9;
        } else if (numberOfReactants == 1) {
            reactantsXOffset += 19;
        };
        int reactantYOffset = yOffset;
        if (numberOfReactants <= 3) reactantYOffset += 9;

        int k = numberOfReactants == 4 ? 2 : 3;

        for (Molecule reactant : reaction.getReactants()) {
            if (i >= 6) continue;
            builder.addSlot(RecipeIngredientRole.INPUT, reactantsXOffset + (19 * (i % k)), reactantYOffset + (i / k) * 19)
                .addIngredient(MoleculeJEIIngredient.TYPE, reactant)
                .addTooltipCallback(ReactionTooltipHelper.reactantTooltip(reaction, reactant))
                .setBackground(getRenderedSlot(), -1, -1);
            i++;
        };

        for (IItemReactant itemReactant : reaction.getItemReactants()) {
            if (i >= 6) continue;
            if (!itemReactant.isCatalyst()) {
                builder.addSlot(RecipeIngredientRole.INPUT, reactantsXOffset + (19 * (i % k)), reactantYOffset + (i / k) * 19)
                    .addItemStacks(itemReactant.getDisplayedItemStacks())
                    .addTooltipCallback(ReactionTooltipHelper.itemReactantTooltip(reaction, itemReactant))
                    .setBackground(getRenderedSlot(), -1, -1);
                i++;
            };
            // TODO display catalysts
        };

        List<PrecipitateReactionResult> precipitates = new ArrayList<>();
        if (reaction.hasResult()) {
            if (reaction.getResult() instanceof PrecipitateReactionResult precipitate) {
                precipitates.add(precipitate);
            } else if (reaction.getResult() instanceof CombinedReactionResult combinedResults) {
                for (ReactionResult combinedResult : combinedResults.getChildren()) {
                    if (combinedResult instanceof PrecipitateReactionResult precipitate) precipitates.add(precipitate);
                };
            };
        };

        int j = 0;

        int numberOfProducts = reaction.getProducts().size() + precipitates.size();
        if (numberOfProducts >= 6) tooManyMoleculesWarning(false, reaction);
        int productsXOffset = 120;
        if (numberOfProducts >= 5) {
        } else if (numberOfProducts % 2 == 0) {
            productsXOffset += 9;
        } else if (numberOfProducts == 1) {
            productsXOffset += 19;
        };
        int productYOffset = yOffset;
        if (numberOfProducts <= 3) productYOffset += 9;

        int l = numberOfProducts == 4 ? 2 : 3;

        for (Molecule product : reaction.getProducts()) {
            if (j >= 6) continue;
            builder.addSlot(RecipeIngredientRole.OUTPUT, productsXOffset + (19 * (j % l)), productYOffset + (j / l) * 19)
                .addIngredient(MoleculeJEIIngredient.TYPE, product)
                .addTooltipCallback(ReactionTooltipHelper.productTooltip(reaction, product))
                .setBackground(getRenderedSlot(), -1, -1);
            j++;
        };

        for (PrecipitateReactionResult precipitate : precipitates) {
            if (j >= 6) continue;
            builder.addSlot(RecipeIngredientRole.OUTPUT, productsXOffset + (19 * (j % l)), productYOffset+ (j / l) * 19)
                .addItemStack(precipitate.getPrecipitate())
                .addTooltipCallback(ReactionTooltipHelper.precipitateTooltip(reaction, precipitate))
                .setBackground(getRenderedSlot(), -1, -1);
            j++;
        };

        int numberOfCatalysts = getNumberOfCatalysts(reaction);
        int m = 0;
        if (reaction.needsUV()) m++; // If there is UV catalyst, this is already drawn

        for (Molecule catalyst : reaction.getOrders().keySet()) {
            if (reaction.getReactants().contains(catalyst)) continue;
            Vector2i pos = getCatalystRenderPosition(m, numberOfCatalysts);
            builder.addSlot(RecipeIngredientRole.CATALYST, pos.x, pos.y)
                .addIngredient(MoleculeJEIIngredient.TYPE, catalyst)
                .addTooltipCallback(ReactionTooltipHelper.catalystTooltip(reaction, catalyst))
                .setBackground(getRenderedSlot(), -1, -1);
            m++;
        };

        for (IItemReactant itemReactant : reaction.getItemReactants()) {
            if (!itemReactant.isCatalyst()) continue;
            Vector2i pos = getCatalystRenderPosition(m, numberOfCatalysts);
            builder.addSlot(RecipeIngredientRole.CATALYST, pos.x, pos.y)
                .addItemStacks(itemReactant.getDisplayedItemStacks())
                .addTooltipCallback(ReactionTooltipHelper.itemReactantTooltip(reaction, itemReactant))
                .setBackground(getRenderedSlot(), -1, -1);
            m++;
        };

        if (DestroyAllConfigs.CLIENT.chemistry.nerdMode.get()) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 163, 68)
                .setOverlay(DestroyGuiTextureDrawable.of(DestroyGuiTextures.NERD_EMOJI), 0, 1)
                .addItemStack(DestroyItems.ABS.asStack()) // Dummy item so we actually get something generated
                .addTooltipCallback(ReactionTooltipHelper.nerdModeTooltip(reaction));
        };
    };

    protected Vector2i getCatalystRenderPosition(int position, int numberOfCatalysts) {
        if (position >= numberOfCatalysts) return new Vector2i(0, 0);
        int y = position >= 2 ? 24 : 58;
        int x;
        if (numberOfCatalysts % 2 == 0 || (numberOfCatalysts == 3 && position <= 1)) {
            x = 70 + ((position % 2) * 19);
        } else {
            x = 78;
        };
        return new Vector2i(x, y);
    };

    protected int getNumberOfCatalysts(Reaction reaction) {
        int number = 0;
        for (Molecule molecule : reaction.getOrders().keySet()) {
            if (!reaction.getReactants().contains(molecule)) number++;
        };
        for (IItemReactant itemReactant : reaction.getItemReactants()) {
            if (itemReactant.isCatalyst()) number++;
        };
        if (reaction.needsUV()) number++;
        return number;
    };

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        DestroyGuiTextures.JEI_LINE.render(graphics, 2, 12);
        DestroyGuiTextures.JEI_LINE.render(graphics, 2, 85);
        (recipe.getReaction().displayAsReversible() ? DestroyGuiTextures.JEI_EQUILIBRIUM_ARROW : AllGuiTextures.JEI_ARROW).render(graphics, yOffset + 37, 46);
    };
    
};
