package com.petrolpark.destroy.chemistry;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Level;

public abstract class ReactionResult {

    protected final Reaction reaction;
    protected final float moles;

    /**
     * @param moles How many moles of {@link Reaction} must take place before this Reaction Result occurs
     * @param reaction The Reaction which results in this
     */
    public ReactionResult(float moles, Reaction reaction) {
        this.moles = moles;
        this.reaction = reaction;
    };

    /**
     * Get the number of moles of Reaction which have to take place before this Reaction Result occurs.
     */
    public float getRequiredMoles() {
        return moles;
    };

    public Reaction getReaction() {
        return reaction;
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
    public abstract void onVatReaction(Level level, VatControllerBlockEntity vatController, Mixture mixture);
};
