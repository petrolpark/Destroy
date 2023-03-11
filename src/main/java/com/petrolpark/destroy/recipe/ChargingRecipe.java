package com.petrolpark.destroy.recipe;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.compat.jei.category.AssemblyChargingSubCategory;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.contraptions.itemAssembly.IAssemblyRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ChargingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

    public ChargingRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.CHARGING, params);
    };

    @Override
    protected int getMaxInputCount() {
        return 1;
    };

    @Override
    protected int getMaxOutputCount() {
        return 1;
    };

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (inv.isEmpty()) return false;
		return ingredients.get(0).test(inv.getItem(0));
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return DestroyLang.translate("recipe.assembly.charging").component();
    };

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(DestroyBlocks.DYNAMO.get());
    };

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {};

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyChargingSubCategory::new;
    };
    
}
