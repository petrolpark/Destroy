package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.AllFluids;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.contraptions.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

@Mixin(CreateRecipeCategory.class)
public class CreateRecipeCategoryMixin {
    
    /**
     * Copied from the {@link com.simibubi.create.compat.jei.category.CreateRecipeCategory Create source code} because I can't be bothered to deal with injection.
     * @param mbAmount
     */
    @Overwrite
    public static IRecipeSlotTooltipCallback addFluidTooltip(int mbAmount) {
        return (view, tooltip) -> {
            Optional<FluidStack> displayed = view.getDisplayedIngredient(ForgeTypes.FLUID_STACK);
			if (displayed.isEmpty()) return;

			FluidStack fluidStack = displayed.get();
            Fluid fluid = fluidStack.getFluid();

			if (fluid.isSame(AllFluids.POTION.get())) {
				Component name = fluidStack.getDisplayName();
				if (tooltip.isEmpty()) {
					tooltip.add(0, name);
                } else {
					tooltip.set(0, name);
                };

				ArrayList<Component> potionTooltip = new ArrayList<>();
				PotionFluidHandler.addPotionTooltip(fluidStack, potionTooltip, 1);
				tooltip.addAll(1, potionTooltip.stream().toList());

			} else if (fluid.isSame(DestroyFluids.MIXTURE.get())) {
                Component name = DestroyLang.translate("mixture.mixture").component();
                boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

                CompoundTag fluidTag = fluidStack.getOrCreateTag();
                List<Component> mixtureTooltip = new ArrayList<>();

                if (view.getRole() == RecipeIngredientRole.INPUT || view.getRole() == RecipeIngredientRole.CATALYST) {
                    name = Component.literal(fluidTag.getString("DisplayName"));
                    mixtureTooltip = DestroyLang.mixtureIngredientTooltip(fluidTag);
                } else if (view.getRole() == RecipeIngredientRole.OUTPUT) {
                    CompoundTag mixtureTag = fluidTag.getCompound("Mixture");
                    if (!mixtureTag.isEmpty()) {
                        ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(mixtureTag);
                        name = mixture.getName();
                        mixtureTooltip = mixture.getContentsTooltip(iupac);
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

			int amount = mbAmount == -1 ? fluidStack.getAmount() : mbAmount;
			Component text = Components.literal(String.valueOf(amount)).append(Lang.translateDirect("generic.unit.millibuckets")).withStyle(ChatFormatting.GOLD);
			if (tooltip.isEmpty())
				tooltip.add(0, text);
			else {
				List<Component> siblings = tooltip.get(0).getSiblings();
				siblings.add(Components.literal(" "));
				siblings.add(text);
			};
        };
    };
};
