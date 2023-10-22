package com.petrolpark.destroy.chemistry.error;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;

/**
 * Chemistry Exceptions pertain to Destroy's chemical system.
 * If a Chemistry Exception is thrown while a {@link com.petrolpark.destroy.chemistry.genericreaction.GenericReaction Generic Reaction}
 * is being generated, it is ignored.
 */
public abstract class ChemistryException extends RuntimeException {

    public ChemistryException(String message) {
        super(message);
    };

    public ChemistryException(String message, Throwable cause) {
        super(message, cause);
    };

    public static class MoleculeDeserializationException extends ChemistryException {

        public MoleculeDeserializationException(String message) {
            super(message);
        };
    };

    public static abstract class FormulaException extends ChemistryException {

        public final Formula formula;

        public FormulaException(Formula formula, String message) {
            super("Problem with Formula '" + formula.serialize() + "': " + message);
            this.formula = formula;
        };

        public static class FormulaModificationException extends FormulaException {

            public FormulaModificationException(Formula formula, String message) {
                super(formula, message);
            };

        };

        public static class FormulaRenderingException extends FormulaException {

            public FormulaRenderingException(Formula formula, String message) {
                super(formula, message);
            };

        };
    };

    public static class TopologyDefinitionException extends ChemistryException {

        public TopologyDefinitionException(String message) {
            super(message);
        };

    };

    public static class FormulaSerializationException extends ChemistryException {

        public FormulaSerializationException(String message) {
            super(message);
        };

    };

    public static class ExampleMoleculeMissingGroupException extends ChemistryException {

        public ExampleMoleculeMissingGroupException(Molecule exampleMolecule) {
            super("Example Molecule '"+exampleMolecule.getFullID()+"' does not contain the group it is meant to be exemplifying.");
        };

    };
};


