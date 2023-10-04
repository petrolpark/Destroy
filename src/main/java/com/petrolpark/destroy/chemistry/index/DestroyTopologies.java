package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Formula.Topology;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DestroyTopologies {

    public static final Topology

    ANTHRACENE = anthracene(false)
        .build("anthracene"),
    
    ANTHRAQUINONE = anthracene(true)
        .build("anthraquinone"), // Not actually anthraquinone (the base topology does not contain the oxygens)

    BENZENE = create(Element.CARBON) // 0
        .sideChain(new Vec3(-cos(30), -sin(30), 0d), new Vec3(-cos(60), -sin(60), 0d))
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(-cos(30), sin(30), 0d).normalize(), new Vec3(-1d, 0d, 0d).normalize())
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(30), 1d + sin(30), 0d)) // 2
            .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(-cos(60), sin(60), 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2d * cos(30), 1d, 0d)) // 3
            .withSideBranch(new Vec3(cos(30), sin(30), 0d), new Vec3(cos(60), sin(60), 0d))
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2d * cos(30), 0d, 0d)) // 4
            .withSideBranch(new Vec3(cos(30), -sin(30), 0d), new Vec3(1d, 0d, 0d))
            .withBondTo(3, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(30), -sin(30), 0d)) // 5
            .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(cos(60), -sin(60), 0d))
            .withBondTo(4, BondType.AROMATIC)
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 2, 3, 4, 5, 0}, new int[]{2, 3, 4, 5, 0, 1}, new int[]{3, 4, 5, 0, 1, 2}, new int[]{4, 5, 0, 1, 2, 3}, new int[]{5, 0, 1, 2, 3, 4}, // Rotations
            new int[]{5, 4, 3, 2, 1, 0}, new int[]{4, 3, 2, 1, 0, 5}, new int[]{3, 2, 1, 0, 5, 4}, new int[]{2, 1, 0, 5, 4, 3}, new int[]{1, 0, 5, 4, 3, 2}, new int[]{0, 5, 4, 3, 2, 1} // Mirrors
        }).build("benzene"),

    CUBANE = create(Element.CARBON) // 0
        .sideChain(new Vec3(-1d, -1d, -1d).normalize(), new Vec3(-1d, -1d, -1d).normalize())
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
        //TODO reflections
        .build("cubane"),

    CYCLOPENTADIENE = create(Element.CARBON) // 0
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(18), 1 + sin(18), 0d)) // 2
            .withBondTo(1, BondType.DOUBLE)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(18) + sin(36), 0.5d, 0d)) // 3
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(18), -sin(18), 0d)) // 4
            .withBondTo(3, BondType.SINGLE)
            .withBondTo(0, BondType.DOUBLE)
            .attach()
        // .reflections(new int[][]{
        //     new int[]{0, 4, 3, 2, 1}
        // })
        .build("cyclopentadiene"),

    CYCLOPENTADIENIDE = create(Element.CARBON) // 0
        .sideChain(new Vec3(cos(144), -sin(144), 0d), new Vec3(-cos(72), sin(72), 0d))
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(cos(144), sin(144), 0d), new Vec3(-1d, 0d, 0d))
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(18), 1 + sin(18), 0d)) // 2
            .withSideBranch(new Vec3(cos(72), sin(72), 0d), new Vec3(cos(108), sin(108), 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(18) + cos(36), 0.5d, 0d)) // 3
            .withSideBranch(new Vec3(1d, 0d, 0d), new Vec3(cos(36), sin(36), 0d))
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(18), -sin(18), 0d)) // 4
            .withSideBranch(new Vec3(cos(72), -sin(72), 0d), new Vec3(cos(36), -sin(36), 0d))
            .withBondTo(0, BondType.AROMATIC)
            .withBondTo(3, BondType.AROMATIC)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 2, 3, 4, 0}, new int[]{2, 3, 4, 0, 1}, new int[]{3, 4, 0, 1, 2}, new int[]{4, 0, 1, 2, 3}, // Rotations
            new int[]{4, 3, 2, 1, 0}, new int[]{3, 2, 1, 0, 4}, new int[]{2, 1, 0, 4, 3}, new int[]{1, 0, 4, 3, 2}, new int[]{0, 4, 3, 2, 1} // Mirrors
        }).build("cyclopentadienide"),

    ISOHYDROBENZOFURAN = create(Element.CARBON) // 0
        .sideChain(new Vec3(-cos(30), -sin(30), 0d), new Vec3(-cos(60), -sin(60), 0d))
        .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(-cos(30), sin(30), 0d), new Vec3(-cos(30), sin(30), 0d))
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(30), 1d + sin(30), 0d)) // 2
            .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * cos(30), 1d, 0d)) // 3
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * cos(30) + cos(18), 1d + sin(18), 0d)) // 4
            .withSideBranch(new Vec3(cos(72), sin(72), 0d), new Vec3(cos(72), sin(72), 0d), BondType.DOUBLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.OXYGEN, new Vec3(2 * cos(30) + cos(18) + sin(36), 0.5d, 0d)) // 5
            .withBondTo(4, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * cos(30) + cos(18), -sin(18), 0d)) // 6
            .withSideBranch(new Vec3(cos(72), -sin(72), 0d), new Vec3(cos(72), -sin(72), 0d), BondType.DOUBLE)
            .withBondTo(5, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(2 * cos(30), 0d, 0d)) // 7
            .withBondTo(3, BondType.AROMATIC)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .atom(Element.CARBON, new Vec3(cos(30), -sin(30), 0d)) // 8
            .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0d))
            .withBondTo(0, BondType.AROMATIC)
            .withBondTo(7, BondType.AROMATIC)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 0, 5, 4, 3, 2}
        }).build("isohydrobenzofuran"),

    OCTASULFUR = create(Element.SULFUR) // 0
        .atom(Element.SULFUR, new Vec3(-0.25d, 0.75d, 0.6124d)) // 1
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(Element.SULFUR, new Vec3(0d, 1.5d, 0d)) // 2
            .withBondTo(1, BondType.SINGLE)
            .attach()
        .atom(Element.SULFUR, new Vec3(0.75d, 1.25d, 0.6124d)) // 3
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(Element.SULFUR, new Vec3(1.5d, 1.5d, 0d)) // 4
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(Element.SULFUR, new Vec3(1.25d, 0.75d, 0.6124d)) // 5
            .withBondTo(4, BondType.SINGLE)
            .attach()
        .atom(Element.SULFUR, new Vec3(1.5d, 0d, 0d)) // 6
            .withBondTo(5, BondType.SINGLE)
            .attach()
        .atom(Element.SULFUR, new Vec3(0.75d, -0.25d, 0.6124d)) // 7
            .withBondTo(0, BondType.SINGLE)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .build("octasulfur");

    public static Topology.Builder anthracene(boolean quinone) {
        return create(Element.CARBON) // 0
            .sideChain(new Vec3(-cos(30), -sin(30), 0d), new Vec3(-cos(30), -sin(30), -0.5d))
            .atom(Element.CARBON, new Vec3(0d, 1d, 0d)) // 1
                .withSideBranch(new Vec3(-cos(30), sin(30), 0d), new Vec3(-cos(30), sin(30), 0.5d).normalize())
                .withBondTo(0, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(cos(30), 1d + sin(30), 0d)) // 2
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, -0.5d).normalize())
                .withBondTo(1, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(2 * cos(30), 1d, 0d)) // 3
                .withBondTo(2, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(3 * cos(30), 1 + sin(30), 0d)) // 4
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, 0.5d).normalize(), quinone ? BondType.DOUBLE : BondType.SINGLE)
                .withBondTo(3, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(4 * cos(30), 1d, 0d)) // 5
                .withBondTo(4, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(5 * cos(30), 1d + sin(30), 0d)) // 6
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, -0.5d).normalize())
                .withBondTo(5, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(6 * cos(30), 1d, 0d)) // 7
                .withSideBranch(new Vec3(cos(30), sin(30), 0d), new Vec3(cos(30), sin(30), 0.5d).normalize())
                .withBondTo(6, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(6 * cos(30), 0d, 0d)) // 8
                .withSideBranch(new Vec3(cos(30), -sin(30), 0d), new Vec3(cos(30), -sin(30), -0.5d).normalize())
                .withBondTo(7, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(5 * cos(30), -sin(30), 0d)) // 9
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0.5d).normalize())
                .withBondTo(8, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(4 * cos(30), 0d, 0d)) // 10
                .withBondTo(5, BondType.AROMATIC)
                .withBondTo(9, BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(3 * cos(30), -sin(30), 0d)) // 11
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, -0.5d).normalize(), quinone ? BondType.DOUBLE : BondType.SINGLE)
                .withBondTo(10, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(2 * cos(30), 0d, 0d)) // 12
                .withBondTo(3, BondType.AROMATIC)
                .withBondTo(11, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(Element.CARBON, new Vec3(cos(30), -sin(30), 0d)) // 13
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0.5d).normalize())
                .withBondTo(0, BondType.AROMATIC)
                .withBondTo(12, BondType.AROMATIC)
                .attach()
            .reflections(new int[][]{
                new int[]{1, 0, 9, 8, 7, 6, 5, 4, 3, 2}, new int[]{6, 5, 4, 3, 2, 1, 0, 9, 8, 7}, new int[]{5, 6, 7, 8, 9, 0, 1, 2, 3, 4}
            });
    };

    private static double cos(float value) {
        return Mth.cos(value * Mth.PI / 180f);
    };

    private static double sin(float value) {
        return Mth.sin(value * Mth.PI / 180f);
    };

    private static Topology.Builder create(Element startingElement) {
        return new Topology.Builder(Destroy.MOD_ID).startWith(startingElement);
    };

    public static void register() {};
};
