package com.petrolpark.destroy.compat.jei;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.item.MoleculeDisplayItem;
import com.petrolpark.destroy.item.TestTubeItem;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.render.ItemStackRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class MoleculeJEIIngredient {

    private static final ItemStack illegalFish;
    static {
        illegalFish = new ItemStack(Items.COD);
        illegalFish.setHoverName(Component.literal("not allowed hypothetical molecules in solution"));
    };

    public static final IIngredientType<Molecule> TYPE = new IIngredientType<Molecule>() {

        @Override
        public Class<? extends Molecule> getIngredientClass() {
            return Molecule.class;
        };

    };

    public static final IIngredientHelper<Molecule> HELPER = new IIngredientHelper<Molecule>() {

        @Override
        public IIngredientType<Molecule> getIngredientType() {
            return TYPE;
        };

        @Override
        public String getDisplayName(Molecule ingredient) {
            return ingredient.getName(false).getString()+ingredient.getName(true).getString();
        };

        @Override
        public String getUniqueId(Molecule ingredient, UidContext context) {
            return ingredient.getFullID();
        };

        @Override
        public ResourceLocation getResourceLocation(Molecule ingredient) {
            return new ResourceLocation(ingredient.getFullID());
        };

        @Override
        public Molecule copyIngredient(Molecule ingredient) {
            return ingredient; // There should be no need to copy Molecules as they cannot be modified
        };

        @Override
        public String getErrorInfo(@Nullable Molecule ingredient) {
            return ingredient == null ? "Molecule ingredient is null" : "Something is wrong with: " + ingredient.getFullID();
        };

        @Override
        public ItemStack getCheatItemStack(Molecule ingredient) {
            if (ingredient.isHypothetical()) return illegalFish;
            Mixture mixture = new Mixture();
            mixture.addMolecule(ingredient, ingredient.getPureConcentration());
            return TestTubeItem.of(MixtureFluid.of(TestTubeItem.CAPACITY, mixture, ""));
        };
    };

    public static final IIngredientRenderer<Molecule> RENDERER = new IIngredientRenderer<Molecule>() {
        private static boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();
        private static final ItemStackRenderer itemStackRenderer = new ItemStackRenderer();

        @Override
        public void render(PoseStack poseStack, Molecule ingredient) {
            itemStackRenderer.render(poseStack, MoleculeDisplayItem.with(ingredient));
        };

        @Override
        public List<Component> getTooltip(Molecule ingredient, TooltipFlag tooltipFlag) {
            return List.of(ingredient.getName(iupac));
        };

    };
};
