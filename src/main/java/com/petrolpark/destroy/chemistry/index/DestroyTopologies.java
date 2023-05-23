package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Formula.Topology;

import net.minecraft.world.phys.Vec3;

public class DestroyTopologies {

    public static final Topology CUBANE = create(Element.CARBON) // 0
        .atom(Element.CARBON, new Vec3(1d, 0d, 0d)) // 1
            .withSideBranch(new Vec3(1d, -1d, -1d).normalize(), new Vec3(1d, -1d, -1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 2
            .withSideBranch(new Vec3(-1d, 1d, -1d).normalize(), new Vec3(-1d, 1d, -1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(0d, 0d, 1d)) // 3
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(1d, 1d, 0d)) // 4
            .withBondTo(1, BondType.SINGLE)
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(1d, 0d, 1d)) // 5
            .withBondTo(1, BondType.SINGLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(0d, 1d, 1d)) // 6
            .withBondTo(2, BondType.SINGLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(1d, 1d, 1d)) // 7
            .withBondTo(4, BondType.SINGLE)
            .withBondTo(5, BondType.SINGLE)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .build("cubane");

    private static Topology.Builder create(Element startingElement) {
        return new Topology.Builder(Destroy.MOD_ID).startWith(startingElement);
    };

    public static void register() {};
};
