package com.petrolpark.destroy.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jozufozu.flywheel.util.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MoleculeRenderer implements IDrawable {

    private static final double BOND_LENGTH = 10;
    private int originX; // X coordinate of this Molecule render on the screen
    private int originY; // Y coordinate of this Molecule render on the screen

    List<Pair<Vec3, Atom>> LOCATIONS_OF_ATOMS;

    public MoleculeRenderer(Molecule molecule) {
        LOCATIONS_OF_ATOMS = new ArrayList<>(molecule.getAtoms().size());
        if (molecule.getAtoms().size() == 1) {

        } else {
            Vec3 startLocation = new Vec3(0d, 0d, 0d);
            Vec3 startDirection = new Vec3(1d, 0d, 0d);
            Vec3 startPlane = new Vec3(0d, 0d, 1d);
            generateBranch(molecule.getRenderBranch(), startLocation, startDirection, startPlane);
        };
        Collections.sort(LOCATIONS_OF_ATOMS, (pair1, pair2) -> Double.compare(pair1.first().z, pair2.first().z)); // Order the Atoms so the furthest back get Rendered first
    };

    @Override
    public int getWidth() {
        return 32;
    };

    @Override
    public int getHeight() {
        return 32;
    };

    @Override
    public void draw(PoseStack poseStack, int xOffset, int yOffset) {
        originX = xOffset;
        originY = yOffset;
        for (Pair<Vec3, Atom> pair : LOCATIONS_OF_ATOMS) {
            renderAtom(pair.second(), pair.first(), poseStack);
        };
    };

    /**
     * Recursively generate the position of all Atoms in a chain, and all side chains.
     * @param poseStack The Pose Stack to use to render everything
     * @param branch
     * @param location The location of the first Atom in this chain
     * @param direction The overall direction in which this chain should continue (as chains zig-zag, this is the direction of net movement)
     * @param plane The normal to the plane in which this chain should appear
     */
    public void generateBranch(Branch branch, Vec3 startLocation, Vec3 direction, Vec3 plane) {
        Vec3 location = new Vec3(startLocation.x, startLocation.y, startLocation.z); // The working location at which to render Atoms; this moves
        Vec3 zig = getGeometry(branch.getNodes().get(1)).getStartDirection(direction, plane).scale(BOND_LENGTH); // Get the geometry around the second node and start the zig zag accordingly
        for (Node node : branch.getNodes()) {
            // Render the Atom at this location
            LOCATIONS_OF_ATOMS.add(Pair.of(new Vec3(location.x, location.y, location.z), node.getAtom()));

            // Determine the location of the next Atom in the chain
            Geometry geometry = getGeometry(node);
            location = location.add(geometry.getBranchContinuation(zig, direction).scale(BOND_LENGTH));
        };
    };

    private void renderAtom(Atom atom, Vec3 location, PoseStack poseStack) {
        GuiGameElement.of(atom.getElement().getPartial()).lighting(AnimatedKinetics.DEFAULT_LIGHTING)
            .scale(23)
            .rotate(-15.5f, 22.5f, 0f)
            .render(poseStack, originX + (int)location.x, originY + (int)location.y);
    };

    private Geometry getGeometry(Node node) {
        return Geometry.TETRAHEDRAL;
    };

    private static enum Geometry {
        TETRAHEDRAL(109.5f);

        private float angle;

        Geometry(float angle) {
            this.angle = angle;
        };

        /**
         * Get the normalized vector of the first zig of the zig zag which moves in the given direction.
         * @param direction The overall direction of the zig zag
         * @param plane The normal to the plane in which this zig zag is
         */
        public Vec3 getStartDirection(Vec3 direction, Vec3 plane) {
            return rotate(direction, plane, angle / 2).normalize();
        };

        /**
         * Get the normalized vector of the next zag in the zig zag.
         * @param branchIn The zig
         * @param direction The overall direction of the zig zag
         */
        public Vec3 getBranchContinuation(Vec3 branchIn, Vec3 direction) {
            return rotate(branchIn, direction, 180).normalize();
        };

    };

    /**
     * @param vec The vector to rotate
     * @param rotationAxis The vector about which to rotate the first vector
     * @param angle The angle in degrees through which to rotate the first vector around the second
     */
    public static Vec3 rotate(Vec3 vec, Vec3 rotationAxis, float angle) {
        Vec3 k = rotationAxis.normalize();
        return vec.scale(Mth.cos(angle)).add(k.cross(vec).scale(Mth.sin(angle))).add(k.scale(k.dot(vec) * (1 - Mth.cos(angle)))); // Rodrigues' formula
    };
};
