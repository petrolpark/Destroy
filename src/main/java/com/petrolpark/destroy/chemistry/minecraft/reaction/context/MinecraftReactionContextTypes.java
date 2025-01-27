package com.petrolpark.destroy.chemistry.minecraft.reaction.context;

import com.petrolpark.destroy.chemistry.api.transformation.context.DoubleReactionContext;
import com.petrolpark.destroy.chemistry.api.transformation.context.IReactionContextType;

public class MinecraftReactionContextTypes {
    
    /**
     * A rudementary approximation of the effect on UV on {@link com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction Reactions} which approximates it as a single intensity of ambiguous wavelength light being supplied.
     * @since Destroy 0.1.0
     * @author petrolpark
     */
    public static final DoubleReactionContext.Type SIMPLE_UV_POWER = new DoubleReactionContext.Type(UVPowerReactionContext::new, 0d);

    /**
     * @see MinecraftReactionContextTypes#SIMPLE_UV_POWER
     */
    protected static class UVPowerReactionContext extends DoubleReactionContext {

        protected UVPowerReactionContext(double value) {
            super(value);
        };

        @Override
        public IReactionContextType<DoubleReactionContext> getReactionContextType() {
            return SIMPLE_UV_POWER;
        };

    };
};
