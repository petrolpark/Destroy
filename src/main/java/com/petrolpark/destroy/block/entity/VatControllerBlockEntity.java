package com.petrolpark.destroy.block.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.entity.behaviour.WhenTargetedBehaviour;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.vat.Vat;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class VatControllerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    protected Optional<Vat> vat;

    protected SmartFluidTankBehaviour tankBehaviour;
    protected LazyOptional<IFluidHandler> fluidCapability;
    protected boolean full;

    protected WhenTargetedBehaviour targetedBehaviour;

    protected int initializationTicks;

    public VatControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        vat = Optional.empty();
        full = false;
        initializationTicks = 3;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        targetedBehaviour = new WhenTargetedBehaviour(this, this::onTargeted);
        behaviours.add(targetedBehaviour);

        tankBehaviour = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.TYPE, this, 1, 5000000, false)
            .whenFluidUpdates(this::onFluidStackChanged)
            .forbidExtraction() // Forbid extraction until the Vat is initialized
            .forbidInsertion(); // Forbid insertion no matter what
        fluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(tankBehaviour.getCapability().orElse(null));
        });
        behaviours.add(tankBehaviour);
    };

    @Override
    public void tick() {
        super.tick();

        if (initializationTicks > 0) {
            initializationTicks--;
        };
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        full = tag.getBoolean("Full");
        if (tag.contains("VatLowerCorner") && tag.contains("VatUpperCorner")) {
            vat = Optional.of(new Vat(NbtUtils.readBlockPos(tag.getCompound("VatLowerCorner")), NbtUtils.readBlockPos(tag.getCompound("VatUpperCorner"))));
        };
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Full", full);
        if (vat.isPresent()) {
            tag.put("VatLowerCorner", NbtUtils.writeBlockPos(vat.get().getLowerCorner()));
            tag.put("VatUpperCorner", NbtUtils.writeBlockPos(vat.get().getUpperCorner()));
        };
    };

    /**
     * Mix a new Fluid Stack into the Vat.
     * @param fluid If not containing a {@link com.petrolpark.destroy.chemistry.Mixture Mixture}, no Fluid will be inserted
     * @return The amount (in mB) of Fluid actually added
     */
    public int addFluid(FluidStack fluidStack) {
        // Don't mix in anything if there's no Vat
        if (!vat.isPresent()) return 0;
        // Don't mix in anything that's not a Mixture
        if (!DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) return 0;

        // If we need to, shrink the Fluid Stack we're trying to insert
        int remainingSpace = getTank().getSpace();
        int fluidAmountInserted = fluidStack.getAmount(); // Assume we insert the entire Fluid Stack
        FluidStack newFluidStack = fluidStack.copy();
        if (fluidStack.getAmount() > remainingSpace) {
            newFluidStack.setAmount(remainingSpace);
            fluidAmountInserted = remainingSpace; // Set the actual amount of Fluid inserted
        };

        // Create the new Mixture
        FluidStack existingFluid = getTank().getFluid();
        Mixture newMixture = Mixture.mix(Map.of(
            Mixture.readNBT(existingFluid.getOrCreateTag().getCompound("Mixture")), (double)existingFluid.getAmount(),
            Mixture.readNBT(newFluidStack.getOrCreateTag().getCompound("Mixture")), (double)newFluidStack.getAmount()
        ));

        // Replace the Fluid in the Tank
        tankBehaviour.allowInsertion();
        getTank().setFluid(MixtureFluid.of(existingFluid.getAmount() + newFluidStack.getAmount(), newMixture, null));
        tankBehaviour.forbidInsertion();

        return fluidAmountInserted;
    };

    private void onFluidStackChanged() {
        if (!vat.isPresent()) return;
        if (getTank().getFluidAmount() >= getVat().get().getCapacity()) {
            full = true;
        } else {
            full = false;
        };
        setChanged();
    };

    public Optional<Vat> getVat() {
        return vat;
    };

    /**
     * Try to make a {@link com.petrolpark.destroy.util.vat.Vat Vat} attached to this Vat Controller.
     */
    @SuppressWarnings("null")
    public void tryMakeVat() {
        if (!hasLevel()) return;

        // Create the Vat starting with the Block behind the Controller
        BlockPos vatInternalStartPos = new BlockPos(getBlockPos().relative(getLevel().getBlockState(getBlockPos()).getValue(VatControllerBlock.FACING).getOpposite()));
        Optional<Vat> newVat = Vat.tryConstruct(getLevel(), vatInternalStartPos);
        if (!newVat.isPresent()) return;

        // Once the Vat has been successfully created
        Collection<BlockPos> sides = newVat.get().getSideBlockPositions();
        sides.forEach(pos -> {
            getLevel().setBlockEntity(DestroyBlockEntityTypes.VAT_SIDE.create(pos, getLevel().getBlockState(pos)));
            getLevel().blockUpdated(pos, level.getBlockState(pos).getBlock());
        });

        vat = Optional.of(newVat.get());
        tankBehaviour.allowExtraction(); // Enable extraction from the Vat now it actually exists

        sendData();
        setChanged();
    };

    @Override
    public void destroy() {
        deleteVat();
        super.destroy();
    };

    @SuppressWarnings("null")
    public void deleteVat() {
        if (!vat.isPresent()) return;
        vat.get().getSideBlockPositions().forEach(pos -> {
            getLevel().removeBlockEntity(pos);
        });
        //TODO handle leftover fluid
    };

    // Nullable, just not annotated so VSC stops giving me ugly yellow lines
    public SmartFluidTank getTank() {
        return tankBehaviour.getPrimaryHandler();
    };

    public boolean isFull() {
        return full;
    };

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidCapability.invalidate();
    };

    /**
     * Height (in blocks) of the Fluid level above the internal base of the Vat.
     * This is server-side.
     * @see VatControllerBlockEntity#getRenderedFluidLevel Client-side Fluid level
     */
    public float getFluidLevel() {
        if (vat.isPresent()) {
            return (float)vat.get().getInternalHeight() * (float)getTank().getFluidAmount() / (float)getTank().getCapacity();
        } else {
            return 0f;
        }
    };

    /**
     * Height (in blocks) of the Fluid level above the internal base of the Vat.
     * This is client-side.
     * @see VatControllerBlockEntity#getFluidLevel Server-side Fluid level
     */
    public float getRenderedFluidLevel(float partialTicks) {
        if (vat.isPresent()) {
            return (float)vat.get().getInternalHeight() * (float)tankBehaviour.getPrimaryTank().getTotalUnits(partialTicks) / (float)getTank().getCapacity();
        } else {
            return 0f;
        }
    };

    private void onTargeted(LocalPlayer player, BlockHitResult blockHitResult) {
        if (vat.isPresent()) {
            CreateClient.OUTLINER.showAABB(Pair.of("vat", getBlockPos()), new AABB(vat.get().getInternalLowerCorner(), vat.get().getUpperCorner()).inflate(1d), 20)
                .colored(0xFF_fffec2);
        };
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getVat().isPresent()) {

        } else if (initializationTicks == 0) {
            DestroyLang.translate("tooltip.vat.not_initialized_1")
                .style(ChatFormatting.RED)
                .forGoggles(tooltip);
            DestroyLang.translate("tooltip.vat.not_initialized_2")
                .style(ChatFormatting.RED)
                .forGoggles(tooltip);
            DestroyLang.translate("tooltip.vat.not_initialized_3")
                .style(ChatFormatting.RED)
                .forGoggles(tooltip);
        };
        return true;
    };
    
};
