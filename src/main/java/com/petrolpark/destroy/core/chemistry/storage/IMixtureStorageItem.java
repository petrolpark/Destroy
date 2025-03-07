package com.petrolpark.destroy.core.chemistry.storage;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.petrolpark.destroy.chemistry.legacy.ClientMixture;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity.VatTankWrapper;
import com.petrolpark.destroy.core.fluid.GeniusFluidTankBehaviour.GeniusFluidTank;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity.CreativeSmartFluidTank;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

/**
 * An Item which can store Mixtures (I feel like you could have figured that one out yourself).
 * This inteface mainly provides a bunch of convenience methods common to most Items which can deal with Fluids.
 */
public interface IMixtureStorageItem {
    
    /**
     * Get the capacity this ItemStack should have. This can and will be called before and after the {@link net.minecraftforge.common.extensions.IForgeItem#initCapabilities Fluid Capability is initialized}.
     * @param stack
     */
    public int getCapacity(ItemStack stack);

    /**
     * Fill a stack of this Item from an external source. This is usually triggered by right-clicking with the Item on a Block.
     * @param stack The {@link IMixtureStorageItem} Stack we are trying to fill
     * @param itemTank The {@link ItemMixtureTank} owned by that ItemStack, already getted for convenience' sake
     * @param otherTank The tank we are trying to fill from, usually something capable of handling mixtures like a {@link GeniusFluidTank}
     * @param maxTransfer The maximum amount (in mB) of Mixture which should be transferred from the other tank to the {@link ItemMixtureTank}
     * @see IMixtureStorageItem#tryFill(ItemStack, ItemMixtureTank, IFluidHandler) Aiming to fill the Item completely
     */
    public default InteractionResult tryFill(ItemStack stack, ItemMixtureTank itemTank, IFluidHandler otherTank, int maxTransfer) {
        if (otherTank == null) return InteractionResult.PASS;
        for (boolean simulate : Iterate.trueAndFalse) {
            FluidStack drained = otherTank.drain(maxTransfer, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            if (drained.isEmpty()) return InteractionResult.FAIL;
            maxTransfer = itemTank.fill(drained, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            if (maxTransfer == 0) return InteractionResult.FAIL;
        };
        return InteractionResult.SUCCESS;
    };

    /**
     * Try to completely fill a stack of this Item from an external source. This is usually triggered by right-clicking with the Item on a Block.
     * @param stack The {@link IMixtureStorageItem} Stack we are trying to fill
     * @param itemTank The {@link ItemMixtureTank} owned by that ItemStack, already getted for convenience' sake
     * @param otherTank The tank we are trying to fill from, usually something capable of handling mixtures like a {@link GeniusFluidTank}
     * @see IMixtureStorageItem#tryFill(ItemStack, ItemMixtureTank, IFluidHandler, int) Filling a specific amount
     */
    public default InteractionResult tryFill(ItemStack stack, ItemMixtureTank itemTank, IFluidHandler otherTank) {
        return tryFill(stack, itemTank, otherTank, itemTank.getRemainingSpace());
    };

    /**
     * Empty this Item into an external source. This is usually triggered by left-clicking with the Item on a Block.
     * @param itemStack The {@link IMixtureStorageItem} Stack we are trying to empty
     * @param itemTank The {@link ItemMixtureTank} owned by that ItemStack, already getted for convenience' sake
     * @param otherTank The tank into which we are trying to empty, usually something capable of handling mixtures like a {@link GeniusFluidTank}
     * @param infiniteFluid Whether to actually empty this Item, or to keep it (typically because we are in Creative)
     * @param maxTransfer The maximum amount (in mB) of Mixture which should be transferred from the {@link ItemMixtureTank} to the other tank
     * @see IMixtureStorageItem#tryEmpty(ItemStack, ItemMixtureTank, IFluidHandler, boolean) Trying to fully empty the Item
     */
    public default InteractionResult tryEmpty(ItemStack stack, ItemMixtureTank itemTank, IFluidHandler otherTank, boolean infiniteFluid, int maxTransfer) {
        if (otherTank == null) return InteractionResult.PASS;
        for (boolean simulate : Iterate.trueAndFalse) {
            FluidStack drained = itemTank.drain(maxTransfer, simulate || infiniteFluid ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            if (drained.isEmpty()) return InteractionResult.FAIL;
            if (otherTank instanceof CreativeSmartFluidTank creativeTank) {
                creativeTank.setContainedFluid(drained);
                return InteractionResult.SUCCESS;
            };
            maxTransfer = otherTank.fill(drained, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            if (maxTransfer == 0) return InteractionResult.FAIL;
        };
        return InteractionResult.SUCCESS;
    };

    /**
     * Try to completely empty this Item into an external source. This is usually triggered by left-clicking with the Item on a Block.
     * @param itemStack The {@link IMixtureStorageItem} Stack we are trying to empty
     * @param itemTank The {@link ItemMixtureTank} owned by that ItemStack, already getted for convenience' sake
     * @param otherTank The tank into which we are trying to empty, usually something capable of handling mixtures like a {@link GeniusFluidTank}
     * @param infiniteFluid Whether to actually empty this Item, or to keep it (typically because we are in Creative)
     * @see IMixtureStorageItem#tryEmpty(ItemStack, ItemMixtureTank, IFluidHandler, boolean, int) Emptying a specific amount
     */
    public default InteractionResult tryEmpty(ItemStack stack, ItemMixtureTank itemTank, IFluidHandler otherTank, boolean infiniteFluid) {
        return tryEmpty(stack, itemTank, otherTank, infiniteFluid, itemTank.getFluidAmount());
    };

    /**
     * Find and select an {@link IFluidHandler} when clicking on a Block. We can then use this tank to empty or fill.
     * @param level
     * @param pos
     * @param state
     * @param face The face of the Block which was clicked
     * @param player
     * @param hand
     * @param stack
     * @param filling {@code true} for a filling the other tank, emptying the Item and {@code false} for a emptying the Item, filling the other tank
     */
    @Nullable
    public default IFluidHandler getTank(Level level, BlockPos pos, BlockState state, @Nullable Direction face, Player player, InteractionHand hand, ItemStack stack, boolean filling) {
        IFluidHandler fluidHandler;
        if (state.getBlock() instanceof ISpecialMixtureContainerBlock specialBlock) fluidHandler = specialBlock.getTankForMixtureStorageItems(this, level, pos, state, face, player, hand, stack, filling);
        else {
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null) return null;
            LazyOptional<IFluidHandler> fluidCap = be.getCapability(ForgeCapabilities.FLUID_HANDLER, face);
            if (!fluidCap.isPresent()) return null;
            fluidHandler = fluidCap.resolve().get(); 
        };
        return fluidHandler;
    };

    /**
     * Sub-method of {@link IMixtureStorageItem#getTank(UseOnContext, boolean)} for selecting how to insert/extract Fluids from the Vat, if we've found one.
     */
    @Nullable
    public default VatTankWrapper selectVatTank(Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack, boolean fillingVat, VatControllerBlockEntity vatController) {
        return new SinglePhaseVatExtraction(vatController, false);
    };

    /**
     * The typical behaviour for right-clicking with a Mixture container on a Block (emptying the Item into the Block).
     * @param context
     */
    public static InteractionResult defaultUseOn(IMixtureStorageItem item, UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        InteractionResult result = item.tryEmpty(context.getItemInHand(), (ItemMixtureTank)context.getItemInHand().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().get(), item.getTank(context.getLevel(), context.getClickedPos(), state, context.getClickedFace(), context.getPlayer(), context.getHand(), context.getItemInHand(), true), context.getPlayer().isCreative());
        item.afterEmpty(context.getLevel(), context.getClickedPos(), state, context.getClickedFace(), context.getPlayer(), context.getHand(), context.getItemInHand(), result);
        return result;
    };

    public default void afterEmpty(Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack, InteractionResult result) {
        if (result == InteractionResult.SUCCESS) level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS);
    };

    public default InteractionResult attack(Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack) {
        return defaultAttack(this, level, pos, state, face, player, hand, stack);
    };

    /**
     * The typical behaviour for left-clicking with a Mixture container on a Block (filling the Item from the Block).
     */
    public static InteractionResult defaultAttack(IMixtureStorageItem item, Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack) {
        InteractionResult result = item.tryFill(stack, (ItemMixtureTank)stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().get(), item.getTank(level, pos, state, face, player, hand, stack, false));
        item.afterFill(level, pos, state, face, player, hand, stack, result);
        return result;
    };

    public static boolean isHolding(Player player, InteractionHand hand) {
        return player.getItemInHand(hand).getItem() instanceof IMixtureStorageItem;
    };

    public default void afterFill(Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack, InteractionResult result) {
        if (result == InteractionResult.SUCCESS) level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS);
    };

    public default boolean isEmpty(ItemStack stack)  {
        return getContents(stack).map(FluidStack::isEmpty).orElse(true);  
    };

    public default int getColor(ItemStack stack) {
        return getContents(stack).map(MixtureFluid::getTintColor).orElse(0xFFFFFFFF);
    };

    public default Optional<FluidStack> getContents(ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(tanks -> tanks.drain(getCapacity(itemStack), FluidAction.SIMULATE));
    };

    public default void setContents(ItemStack itemStack, FluidStack fluidStack) {
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(tanks -> tanks.fill(fluidStack, FluidAction.EXECUTE));
    };

    public Component getNameRegardlessOfFluid(ItemStack stack);

    public default Component getNameWithFluid(ItemStack stack) {
        FluidStack contents = getContents(stack).orElse(FluidStack.EMPTY);
        if (contents.isEmpty()) return Component.translatable(stack.getDescriptionId());
        return Component.translatable(stack.getDescriptionId() + ".filled", contents.getDisplayName());
    };

    static final DecimalFormat df = new DecimalFormat();

    static class DF {
        static {
            df.setMinimumFractionDigits(1);
            df.setMaximumFractionDigits(1);
        };
    };

    public default void addContentsDescription(ItemStack stack, List<Component> tooltip) {
        getContents(stack).ifPresent(fluidStack -> {

            if (fluidStack.isEmpty()) return;

            float temperature = 289f;

            tooltip.add(Component.literal(""));
        
            CompoundTag mixtureTag = fluidStack.getOrCreateTag().getCompound("Mixture");
            if (!mixtureTag.isEmpty()) { // If this is a Mixture
                ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ClientMixture::new, mixtureTag);

                boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();
                temperature = mixture.getTemperature();
                tooltip.addAll(mixture.getContentsTooltip(iupac, false, false, fluidStack.getAmount(), df).stream().map(c -> c.copy()).toList());
            };

            tooltip.add(2, Component.literal(" "+fluidStack.getAmount()).withStyle(ChatFormatting.GRAY).append(DestroyLang.translateDirect("generic.unit.millibuckets")).append(" "+DestroyAllConfigs.CLIENT.chemistry.temperatureUnit.get().of(temperature, df)));
        });
    };

    public static class SinglePhaseVatExtraction extends VatTankWrapper {

        public final boolean gas;

        public SinglePhaseVatExtraction(VatControllerBlockEntity vatController, boolean gas) {
            super(() -> vatController, vatController::addFluid);
            this.gas = gas;
        };

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return gas ? drainGasTank(resource, action) : drainLiquidTank(resource, action);
        };

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return gas ? drainGasTank(maxDrain, action) : drainLiquidTank(maxDrain, action);
        };

    };  
};
