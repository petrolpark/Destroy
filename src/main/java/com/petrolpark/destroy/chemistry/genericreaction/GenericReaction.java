package com.petrolpark.destroy.chemistry.genericreaction;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.error.ChemistryException;

import net.minecraft.resources.ResourceLocation;

public abstract class GenericReaction {

    /**
     * The set of all Generic Reactions known to Destroy.
     */
    public static Set<GenericReaction> GENERIC_REACTIONS = new HashSet<>();

    /**
     * The identifier for this Generic Reaction, which JEI uses to find the title and description.
     */
    public final ResourceLocation id;

    /**
     * The example Reaction to be displayed in JEI.
     */
    private Reaction exampleReaction;

    public GenericReaction(ResourceLocation id) {
        this.id = id;
    };

    public abstract boolean involvesSingleGroup();

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

    protected static MoleculeBuilder moleculeBuilder() {
        return new MoleculeBuilder("novel");
    };

    protected static ReactionBuilder reactionBuilder() {
        return Reaction.generatedReactionBuilder();
    };

    /**
     * Instantiate a {@link com.petrolpark.destroy.chemistry.error.ChemistryException Chemistry Exception}. These kinds of exceptions are
     * ignored when a Mixture is generating generic Reactions.
     * @param string The message of the exception. The identifier of this Generic Reaction will be prepended if this error is logged.
     */
    protected GenericReactionGenerationException exception(String string) {
        return new GenericReactionGenerationException("Problem generating " + (involvesSingleGroup() ? "single" : "double") + "-Group Generic Reaction '" + id.toString() + "': " + string);
    };

    public class GenericReactionGenerationException extends ChemistryException {

        public GenericReactionGenerationException(String message) {
            super(message);
        };

    };

};
