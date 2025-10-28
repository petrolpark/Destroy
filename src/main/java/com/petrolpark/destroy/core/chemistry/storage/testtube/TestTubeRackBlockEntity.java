package com.petrolpark.destroy.core.chemistry.storage.testtube;

import static com.petrolpark.compat.create.CreateClient.OUTLINER;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.DestroyTags.Items;
import com.petrolpark.destroy.core.block.entity.ISpecialWhenHoveredBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Pair;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class TestTubeRackBlockEntity extends SmartBlockEntity implements ISpecialWhenHoveredBlockEntity {

    public TestTubeRackInventory inv;
    private LazyOptional<TestTubeRackInventory> lazyItemHandler;

    public TestTubeRackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inv = new TestTubeRackInventory();
        lazyItemHandler = LazyOptional.empty();
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        lazyItemHandler = LazyOptional.of(() -> inv);
    };

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        lazyItemHandler.invalidate();
    };

    @Override
    public void tick() {
        super.tick();
        sendData();
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        inv = new TestTubeRackInventory();
        inv.deserializeNBT(tag.getCompound("Inventory"));
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("Inventory", inv.serializeNBT());
    };
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void whenLookedAt(LocalPlayer player, BlockHitResult result) {
        int tube = TestTubeRackBlock.getTargetedTube(getBlockState(), getBlockPos(), player);
        if (tube == -1) return;
        if (inv.isItemValid(tube, player.getItemInHand(InteractionHand.MAIN_HAND)) || !inv.getStackInSlot(tube).isEmpty()) OUTLINER.showAABB(Pair.of("test_tube_rack_" + tube, getBlockPos()), TestTubeRackBlock.getTubeBox(getBlockState(), getBlockPos(), tube), 1)
            .lineWidth(1 / 64f)
            .colored(0xFF7F7F7F);
    };

    public class TestTubeRackInventory extends ItemStackHandler {

        public TestTubeRackInventory() {
            super(4);
        };

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        };

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Items.TEST_TUBE_RACK_STORABLE.tag);
        };

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            notifyUpdate();
        };
    };
    
};
