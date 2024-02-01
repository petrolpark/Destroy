package com.petrolpark.destroy.item;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.VatSideBlock;
import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.chemistry.ClientMixture;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.item.renderer.ILayerTintsWithAlphaItem;
import com.petrolpark.destroy.util.ChemistryDamageHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class TestTubeItem extends ItemFluidContainer implements ILayerTintsWithAlphaItem {

    public static final int CAPACITY = 200;
    private static final DecimalFormat df = new DecimalFormat();

    static {
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    };

    public TestTubeItem(Properties properties) {
        super(properties, CAPACITY);
    };

    public static ItemStack of(FluidStack fluidStack) {
        ItemStack stack = DestroyItems.TEST_TUBE.asStack(1);
        setContents(stack, fluidStack);
        return stack;
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        Optional<FluidStack> contentsOptional = getContents(stack);
        if (entity instanceof LivingEntity livingEntity && contentsOptional.isPresent()) {
            ChemistryDamageHelper.damage(level, livingEntity, contentsOptional.get(), false);
        };
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        VatControllerBlockEntity vatController = null;
        if (state.getBlock() instanceof VatControllerBlock) {
            vatController = level.getBlockEntity(pos, DestroyBlockEntityTypes.VAT_CONTROLLER.get()).orElse(null);
        } else if (state.getBlock() instanceof VatSideBlock) {
            vatController = level.getBlockEntity(pos, DestroyBlockEntityTypes.VAT_SIDE.get()).map(VatSideBlockEntity::getController).orElse(null);
        };
        if (vatController != null) { // If there is a Vat to insert/withdraw from
            if (isEmpty(context.getItemInHand())) { // Draining the Vat
                for (boolean simulate : Iterate.trueAndFalse) {
                    FluidStack drained = vatController.getLiquidTank().drain(200, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
                    if (drained.isEmpty()) return InteractionResult.FAIL;
                    if (!simulate) {
                        vatController.updateCachedMixture();
                        vatController.updateGasVolume();
                        player.setItemInHand(context.getHand(), of(drained));
                    };
                };
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else { // Filling the Vat
                FluidStack fs = getContents(context.getItemInHand()).orElse(FluidStack.EMPTY).copy();
                if (fs.isEmpty()) return InteractionResult.FAIL;
                for (boolean simulate : Iterate.trueAndFalse) {
                    int filled = vatController.addFluid(fs, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
                    if (filled == 0) return InteractionResult.FAIL;
                    if (!simulate && !player.isCreative()) {
                        fs.setAmount(fs.getAmount() - filled);
                        player.setItemInHand(context.getHand(), of(fs));
                    };
                };
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        };
        return InteractionResult.PASS;
    };

    @Override
    public Component getName(ItemStack itemStack) {
        if (isEmpty(itemStack)) return Component.translatable("item.destroy.test_tube.empty");
        return Component.translatable("item.destroy.test_tube.filled", getContents(itemStack).get().getDisplayName());
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        if (!DestroyItems.TEST_TUBE.isIn(stack)) return;
        getContents(stack).ifPresent(fluidStack -> {

            if (fluidStack.isEmpty()) return;

            String temperature = "";

            tooltip.add(Component.literal(""));
        
            CompoundTag mixtureTag = fluidStack.getOrCreateTag().getCompound("Mixture");
            if (!mixtureTag.isEmpty()) { // If this is a Mixture
                ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ClientMixture::new, mixtureTag);

                boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();
                temperature = df.format(mixture.getTemperature());
                tooltip.addAll(mixture.getContentsTooltip(iupac, false, false, fluidStack.getAmount(), df).stream().map(c -> c.copy()).toList());
            };

            tooltip.add(2, Component.literal(" "+fluidStack.getAmount()).withStyle(ChatFormatting.GRAY).append(Lang.translateDirect("generic.unit.millibuckets")).append(" "+temperature+"K"));
        });
    };

    public static boolean isEmpty(ItemStack stack)  {
        return getContents(stack).map(FluidStack::isEmpty).orElse(true);  
    };

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return getContents(stack).map(MixtureFluid::getTintColor).orElse(0xFFFFFFFF);
        } else {
            return 0xFFFFFFFF;
        }
    };

    public static Optional<FluidStack> getContents(ItemStack itemStack) {
        return itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(tanks -> tanks.drain(CAPACITY, FluidAction.SIMULATE));
    };

    public static void setContents(ItemStack itemStack, FluidStack fluidStack) {
        itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(tanks -> tanks.fill(fluidStack, FluidAction.EXECUTE));
    };
};
