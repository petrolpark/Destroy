package com.petrolpark.destroy.client.ponder.instruction;

import java.util.HashSet;
import java.util.Set;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.foundation.ponder.instruction.TickingInstruction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HighlightTagInstruction extends TickingInstruction {

    @OnlyIn(Dist.CLIENT)
    public static final Set<PonderTag> highlightedTags = new HashSet<>();

    public final PonderTag tag;

    public HighlightTagInstruction(PonderTag tag, int duration) {
        super(false, duration);
        this.tag = tag;
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (isComplete()) {
            highlightedTags.remove(tag);
        } else {
            highlightedTags.add(tag);
        };
    };
    
};
