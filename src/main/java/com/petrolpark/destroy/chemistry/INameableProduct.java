package com.petrolpark.destroy.chemistry;

import net.minecraft.network.chat.Component;

public interface INameableProduct {
    
    /**
     * The display name of this Molecule or Salt.
     * @param iupac Whether the IUPAC systematic name should be used rather than the common name
     */
    public Component getName(boolean iupac); 
};
