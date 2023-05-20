package com.petrolpark.destroy.compat.jei.category;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.world.item.crafting.Recipe;

public abstract class DestroyRecipeCategory<T extends Recipe<?>> extends CreateRecipeCategory<T> {

    protected final IJeiHelpers helpers;

    public DestroyRecipeCategory(Info<T> info, IJeiHelpers helpers) {
        super(info);
        this.helpers = helpers;
    };

    public interface Factory<T extends Recipe<?>> {
		CreateRecipeCategory<T> create(Info<T> info, IJeiHelpers helpers);
	};
    
};
