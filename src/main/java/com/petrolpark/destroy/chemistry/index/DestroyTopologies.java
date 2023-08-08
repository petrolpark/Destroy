package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Formula.Topology;

import net.minecraft.world.phys.Vec3;

public class DestroyTopologies {

    public static final Topology

    ANTHRACENE = anthracene(false)
        .build("anthracene"),
    
    ANTHRAQUINONE = anthracene(true)
        .build("anthraquinone"), // Not actually anthraquinone (the base topology does not contain the oxygens)

    BENZENE = create(Element.CARBON) // 0
        //TODO Side branch on original Atom, (-cos(30), -sin(30)) (-cos(60), -sin(60))
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(-Mth.cos(30), Mth.sin(30), 0d).normalize(), new Vec3(-1d, 0d, 0d).normalize())
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(30), 1d + Mth.sin(30), 0d)) // 2
            .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(-Mth.cos(60), Mth.sin(60), 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2d * Mth.cos(30), 1d, 0d)) // 3
            .withSideBranch(new Vec3(Mth.cos(30), Mth.sin(30), 0d), new Vec3(Mth.cos(60), Mth.sin(60), 0d))
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2d * Mth.cos(30), 0d, 0d)) // 4
            .withSideBranch(new Vec3(Mth.cos(30), -Mth.sin(30), 0d), new Vec3(1d, 0d, 0d))
            .withBondTo(3, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(30), -Mth.sin(30), 0d)) // 5
            .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(Mth.cos(60), -Mth.sin(60), 0d))
            .withBondTo(4, BondType.AROMATIC)
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .build("benzene"),

    CUBANE = create(Element.CARBON) // 0
        .atom(Element.CARBON, new Vec3(1d, 0d, 0d)) // 1
            .withSideBranch(new Vec3(1d, -1d, -1d).normalize(), new Vec3(1d, -1d, -1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 2
            .withSideBranch(new Vec3(-1d, 1d, -1d).normalize(), new Vec3(-1d, 1d, -1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(0d, 0d, 1d)) // 3
            .withSideBranch(new Vec3(-1d, -1d, 1d).normalize(), new Vec3(-1d, -1d, 1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(1d, 1d, 0d)) // 4
            .withSideBranch(new Vec3(1d, 1d, -1d).normalize(), new Vec3(1d, 1d, -1d).normalize())
            .withBondTo(1, BondType.SINGLE)
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(1d, 0d, 1d)) // 5
            .withSideBranch(new Vec3(1d, -1d, 1d).normalize(), new Vec3(1d, -1d, 1d).normalize())
            .withBondTo(1, BondType.SINGLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(0d, 1d, 1d)) // 6
            .withSideBranch(new Vec3(-1d, 1d, 1d).normalize(), new Vec3(-1d, 1d, 1d).normalize())
            .withBondTo(2, BondType.SINGLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(1d, 1d, 1d)) // 7
            .withSideBranch(new Vec3(1d, 1d, 1d).normalize(), new Vec3(1d, 1d, 1d).normalize())
            .withBondTo(4, BondType.SINGLE)
            .withBondTo(5, BondType.SINGLE)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .build("cubane"),

    CYCLOPENTADIENE = create(Element.CARBON) // 0
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(18), 1 + Mth.sin(18), 0d)) // 2
            .withBondTo(1, BondType.DOUBLE)
            .attach()
        .atom(Element.CARBON, new Vec(Mth.cos(18) + Mth.sin(36), 0.5d, 0d)) // 3
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(18), -Mth.sin(18), 0d)) // 4
            .withBondTo(3, BondType.SINGLE)
            .withBondTo(0, BondType.DOUBLE)
            .attach()
        .build("cyclopentadiene"),

    CYCLOPENTADIENIDE = create(Element.CARBON) // 0
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(Mth.cos(144), Mth.sin(144), 0d), new Vec3(-1d, 0d, 0d))
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(18), 1 + Mth.sin(18), 0d)) // 2
            .withSideBranch(new Vec3(Mth.cos(72), Mth.sin(72), 0d), new Vec3(Mth.cos(108), Mth.sin(108), 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(18) + Mth.cos(36), 0.5d, 0d)) // 3
            .withSideBranch(new Vec3(1d, 0d, 0d), new Vec3(Mth.cos(36), Mth.sin(36), 0d))
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(18), 1 + Mth.sin(18), 0d)) // 5
            .withSideBranch(new Vec3(Mth.cos(72), -Mth.sin(72), 0d), new Vec3(Mth.cos(36), -Mth.sin(36), 0d))
            .withBondTo(0, BondType.AROMATIC)
            .withBondTo(5, BondType.AROMATIC)
            .attach()
        .build("cyclopentadienide"),

    ISOHYDROBENZOFURAN = create(Element.CARBON) // 0
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(-Mth.cos(30), Mth.sin(30), 0d), new Vec3(-Mth.cos(30), Mth.sin(30), 0d))
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(30), 1d + Mth.sin(30), 0d)) // 2
            .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * Mth.cos(30), 0d, 0d)) // 3
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * Mth.cos(30) + Mth.cos(18), 1d + sin(18), 0d)) // 4
            .withSideBranch(new Vec3(Mth.cos(72), Mth.sin(72), 0d), new Vec3(Mth.cos(72), Mth.sin(72), 0d))
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.OXYGEN, new Vec3(2 * Mth.cos(30) + Mth.cos(18) + Mth.sin(36), 0.5d, 0d)) // 5
            .withSideBranch(new Vec3(Mth.cos(72), -Mth.sin(72), 0d), new Vec3(Mth.cos(72), -Mth.sin(72), 0d))
            .withBondTo(4, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * Mth.cos(30) + Mth.cos(18), -Mth.sin(18))) // 6
            .withBondTo(5, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * Mth.cos(30), 0d, 0d)) // 7
            .withBondTo(3, BondType.AROMATIC)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(Mth.cos(30), -Mth.sin(30), 0d)) // 8
            .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0d))
            .withBondTo(0, BondType.AROMATIC)
            .withBondTo(7, BondType.AROMATIC)
            .attach()
        .build("isohydrobenzofuran");

    public Topology.Builder anthracene(boolean quinone) {
        //TODO Side branch on original Atom, (-cos(30), -sin(30), 0) (-cos(30), -sin(30), -0.5)
        return create(Element.CARBON) // 0
            .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
                .withSideBranch(new Vec3(-Mth.cos(30), Mth.sin(30), 0d), new Vec3(-Mth.cos(30), Mth.sin(30), 0.5d).normalize())
                .withBondTo(0, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(Mth.cos(30), 1d + Mth.sin(30), 0d)) // 2
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, -0.5d).normalize())
                .withBondTo(1, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(2 * Mth.cos(30), 1d, 0d)) // 3
                .withBondTo(2, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(3 * Mth.cos(30), 1 + Mth.sin(30), 0d)) // 4
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, 0.5d).normalize())
                .withBondTo(3, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(4 * Mth.cos(30), 1d, 0d)) // 5
                .withBondTo(4, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(5 * Mth.cos(30), 1d + Mth.sin(30), 0d)) // 6
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, -0.5d).normalize())
                .withBondTo(5, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(6 * Mth.cos(30), 1d, 0d)) // 7
                .withSideBranch(new Vec3(Mth.cos(30), Mth.sin(30), 0d), new Vec3(Mth.cos(30), Mth.sin(30), 0.5d).normalize())
                .withBondTo(6, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(6 * Mth.cos(30), 0d, 0d)) // 8
                .withSideBranch(new Vec3(Mth.cos(30), -Mth.sin(30), 0d), new Vec3(Mth.cos(30), -Mth.sin(30), -0.5d).normalize())
                .withBondTo(7, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(5 * Mth.cos(30), -Mth.sin(30), 0d)) // 9
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0.5d).normalize())
                .withBondTo(8, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(4 * Mth.cos(30), 0d, 0d)) // 10
                .withBondTo(5, BondType.AROMATIC)
                .withBondTo(9, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(3 * Mth.cos(30), -Mth.sin(30), 0d)) // 11
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, -0.5d).normalize())
                .withBondTo(10, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(2 * Mth.cos(30), 0d, 0d)) // 12
                .withBondTo(3, BondType.AROMATIC)
                .withBondTo(11, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(Mth.cos(30), -Mth.sin(30), 0d)) // 13
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0.5d).normalize())
                .withBondTo(12, BondType.AROMATIC)
                .attach()
    };

    private static Topology.Builder create(Element startingElement) {
        return new Topology.Builder(Destroy.MOD_ID).startWith(startingElement);
    };

    public static void register() {};
};
