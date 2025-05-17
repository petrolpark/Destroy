package com.petrolpark.destroy.mixin.compat.jei;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.chemistry.legacy.ClientMixture;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.AllFluids;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.createmod.catnip.lang.Lang;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = CreateRecipeCategory.class, remap = false)
public abstract class CreateRecipeCategoryMixin<T extends Recipe<?>> {

    private static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(3);
        df.setMaximumFractionDigits(3);
    };

    /**
     * A map of the IDs of Create Categories to the classes of Recipe those Categories describe.
     */
    private static final Map<String, Class<? extends Recipe<?>>> CATEGORIES_AND_CLASSES = new HashMap<>();

    static {
        CATEGORIES_AND_CLASSES.put("mixing", MixingRecipe.class);
        CATEGORIES_AND_CLASSES.put("packing", CompactingRecipe.class);
        CATEGORIES_AND_CLASSES.put("spout_filling", FillingRecipe.class);
        CATEGORIES_AND_CLASSES.put("draining", EmptyingRecipe.class);
        CATEGORIES_AND_CLASSES.put("sequenced_assembly", SequencedAssemblyRecipe.class);
    };

    /**
     * Injection into {@link com.simibubi.create.compat.jei.category.CreateRecipeCategory#CreateRecipeCategory CreateRecipeCategory}.
     * As Create's {@link mezz.jei.api.recipe.RecipeType Recipe Types} are not exposed by default, we snipe them here and add them to the
     * {@link com.petrolpark.destroy.compat.jei.DestroyJEI#MIXTURE_APPLICABLE_RECIPE_TYPES list of Recipe Types} for which {@link com.petrolpark.destroy.chemistry.legacy.LegacyMixture Mixtures}
     * can be {@link com.petrolpark.destroy.core.recipe.ingredient.fluid.MoleculeFluidIngredient ingredients} or results.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN"),
        remap = false
    )
    public void inInit(Info<T> info, CallbackInfo ci) {

        String recipeTypeId = info.recipeType().getUid().getPath();
        if (CATEGORIES_AND_CLASSES.containsKey(recipeTypeId)) {
            DestroyJEI.MIXTURE_APPLICABLE_RECIPE_TYPES.put(info.recipeType(), CATEGORIES_AND_CLASSES.get(recipeTypeId));
        };
    };
    
    /**
     * Copied from the {@link com.simibubi.create.compat.jei.category.CreateRecipeCategory#addFluidTooltip Create source code} because I can't be bothered to deal with Injection.
     * Modifies the tooltip for Fluid Stacks which are {@link com.petrolpark.destroy.chemistry.legacy.LegacyMixture Mixtures}.
     */

    @Inject(
            method = "Lcom/simibubi/create/compat/jei/category/CreateRecipeCategory;addPotionTooltip(Lmezz/jei/api/gui/ingredient/IRecipeSlotView;Ljava/util/List;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Optional;get()Ljava/lang/Object;"),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void inAddPotionTooltip(IRecipeSlotView view, List<Component> tooltip, CallbackInfo ci, Optional displayed) {
        if (DestroyFluids.isMixture((FluidStack) displayed.get())) {
            Component name = DestroyLang.translate("mixture.mixture").component();
            boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

            CompoundTag fluidTag = ((FluidStack) displayed.get()).getOrCreateTag();
            List<Component> mixtureTooltip = new ArrayList<>();

            if (view.getRole() == RecipeIngredientRole.INPUT || view.getRole() == RecipeIngredientRole.CATALYST) {
                mixtureTooltip = DestroyLang.mixtureIngredientTooltip(fluidTag);
            } else if (view.getRole() == RecipeIngredientRole.OUTPUT) {
                CompoundTag mixtureTag = fluidTag.getCompound("Mixture");
                if (!mixtureTag.isEmpty()) {
                    ClientMixture mixture = ClientMixture.readNBT(ClientMixture::new, mixtureTag);
                    name = mixture.getName();
                    mixtureTooltip = mixture.getContentsTooltip(iupac, false, false, ((FluidStack) displayed.get()).getAmount(), df);
                } else {
                    mixtureTooltip = List.of(DestroyLang.translate("mixture.empty").component());
                };
            };
            tooltip.add(name);
            tooltip.addAll(mixtureTooltip);
        };
    };
};
