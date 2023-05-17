package com.petrolpark.destroy.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.util.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MoleculeRenderer {

    protected static final double BOND_LENGTH = 10;
    protected int originX; // X coordinate of this Molecule render on the screen
    protected int originY; // Y coordinate of this Molecule render on the screen

    List<Pair<Vec3, Atom>> LOCATIONS_OF_ATOMS;
    List<BondRenderInstance> BONDS;

    public MoleculeRenderer(Molecule molecule) {
        LOCATIONS_OF_ATOMS = new ArrayList<>(molecule.getAtoms().size());
        BONDS = new ArrayList<>();
        if (molecule.getAtoms().size() == 1) {

        } else {
            Vec3 startLocation = new Vec3(0f, 0f, 0f);
            Vec3 startDirection = new Vec3(1f, 0f, 0f);
            Vec3 startPlane = new Vec3(0f, 0f, 1f);
            generateBranch(molecule.getRenderBranch(), startLocation, startDirection, startPlane, rotate(startDirection, startPlane, 180f + (getGeometry(molecule.getRenderBranch().getNodes().get(1)).getAngle() * 0.5f))
                
            );
        };
        Collections.sort(LOCATIONS_OF_ATOMS, (pair1, pair2) -> Double.compare(pair1.first().z, pair2.first().z)); // Order the Atoms so the furthest back get Rendered first
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

        boolean temp = false;

        int i = 0; // Total number of nodes rendered
        for (Node node : branch.getNodes()) {
            // Mark the atom for rendering at this location
            LOCATIONS_OF_ATOMS.add(Pair.of(new Vec3(location.x, location.y, location.z), node.getAtom()));

            // If that was the last Atom, there is no need to render the next bond or determine the position of the next Atom
            if (i >= branch.getNodes().size()) break;

            // Determine the Geometry of this node
            Geometry geometry = getGeometry(node);

            // Determine the orientation of this Atom
            ConfinedGeometry confinedGeometry = geometry.confine(zag, plane, direction);

            // Get the zag from the zig
            zag = confinedGeometry.getZag();

            // Mark the bond for rendering at this location
            BONDS.add(new BondRenderInstance(BondType.SINGLE, location.add(zag.scale(0.5f * BOND_LENGTH)), zag));

            // Get the position of the next Atom
            location = location.add(zag.scale(BOND_LENGTH));

            // int i = 1;
            // for (Entry<Branch, BondType> sideBranchAndBondType : node.getSideBranches().entrySet()) {
            //     if (temp) break;
            //     Branch sideBranch = sideBranchAndBondType.getKey();
            //     Vec3 sideZag = confinedGeometry.getZag(i);
            //     Vec3 newPlane = sideZag.cross(confinedGeometry.getZig());
            //     generateBranch(sideBranch, location.add(sideZag.scale(BOND_LENGTH)), rotate(sideZag, newPlane, 90f), newPlane, sideZag);
            //     temp = true;
            //     i++;
            // };

            // Increment the number of Atoms rendered
            i++;
        };
    };

    protected void renderAtom(Atom atom, Vec3 location, PoseStack poseStack) {
        poseStack.pushPose();
        GuiGameElement.of(atom.getElement().getPartial()).lighting(AnimatedKinetics.DEFAULT_LIGHTING)
            .scale(23)
            .rotate(15.5f, 22.5f, 0f)
            .render(poseStack, originX + (int)location.x, originY + (int)location.y);
        poseStack.popPose();
    };

    protected void renderBond(BondRenderInstance bond, PoseStack poseStack) {
        poseStack.pushPose();
        GuiGameElement.of(bond.type().getPartial()).lighting(AnimatedKinetics.DEFAULT_LIGHTING)
            .scale(23)
            .rotate(bond.xRot(), bond.yRot(), bond.zRot())
            .render(poseStack, originX + (int)bond.position().x, originY + (int)bond.position().y);
        poseStack.popPose();
    };

    private Geometry getGeometry(Node node) {
        return Geometry.TETRAHEDRAL;
    };

    public static enum Geometry {

        LINEAR(new Vec3(1f, 0f, 0f)),
        TRIGONAL_PLANAR(new Vec3(0.5f, Mth.sin(60), 0f).normalize(), new Vec3(0.5f, -Mth.sin(60), 0f).normalize()),
        TETRAHEDRAL(new Vec3(0.333333f, -0.942809f, 0f), new Vec3(0.333333f, 0.471405f, 0.816497f), new Vec3(0.333333f, 0.471405f, -0.816497f)),
        OCTAHEDRAL(new Vec3(1f, 0f, 0f), new Vec3(0f, 1f, 0f), new Vec3(0f, -1f, 0f), new Vec3(0f, 0f, 1f), new Vec3(0f, 0f, -1f));

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
        public float getAngle() {
            float angle = angleBetween(standardDirection, connections.get(0), new Vec3(0f, 0f, 1f));
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
            if (plane.dot(direction) > 0.000001f) throw new IllegalStateException("Chains of Molecules being rendered in a plane must continue in a direction in that plane.");

            // Determine how the zig was transformed from (1,0,0)
            Vec3 rotationVec = zig.cross(standardDirection);
            float angle = angleBetween(standardDirection, zig, rotationVec);

            // Calculate the adjusted zag vector by applying the same transformation to the XY-coplanar output vector for this geometry
            Vec3 zag = rotate(connections.get(0), rotationVec, angle);

            // Determine whether the continuation vector flipped by 180 degrees is more faithful to the direction in which this branch should be going
            boolean flip = distanceFromPointToLine(zig.add(rotate(zag, zig, 180f)), Vec3.ZERO, direction) < distanceFromPointToLine(zig.add(zag), Vec3.ZERO, direction);

            return new ConfinedGeometry(this, rotationVec, angle, flip);
        };
    };

    private static class ConfinedGeometry {
        final Geometry geometry;
        final Vec3 rotationAxis;
        final float angle;
        /**
         * Whether to rotate this Geometry 180 degrees around the input connection.
         */
        final boolean flip;

        private ConfinedGeometry(Geometry geometry, Vec3 rotationAxis, float angle, boolean flip) {
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
                return rotate(unflipped, rotate(Geometry.standardDirection, rotationAxis, angle), 180);
            }
        };
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
     * The directional angle in degrees between two vectors, between 0 and 360.
     * @param vec1
     * @param vec2
     * @param plane The approximate vector around which {@code vec1} was rotated to get {@code vec2}
     */
    public static float angleBetween(Vec3 vec1, Vec3 vec2, Vec3 plane) {
        float angle = (float)Math.acos(vec1.dot(vec2) / (vec1.length() * vec2.length())) * 180f / Mth.PI;
        if (vec1.dot(vec2.cross(plane)) < 0f) angle = 360f - angle;
        return angle;
    };

    /**
     * The shortest (perpendicular) distance from a point to a line.
     * @param point The point to which to find the distance
     * @param linePoint Any point on the line
     * @param lineDirection The direction vector of the line
     */
    public static float distanceFromPointToLine(Vec3 point, Vec3 linePoint, Vec3 lineDirection) {
        return (float) ( (point.subtract(linePoint)).cross(lineDirection).length() / lineDirection.length() );
    };

    protected static record BondRenderInstance(BondType type, Vec3 position, float xRot, float yRot, float zRot) {

        public BondRenderInstance(BondType type, Vec3 position, Vec3 zig) {
            this(type, position, (float)Math.atan2(zig.y, zig.z) * 180f / Mth.PI, (float)Math.atan2(zig.z, zig.x) * 180f / Mth.PI, (float)Math.atan2(zig.x, zig.y) * 180f / Mth.PI);
        };
    };
};
