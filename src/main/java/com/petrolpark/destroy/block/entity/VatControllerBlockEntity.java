package com.petrolpark.destroy.block.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.entity.behaviour.WhenTargetedBehaviour;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
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
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class VatControllerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    protected Optional<Vat> vat;

    protected Mixture cachedMixture;
    protected SmartFluidTankBehaviour tankBehaviour;
    protected LazyOptional<IFluidHandler> fluidCapability;
    protected boolean full;

    protected WhenTargetedBehaviour targetedBehaviour;

    protected int initializationTicks;
    /**
     * Whether the {@link com.petrolpark.destroy.util.vat.Vat Vat} associated with this Vat Controller is already under the process of being deleted.
     */
    protected boolean underDeconstruction;

    public VatControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        vat = Optional.empty();
        full = false;
        initializationTicks = 3;
        underDeconstruction = false;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        targetedBehaviour = new WhenTargetedBehaviour(this, this::onTargeted);
        behaviours.add(targetedBehaviour);

        tankBehaviour = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.TYPE, this, 1, 1000000, false) // Tank capacity is set very high but is not this high in effect
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
        if (tag.contains("Vat", Tag.TAG_COMPOUND)) {
            vat = Vat.read(tag.getCompound("Vat"));
        } else {
            vat = Optional.empty();
        };
        underDeconstruction = tag.getBoolean("UnderDeconstruction");
        updateCachedMixture();
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Full", full);
        if (vat.isPresent()) {
            CompoundTag vatTag = new CompoundTag();
            vat.get().write(vatTag);
            tag.put("Vat", vatTag);
        };
        tag.putBoolean("UnderDeconstruction", underDeconstruction);
    };

    /**
     * Mix a new Fluid Stack into the Vat.
     * @param fluid If not containing a {@link com.petrolpark.destroy.chemistry.Mixture Mixture}, no Fluid will be inserted
     * @return The amount (in mB) of Fluid actually added
     */
    public int addFluid(FluidStack fluidStack) {
        // Don't mix in anything if there's no Vat
        if (!vat.isPresent() || full) return 0;
        // Don't mix in anything that's not a Mixture
        if (!DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid())) return 0;

        // If we need to, shrink the Fluid Stack we're trying to insert
        int remainingSpace = getCapacity() - getTank().getFluidAmount();
        int fluidAmountInserted = fluidStack.getAmount(); // Assume we insert the entire Fluid Stack
        FluidStack newFluidStack = fluidStack.copy();
        if (fluidStack.getAmount() > remainingSpace) {
            newFluidStack.setAmount(remainingSpace);
            fluidAmountInserted = remainingSpace; // Set the actual amount of Fluid inserted
            full = true;
        };

        FluidStack existingFluid = getTank().getFluid();
        Mixture newMixture;

        if (getTank().isEmpty()) { // If this is the first Mixture to be added to the Tank
            // Set the 'new' Mixture to the one in this Fluid Stack
            newMixture = Mixture.readNBT(newFluidStack.getOrCreateTag().getCompound("Mixture"));
        } else { // If we're mixing in new Fluid
            // Create the new Mixture
            newMixture = Mixture.mix(Map.of(
                Mixture.readNBT(existingFluid.getOrCreateTag().getCompound("Mixture")), (double)existingFluid.getAmount(),
                Mixture.readNBT(newFluidStack.getOrCreateTag().getCompound("Mixture")), (double)newFluidStack.getAmount()
            ));
        };

        // Replace the Fluid in the Tank
        tankBehaviour.allowInsertion();
        getTank().setFluid(MixtureFluid.of(existingFluid.getAmount() + newFluidStack.getAmount(), newMixture, null));
        tankBehaviour.forbidInsertion();

        updateCachedMixture();

        return fluidAmountInserted;
    };

    private void onFluidStackChanged() {
        if (!vat.isPresent()) return;
        if (getTank().getFluidAmount() >= getVatOptional().get().getCapacity()) {
            full = true;
        } else {
            full = false;
        };
        updateCachedMixture();
        setChanged();
    };

    public Optional<Vat> getVatOptional() {
        return vat;
    };

    public int getCapacity() {
        if (vat.isEmpty()) return 0;
        return vat.get().getCapacity();
    };

    private void updateCachedMixture() {
        Mixture emptyMixture = new Mixture();
        if (!getVatOptional().isPresent()) {
            cachedMixture = emptyMixture;
            return;
        };
        FluidStack containedFluid = getTank().getFluid();
        if (containedFluid.isEmpty() || !containedFluid.getOrCreateTag().contains("Mixture")) {
            cachedMixture = emptyMixture;
            return;
        };
        cachedMixture = Mixture.readNBT(getTank().getFluid().getOrCreateTag().getCompound("Mixture"));
    };

    /**
     * Try to make a {@link com.petrolpark.destroy.util.vat.Vat Vat} attached to this Vat Controller.
     */
    @SuppressWarnings("null")
    public boolean tryMakeVat() {
        if (!hasLevel() || getLevel().isClientSide()) return false;

        // Create the Vat starting with the Block behind the Controller
        BlockPos vatInternalStartPos = new BlockPos(getBlockPos().relative(getLevel().getBlockState(getBlockPos()).getValue(VatControllerBlock.FACING).getOpposite()));
        Optional<Vat> newVat = Vat.tryConstruct(getLevel(), vatInternalStartPos);
        if (!newVat.isPresent()) return false;

        // Once the Vat has been successfully created...
        Collection<BlockPos> sides = newVat.get().getSideBlockPositions();
        // For each Block which makes up a side of this Vat...
        sides.forEach(pos -> {
            BlockState oldState = getLevel().getBlockState(pos);
            if (oldState.is(DestroyBlocks.VAT_CONTROLLER.get())) return;
            // ...replace it with a Vat Side Block which imitates the Block it's replacing
            getLevel().setBlockAndUpdate(pos, DestroyBlocks.VAT_SIDE.getDefaultState());
            getLevel().getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).ifPresent(vatSide -> {
                // Configure the Vat Side
                vatSide.setMaterial(oldState);
                vatSide.setConsumedItem(new ItemStack(oldState.getBlock().asItem())); // Required to co-operate with the Copycat Block's internals
                vatSide.controllerPosition = getBlockPos();
                vatSide.direction = newVat.get().whereIsSideFacing(pos);
                vatSide.updateDisplayType(pos.relative(vatSide.direction));
                vatSide.notifyUpdate();
            });
        });

        vat = Optional.of(newVat.get());
        tankBehaviour.allowExtraction(); // Enable extraction from the Vat now it actually exists

        notifyUpdate();
        return true;
    };

    @Override
    public void destroy() {
        deleteVat(getBlockPos());
        super.destroy();
    };

    @SuppressWarnings("null")
    public void deleteVat(BlockPos posDestroyed) {
        if (underDeconstruction || getLevel().isClientSide()) return;
        underDeconstruction = true;

        getTank().drain(getTank().getCapacity(), FluidAction.EXECUTE);

        tankBehaviour.forbidExtraction(); // Forbid Fluid extraction now this Vat no longer exists
        if (!vat.isPresent()) return;
        vat.get().getSideBlockPositions().forEach(pos -> {
            if (!pos.equals(posDestroyed)) {
                getLevel().getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).ifPresent(vatSide -> {
                    BlockState newState = vatSide.getMaterial();
                    getLevel().removeBlock(pos, false);
                    getLevel().setBlockAndUpdate(pos, newState);
                });
            };
        });

        vat = Optional.empty();
        underDeconstruction = false;
        notifyUpdate();
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
            return (float)vat.get().getInternalHeight() * (float)getTank().getFluidAmount() / (float)getCapacity();
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
            return (float)vat.get().getInternalHeight() * (float)tankBehaviour.getPrimaryTank().getTotalUnits(partialTicks) / (float)getCapacity();
        } else {
            return 0f;
        }
    };

    public float getTemperature() {
        if (getVatOptional().isEmpty() || cachedMixture == null) return LevelPollution.getLocalTemperature(getLevel(), getBlockPos());
        return cachedMixture.getTemperature();
    };

    public float getPressure() {
        if (!getVatOptional().isPresent()) return 0f;
        float moles = 1f; // TODO calculate moles of gas
        return moles * Reaction.GAS_CONSTANT * cachedMixture.getTemperature() / vat.get().getVolume();
    };

    /**
     * Get the pressure (above air pressure) of gas in this Vat as a proportion of the {@link com.petrolpark.destroy.util.vat.Vat#getMaxPressure maximum pressure}
     * the Vat can withstand.
     */
    public float getPercentagePressure() {
        if (!getVatOptional().isPresent()) return 0f;
        Vat vat = getVatOptional().get();
        return getPressure() / vat.getMaxPressure();
    };

    private void onTargeted(LocalPlayer player, BlockHitResult blockHitResult) {
        if (vat.isPresent()) {
            CreateClient.OUTLINER.showAABB(Pair.of("vat", getBlockPos()), new AABB(vat.get().getInternalLowerCorner(), vat.get().getUpperCorner()).inflate(1d), 20)
                .colored(0xFF_fffec2);
        };
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getVatOptional().isPresent()) {

        } else if (initializationTicks == 0) {
            TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.vat.not_initialized").string(), TooltipHelper.Palette.RED).forEach(component -> {
                DestroyLang.builder().add(component.copy()).forGoggles(tooltip);
            });
        };
        return true;
    };
    
};
