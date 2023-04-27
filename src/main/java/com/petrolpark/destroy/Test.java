package com.petrolpark.destroy;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Used during development for testing aspects of the Chemistry system without having to run the whole game.
 */
@SuppressWarnings("unused")
public class Test {

    public static void main(String args[]) {

        Vec3 standard = new Vec3(1f, 0f, 0f);
        Vec3 plane = new Vec3(0f, 0f, 1f);

        for (float f = 0f; f < 360f; f += 1f) {
            Destroy.LOGGER.info("Angle: "+ f);
            Destroy.LOGGER.info("My personal opinion of the angle: " + MoleculeRenderer.angleBetween(standard, MoleculeRenderer.rotate(standard, plane, f), plane));
        };

        System.out.println(""+Mth.sin(30));
        Destroy.LOGGER.info(""+MoleculeRenderer.rotate(new Vec3(1f, 0f, 0f), new Vec3(0f, 0f, 1f), 90f + MoleculeRenderer.Geometry.TETRAHEDRAL.getAngle() * 0.5f));

        // DestroyGroupFinder.register();
        // DestroyMolecules.register();
        // DestroyReactions.register();
        // DestroyGenericReactions.register();

        // Molecule myMolecule = builder()
        //     .structure(Formula.deserialize("linear:C(C)C"))
        //     .build();

        // //System.out.println(DestroyMolecules.ASPIRIN.getStructuralFormula());
        // System.out.println(DestroyMolecules.CHLORIDE.getName(true).toString());
        
    };

    private static MoleculeBuilder builder() {
        return new MoleculeBuilder("novel");
    };
}
