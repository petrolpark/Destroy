package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * Molecule Tags do not affect the behaviour of {@link Molecule Molecules} in {@link Mixture Mixtures}, but instruct other parts of the game on how to deal with them.
 */
public class MoleculeTag {

    private final String nameSpace;
    private final String id;

    private int color;

    public static final Map<String, MoleculeTag> MOLECULE_TAGS = new HashMap<>();

    public MoleculeTag(String nameSpace, String id) { //TODO replace with proper registry
        this.nameSpace = nameSpace;
        this.id = id;
        color = 0xFFFFFF;
        MOLECULE_TAGS.put(nameSpace + ":" + id, this);
    };

    public MoleculeTag withColor(int color) {
        this.color = color;
        return this;
    };

    public Component getFormattedName() {
        return Component.translatable(nameSpace + ".molecule_tag." + id).withStyle(Style.EMPTY.withColor(color));
    };  
};
