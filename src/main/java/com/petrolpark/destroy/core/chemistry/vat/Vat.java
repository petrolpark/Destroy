package com.petrolpark.destroy.core.chemistry.vat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterial;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.StructureTransform;

import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.petrolpark.compat.create.CreateClient.OUTLINER;

/**
 * A physical Vat and the Blocks that make it up. Handling of the contents of the Vat and the actual chemistry
 * is done in {@link com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity VatControllerBlockEntity}.
 */
public class Vat {

    public static final int MB_PER_BLOCK = 1000;

    static {
        VatMaterial.registerDestroyVatMaterials();
    };

    // Global positions of the corners of this Vat. In NBT they are stored relative to the Controller position.
    private BlockPos lowerCorner;
    private BlockPos upperCorner;

    private ImmutableList<BlockPos> sides; // NOT synced server/client

    /**
     * The {@link VatMaterial#maxPressure maximum pressure} of the weakest Block making up this Vat.
     */
    private float maximumPressure;
    /**
     * The {@link Vat#maximumPressure weakest Block} in this Vat.
     */
    private BlockState weakestBlockState;

    /**
     * The conductance (in watts per kelvin) of the area of this Vat.
     */
    private float conductance;

    private Vat(BlockPos lowerCorner, BlockPos upperCorner) {
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
    };

    @SuppressWarnings("deprecation")
    public static Optional<Vat> read(CompoundTag tag, BlockPos controllerPos) {
        if (!tag.contains("LowerCorner", Tag.TAG_COMPOUND) || !tag.contains("UpperCorner", Tag.TAG_COMPOUND)) return Optional.empty();
        Vat vat = new Vat(NbtUtils.readBlockPos(tag.getCompound("LowerCorner")).offset(controllerPos), NbtUtils.readBlockPos(tag.getCompound("UpperCorner")).offset(controllerPos));
        vat.conductance = tag.getFloat("Conductance");
        vat.weakestBlockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("WeakestBlock"));
        vat.maximumPressure = VatMaterial.getMaterial(vat.weakestBlockState).map(VatMaterial::maxPressure).orElseGet(() -> 0f);
        return Optional.of(vat);
    };

    public void write(CompoundTag tag, BlockPos controllerPos) {
        tag.put("LowerCorner", NbtUtils.writeBlockPos(lowerCorner.subtract(controllerPos)));
        tag.put("UpperCorner", NbtUtils.writeBlockPos(upperCorner.subtract(controllerPos)));
        tag.put("LastKnownControllerPos", NbtUtils.writeBlockPos(controllerPos));
        tag.putFloat("Conductance", conductance);
        tag.put("WeakestBlock", NbtUtils.writeBlockState(weakestBlockState));
    };

    /**
     * Try and form a Vat.
     * @param level
     * @param pos The position of the first space in the Vat
     */
    public static Optional<Vat> tryConstruct(Level level, BlockPos pos, BlockPos controllerPos) {

        boolean successful = true;

        EnumMap<Direction, Integer> dimensions = new EnumMap<>(Direction.class) {
            @Override
            public Integer get(Object key) {
                Integer value = super.get(key);
                if (value == null) {
                    return 0;
                } else {
                    return value;
                }
            };
        };

        // Expand the dimenions of the vat in each direction as far as possible
        tryExpandInDirection: for (Direction direction : Direction.values()) {

            while (true) {

                // Don't expand more than a diameter of 5 blocks
                if (dimensions.get(direction) + dimensions.get(direction.getOpposite()) > 5) {
                    successful = false;
                    break tryExpandInDirection;
                };
                
                // Add a new one Block thick plane on and see if its all valid
                BlockPos newPlaneCentre = new BlockPos(pos).relative(direction, 1 + dimensions.get(direction));

                // We're defining the plane with two opposing corners
                BlockPos lowerCorner = new BlockPos(newPlaneCentre);
                BlockPos upperCorner = new BlockPos(newPlaneCentre);

                // Scale the plane perpendicular to the direction we're trying to expand in
                tryAddNewPlane: for (Direction secondaryDirection : Direction.values()) {
                    if (direction.getAxis() == secondaryDirection.getAxis()) continue tryAddNewPlane;
                    if (secondaryDirection.getAxisDirection() == AxisDirection.POSITIVE) {
                        upperCorner = upperCorner.relative(secondaryDirection, dimensions.get(secondaryDirection));
                    } else {
                        lowerCorner = lowerCorner.relative(secondaryDirection, dimensions.get(secondaryDirection));
                    };
                };

                // Now check all Blocks in the plane
                boolean allAir = true;
                boolean allWalls = true;
                for (BlockPos blockPos : BlockPos.betweenClosed(lowerCorner, upperCorner)) {
                    BlockState state = level.getBlockState(blockPos);
                    if (state.isAir()) {
                        allWalls = false;
                    } else {
                        allAir = false;
                        Block block = state.getBlock();
                        if (!VatMaterial.isValid(state) || (block instanceof VatControllerBlock && !blockPos.equals(controllerPos))) allWalls = false;
                    }
                };

                // If we have reached a boundary where everything is a wall
                if (allWalls) {
                    continue tryExpandInDirection;
                // If we have reached an illegal block
                } else if (!allAir) {
                    successful = false;
                    break tryExpandInDirection;
                };

                // Increment the value in this direction
                dimensions.merge(direction, 1, (i1, i2) -> i1 + 1);
            }
        };

        if (!successful) return Optional.empty();

        // Determine the precise location of the Vat, inflated by 1 in each direction so it includes the walls
        int topSide = pos.getY() + 1 + dimensions.get(Direction.UP);
        int northSide = pos.getZ() - 1 - dimensions.get(Direction.NORTH);
        int eastSide = pos.getX() + 1 + dimensions.get(Direction.EAST);
        int southSide = pos.getZ() + 1 + dimensions.get(Direction.SOUTH);
        int westSide = pos.getX() - 1 - dimensions.get(Direction.WEST);
        int bottomSide = pos.getY() - 1 - dimensions.get(Direction.DOWN);
        BlockPos lowerCorner = new BlockPos(westSide, bottomSide, northSide);
        BlockPos upperCorner = new BlockPos(eastSide, topSide, southSide);

        List<BlockPos> sides = new ArrayList<>();
        float maximumPressure = Float.MAX_VALUE;
        float conductance = 0f;
        BlockState weakestBlockState = Blocks.AIR.defaultBlockState();

        for (BlockPos blockPos : BlockPos.betweenClosed(lowerCorner, upperCorner)) {
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            // Check all blocks which form a face of the Vat, but aren't an edge or corner

            boolean onXSide = (x == eastSide || x == westSide); // A
            boolean onYSide = (y == topSide || y == bottomSide); // B
            boolean onZSide = (z == northSide || z == southSide); // C
            /*
             * Check all sides which are on a face, but are not an edge or corner.
             * A B C Output
             * 0 0 0 0
             * 0 0 1 1
             * 0 1 0 1
             * 0 1 1 0
             * 1 0 0 1
             * 1 0 1 0
             * 1 1 0 0
             * 1 1 1 0
             */
            if (((onXSide ^ onYSide) ^ onZSide) && !(onXSide && onYSide)) {
                BlockState state = level.getBlockState(blockPos);
                if (!VatMaterial.isValid(state)) {
                    successful = false;
                    break;
                };
                VatMaterial material = VatMaterial.getMaterial(state).get();
                if (material.maxPressure() < maximumPressure) {
                    maximumPressure = material.maxPressure();
                    weakestBlockState = state;
                };
                conductance += material.thermalConductivity(); // As area and width = 1, conductivity = conductance
                sides.add(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            };
        };

        if (successful) {
            Vat vat = new Vat(lowerCorner, upperCorner);
            vat.sides = ImmutableList.copyOf(sides);
            vat.maximumPressure = maximumPressure;
            vat.conductance = conductance;
            vat.weakestBlockState = weakestBlockState;
            return Optional.of(vat);
        } else {
            return Optional.empty();
        }
    };

    public int getCapacity() {
        return (upperCorner.getX() - getInternalLowerCorner().getX())
            * (getInternalHeight())
            * (upperCorner.getZ() - getInternalLowerCorner().getZ())
            * MB_PER_BLOCK;
    };

    public BlockPos getLowerCorner() {
        return lowerCorner;
    };

    public BlockPos getInternalLowerCorner() {
        return lowerCorner.above().east().south();
    };

    public BlockPos getUpperCorner() {
        return upperCorner;
    };

    public BlockPos getInternalUpperCorner() {
        return upperCorner.below().west().north();
    };

    public int getInternalHeight() {
        return upperCorner.getY() - getInternalLowerCorner().getY();
    };

    public int getInternalWidth() {
        return upperCorner.getX() - getInternalLowerCorner().getX();
    };

    public int getInternalLength() {
        return upperCorner.getZ() - getInternalLowerCorner().getZ();
    };

    public void transform(StructureTransform transform, BlockPos newControllerPos) {
        BlockPos controllerMotion = transform.unapply(newControllerPos).subtract(newControllerPos);
        upperCorner = transform.apply(upperCorner.offset(controllerMotion));
        lowerCorner = transform.apply(lowerCorner.offset(controllerMotion));
        BlockPos newUpper = new BlockPos(Math.max(upperCorner.getX(), lowerCorner.getX()), Math.max(upperCorner.getY(), lowerCorner.getY()), Math.max(upperCorner.getZ(), lowerCorner.getZ()));
        BlockPos newLower = new BlockPos(Math.min(upperCorner.getX(), lowerCorner.getX()), Math.min(upperCorner.getY(), lowerCorner.getY()), Math.min(upperCorner.getZ(), lowerCorner.getZ()));
        lowerCorner = newLower;
        upperCorner = newUpper;
        sides = null;
    };

    /**
     * Internal volume (in Blocks) of this Vat.
     * @return
     */
    public int getVolume() {
        return getInternalHeight() * getInternalWidth() * getInternalLength();
    };

    public Vec3 getCenter() {
        return VecHelper.getCenterOf(getInternalLowerCorner()).add(VecHelper.getCenterOf(getInternalUpperCorner())).scale(0.5d);
    };

    public Collection<BlockPos> getSideBlockPositions() {
        if (this.sides == null) {
            List<BlockPos> newSides = new ArrayList<>();
            for (BlockPos blockPos : BlockPos.betweenClosed(lowerCorner, upperCorner)) {
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                // Check all blocks which form a face of the Vat, but aren't an edge or corner
    
                boolean onXSide = (x == lowerCorner.getX() || x == upperCorner.getX());
                boolean onYSide = (y == lowerCorner.getY() || y == upperCorner.getY());
                boolean onZSide = (z == lowerCorner.getZ() || z == upperCorner.getZ());

                if (((onXSide ^ onYSide) ^ onZSide) && !(onXSide && onYSide)) {
                    newSides.add(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                };
            };
            sides = ImmutableList.copyOf(newSides);
        };
        return sides;
    };
    
    /**
     * The {@link Vat#maximumPressure weakest Block} in this Vat.
     */
    public BlockState getWeakestBlock() {
        return weakestBlockState;
    };

    /**
     * The {@link VatMaterial#maxPressure maximum pressure} of the weakest Block making up this Vat.
     */
    public float getMaxPressure() {
        return maximumPressure;
    };

    /**
     * The thermal conductance (in watts per kelvin) of the area of this Vat.
     */
    public float getConductance() {
        return conductance;
    };

    /**
     * Get the outward-facing Direction corresponding to the side of the Vat this Block Pos is on.
     * @param sideBlockPos
     * @return {@code null} if the Block Pos is not part of the Vat's sides
     */
    @Nullable
    public Direction whereIsSideFacing(BlockPos sideBlockPos) {
        if (sideBlockPos.getX() == getUpperCorner().getX()) return Direction.EAST;
        if (sideBlockPos.getX() == getLowerCorner().getX()) return Direction.WEST;
        if (sideBlockPos.getY() == getUpperCorner().getY()) return Direction.UP;
        if (sideBlockPos.getY() == getLowerCorner().getY()) return Direction.DOWN;
        if (sideBlockPos.getZ() == getUpperCorner().getZ()) return Direction.SOUTH;
        if (sideBlockPos.getZ() == getLowerCorner().getZ()) return Direction.NORTH;
        return null;
    };

    @OnlyIn(Dist.CLIENT)
    public void showBoundingBox() {
        OUTLINER.showAABB(Pair.of("outliner", lowerCorner), new AABB(lowerCorner, upperCorner), 100)
            .colored(0xFF_ebd31e);
    };
};
