package com.petrolpark.destroy.chemistry;

public class Bond {
    private BondType type;
    private Atom srcAtom;
    private Atom destAtom;

    public Bond(Atom sourceAtom, Atom destinationAtom, BondType type) {
        this.srcAtom = sourceAtom;
        this.destAtom = destinationAtom;
        this.type = type;
    };

    public Atom getSourceAtom() {
        return this.srcAtom;
    };

    public Atom getDestinationAtom() {
        return this.destAtom;
    };

    public BondType getType() {
        return this.type;
    };

    /**
     * As Bonds are directed, this returns the Bond in the other direction
     * (e.g. if this bond represents the Bond from A to B, the mirror is the Bond from B to A)
     * @return
     */
    public Bond getMirror() {
        return new Bond(destAtom, srcAtom, type);
    };

    public enum BondType {
        SINGLE(1f, ""),
        DOUBLE(2f, "="),
        TRIPLE(3f, "#"),
        AROMATIC(1.5f, "~")
        ;
    
        private float singleBondEquivalent;
        private String FROWNSCode;
    
        private BondType(float singleBondEquivalent, String FROWNSCode) {
            this.singleBondEquivalent = singleBondEquivalent;
            this.FROWNSCode = FROWNSCode;
        };
    
        public float getEquivalent() {
            return this.singleBondEquivalent;
        };

        public  String getFROWNSCode() {
            return this.FROWNSCode;
        };
    };
};
