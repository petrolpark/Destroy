package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Formula.Topology;

import net.minecraft.world.phys.Vec3;

public class DestroyTopologies {

    public static final Topology

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
        .build("cyclopentadiene");

    private static Topology.Builder create(Element startingElement) {
        return new Topology.Builder(Destroy.MOD_ID).startWith(startingElement);
    };

    public static void register() {};
};
