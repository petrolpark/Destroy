package com.petrolpark.destroy.chemistry.legacy;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

/**
 * A directional covalent Bond between two {@link LegacyAtom Atoms}.
 * Within a {@link LegacyMolecularStructure structure}, each 'bonded' Atom has its own Bond object associated with it.
 * These are not the same object, as each Bond is in an {@link LegacyBond#getMirror opposite direction}.
 */
public class LegacyBond {

    /**
     * Whether this Bond is {@link BondType single, double, triple, etc}.
     */
    private volatile BondType type;

    /**
     * The {@link LegacyAtom} from which this Bond is.
     */
    private LegacyAtom srcAtom;

    /**
     * The {@link LegacyAtom} to which this Bond is.
     */
    private LegacyAtom destAtom;

    /**
     * @see LegacyBond
     * @param sourceAtom The {@link LegacyAtom} from which this Bond is.
     * @param destinationAtom The {@link LegacyAtom} to which this Bond is.
     * @param type Whether this Bond is {@link BondType single, double, triple, etc}.
     */
    public LegacyBond(LegacyAtom sourceAtom, LegacyAtom destinationAtom, BondType type) {
        this.srcAtom = sourceAtom;
        this.destAtom = destinationAtom;
        this.type = type;
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LegacyBond otherBond)) return false;
        return (type == otherBond.type && srcAtom == otherBond.srcAtom && destAtom == otherBond.destAtom);
    };

    /**
     * The {@link LegacyAtom} from which this Bond is.
     */
    public LegacyAtom getSourceAtom() {
        return this.srcAtom;
    };

    /**
     * The {@link LegacyAtom} to which this Bond is.
     */
    public LegacyAtom getDestinationAtom() {
        return this.destAtom;
    };

    /**
     * Whether this Bond is {@link BondType Single, Double, Triple, etc}.
     */
    public BondType getType() {
        return this.type;
    };

    /**
     * Set the {@link BondType type} of this Bond.
     */
    public void setType(BondType type) {
        this.type = type;
    };

    /**
     * As Bonds are directed, this returns a Bond in the other direction. The Bond will be of the same {@link BondType type}.
     * <p>This method instantiates a new Bond. Therefore, if the Bond is already part of a {@link LegacyMolecularStructure structure}, this will not return the pre-existing mirror Bond.
     * Strictly this pre-existing Bond should never be required, except when {@link LegacyMolecularStructure#remove removing} an Atom from a structure. </p>
     * <p>For example, if this bond represents the Bond from {@link LegacyAtom} A to Atom B, this returns a Bond from Atom B to Atom A.</p>
     */
    public LegacyBond getMirror() {
        return new LegacyBond(destAtom, srcAtom, type);
    };

    /**
     * A 'type' of {@link LegacyBond} - be that single, double, triple, etc.
     * Different types of Bond are rendered differently, and the {@link LegacyBond.BondType#getEquivalent single-bond-equivalent}
     * of the Bonds to an {@link LegacyAtom} are used to determine the valency when {@link LegacyMolecularStructure#addAllHydrogens automatically adding Hydrogens} to a generated {@link LegacyMolecularStructure structure}.
     */
    public enum BondType {
        /**
         * A 2-center-2-electron covalent Bond.
         */
        SINGLE(1f, ""),
        /**
         * A 2-center-4-electron covalent Bond.
         */
        DOUBLE(2f, "="),
        /**
         * A 2-center-6-electron covalent Bond.
         */
        TRIPLE(3f, "#"),
        /**
         * A Bond best represented in a Lewis structure as a 2-center-3-electron covalent Bond.
         */
        AROMATIC(1.5f, "~")
        ;
    
        private float singleBondEquivalent;
        private String FROWNSCode;

        private PartialModel partial;
    
        private BondType(float singleBondEquivalent, String FROWNSCode) {
            this.singleBondEquivalent = singleBondEquivalent;
            this.FROWNSCode = FROWNSCode;
        };
    
        /**
         * The number of {@link BondType#SINGLE single} {@link LegacyBond Bonds} to which this type of Bond is equivalent - essentially this the Bond Order.
         */
        public float getEquivalent() {
            return this.singleBondEquivalent;
        };

        /**
         * The character used to represent this type of {@link LegacyBond} in <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a>, if applicable.
         * For a {@link BondType#SINGLE single Bond}, this will be an empty String.
         */
        public String getFROWNSCode() {
            return this.FROWNSCode;
        };

        /**
         * Get a type of {@link LegacyBond} from a character in a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
         * @param c The symbol representing a type of Bond
         * @return If an unrecognised character is supplied, defaults to a {@link BondType#SINGLE single Bond}.
         */
        public static BondType fromFROWNSCode(char c) {
            BondType bondType = SINGLE;
            switch (c) {
                case '=':
                    bondType = DOUBLE;
                    break;
                case '#':
                    bondType = TRIPLE;
                    break;
                case '~':
                    bondType = AROMATIC;
                    break;
            };
            return bondType;
        };

        public PartialModel getPartial() {
            return partial;
        };
    
        public void setPartial(PartialModel partial) {
            this.partial = partial;
        };
    };
};
