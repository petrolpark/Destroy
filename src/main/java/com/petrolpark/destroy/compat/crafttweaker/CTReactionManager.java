package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.compat.crafttweaker.action.RemoveMoleculeAction;
import com.petrolpark.destroy.compat.crafttweaker.action.RemoveReactionAction;
import org.openzen.zencode.java.ZenCodeType;

/**
 * Use this class to manage Reactions of this mod. Use <reaction> bracket handler to query an existing reaction.
 * Example: <reaction:destroy:abs_polymerization>
 * <p>
 * Use {@link CTReactionManager#create(String)} to create a {@link com.petrolpark.destroy.compat.crafttweaker.natives.CTReactionBuilder}
 * if you want to create a new reaction
 * <p>
 * For reaction
 * @docParam this <reaction:destroy:abs_polymerization>
 */
@ZenRegister
@ZenCodeType.Name("mods.destroy.Reactions")
@Document("mods/destroy/Reactions")
public class CTReactionManager {
    @ZenCodeType.Method
    public static Reaction.ReactionBuilder create(String id) {
        return new Reaction.ReactionBuilder("crafttweaker")
            .id(id);
    }

    @ZenCodeType.Method
    public static Reaction getReactionById(String reactionId) {
        return Reaction.REACTIONS.get(reactionId);
    }

    @ZenCodeType.Method
    public static void removeReaction(Reaction reaction) {
        CraftTweakerAPI.apply(new RemoveReactionAction(reaction));
    }

    @ZenCodeType.Method
    @BracketResolver("reaction")
    public static Reaction getReaction(String tokens) {
        return getReactionById(tokens);
    }
}
