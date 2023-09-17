package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.item.MoleculeDisplayItem;
import com.petrolpark.destroy.item.TestTubeItem;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class MoleculeJEIIngredient {

    private static final ItemStack illegalFish;
    static {
        illegalFish = new ItemStack(Items.COD);
        illegalFish.setHoverName(Component.literal("Impossible Fish"));
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
            if (ingredient.isNovel()) return Destroy.asResource("novel_molecule");
            return new ResourceLocation(ingredient.getFullID());
        };

        @Override
        public String getDisplayModId(Molecule ingredient) {
            if (ingredient.isNovel()) return "Destroy";
            return IIngredientHelper.super.getDisplayModId(ingredient);
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
            if (ingredient.isHypothetical() || ingredient == DestroyMolecules.PROTON) return illegalFish;
            return TestTubeItem.of(MixtureFluid.of(TestTubeItem.CAPACITY, Mixture.pure(ingredient), ""));
        };
    };

    public static final IIngredientRenderer<Molecule> RENDERER = new IIngredientRenderer<Molecule>() {
        private static boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

        @Override
        public void render(GuiGraphics graphics, Molecule ingredient) {
            graphics.renderItem(MoleculeDisplayItem.with(ingredient), 0, 0); // TODO check positioning
        };

        @Override
        public List<Component> getTooltip(Molecule ingredient, TooltipFlag tooltipFlag) {
            List<Component> tooltips = new ArrayList<>();
            tooltips.add(ingredient.getName(iupac));
            tooltips.addAll(MoleculeDisplayItem.getLore(ingredient));
            return tooltips;
        };

    };
};
