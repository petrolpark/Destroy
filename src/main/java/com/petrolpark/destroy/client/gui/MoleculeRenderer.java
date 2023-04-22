package com.petrolpark.destroy.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
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
            Vec3 startDirection = new Vec3(1f, 0f, 0d).normalize();
            Vec3 startPlane = new Vec3(0d, 0d, 1d);
            generateBranch(molecule.getRenderBranch(), startLocation, startDirection, startPlane, rotate(startDirection, startPlane, //getGeometry(molecule.getRenderBranch().getNodes().get(1)).getAngle() / 2
            54.5f
            ));
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
     * @param startLocation The location of the first Atom in this chain
     * @param direction The overall direction in which this chain should continue (as chains zig-zag, this is the direction of net movement)
     * @param plane The normal to the plane in which this chain should appear
     * @param zig The vector representing the direction of the first zig in the chain
     */
    public void generateBranch(Branch branch, Vec3 startLocation, Vec3 direction, Vec3 plane, Vec3 zig) {
        Vec3 location = new Vec3(startLocation.x, startLocation.y, startLocation.z); // The working location at which to render Atoms; this moves
        Vec3 zag = new Vec3(zig.x, zig.y, zig.z); // The direction of the next bond; this changes, hopefully in a zigzagular fashion
        for (Node node : branch.getNodes()) {
            // Render the Atom at this location
            LOCATIONS_OF_ATOMS.add(Pair.of(new Vec3(location.x, location.y, location.z), node.getAtom()));

            // Determine the location of the next Atom in the chain
            Geometry geometry = getGeometry(node);

            zag = geometry.getBranchContinuation(zag, plane, direction); // Get the next zag
            location = location.add(zag.scale(BOND_LENGTH)); // Get the position of the next Atom
        };
    };

    private void renderAtom(Atom atom, Vec3 location, PoseStack poseStack) {
        GuiGameElement.of(atom.getElement().getPartial()).lighting(AnimatedKinetics.DEFAULT_LIGHTING)
            .scale(23)
            .rotate(15.5f, 22.5f, 0f)
            .render(poseStack, originX + (int)location.x, originY + (int)location.y);
    };

    private Geometry getGeometry(Node node) {
        return Geometry.TETRAHEDRAL;
    };

    private static enum Geometry {
        LINEAR(new Vec3(1f, 0f, 0f)),
        TRIGONAL_PLANAR(new Vec3(0.5f, Mth.sin(60), 0f).normalize(), new Vec3(0.5f, -Mth.sin(60), 0f).normalize()),
        TETRAHEDRAL(new Vec3(0.333333f, -0.942809f, 0f), new Vec3(0.333333f, 0.471405f, 0.816497f), new Vec3(0.333333f, 0.471405f, -0.816497f)),
        OCTAHEDRAL(new Vec3(1f, 0f, 0f), new Vec3(0f, 1f, 0f), new Vec3(0f, -1f, 0f), new Vec3(0f, 0f, 1f), new Vec3(0f, 0f, -1f));

        /**
         * Each additional connection out of this Geometry
         * relative to the inward Vector (1, 0, 0), with
         * the first vector also in the XY plane.
         */
        final ImmutableList<Vec3> connections;

        Geometry(Vec3 ...connections) {
            this.connections = ImmutableList.copyOf(connections);
        };

        /**
         * Get the angle between the connections around this Geometry -
         * specifically, the angle between the input vector (1,0,0) and the XY coplanar output vector.
         */
        public float getAngle() {
            return angleBetween(new Vec3(1f, 0f, 0f), connections.get(0));
        };

        /**
         * Get the normalized vector of the next zag in the zig zag.
         * @param branchIn The zig
         * @param plane The normal to the plane in which this chain is being rendered
         * @param direction The overall direction in which the chain should continue, which should be in the plane
         */
        public Vec3 getBranchContinuation(Vec3 branchIn, Vec3 plane, Vec3 direction) {

            // Check the direction vector is in the plane
            if (plane.dot(direction) > 0.000001f) throw new IllegalStateException("Error rendering Molecule");

            // Determine how the zig was transformed from (1,0,0)
            Vec3 rotationVec = branchIn.cross(new Vec3(1f, 0f, 0f));
            float angle = angleBetween(branchIn, direction);

            // Calculate the adjusted zag vector by applying the same transformation to the XY-coplanar output vector for this geometry
            Vec3 continuationVec = rotate(connections.get(0), rotationVec, angle);

            // Determine whether the continuation vector flipped by 180 degrees is more faithful to the direction in which this branch should be going
            boolean flip = distanceFromPointToLine(branchIn.add(rotate(continuationVec, direction, 180)), Vec3.ZERO, direction) > distanceFromPointToLine(branchIn.add(continuationVec), Vec3.ZERO, direction)
                //&& rotate(continuationVec, plane, 180).dot(direction) > 0 // Ensure they are facing the same direction
            ;

            return flip ? rotate(continuationVec, direction, 180) : continuationVec;
        };
    };

    private static class ConfinedGeometry {
        Geometry geometry;
    };

    /**
     * @param vec The vector to rotate
     * @param rotationAxis The vector about which to rotate the first vector
     * @param angle The angle in degrees through which to rotate the first vector around the second
     */
    public static Vec3 rotate(Vec3 vec, Vec3 rotationAxis, float angle) {
        Vec3 k = rotationAxis.normalize();
        float angleInRads = angle * Mth.PI / 180;
        // Rodrigues' formula
        return vec.scale(Mth.cos(angleInRads))
            .add(k.cross(vec).scale(Mth.sin(angleInRads)))
            .add(k.scale(k.dot(vec) * (1 - Mth.cos(angleInRads)))); 
    };

    /**
     * The angle in degrees between two vectors.
     * @param vec1
     * @param vec2
     */
    public static float angleBetween(Vec3 vec1, Vec3 vec2) {
        return (float)Math.acos(vec1.dot(vec2) / (vec1.length() * vec2.length())) * 180f / Mth.PI;
    };

    /**
     * The shortest (perpendicular) distance from a point to a line
     * @param linePoint Any point on the line
     * @param lineDirection The direction vector of the line
     * @param point The point to which to find the distance
     */
    public static float distanceFromPointToLine(Vec3 point, Vec3 linePoint, Vec3 lineDirection) {
        return (float) ( (point.subtract(linePoint)).cross(lineDirection).length() / lineDirection.length() );
    };
};
