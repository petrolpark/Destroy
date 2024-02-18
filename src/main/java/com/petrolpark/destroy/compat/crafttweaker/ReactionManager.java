package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.chemistry.Reaction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.destroy.Reactions")
@Document("mods/destroy/Reactions")
public class ReactionManager {
    @ZenCodeType.Method
    public static Reaction.ReactionBuilder create() {
        return new Reaction.ReactionBuilder("crafttweaker");
    }

    @ZenCodeType.Method
    public static Reaction getReactionById(String reactionId) {
        return Reaction.REACTIONS.get(reactionId);
    }
}
