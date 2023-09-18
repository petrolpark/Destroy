package com.petrolpark.destroy.chemistry.genericreaction;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

import net.minecraft.resources.ResourceLocation;

public abstract class GenericReaction {

    /**
     * The set of all Generic Reactions known to Destroy.
     */
    public static Set<GenericReaction> GENERIC_REACTIONS = new HashSet<>();

    /**
     * The identifier for this Generic Reaction, which JEI uses to find the title and description.
     */
    protected final ResourceLocation id;

    /**
     * The example Reaction to be displayed in JEI.
     */
    private Reaction exampleReaction;

    public GenericReaction(ResourceLocation id) {
        this.id = id;
    };

    protected static MoleculeBuilder moleculeBuilder() {
        return new MoleculeBuilder("novel");
    };

    public abstract Boolean involvesSingleGroup();

    public Reaction getExampleReaction() {
        if (exampleReaction == null) exampleReaction = generateExampleReaction();
        return exampleReaction;
    };

    /**
     * Generate a Reaction to be used to exemplify this Reaction. There should not usually be any need to override this.
     * The {@link com.petrolpark.destroy.chemistry.GroupType groups} of the resultant {@link com.petrolpark.destroy.chemistry.Molecule#isNovel novel}
     * {@link com.petrolpark.destroy.chemistry.Molecule Molecules} are used to determine what functional groups this Generic Reaction produces.
     */
    @NotNull
    protected abstract Reaction generateExampleReaction();

};
