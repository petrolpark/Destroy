package com.petrolpark.destroy.core.chemistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Math;
import org.joml.Quaternionf;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.petrolpark.destroy.chemistry.legacy.LegacyAtom;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure.Topology.SideChainInformation;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Edge;
import com.petrolpark.destroy.chemistry.serializer.Node;
import com.petrolpark.util.MathsHelper;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.ILightingSettings;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.ponder.render.VirtualRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;

public class MoleculeRenderer {

    protected String moleculeID;

    protected AABB bb;

    protected static final double SCALE = 23d;
    protected static final double BOND_LENGTH = SCALE / 2;

    protected BakedModel model;

    /**
     * The list of Atoms and Bonds to render, and their locations.
     * This is ordered from back to front.
     */
    List<Pair<Vec3, IRenderableMoleculePart>> RENDERED_OBJECTS;

    public MoleculeRenderer(LegacySpecies molecule) {
        moleculeID = molecule.getFullID();
        bb = new AABB(Vec3.ZERO, Vec3.ZERO);
        RENDERED_OBJECTS = new ArrayList<>();

        // Monatomic Molecules
        if (molecule.getAtoms().size() == 1) {
            RENDERED_OBJECTS.add(Pair.of(Vec3.ZERO, new AtomRenderInstance(molecule.getAtoms().iterator().next())));

        // Cyclic Molecules
        } else if (molecule.isCyclic()) {
            // Add all Atoms in the cycle
            Map<LegacyAtom, Vec3> cyclicAtomsAndLocations = new HashMap<>(); // Store the location of each Atom so we can refer to them when adding the Bonds
            molecule.getCyclicAtomsForRendering().forEach(pair -> {
                cyclicAtomsAndLocations.put(pair.getSecond(), pair.getFirst());
                RENDERED_OBJECTS.add(Pair.of(
                    pair.getFirst().scale(BOND_LENGTH), // The relative location of the Atom
                    new AtomRenderInstance(pair.getSecond()) // The Element of the Atom
                ));
            });
            // Add all Bonds in the cycle
            molecule.getCyclicBondsForRendering().forEach(bond -> {
                Vec3 sourceAtomLocation = cyclicAtomsAndLocations.get(bond.getSourceAtom());
                Vec3 zig = cyclicAtomsAndLocations.get(bond.getDestinationAtom()).subtract(sourceAtomLocation).normalize();
                RENDERED_OBJECTS.add(Pair.of(
                   sourceAtomLocation.scale(BOND_LENGTH).add(zig.scale(BOND_LENGTH / 2)),
                   BondRenderInstance.fromZig(bond.getType(), zig)
                ));
            });
            // Add all side chains
            molecule.getSideChainsForRendering().forEach(pair -> {
                SideChainInformation sideChainInfo = pair.getFirst();
                Vec3 zig = sideChainInfo.bondDirection();
                Vec3 cyclicAtomLocation = cyclicAtomsAndLocations.get(sideChainInfo.atom()).scale(BOND_LENGTH);
                Vec3 startLocation = cyclicAtomLocation.add(zig.scale(BOND_LENGTH));
                Vec3 startDirection = sideChainInfo.branchDirection();
                Vec3 startPlane = sideChainInfo.bondDirection().cross(sideChainInfo.branchDirection());
                RENDERED_OBJECTS.add(Pair.of(
                    cyclicAtomLocation.add(zig.scale(BOND_LENGTH / 2)),
                    BondRenderInstance.fromZig(sideChainInfo.bondType(), zig)
                ));

                generateBranch(pair.getSecond(), startLocation, startDirection, startPlane, zig, true);
            });

        // Standard branched Molecules
        } else {
            Vec3 startLocation = new Vec3(0d, 0d, 0d);
            Vec3 startDirection = new Vec3(1d, 0d, -1d).normalize();
            Vec3 startPlane = new Vec3(1d, 0d, 1d).normalize();
            generateBranch(
                molecule.getRenderBranch(),
                startLocation,
                startDirection,
                startPlane,
                MathsHelper.rotate(startDirection, startPlane, 180d + (getGeometry(molecule.getRenderBranch().getNodes().get(1), false).getAngle() * 0.5d)),
                false
            );
        };

        // Order the Atoms and Bonds so the furthest back get Rendered first
        Collections.sort(RENDERED_OBJECTS, (pair1, pair2) -> Double.compare(pair1.getFirst().z, pair2.getFirst().z));

        // Rescale the Renderer to fit every Atom
        for (Pair<Vec3, IRenderableMoleculePart> pair : RENDERED_OBJECTS) {
            bb = bb.minmax(new AABB(pair.getFirst(), pair.getFirst()));
        };

        // Bake all rendered objects into a single model
        IModelBuilder<?> builder = IModelBuilder.of(false, true, true, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY,
            UnitTextureAtlasSprite.INSTANCE, RenderTypeGroup.EMPTY);
        QuadBakingVertexConsumer buffer = new QuadBakingVertexConsumer(builder::addUnculledFace);
        buffer.setTintIndex(-1);
        for (Pair<Vec3, IRenderableMoleculePart> pair : RENDERED_OBJECTS) {
            pair.getSecond().renderInto(buffer, pair.getFirst());
        };
        model = builder.build();
    };

    public int getWidth() {
        return (int)bb.getXsize();
    };

    public int getHeight() {
        return (int)bb.getYsize();
    };

    /**
     * Draw all Atoms and Bonds in this Molecule.
     */
    public void render(int xPosition, int yPosition, GuiGraphics graphics) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        Vec3 center = bb.getCenter();
        poseStack.translate(center.x - bb.minX + xPosition, center.y - bb.minY + yPosition, -200);
        poseStack.mulPose(Axis.YP.rotationDegrees(AnimationTickHolder.getRenderTime()));
        poseStack.translate(-center.x, -center.y, -center.z);

        UIRenderHelper.flipForGuiRender(poseStack);
        Lighting.setupFor3DItems();
        Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
            poseStack, graphics.bufferSource().getBuffer(Sheets.cutoutBlockSheet()));
        graphics.flush();

        poseStack.popPose();
    };

    /**
     * Draw all Atoms and Bonds in this Molecule into the provided buffer. This is intended to be used when batch rendering molecules in JEI.
     */
    public void renderItem(int xPosition, int yPosition, int width, int height, PoseStack poseStack, MultiBufferSource.BufferSource buffer) {
        float scale = java.lang.Math.min(0.5f, java.lang.Math.min((width+2)/(getWidth()+1f), (height+2)/(getHeight()+1f)));

        poseStack.pushPose();
        Vec3 center = bb.getCenter();
        poseStack.translate(xPosition + width/2, yPosition + height/2, 50);
        poseStack.mulPose(Axis.XP.rotationDegrees(-10));
        poseStack.mulPose(Axis.YP.rotationDegrees(AnimationTickHolder.getRenderTime()));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-center.x, -center.y, -center.z);

        UIRenderHelper.flipForGuiRender(poseStack);
        Lighting.setupFor3DItems();
        Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
            poseStack, buffer.getBuffer(Sheets.cutoutBlockSheet()));

        poseStack.popPose();
    };

    /**
     * Recursively generate the position of all Atoms in a chain, and all side chains.
     * @param branch
     * @param startLocation The location of the first Atom in this chain
     * @param direction The overall direction in which this chain should continue (as chains zig-zag, this is the direction of net movement)
     * @param plane The normal to the plane in which this chain should appear
     * @param zig The vector representing the direction of the imaginary first zig in the chain - the zig from some imaginary Atom to the first Atom in the Branch
     * @param addOneConnectionToFirstNode Whether to add one to the number of connections the first node it thinks it has (i.e. a linear Geometry will become trigonal planar);
     */
    public void generateBranch(Branch branch, Vec3 startLocation, Vec3 direction, Vec3 plane, Vec3 zig, boolean addOneConnectionToFirstNode) {
        Vec3 location = new Vec3(startLocation.x, startLocation.y, startLocation.z); // The working location at which to render Atoms; this moves
        Vec3 zag = new Vec3(zig.x, zig.y, zig.z); // The direction of the next bond; this changes, hopefully in a zigzagular fashion

        // Total number of Atoms rendered
        int i = 0;

        for (Node node : branch.getNodes()) {
            // Mark the Atom for rendering at this location
            RENDERED_OBJECTS.add(Pair.of(new Vec3(location.x, location.y, location.z), new AtomRenderInstance(node.getAtom())));

            // Increment the number of Atoms rendered
            i++;

            // Determine the Geometry of this node
            Geometry geometry = getGeometry(node, addOneConnectionToFirstNode);
            if (addOneConnectionToFirstNode) addOneConnectionToFirstNode = false; // Only add an additional connection to the first Node

            // Determine the orientation of this Atom
            ConfinedGeometry confinedGeometry = geometry.confine(zag, plane, direction);

            // Get the zag from the zig
            zag = confinedGeometry.getZag();

            // Render side chains
            int j = 1;
            for (Entry<Branch, BondType> sideBranchAndBondType : node.getOrderedSideBranches()) {

                Vec3 sideZag; // The zag which will connect the side chain
                if (j == geometry.connections.size()) { // This occurs if we're adding side chains on the first Node in the branch: if so, we need to add the side chain 'behind' it
                    sideZag = confinedGeometry.getInverseZig();
                } else {
                    sideZag = confinedGeometry.getZag(j);
                };

                Branch sideBranch = sideBranchAndBondType.getKey();
                Vec3 newPlane = confinedGeometry.getZig().cross(sideZag);
                RENDERED_OBJECTS.add(Pair.of(location.add(sideZag.scale(0.5d * BOND_LENGTH)), BondRenderInstance.fromZig(sideBranchAndBondType.getValue(),  sideZag)));
                generateBranch(sideBranch, location.add(sideZag.scale(BOND_LENGTH)), MathsHelper.rotate(sideZag, newPlane, 90d), newPlane, sideZag, true);
                j++;
            };

            // If that was the last Atom, there is no need to render the next bond or determine the position of the next Atom
            if (i >= branch.getNodes().size()) break;

            BondType bondType = BondType.SINGLE;
            for (Edge edge : node.getEdges()) {
                if (edge.getSourceNode() == branch.getNodes().get(i - 1)) bondType = edge.bondType;
            };

            // Mark the Bond for rendering at this location
            RENDERED_OBJECTS.add(Pair.of(location.add(zag.scale(0.5d * BOND_LENGTH)), BondRenderInstance.fromZig(bondType, zag)));

            // Get the position of the next Atom in the chain
            location = location.add(zag.scale(BOND_LENGTH));
        };
    };

    private Geometry getGeometry(Node node, boolean addOne) {
        int connections = node.getEdges().size() + node.getSideBranches().size() + (addOne ? 1 : 0);
        return node.getAtom().getElement().getGeometry(connections);
    };

    public static enum Geometry {

        LINEAR(new Vec3(1d, 0d, 0d)),
        V_SHAPE(new Vec3(0.333333d, -0.942809d, 0d).normalize()),
        TRIGONAL_PLANAR(new Vec3(0.5d, 0.86602540378d, 0d).normalize(), new Vec3(0.5d, -0.86602540378d, 0d).normalize()),
        TRIGONAL_PYRAMIDAL(new Vec3(0.333333d, -0.942809d, 0d).normalize(), new Vec3(0.333333d, 0.471405d, 0.816497d).normalize()),
        TETRAHEDRAL(new Vec3(0.333333d, -0.942809d, 0d).normalize(), new Vec3(0.333333d, 0.471405d, 0.816497d).normalize(), new Vec3(0.333333d, 0.471405d, -0.816497d).normalize()),
        OCTAHEDRAL(new Vec3(1d, 0d, 0d), new Vec3(0d, 1d, 0d), new Vec3(0d, -1d, 0d), new Vec3(0d, 0d, 1d), new Vec3(0d, 0d, -1d));

        /**
         * The default input direction for a Geometry, to which all the output directions are relative.
         */
        private static final Vec3 standardDirection = new Vec3(1d, 0d, 0d);
        private static final Vec3 inverseStandardDirection = new Vec3(-1d, 0d, 0d);

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
            double angle = MathsHelper.angleBetween(standardDirection, connections.get(0), new Vec3(0d, 0d, 1d));
            return angle < 90d ? 180d - angle : angle; // We always want the obtuse angle
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
            double angle = MathsHelper.angleBetween(standardDirection, zig, rotationVec);

            // Calculate the adjusted zag vector by applying the same transformation to the XY-coplanar output vector for this geometry
            Vec3 zag = MathsHelper.rotate(connections.get(0), rotationVec, angle);

            // Determine whether the continuation vector flipped by 180 degrees is more faithful to the direction in which this branch should be going
            boolean flip = distanceFromPointToLine(zig.add(MathsHelper.rotate(zag, zig, 180d)), Vec3.ZERO, direction) < distanceFromPointToLine(zig.add(zag), Vec3.ZERO, direction);

            return new ConfinedGeometry(this, rotationVec, angle, flip);
        };

        public List<Vec3> getConnections(boolean includeInput) {
            if (!includeInput) return connections;
            List<Vec3> connectionsAndInput = new ArrayList<>(connections.size() + 1);
            connectionsAndInput.addAll(connections);
            connectionsAndInput.add(inverseStandardDirection);
            return connectionsAndInput;
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
            return MathsHelper.rotate(Geometry.standardDirection, rotationAxis, angle);
        };

        private Vec3 getInverseZig() {
            return MathsHelper.rotate(Geometry.inverseStandardDirection, rotationAxis, angle);
        };

        private Vec3 getZag() {
            return getZag(0);
        };

        private Vec3 getZag(int index) {
            Vec3 unflipped = MathsHelper.rotate(geometry.connections.get(index), rotationAxis, angle);
            if (!flip) {
                return unflipped;
            } else {
                return MathsHelper.rotate(unflipped, MathsHelper.rotate(Geometry.standardDirection, rotationAxis, angle), 180d);
            }
        };
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

    protected static interface IRenderableMoleculePart {
        public void render(GuiGraphics graphics, Vec3 location);
        public void renderInto(VertexConsumer builder, Vec3 location);
    };

    protected static record BondRenderInstance(BondType type, Quaternionf rotation) implements IRenderableMoleculePart {

        private static Vec3 bond = new Vec3(1d, 0d, 0d);

        /**
         * Generate a Bond render instance betweem two Atoms
         * @param type The {@link com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType type} of the bond
         * @param zig The direction vector which connects the two Atoms this Bond connects
         */
        public static BondRenderInstance fromZig(BondType type, Vec3 zig) {
            Vec3 z = zig.normalize();
            Vec3 axis = bond.cross(z);
            Quaternionf q = new Quaternionf(axis.x(), axis.y(), axis.z(), (float)bond.dot(z) + (float)Math.sqrt(bond.lengthSqr() * z.lengthSqr()));
            if(!q.normalize().isFinite())
                q = new Quaternionf(0f, 0f, 0f, 1f); // identity

            return new BondRenderInstance(type, q);
        };

        @Override
        public void render(GuiGraphics graphics, Vec3 location) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(location.x, location.y, location.z);
            TransformStack.of(poseStack)
                .rotateCentered(rotation);
            GuiGameElement.of(type().getPartial())
                .lighting(ILightingSettings.DEFAULT_FLAT)
                .scale(SCALE)
                .render(graphics, 0, 0);
            poseStack.popPose();
        };

        @Override
        public void renderInto(VertexConsumer builder, Vec3 location) {
            PoseStack poseStack = new PoseStack();

            // Models need to be flipped on the Y axis when rendered in the GUI in order to appear upright
            // Molecules were originally rendered piece by piece, with each piece being flipped individually
            // Now that molecules are baked into a single model, the Y coordinates need to be flipped so each
            // piece appears in the correct position once the entire model is flipped again
            poseStack.translate(location.x, -location.y, location.z);
            TransformStack.of(poseStack)
                    .rotate(new Quaternionf(-rotation.x, rotation.y, -rotation.z, rotation.w)); // flip rotation around Y axis
            poseStack.scale((float)SCALE, (float)SCALE, (float)SCALE);

            Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .renderModel(poseStack.last(), builder, Blocks.AIR.defaultBlockState(), type().getPartial().get(), 1, 1, 1,
                    0, OverlayTexture.NO_OVERLAY, VirtualRenderHelper.VIRTUAL_DATA, RenderType.solid());
        };
    };

    protected static record AtomRenderInstance(LegacyAtom atom) implements IRenderableMoleculePart {

        @Override
        public void render(GuiGraphics graphics, Vec3 location) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(location.x, location.y, location.z);
            GuiGameElement.of(atom.getPartial())
                .scale(SCALE)
                //.rotate(15.5d, 22.5d, 0d)
                .render(graphics, 0, 0);
            poseStack.popPose();
        };

        @Override
        public void renderInto(VertexConsumer builder, Vec3 location) {
            PoseStack poseStack = new PoseStack();

            poseStack.translate(location.x, -location.y, location.z);
            poseStack.scale((float)SCALE, (float)SCALE, (float)SCALE);

            Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .renderModel(poseStack.last(), builder, Blocks.AIR.defaultBlockState(), atom.getPartial().get(), 1, 1, 1,
                    0, OverlayTexture.NO_OVERLAY, VirtualRenderHelper.VIRTUAL_DATA, RenderType.solid());
        };
    };
};
