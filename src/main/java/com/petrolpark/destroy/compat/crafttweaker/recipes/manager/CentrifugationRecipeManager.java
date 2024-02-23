package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/CentrifugationManager")
@ZenCodeType.Name("mods.destroy.CentrifugationManager")
public class CentrifugationRecipeManager implements IDestroyRecipeManager<CentrifugationRecipe> {

    /**
     * Adds recipe to the centrifuge
     * @param name Name of the recipe
     * @param input The input fluid
     * @param output 2 output fluids per 1 mB of input
     * @param processingTime Processing time of this recipe in ticks
     *
     * @docParam name "split_lava"
     * @docParam input <fluid:minecraft:lava>
     * @docParam output [<fluid:minecraft:water>, <fluid:create:builders_tea> * 200]
     */
    @ZenCodeType.Method
    public void addRecipe(String name, CTFluidIngredient input, IFluidStack[] output, @ZenCodeType.OptionalInt(1200) int processingTime) {
        name = fixRecipeName(name);

        checkMixture(input);
        if(output.length != 2) {
            throw new IllegalArgumentException("Centrifugation recipe should have exactly 2 outputs");
        }
        ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
        ProcessingRecipeBuilder<CentrifugationRecipe> builder = new ProcessingRecipeBuilder<>(getSerializer().getFactory(), resourceLocation);
        builder.withFluidIngredients(CTDestroy.mapFluidIngredients(input));
        for(IFluidStack fraction : output) {
            CTDestroy.output(builder, fraction);
        }
        builder.duration(processingTime);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, builder.build()));
    }

    @Override
    public DestroyRecipeTypes getDestroyRecipeType() {
        return DestroyRecipeTypes.CENTRIFUGATION;
    }
}
