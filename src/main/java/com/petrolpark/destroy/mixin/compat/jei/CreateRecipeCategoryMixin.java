package com.petrolpark.destroy.mixin.compat.jei;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.chemistry.legacy.ClientMixture;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.fluids.transfer.EmptyingRecipe;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = CreateRecipeCategory.class, remap = false)
public abstract class CreateRecipeCategoryMixin<T extends Recipe<?>> {

    @Unique
    private static final DecimalFormat destroy$df = new DecimalFormat();
    static {
        destroy$df.setMinimumFractionDigits(3);
        destroy$df.setMaximumFractionDigits(3);
    };

    /**
     * A map of the IDs of Create Categories to the classes of Recipe those Categories describe.
     */
    @Unique
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

    @Inject(
        method = "addPotionTooltip(Lmezz/jei/api/gui/ingredient/IRecipeSlotView;Ljava/util/List;)V",
        at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Optional;get()Ljava/lang/Object;"),
        remap = false,
        locals = LocalCapture.CAPTURE_FAILHARD)
    private static void inAddPotionTooltip(IRecipeSlotView view, List<Component> tooltip, CallbackInfo ci, Optional<FluidStack> displayed) {
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
                    mixtureTooltip = mixture.getContentsTooltip(iupac, false, false, ((FluidStack) displayed.get()).getAmount(), destroy$df);
                } else {
                    mixtureTooltip = List.of(DestroyLang.translate("mixture.empty").component());
                };
            };

            if (tooltip.isEmpty()) {
                tooltip.add(0, name);
            } else {
                tooltip.set(0, name);
            };
            tooltip.addAll(1, mixtureTooltip);
        };
    };
};
