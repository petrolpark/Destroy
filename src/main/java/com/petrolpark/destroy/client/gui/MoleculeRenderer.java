package com.petrolpark.destroy.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MoleculeRenderer {

    /**
     * The distance from 0 to the highest X value of any rendered object.
     */
    protected int x;
    /**
     * The distance from 0 to the highest Y value of any rendered object.
     */
    protected int y;
    /**
     * How far below X = 0 this rendered Molecule extends.
     */
    protected int xOffset;
    /**
     * How far above Y = 0 this rendered Molecule extends (remember positive Y is down).
     */
    protected int yOffset;

    protected static final double BOND_LENGTH = 12;

    /**
     * The list of Atoms and Bonds to render, and their locations.
     * This is ordered from back to front.
     */
    List<Pair<Vec3, IRenderable>> RENDERED_OBJECTS;

    public MoleculeRenderer(Molecule molecule) {
        x = 0;
        y = 0;
        xOffset = 0;
        yOffset = 0;
        RENDERED_OBJECTS = new ArrayList<>();

        if (molecule.getAtoms().size() == 1) { // Monatomic Molecules
            RENDERED_OBJECTS.add(Pair.of(Vec3.ZERO, new AtomRenderInstance(molecule.getAtoms().iterator().next().getElement())));
        } else if (molecule.isCyclic()) { // Cyclic Molecules
            molecule.getCyclicAtomsForRendering().forEach(pair -> {
                RENDERED_OBJECTS.add(Pair.of(
                    pair.getFirst().scale(BOND_LENGTH), // The relative location of the Atom
                    new AtomRenderInstance(pair.getSecond().getElement()) // The Element of the Atom
                ));
            });
        } else { // Standard branched Molecules
            Vec3 startLocation = new Vec3(0d, 0d, 0d);
            Vec3 startDirection = new Vec3(1d, 0d, -1d).normalize();
            Vec3 startPlane = new Vec3(1d, 0d, 1d).normalize();
            generateBranch(molecule.getRenderBranch(), startLocation, startDirection, startPlane, rotate(startDirection, startPlane, 180d + (getGeometry(molecule.getRenderBranch().getNodes().get(1)).getAngle() * 0.5d)));
        };

        // Order the Atoms and Bonds so the furthest back get Rendered first
        Collections.sort(RENDERED_OBJECTS, (pair1, pair2) -> Double.compare(pair1.getFirst().z, pair2.getFirst().z));

        // Rescale the Renderer to fit every Atom
        for (Pair<Vec3, IRenderable> pair : RENDERED_OBJECTS) {
            x = Math.max(x, (int)pair.getFirst().x);
            y = Math.max(y, (int)pair.getFirst().y);
            // Set the X and Y offSets to the positive of the most negative respective coordinate of any rendered object present
            xOffset = -(int)Math.min(-xOffset, pair.getFirst().x);
            yOffset = -(int)Math.max(-yOffset, pair.getFirst().y);
        };

    };

    public int getWidth() {
        return x + xOffset;
    };

    public int getHeight() {
        return y;
    };

    /**
     * Draw all Atoms and Bonds in this Molecule.
     */
    public void render(PoseStack poseStack, int xPosition, int yPosition) {
        poseStack.translate(xPosition + xOffset, yPosition, 0d);
        poseStack.pushPose();
        for (Pair<Vec3, IRenderable> pair : RENDERED_OBJECTS) {
            pair.getSecond().render(poseStack, pair.getFirst());
        };
        poseStack.popPose();
    };

    /**
     * Recursively generate the position of all Atoms in a chain, and all side chains.
     * @param poseStack The Pose Stack to use to render everything
     * @param branch
     * @param startLocation The location of the first Atom in this chain
     * @param direction The overall direction in which this chain should continue (as chains zig-zag, this is the direction of net movement)
     * @param plane The normal to the plane in which this chain should appear
     * @param zig The vector representing the direction of the imaginary first zig in the chain - the zig from some imaginary Atom to the first Atom in the Branch
     */
    public void generateBranch(Branch branch, Vec3 startLocation, Vec3 direction, Vec3 plane, Vec3 zig) {
        Vec3 location = new Vec3(startLocation.x, startLocation.y, startLocation.z); // The working location at which to render Atoms; this moves
        Vec3 zag = new Vec3(zig.x, zig.y, zig.z); // The direction of the next bond; this changes, hopefully in a zigzagular fashion

        // Total number of Atoms rendered
        int i = 0;

        for (Node node : branch.getNodes()) {
            // Mark the Atom for rendering at this location
            RENDERED_OBJECTS.add(Pair.of(new Vec3(location.x, location.y, location.z), new AtomRenderInstance(node.getAtom().getElement())));

            // Increment the number of Atoms rendered
            i++;

            // If that was the last Atom, there is no need to render the next bond or determine the position of the next Atom
            if (i >= branch.getNodes().size()) break;

            // Determine the Geometry of this node
            Geometry geometry = getGeometry(node);

            // Determine the orientation of this Atom
            ConfinedGeometry confinedGeometry = geometry.confine(zag, plane, direction);

            // Get the zag from the zig
            zag = confinedGeometry.getZag();

            // Mark the Bond for rendering at this location
            RENDERED_OBJECTS.add(Pair.of(location.add(zag.scale(0.5d * BOND_LENGTH)), new BondRenderInstance(BondType.SINGLE, zag)));

            // Render side chains
            int j = 1;
            for (Entry<Branch, BondType> sideBranchAndBondType : node.getSideBranches().entrySet()) {
                if (j >= geometry.connections.size()) break; // This should never happen
                Branch sideBranch = sideBranchAndBondType.getKey();
                Vec3 sideZag = confinedGeometry.getZag(j);
                Vec3 newPlane = confinedGeometry.getZig().cross(sideZag);
                RENDERED_OBJECTS.add(Pair.of(location.add(sideZag.scale(0.5d * BOND_LENGTH)), new BondRenderInstance(BondType.SINGLE,  sideZag)));
                generateBranch(sideBranch, location.add(sideZag.scale(BOND_LENGTH)), rotate(sideZag, newPlane, 90d), newPlane, sideZag);
                j++;
            };

            // Get the position of the next Atom in the chain
            location = location.add(zag.scale(BOND_LENGTH));
        };
    };

    private Geometry getGeometry(Node node) {
        return Geometry.TETRAHEDRAL;
    };

    public static enum Geometry {

        LINEAR(new Vec3(1d, 0d, 0d)),
        TRIGONAL_PLANAR(new Vec3(0.5d, 0.86602540378d, 0d).normalize(), new Vec3(0.5d, -0.86602540378d, 0d).normalize()),
        TETRAHEDRAL(new Vec3(0.333333d, -0.942809d, 0d).normalize(), new Vec3(0.333333d, 0.471405d, 0.816497d).normalize(), new Vec3(0.333333d, 0.471405d, -0.816497d).normalize()),
        OCTAHEDRAL(new Vec3(1d, 0d, 0d), new Vec3(0d, 1d, 0d), new Vec3(0d, -1d, 0d), new Vec3(0d, 0d, 1d), new Vec3(0d, 0d, -1d));

        /**
         * The default input direction for a Geometry, to which all the output directions are relative.
         */
        private static final Vec3 standardDirection = new Vec3(1f, 0f, 0f);

        /**
         * The normalized direction vector of each additional connection out of this Geometry
         * relative to the {@link Geometry#standardDirection standard input direction vector},
         * with the first output vector also in the XY plane.
         */
        final ImmutableList<Vec3> connections;

        Geometry(Vec3 ...connections) {
            this.connections = ImmutableList.copyOf(connections);
        };

        /**
         * Get the angle in degrees between the connections around this Geometry -
         * specifically, the angle between the input vector (1,0,0) and the XY-coplanar output vector.
         */
        public double getAngle() {
            double angle = angleBetween(standardDirection, connections.get(0), new Vec3(0f, 0f, 1f));
            return angle < 90f ? 180f - angle : angle; // We always want the obtuse angle
        };

        /**
         * Confine this Geometry to a specified plane, with the zag oriented optimally to continue the chain in the given direction.
         * @param zig The zig ({@link Geometry#connections input direction vector})
         * @param plane The normal to the plane in which this chain is being rendered (the plane in which the zig and the zag should lie)
         * @param direction The overall direction in which the chain should continue, which should be in the plane
         */
        public ConfinedGeometry confine(Vec3 zig, Vec3 plane, Vec3 direction) {

            // Check the direction vector is in the plane
            if (plane.dot(direction) > 0.000001d) throw new IllegalStateException("Chains of Molecules being rendered in a plane must continue in a direction in that plane.");

            // Determine how the zig was transformed from (1,0,0)
            Vec3 rotationVec = zig.cross(standardDirection);
            double angle = angleBetween(standardDirection, zig, rotationVec);

            // Calculate the adjusted zag vector by applying the same transformation to the XY-coplanar output vector for this geometry
            Vec3 zag = rotate(connections.get(0), rotationVec, angle);

            // Determine whether the continuation vector flipped by 180 degrees is more faithful to the direction in which this branch should be going
            boolean flip = distanceFromPointToLine(zig.add(rotate(zag, zig, 180d)), Vec3.ZERO, direction) < distanceFromPointToLine(zig.add(zag), Vec3.ZERO, direction);

            return new ConfinedGeometry(this, rotationVec, angle, flip);
        };
    };

    private static class ConfinedGeometry {
        final Geometry geometry;
        final Vec3 rotationAxis;
        final double angle;
        /**
         * Whether to rotate this Geometry 180 degrees around the input connection.
         */
        final boolean flip;

        private ConfinedGeometry(Geometry geometry, Vec3 rotationAxis, double angle, boolean flip) {
            this.geometry = geometry;
            this.rotationAxis = rotationAxis;
            this.angle = angle;
            this.flip = flip;
        };

        private Vec3 getZig() {
            return rotate(Geometry.standardDirection, rotationAxis, angle);
        };

        private Vec3 getZag() {
            return getZag(0);
        };

        private Vec3 getZag(int index) {
            Vec3 unflipped = rotate(geometry.connections.get(index), rotationAxis, angle);
            if (!flip) {
                return unflipped;
            } else {
                return rotate(unflipped, rotate(Geometry.standardDirection, rotationAxis, angle), 180d);
            }
        };
    };

    /**
     * @param vec The vector to rotate
     * @param rotationAxis The vector about which to rotate the first vector
     * @param angle The angle in degrees through which to rotate the first vector around the second
     */
    public static Vec3 rotate(Vec3 vec, Vec3 rotationAxis, double angle) {
        Vec3 k = rotationAxis.normalize();
        float angleInRads = (float)(angle * Mth.PI / 180d);
        // Rodrigues' formula
        return vec.scale(Mth.cos(angleInRads))
            .add(k.cross(vec).scale(Mth.sin(angleInRads)))
            .add(k.scale(k.dot(vec) * (1 - Mth.cos(angleInRads)))); 
    };

    /**
     * The directional angle in degrees between two vectors, between 0 and 360.
     * @param vec1
     * @param vec2
     * @param plane The approximate vector around which {@code vec1} was rotated to get {@code vec2}
     */
    public static double angleBetween(Vec3 vec1, Vec3 vec2, Vec3 plane) {
        double angle = Math.acos(vec1.dot(vec2) / (vec1.length() * vec2.length())) * 180f / Mth.PI;
        if (vec1.dot(vec2.cross(plane)) < 0f) angle = 360f - angle;
        return angle;
    };

    /**
     * The shortest (perpendicular) distance from a point to a line.
     * @param point The point to which to find the distance
     * @param linePoint Any point on the line
     * @param lineDirection The direction vector of the line
     */
    public static double distanceFromPointToLine(Vec3 point, Vec3 linePoint, Vec3 lineDirection) {
        return (point.subtract(linePoint)).cross(lineDirection).length() / lineDirection.length();
    };

    protected static interface IRenderable {
        public void render(PoseStack poseStack, Vec3 location);
    };

    protected static record BondRenderInstance(BondType type, double xRot, double yRot, double zRot) implements IRenderable {

        public BondRenderInstance(BondType type, Vec3 zig) {
            this(type, 0d, Math.atan2(zig.z, zig.x) * 180d / Mth.PI, 90d + Math.atan2(zig.x, zig.y) * 180d / Mth.PI);
        };

        @Override
        public void render(PoseStack poseStack, Vec3 location) {
            poseStack.pushPose();
            poseStack.translate(location.x, location.y, 0d);
            GuiGameElement.of(type().getPartial())
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .scale(23)
                .rotate(xRot(), yRot(), zRot())
                .render(poseStack, 0, 0);
            poseStack.popPose();
        };
    };

    protected static record AtomRenderInstance(Element element) implements IRenderable {

        @Override
        public void render(PoseStack poseStack, Vec3 location) {
            poseStack.pushPose();
            poseStack.translate(location.x, location.y, 0d);
            GuiGameElement.of(element.getPartial())
                .scale(23)
                .rotate(15.5f, 22.5f, 0f)
                .render(poseStack, 0, 0);
            poseStack.popPose();
        };
    };
};
