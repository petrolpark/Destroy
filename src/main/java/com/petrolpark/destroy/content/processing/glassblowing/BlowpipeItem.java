package com.petrolpark.destroy.content.processing.glassblowing;

import java.util.List;
import java.util.function.Consumer;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.ScreenOpener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;
import com.petrolpark.destroy.core.chemistry.hazard.ChemistryHazardHelper;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.CustomArmPoseItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.DistExecutor;

public class BlowpipeItem extends BlockItem implements CustomArmPoseItem {

    public static final int TIME_TO_MOVE_TO_MOUTH = 10;

    public BlowpipeItem(BlowpipeBlock block, Properties properties) {
        super(block, properties.stacksTo(1));
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        int progress = tag.getInt("Progress");

        GlassblowingRecipe recipe = BlowpipeBlockEntity.readRecipe(level, tag);

        // Choosing a recipe
        if (!(player instanceof FakePlayer) && ((player.isShiftKeyDown() && progress == 0) || recipe == null)) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(hand));
            return InteractionResultHolder.success(stack);
        };

        // Default behaviour
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        
        // Continuing blowing
        FluidTank tank = new FluidTank(BlowpipeBlockEntity.TANK_CAPACITY);
        tank.readFromNBT(tag.getCompound("Tank"));
        if (result.getResult() == InteractionResult.PASS && recipe != null && !tank.isEmpty()) { // Only begin if we have Fluid - the only Fluid we can have is the right one for the recipe

            if (progress > BlowpipeBlockEntity.BLOWING_DURATION) { // If done
                return InteractionResultHolder.pass(stack); // Nothing left to do
            } else if (progress / BlowpipeBlockEntity.BLOWING_DURATION > BlowpipeBlockEntity.BLOWING_TIME_PROPORTION) { // If waiting to cool
                return InteractionResultHolder.pass(stack); // Wait
            } else { // If still blowing
                if (ChemistryHazardHelper.Protection.MOUTH_COVERED.isProtected(player)) { // Check if the mouth is covered
                    player.displayClientMessage(DestroyLang.translate("tooltip.eating_prevented.mouth_protected").component(), true);
                } else {
                    player.startUsingItem(hand);
                };
                return InteractionResultHolder.fail(stack);
            }
        };
        return result;
    };

    public static FluidIngredient getFluidIngredient(CompoundTag tag) {
        if (!tag.contains("RequiredFluid", Tag.TAG_STRING)) return null;
        return FluidIngredient.deserialize(GsonHelper.parse(tag.getString("RequiredFluid"), true));  
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();
        FluidIngredient ingredient = getFluidIngredient(tag);
        if (ingredient != null) {
            BlockPos pos = context.getClickedPos();
            Direction face = context.getClickedFace();
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                FluidTank blowpipeTank = new FluidTank(BlowpipeBlockEntity.TANK_CAPACITY);
                blowpipeTank.readFromNBT(tag.getCompound("Tank"));
                if (blowpipeTank.isEmpty()) {
                    LazyOptional<IFluidHandler> cap = be.getCapability(ForgeCapabilities.FLUID_HANDLER, face);
                    if (cap.isPresent()) {
                        IFluidHandler fh = cap.resolve().get();
                        for (boolean simulate : Iterate.trueAndFalse) {
                            FluidStack drained = fh.drain(ingredient.getRequiredAmount(), simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
                            if (!ingredient.test(drained) || drained.getAmount() < ingredient.getRequiredAmount()) return InteractionResult.FAIL;
                            if (!simulate) {
                                blowpipeTank.fill(drained, FluidAction.EXECUTE);
                                tag.put("Tank", blowpipeTank.writeToNBT(new CompoundTag()));
                                return InteractionResult.SUCCESS;
                            };
                        };
                    };
                };
            };
        };
        if (context.getPlayer() instanceof DeployerFakePlayer deployer) {
            if (BlowpipeBlock.getDeployerPlacer(level, deployer) == null) return InteractionResult.FAIL; // If on a contraption, don't place
        };
        return super.useOn(context);
    };

    public boolean finishBlowing(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt("Progress") < BlowpipeBlockEntity.BLOWING_DURATION) return false;
        tag.put("Tank", new FluidTank(1000).writeToNBT(new CompoundTag())); // Empty the Tank
        tag.putInt("Progress", 0); // Reset progress
        tag.putInt("LastProgress", 0);
        GlassblowingRecipe recipe = BlowpipeBlockEntity.readRecipe(level, tag);
        List<ItemStack> results = recipe.rollResults();
        level.playSound(null, player.getOnPos(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS);
        results.forEach(s -> player.getInventory().placeItemBackInInventory(s)); // Grant recipe results
        return true;
    };

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        CompoundTag tag = stack.getOrCreateTag();
        FluidTank tank = new FluidTank(1000);
        tank.readFromNBT(tag.getCompound("Tank"));
        if (!tank.isEmpty() && (float)stack.getOrCreateTag().getInt("Progress") / (float)BlowpipeBlockEntity.BLOWING_DURATION < BlowpipeBlockEntity.BLOWING_TIME_PROPORTION) {
            entity.setSecondsOnFire(3);
        };
        return false;
    };

    @OnlyIn(Dist.CLIENT)
    public void openScreen(InteractionHand hand) {
        ScreenOpener.open(new BlowpipeScreen(hand));
    };

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        return IPickUpPutDownBlock.removeItemFromInventory(pContext, super.place(pContext));
    };

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || newStack.getItem() != oldStack.getItem();
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        CompoundTag tag = stack.getOrCreateTag();
        if (BlowpipeBlockEntity.readRecipe(level, tag) == null) {
            tag.putInt("Progress", 0);
            tag.putInt("LastProgress", 0);
            tag.putBoolean("Blowing", false);
            return;
        };

        int progress = tag.getInt("Progress");
        tag.putInt("LastProgress", progress);

        boolean increaseProgress = (float)progress / (float)BlowpipeBlockEntity.BLOWING_DURATION > BlowpipeBlockEntity.BLOWING_TIME_PROPORTION; // If we're at the cooling stage, progress increases even if not actively blowing

        if (entity instanceof Player player && player.isUsingItem() && isSelected) {
            int ticksUsing = player.getTicksUsingItem();
            if (ticksUsing > TIME_TO_MOVE_TO_MOUTH) {
                increaseProgress = true;
                player.setAirSupply(player.getAirSupply() - 10);
            };
            tag.putBoolean("Blowing", true);
        } else {
            tag.putBoolean("Blowing", false);
        };

        if (increaseProgress && progress < BlowpipeBlockEntity.BLOWING_DURATION) {
            tag.putInt("Progress", progress + 1);
            if(progress < BlowpipeBlockEntity.BLOWING_DURATION && progress + 1 >= BlowpipeBlockEntity.BLOWING_DURATION && entity instanceof Player player) {
                DestroyAdvancementTrigger.BLOWPIPE.award(level, player);
            }
        }
    };

    @Override
    public @Nullable ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        if (!player.swinging && stack.getOrCreateTag().getBoolean("Blowing")) return ArmPose.SPYGLASS;
        return null;
    };

    @Override
    public int getUseDuration(ItemStack stack) {
        return BlowpipeBlockEntity.BLOWING_DURATION + TIME_TO_MOVE_TO_MOUTH - stack.getOrCreateTag().getInt("Progress");
    };

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt("Progress") == BlowpipeBlockEntity.BLOWING_DURATION) {
            tag.putInt("Progress", 0);
            tag.putInt("LastProgress", 0);
            tag.put("Tank", new FluidTank(BlowpipeBlockEntity.TANK_CAPACITY).writeToNBT(new CompoundTag())); // Empty the Tank
        };
        return stack;
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new BlowpipeItemRenderer()));
    };

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BlowpipeSpoutFillingFluidHandler(stack);
    };

    /**
     * Fluid Handler capability which only exists to allow Blowpipes to be filled by Spouts.
     */
    protected static class BlowpipeSpoutFillingFluidHandler implements IFluidHandlerItem, ICapabilityProvider {

        protected final ItemStack stack;

        public BlowpipeSpoutFillingFluidHandler(ItemStack stack) {
            this.stack = stack;
        };
        
        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap != ForgeCapabilities.FLUID_HANDLER_ITEM) return LazyOptional.empty();
            return LazyOptional.of(() -> this).cast();
        };

        @Override
        public int getTanks() {
            return 1;
        };

        @Override
        public @NotNull FluidStack getFluidInTank(int tankNo) {
            FluidTank tank = new FluidTank(getTankCapacity(0));
            tank.readFromNBT(stack.getOrCreateTag().getCompound("Tank"));
            return tank.getFluid();
        };

        @Override
        public int getTankCapacity(int tank) {
            return BlowpipeBlockEntity.TANK_CAPACITY;
        };

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return true;
        };

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            CompoundTag tag = stack.getOrCreateTag();
            FluidIngredient ingredient = getFluidIngredient(tag);
            if (ingredient != null && ingredient.test(resource) && resource.getAmount() >= ingredient.getRequiredAmount()) {
                FluidTank tank = new FluidTank(getTankCapacity(0));
                tank.readFromNBT(tag.getCompound("Tank"));
                if (!tank.isEmpty()) return 0;
                if (action.execute()) {
                    tank.fill(resource, FluidAction.EXECUTE);
                    tag.put("Tank", tank.writeToNBT(new CompoundTag()));
                };
                return ingredient.getRequiredAmount();
            };
            return 0;
        };

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        };

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        };

        @Override
        public @NotNull ItemStack getContainer() {
            return stack;
        };

    };
    
};
