package com.petrolpark.destroy.chemistry.api.species;

import com.petrolpark.destroy.chemistry.api.property.ICharge;
import com.petrolpark.destroy.chemistry.api.property.IRelativeAtomicMass;
import com.petrolpark.destroy.chemistry.api.transformation.reaction.IReaction;

/**
 * A distinguisher of two {@link ISpecies}.
 * For example, by default, Destroy does not consider stereochemistry so E-1,2-dichloroethene and Z-1,2-dichloroethene would be the same in most cases.
 * But this may not be wanted in every circumstance, which is what overriding the default Destroy comparator would do.
 * @since Destroy 0.1.0
 * @author petrolpark
 */
public interface ISpeciesComparator {
    
    /**
     * Whether two {@link ISpecies} are equivalent for the relevant purposes. For example, this may include:
     * <ul>
     * <li> Same {@link IRelativeAtomicMass}, {@link ICharge}, etc.
     * <li> Same possible {@link IReaction}s
     * <li> Same activation energy for a given {@link IReaction}
     * </ul>
     * <p>Obviously this should follow the rules for {@link Object#equals(Object) equivalence}. </p>
     * @param species1
     * @param species2
     * @return {@code true} if the two {@link ISpecies} are interchangeable
     */
    public boolean equal(ISpecies species1, ISpecies species2);
};
