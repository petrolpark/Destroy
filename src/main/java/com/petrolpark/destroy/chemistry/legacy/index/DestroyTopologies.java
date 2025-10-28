package com.petrolpark.destroy.chemistry.legacy.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure.Topology;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DestroyTopologies {

    public static final Topology

    ANTHRACENE = anthracene(false)
        .build("anthracene"),
    
    ANTHRAQUINONE = anthracene(true)
        .build("anthraquinone"), // Not actually anthraquinone (the base topology does not contain the oxygens)

    BENZENE = create(LegacyElement.CARBON) // 0
        .sideChain(new Vec3(-cos(30), -sin(30), 0d), new Vec3(-cos(60), -sin(60), 0d))
        .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(-cos(30), sin(30), 0d).normalize(), new Vec3(-1d, 0d, 0d).normalize())
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(30), 1d + sin(30), 0d)) // 2
            .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(-cos(60), sin(60), 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(2d * cos(30), 1d, 0d)) // 3
            .withSideBranch(new Vec3(cos(30), sin(30), 0d), new Vec3(cos(60), sin(60), 0d))
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(2d * cos(30), 0d, 0d)) // 4
            .withSideBranch(new Vec3(cos(30), -sin(30), 0d), new Vec3(1d, 0d, 0d))
            .withBondTo(3, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(30), -sin(30), 0d)) // 5
            .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(cos(60), -sin(60), 0d))
            .withBondTo(4, BondType.AROMATIC)
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 2, 3, 4, 5, 0}, new int[]{2, 3, 4, 5, 0, 1}, new int[]{3, 4, 5, 0, 1, 2}, new int[]{4, 5, 0, 1, 2, 3}, new int[]{5, 0, 1, 2, 3, 4}, // Rotations
            new int[]{5, 4, 3, 2, 1, 0}, new int[]{4, 3, 2, 1, 0, 5}, new int[]{3, 2, 1, 0, 5, 4}, new int[]{2, 1, 0, 5, 4, 3}, new int[]{1, 0, 5, 4, 3, 2}, new int[]{0, 5, 4, 3, 2, 1} // Mirrors
        }).build("benzene"),

    CUBANE = create(LegacyElement.CARBON) // 0
        .sideChain(new Vec3(-1d, -1d, -1d).normalize(), new Vec3(-1d, -1d, -1d).normalize())
        .atom(LegacyElement.CARBON, new Vec3(1d, 0d, 0d)) // 1
            .withSideBranch(new Vec3(1d, -1d, -1d).normalize(), new Vec3(1d, -1d, -1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 0d)) // 2
            .withSideBranch(new Vec3(-1d, 1d, -1d).normalize(), new Vec3(-1d, 1d, -1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0d, 0d, 1d)) // 3
            .withSideBranch(new Vec3(-1d, -1d, 1d).normalize(), new Vec3(-1d, -1d, 1d).normalize())
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(1d, 1d, 0d)) // 4
            .withSideBranch(new Vec3(1d, 1d, -1d).normalize(), new Vec3(1d, 1d, -1d).normalize())
            .withBondTo(1, BondType.SINGLE)
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(1d, 0d, 1d)) // 5
            .withSideBranch(new Vec3(1d, -1d, 1d).normalize(), new Vec3(1d, -1d, 1d).normalize())
            .withBondTo(1, BondType.SINGLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 1d)) // 6
            .withSideBranch(new Vec3(-1d, 1d, 1d).normalize(), new Vec3(-1d, 1d, 1d).normalize())
            .withBondTo(2, BondType.SINGLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(1d, 1d, 1d)) // 7
            .withSideBranch(new Vec3(1d, 1d, 1d).normalize(), new Vec3(1d, 1d, 1d).normalize())
            .withBondTo(4, BondType.SINGLE)
            .withBondTo(5, BondType.SINGLE)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        //TODO reflections
        .build("cubane"),

    CYCLOHEXENE = create(LegacyElement.CARBON) // 0
        .sideChain(new Vec3(-sin(30), -cos(30), 0), new Vec3(-sin(30), -cos(30), 0), BondType.SINGLE)
        .atom(LegacyElement.CARBON, new Vec3(-sin(30), cos(30), 0)) // 1
            .withBondTo(0, BondType.SINGLE)
            .withSideBranch(new Vec3(-0.75d, 0, 0.5d).normalize(), new Vec3(-0.75d, 0, 0.5d).normalize(), BondType.SINGLE)
            .withSideBranch(new Vec3(-0.75d, 0, -.5d).normalize(), new Vec3(-0.75d, 0, -0.5d).normalize(), BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0, 2 * cos(30) - 0.1d, -0.2d)) // 2
            .withBondTo(1, BondType.SINGLE)
            .withSideBranch(new Vec3(-cos(30), sin(30), 0.1).normalize(), new Vec3(-cos(30), sin(30), 0.1).normalize(), BondType.SINGLE)
            .withSideBranch(new Vec3(0.1d, 0, -1).normalize(), new Vec3(0.1d, 0, -1).normalize(), BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0.8d, 2 * cos(30) - 0.1d, 0.2d)) // 3
            .withBondTo(2, BondType.SINGLE)
            .withSideBranch(new Vec3(cos(30), sin(30), -0.1).normalize(), new Vec3(cos(30), sin(30), -0.1).normalize(), BondType.SINGLE)
            .withSideBranch(new Vec3(-0.1d, 0, 1).normalize(), new Vec3(-0.1d, 0, 1).normalize(), BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0.8d + sin(30), cos(30), 0)) // 4
            .withBondTo(3, BondType.SINGLE)
            .withSideBranch(new Vec3(0.75d, 0, 0.5d).normalize(), new Vec3(0.75d, 0, 0.5d).normalize(), BondType.SINGLE)
            .withSideBranch(new Vec3(0.75d, 0, -.5d).normalize(), new Vec3(0.75d, 0, -0.5d).normalize(), BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(0.8d, 0d, 0d)) // 5
            .withBondTo(4, BondType.SINGLE)
            .withBondTo(0, BondType.DOUBLE)
            .withSideBranch(new Vec3(sin(30), -cos(30), 0d), new Vec3(sin(30), -cos(30), 0d), BondType.SINGLE)
            .attach()
        //TODO symmetries
        .build("cyclohexene"),

    CYCLOPENTADIENE = create(LegacyElement.CARBON) // 0
        .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(18), 1 + sin(18), 0d)) // 2
            .withBondTo(1, BondType.DOUBLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(18) + sin(36), 0.5d, 0d)) // 3
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(18), -sin(18), 0d)) // 4
            .withBondTo(3, BondType.SINGLE)
            .withBondTo(0, BondType.DOUBLE)
            .attach()
        // .reflections(new int[][]{
        //     new int[]{0, 4, 3, 2, 1}
        // })
        .build("cyclopentadiene"),

    CYCLOPENTADIENIDE = create(LegacyElement.CARBON) // 0
        .sideChain(new Vec3(cos(144), -sin(144), 0d), new Vec3(-cos(72), sin(72), 0d))
        .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(cos(144), sin(144), 0d), new Vec3(-1d, 0d, 0d))
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(18), 1 + sin(18), 0d)) // 2
            .withSideBranch(new Vec3(cos(72), sin(72), 0d), new Vec3(cos(108), sin(108), 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(18) + cos(36), 0.5d, 0d)) // 3
            .withSideBranch(new Vec3(1d, 0d, 0d), new Vec3(cos(36), sin(36), 0d))
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(18), -sin(18), 0d)) // 4
            .withSideBranch(new Vec3(cos(72), -sin(72), 0d), new Vec3(cos(36), -sin(36), 0d))
            .withBondTo(0, BondType.AROMATIC)
            .withBondTo(3, BondType.AROMATIC)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 2, 3, 4, 0}, new int[]{2, 3, 4, 0, 1}, new int[]{3, 4, 0, 1, 2}, new int[]{4, 0, 1, 2, 3}, // Rotations
            new int[]{4, 3, 2, 1, 0}, new int[]{3, 2, 1, 0, 4}, new int[]{2, 1, 0, 4, 3}, new int[]{1, 0, 4, 3, 2}, new int[]{0, 4, 3, 2, 1} // Mirrors
        }).build("cyclopentadienide"),

    DIBORANE = create(LegacyElement.BORON) // 0
        .sideChain(new Vec3(-sin(30), 0, cos(30)), new Vec3(-sin(30), 0, cos(30)))
        .sideChain(new Vec3(-sin(30), 0, -cos(30)), new Vec3(-sin(30), 0, -cos(30)))
        .atom(LegacyElement.HYDROGEN, new Vec3(cos(45), sin(45), 0)) // 1
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.BORON, new Vec3(2 * cos(45), 0, 0)) // 2
            .withSideBranch(new Vec3(sin(30), 0, cos(30)), new Vec3(sin(30), 0, cos(30)))
            .withSideBranch(new Vec3(sin(30), 0, -cos(30)), new Vec3(sin(30), 0, -cos(30)))
            .withBondTo(1, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.HYDROGEN, new Vec3(cos(45), -sin(45), 0))
            .withBondTo(0, BondType.SINGLE)
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 0, 3, 2}, new int[]{2, 3, 0, 1}, new int[]{3, 2, 1, 0}
        }).build("diborane"),


    ISOHYDROBENZOFURAN = create(LegacyElement.CARBON) // 0
        .sideChain(new Vec3(-cos(30), -sin(30), 0d), new Vec3(-cos(60), -sin(60), 0d))
        .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 0d)) // 1
            .withSideBranch(new Vec3(-cos(30), sin(30), 0d), new Vec3(-cos(30), sin(30), 0d))
            .withBondTo(0, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(30), 1d + sin(30), 0d)) // 2
            .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, 0d))
            .withBondTo(1, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(2 * cos(30), 1d, 0d)) // 3
            .withBondTo(2, BondType.AROMATIC)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(2 * cos(30) + cos(18), 1d + sin(18), 0d)) // 4
            .withSideBranch(new Vec3(cos(72), sin(72), 0d), new Vec3(cos(72), sin(72), 0d), BondType.DOUBLE)
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.OXYGEN, new Vec3(2 * cos(30) + cos(18) + sin(36), 0.5d, 0d)) // 5
            .withBondTo(4, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(2 * cos(30) + cos(18), -sin(18), 0d)) // 6
            .withSideBranch(new Vec3(cos(72), -sin(72), 0d), new Vec3(cos(72), -sin(72), 0d), BondType.DOUBLE)
            .withBondTo(5, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(2 * cos(30), 0d, 0d)) // 7
            .withBondTo(3, BondType.AROMATIC)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.CARBON, new Vec3(cos(30), -sin(30), 0d)) // 8
            .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0d))
            .withBondTo(0, BondType.AROMATIC)
            .withBondTo(7, BondType.AROMATIC)
            .attach()
        .reflections(new int[][]{
            new int[]{1, 0, 5, 4, 3, 2}
        }).build("isohydrobenzofuran"),

    OCTASULFUR = create(LegacyElement.SULFUR) // 0
        .atom(LegacyElement.SULFUR, new Vec3(-0.25d, 0.75d, 0.6124d)) // 1
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.SULFUR, new Vec3(0d, 1.5d, 0d)) // 2
            .withBondTo(1, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.SULFUR, new Vec3(0.75d, 1.75d, 0.6124d)) // 3
            .withBondTo(2, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.SULFUR, new Vec3(1.5d, 1.5d, 0d)) // 4
            .withBondTo(3, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.SULFUR, new Vec3(1.75d, 0.75d, 0.6124d)) // 5
            .withBondTo(4, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.SULFUR, new Vec3(1.5d, 0d, 0d)) // 6
            .withBondTo(5, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.SULFUR, new Vec3(0.75d, -0.25d, 0.6124d)) // 7
            .withBondTo(0, BondType.SINGLE)
            .withBondTo(6, BondType.SINGLE)
            .attach()
        .build("octasulfur"),

    TETRABORATE = create(LegacyElement.BORON, -1) // 0
        .sideChain(new Vec3(0, -cos(45), sin(45)), new Vec3(1, -cos(45), sin(45)).normalize())
        .atom(LegacyElement.BORON, -1, new Vec3(0, 2 * cos(45), 0)) // 1
            .withSideBranch(new Vec3(0, cos(45), sin(45)), new Vec3(-1, cos(45), sin(45)).normalize())
            .attach()
        .atom(LegacyElement.OXYGEN, new Vec3(0, sin(45), cos(45))) // 2
            .withBondTo(0, BondType.SINGLE)
            .withBondTo(1, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.OXYGEN, new Vec3(cos(45), 0, -sin(45))) // 3
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.OXYGEN, new Vec3(cos(45),  2 * cos(45), -sin(45))) // 4
            .withBondTo(1, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.BORON, new Vec3(2 * cos(45), cos(45), -sin(45))) // 5
            .withSideBranch(new Vec3(1, 0, 0), new Vec3(1, 1, -1).normalize())
            .withBondTo(3, BondType.SINGLE)
            .withBondTo(4, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.OXYGEN, new Vec3(-cos(45), 0, -sin(45))) // 6
            .withBondTo(0, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.OXYGEN, new Vec3(-cos(45),  2 * cos(45), -sin(45))) // 7
            .withBondTo(1, BondType.SINGLE)
            .attach()
        .atom(LegacyElement.BORON, new Vec3(-2 * cos(45), cos(45), -sin(45))) // 8
            .withBondTo(6, BondType.SINGLE)
            .withBondTo(7, BondType.SINGLE)
            .withSideBranch(new Vec3(-1, 0, 0), new Vec3(-1, -1, -1).normalize())
            .attach()
        .reflections(new int[][]{
            new int[]{1, 0, 2, 3}, new int[]{0, 1, 3, 2}, new int[]{1, 0, 3, 2}
        }).build("tetraborate");

    public static Topology.Builder anthracene(boolean quinone) {
        return create(LegacyElement.CARBON) // 0
            .sideChain(new Vec3(-cos(30), -sin(30), 0d), new Vec3(-cos(30), -sin(30), -0.5d))
            .atom(LegacyElement.CARBON, new Vec3(0d, 1d, 0d)) // 1
                .withSideBranch(new Vec3(-cos(30), sin(30), 0d), new Vec3(-cos(30), sin(30), 0.5d).normalize())
                .withBondTo(0, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(cos(30), 1d + sin(30), 0d)) // 2
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, -0.5d).normalize())
                .withBondTo(1, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(2 * cos(30), 1d, 0d)) // 3
                .withBondTo(2, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(3 * cos(30), 1 + sin(30), 0d)) // 4
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, 0.5d).normalize(), quinone ? BondType.DOUBLE : BondType.SINGLE)
                .withBondTo(3, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(4 * cos(30), 1d, 0d)) // 5
                .withBondTo(4, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(5 * cos(30), 1d + sin(30), 0d)) // 6
                .withSideBranch(new Vec3(0d, 1d, 0d), new Vec3(0d, 1d, -0.5d).normalize())
                .withBondTo(5, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(6 * cos(30), 1d, 0d)) // 7
                .withSideBranch(new Vec3(cos(30), sin(30), 0d), new Vec3(cos(30), sin(30), 0.5d).normalize())
                .withBondTo(6, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(6 * cos(30), 0d, 0d)) // 8
                .withSideBranch(new Vec3(cos(30), -sin(30), 0d), new Vec3(cos(30), -sin(30), -0.5d).normalize())
                .withBondTo(7, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(5 * cos(30), -sin(30), 0d)) // 9
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, 0.5d).normalize())
                .withBondTo(8, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(4 * cos(30), 0d, 0d)) // 10
                .withBondTo(5, BondType.AROMATIC)
                .withBondTo(9, BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(3 * cos(30), -sin(30), 0d)) // 11
                .withSideBranch(new Vec3(0d, -1d, 0d), new Vec3(0d, -1d, -0.5d).normalize(), quinone ? BondType.DOUBLE : BondType.SINGLE)
                .withBondTo(10, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(2 * cos(30), 0d, 0d)) // 12
                .withBondTo(3, BondType.AROMATIC)
                .withBondTo(11, quinone ? BondType.SINGLE : BondType.AROMATIC)
                .attach()
            .atom(LegacyElement.CARBON, new Vec3(cos(30), -sin(30), 0d)) // 13
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

    private static Topology.Builder create(LegacyElement startingElement) {
        return new Topology.Builder(Destroy.MOD_ID).startWith(startingElement);
    };

    private static Topology.Builder create(LegacyElement startingElement, double charge) {
        return new Topology.Builder(Destroy.MOD_ID).startWith(startingElement, charge);
    };

    public static void register() {};
};
