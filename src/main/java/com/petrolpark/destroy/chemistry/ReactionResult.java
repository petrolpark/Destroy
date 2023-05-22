package com.petrolpark.destroy.chemistry;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Level;

public abstract class ReactionResult {

    protected final float moles;

    /**
     * @param moles How many moles of Reaction must take place before this Reaction Result occurs
     */
    public ReactionResult(float moles) {
        this.moles = moles;
    };

    /**
     * Get the number of moles of Reaction which have to take place before this Reaction Result occurs.
     */
    public float getRequiredMoles() {
        return moles;
    };

    /**
     * Do something when the Reaction finishes in a Basin.
     * @param level The Level in which the Basin is
     * @param basin The Block Entity associated with the Basin
     * @param mixture The Mixture at the time when this Reaction Result occurs
     */
    public abstract void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture);

    /**
     * Do something when the Reaction finishes in a Vat.
     * @param level The Level in which the Vat is
     * @param mixture The Mixture at the time when this Reaction Result occurs
     */
    public abstract void onVatReaction(Level level, Mixture mixture); //TODO add vat class
};
