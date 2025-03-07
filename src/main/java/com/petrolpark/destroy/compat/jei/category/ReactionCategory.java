package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2i;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.JEITextureDrawable;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.chemistry.legacy.IItemReactant;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.client.stackedtextbox.AbstractStackedTextBox;
import com.petrolpark.destroy.client.stackedtextbox.AbstractStackedTextBox.LinesAndActivationAreas;
import com.petrolpark.destroy.compat.jei.MoleculeJEIIngredient;
import com.petrolpark.destroy.compat.jei.tooltip.ReactionTooltipHelper;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.chemistry.recipe.ReactionRecipe;
import com.petrolpark.destroy.core.chemistry.recipe.ReactionRecipe.GenericReactionRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.lang.FontHelper.Palette;

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

    public static RecipeType<? extends ReactionRecipe> TYPE;

    public static final Map<LegacyReaction, ReactionRecipe> RECIPES = new HashMap<>();

    public ReactionCategory(Info<T> info, IJeiHelpers helpers) {
        super(info, helpers);
        if (TYPE == null) TYPE = info.recipeType();
    };

    /**
     * Generate every Reaction's recipe to go in JEI.
     */
    static {
        for (LegacyReaction reaction : LegacyReaction.REACTIONS.values()) {
            if (reaction.includeInJei()) RECIPES.put(reaction, ReactionRecipe.create(reaction));
        };
    };

    @Override
    public Collection<LinesAndActivationAreas> getHoverableTexts(ReactionRecipe recipe) {
        LegacyReaction reaction = recipe.getReaction();
        if (reaction.getId() == null && !(recipe instanceof GenericReactionRecipe)) return List.of();

        Minecraft minecraft = Minecraft.getInstance();
        List<LinesAndActivationAreas> paragraphs = new ArrayList<>(2);
        paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(Component.translatable(getTranslationKey(recipe)).getString(), 2, 2, 176, minecraft.screen, minecraft.font, DARK_GRAY_AND_BLUE, false));
        paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(Component.translatable(getTranslationKey(recipe) + ".description").getString(), 2, 90, 176, minecraft.screen, minecraft.font, DARK_GRAY_AND_BLUE, false));
        
        if (reaction.needsUV()) {
            Vector2i pos = getCatalystRenderPosition(0, getNumberOfCatalysts(reaction));
            paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(DestroyLang.translate("tooltip.reaction.ultraviolet").string(), pos.x + 3, pos.y + 4, 100, minecraft.screen, minecraft.font, DestroyLang.WHITE_AND_WHITE, false));
        };

        if (reaction.isHalfReaction()) {
            int reactants = getNumberOfReactants(reaction);
            Vector2i pos = getReactantRenderPosition(reactants - 1, reactants);
            paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(DestroyLang.translate("tooltip.reaction.electrons", DestroyLang.nothingIfOne(reaction.getElectronsTransferred())).string(), pos.x + 3, pos.y + 4, 100, minecraft.screen, minecraft.font, DestroyLang.WHITE_AND_WHITE, false));
            paragraphs.add(AbstractStackedTextBox.getTextAndActivationAreas(DestroyLang.translate("tooltip.reaction.standard_half_cell_potential_hoverable", reaction.getStandardHalfCellPotential()).string(), 72, 36, 100, minecraft.screen, minecraft.font, DestroyLang.WHITE_AND_WHITE, false));
        };

        return paragraphs;
    };

    protected String getTranslationKey(ReactionRecipe recipe) {
        LegacyReaction reaction = recipe.getReaction();
        return reaction.getNameSpace() + ".reaction." + reaction.getId();
    };

    @Override
    public Palette getPaletteForBoxes() {
        return WHITE_AND_AQUA;
    };

    private static void tooManyMoleculesWarning(boolean reactants, LegacyReaction reaction) {
        Destroy.LOGGER.warn("Reaction '"+reaction.getFullId()+"' has too many " + (reactants ? "reactants" : "products") + " to fit on JEI.");
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        LegacyReaction reaction = recipe.getReaction();

        int i = 0;

        int numberOfReactants = getNumberOfReactants(reaction);
        if (numberOfReactants >= 6) tooManyMoleculesWarning(true, reaction);

        for (LegacySpecies reactant : reaction.getReactants()) {
            if (i >= 6) continue;
            Vector2i pos = getReactantRenderPosition(i, numberOfReactants);
            builder.addSlot(RecipeIngredientRole.INPUT, pos.x, pos.y)
                .addIngredient(MoleculeJEIIngredient.TYPE, reactant)
                .addRichTooltipCallback(ReactionTooltipHelper.reactantTooltip(reaction, reactant))
                .setBackground(getRenderedSlot(), -1, -1);
            i++;
        };

        for (IItemReactant itemReactant : reaction.getItemReactants()) {
            if (i >= 6) continue;
            if (!itemReactant.isCatalyst()) {
                Vector2i pos = getReactantRenderPosition(i, numberOfReactants);
                builder.addSlot(reaction.displayAsReversible() ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT, pos.x, pos.y)
                    .addItemStacks(itemReactant.getDisplayedItemStacks())
                    .addRichTooltipCallback(ReactionTooltipHelper.itemReactantTooltip(reaction, itemReactant))
                    .setBackground(getRenderedSlot(), -1, -1);
                i++;
            };
        };

        Collection<PrecipitateReactionResult> precipitates = reaction.hasResult() ? reaction.getResult().getAllPrecipitates() : Collections.emptyList();

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

        for (LegacySpecies product : reaction.getProducts()) {
            if (j >= 6) continue;
            builder.addSlot(RecipeIngredientRole.OUTPUT, productsXOffset + (19 * (j % l)), productYOffset + (j / l) * 19)
                .addIngredient(MoleculeJEIIngredient.TYPE, product)
                .addRichTooltipCallback(ReactionTooltipHelper.productTooltip(reaction, product))
                .setBackground(getRenderedSlot(), -1, -1);
            j++;
        };

        for (PrecipitateReactionResult precipitate : precipitates) {
            if (j >= 6) continue;
            builder.addSlot(reaction.displayAsReversible() ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.OUTPUT, productsXOffset + (19 * (j % l)), productYOffset+ (j / l) * 19)
                .addItemStack(precipitate.getPrecipitate())
                .addRichTooltipCallback(ReactionTooltipHelper.precipitateTooltip(reaction, precipitate))
                .setBackground(getRenderedSlot(), -1, -1);
            j++;
        };

        int numberOfCatalysts = getNumberOfCatalysts(reaction);
        int m = 0;
        if (reaction.needsUV()) m++; // If there is UV catalyst, this is already drawn

        for (LegacySpecies catalyst : reaction.getOrders().keySet()) {
            if (reaction.getReactants().contains(catalyst)) continue;
            Vector2i pos = getCatalystRenderPosition(m, numberOfCatalysts);
            builder.addSlot(RecipeIngredientRole.CATALYST, pos.x, pos.y)
                .addIngredient(MoleculeJEIIngredient.TYPE, catalyst)
                .addRichTooltipCallback(ReactionTooltipHelper.catalystTooltip(reaction, catalyst))
                .setBackground(getRenderedSlot(), -1, -1);
            m++;
        };

        for (IItemReactant itemReactant : reaction.getItemReactants()) {
            if (!itemReactant.isCatalyst()) continue;
            Vector2i pos = getCatalystRenderPosition(m, numberOfCatalysts);
            builder.addSlot(RecipeIngredientRole.CATALYST, pos.x, pos.y)
                .addItemStacks(itemReactant.getDisplayedItemStacks())
                .addRichTooltipCallback(ReactionTooltipHelper.itemReactantTooltip(reaction, itemReactant))
                .setBackground(getRenderedSlot(), -1, -1);
            m++;
        };

        if (DestroyAllConfigs.CLIENT.chemistry.nerdMode.get()) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 163, 68)
                .setOverlay(JEITextureDrawable.of(PetrolparkGuiTexture.JEI_NERD_EMOJI), 0, 1)
                .addItemStack(DestroyItems.ABS.asStack()) // Dummy item so we actually get something generated
                .addRichTooltipCallback(ReactionTooltipHelper.nerdModeTooltip(reaction));
        };
    };

    protected Vector2i getReactantRenderPosition(int position, int numberOfReactants) {
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
        return new Vector2i(reactantsXOffset + (19 * (position % k)), reactantYOffset + (position / k) * 19);
    };

    protected int getNumberOfReactants(LegacyReaction reaction) {
        return reaction.getReactants().size() + reaction.getItemReactants().stream().filter(ir -> !ir.isCatalyst()).toList().size() + (reaction.isHalfReaction() ? 1 : 0);
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

    protected int getNumberOfCatalysts(LegacyReaction reaction) {
        int number = 0;
        for (LegacySpecies molecule : reaction.getOrders().keySet()) {
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
        PetrolparkGuiTexture.JEI_LINE.render(graphics, 2, 12);
        PetrolparkGuiTexture.JEI_LINE.render(graphics, 2, 85);
        (recipe.getReaction().displayAsReversible() ? PetrolparkGuiTexture.JEI_EQUILIBRIUM_ARROW : AllGuiTextures.JEI_ARROW).render(graphics, yOffset + 37, 46);
    };
    
};
