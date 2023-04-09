package com.petrolpark.destroy.chemistry;

/**
 * A directional covalent Bond between two {@link Atom Atoms}.
 * Within a {@link Formula structure}, each 'bonded' Atom has its own Bond object associated with it.
 * These are not the same object, as each Bond is in an {@link Bond#getMirror opposite direction}.
 */
public class Bond {

    /**
     * Whether this Bond is {@link BondType single, double, triple, etc}.
     */
    private BondType type;

    /**
     * The {@link Atom} from which this Bond is.
     */
    private Atom srcAtom;

    /**
     * The {@link Atom} to which this Bond is.
     */
    private Atom destAtom;

    /**
     * @see Bond
     * @param sourceAtom The {@link Atom} from which this Bond is.
     * @param destinationAtom The {@link Atom} to which this Bond is.
     * @param type Whether this Bond is {@link BondType single, double, triple, etc}.
     */
    public Bond(Atom sourceAtom, Atom destinationAtom, BondType type) {
        this.srcAtom = sourceAtom;
        this.destAtom = destinationAtom;
        this.type = type;
    };

    /**
     * The {@link Atom} from which this Bond is.
     */
    public Atom getSourceAtom() {
        return this.srcAtom;
    };

    /**
     * The {@link Atom} to which this Bond is.
     */
    public Atom getDestinationAtom() {
        return this.destAtom;
    };

    /**
     * Whether this Bond is {@link BondType Single, Double, Triple, etc}.
     */
    public BondType getType() {
        return this.type;
    };

    /**
     * As Bonds are directed, this returns a Bond in the other direction. The Bond will be of the same {@link BondType type}.
     * <p>This method instantiates a new Bond. Therefore, if the Bond is already part of a {@link Formula structure}, this will not return the pre-existing mirror Bond.
     * Strictly this pre-existing Bond should never be required, except when {@link Formula#remove removing} an Atom from a structure. </p>
     * <p>For example, if this bond represents the Bond from {@link Atom} A to Atom B, this returns a Bond from Atom B to Atom A.</p>
     */
    public Bond getMirror() {
        return new Bond(destAtom, srcAtom, type);
    };

    /**
     * A 'type' of {@link Bond} - be that single, double, triple, etc.
     * Different types of Bond are rendered differently, and the {@link Bond.BondType#getEquivalent single-bond-equivalent}
     * of the Bonds to an {@link Atom} are used to determine the valency when {@link Formula#addAllHydrogens automatically adding Hydrogens} to a generated {@link Formula structure}.
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
    
        private BondType(float singleBondEquivalent, String FROWNSCode) {
            this.singleBondEquivalent = singleBondEquivalent;
            this.FROWNSCode = FROWNSCode;
        };
    
        /**
         * The number of {@link BondType#SINGLE single} {@link Bond Bonds} to which this type of Bond is equivalent - essentially this the Bond Order.
         */
        public float getEquivalent() {
            return this.singleBondEquivalent;
        };

        /**
         * The character used to represent this type of {@link Bond} in <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a>, if applicable.
         * For a {@link BondType#SINGLE single Bond}, this will be an empty String.
         */
        public String getFROWNSCode() {
            return this.FROWNSCode;
        };

        /**
         * Get a type of {@link Bond} from a character in a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
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
    };
};
