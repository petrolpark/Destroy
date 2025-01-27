package com.petrolpark.destroy.chemistry.api.property;

/**
 * An object of which instances have an associated relative atomic mass (which can be 0).
 * @since Destroy 0.1.0
 * @author petrolpark
 */
@FunctionalInterface
public interface IRelativeAtomicMass {
    
    /**
     * Get the relative atomic mass of whatever this is, in grams per mole.
     * This can be {@code 0d} (but should not be negative), so the proper precautions should be taken for that.
     * @return A mass in grams per mole, greater than or equal to {@link 0d}.
     */
    public double getMass();
};
