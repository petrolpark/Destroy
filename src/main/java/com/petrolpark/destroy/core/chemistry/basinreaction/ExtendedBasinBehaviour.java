package com.petrolpark.destroy.core.chemistry.basinreaction;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.ReactionResult;
import com.petrolpark.destroy.core.pollution.PollutionHelper;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Destroy.MOD_ID)
public class ExtendedBasinBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<ExtendedBasinBehaviour> TYPE = new BehaviourType<>();

    public boolean tooFullToReact;
    private Map<ReactionResult, Integer> reactionResults;
    public FluidStack evaporatedFluid;

    public ExtendedBasinBehaviour(SmartBlockEntity be) {
        super(be);
        tooFullToReact = false;
        reactionResults = new HashMap<>();
        evaporatedFluid = FluidStack.EMPTY;
    };

    public void setReactionResults(Map<ReactionResult, Integer> results) {
        this.reactionResults = results;
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        if (!blockEntity.hasLevel()) return;
        if (!(blockEntity instanceof BasinBlockEntity basin) || basin.getLevel().isClientSide()) return; // It thinks getLevel() might be null (it's not)

        BlockEntity potentialOperator = getWorld().getBlockEntity(getPos().above(2));
        if (potentialOperator instanceof MechanicalMixerBlockEntity mixer) {
            if (mixer.processingTicks == 1) enactReactionResults(basin);
        };
        
    };

    public void enactReactionResults(BasinBlockEntity basin) {
        
        for (ReactionResult result : reactionResults.keySet()) {
            for (int i = 0; i < reactionResults.get(result); i++) result.onBasinReaction(basin.getLevel(), basin);
        };
        reactionResults.clear();

        if (!evaporatedFluid.isEmpty()) {
            PollutionHelper.pollute(basin.getLevel(), basin.getBlockPos(), evaporatedFluid);
            evaporatedFluid = FluidStack.EMPTY;
        };
    };

    /**
	 * Block destroyed or removed. Requires block to call ITE::onRemove
	 */
	public void destroy() {
        if (!evaporatedFluid.isEmpty() && blockEntity.getLevel() instanceof ServerLevel serverLevel) {
            PollutionHelper.pollute(serverLevel, blockEntity.getBlockPos(), evaporatedFluid);
            evaporatedFluid = FluidStack.EMPTY;
        };
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        tooFullToReact = nbt.getBoolean("TooFullToReact");

        reactionResults = new HashMap<>();
        ListTag results = nbt.getList("Results", Tag.TAG_COMPOUND);
        results.forEach(tag -> {
            CompoundTag resultTag = (CompoundTag) tag;
            ReactionResult result = LegacyReaction.get(resultTag.getString("Result")).getResult();
            int number = resultTag.getInt("Count");
            if (result == null) return;
            reactionResults.put(result, number);
        });

        evaporatedFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("EvaporatedFluidStack"));
	};

    @Override
	public void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putBoolean("TooFullToReact", tooFullToReact);

        nbt.put("Results", NBTHelper.writeCompoundList(reactionResults.entrySet().stream().filter(entry -> entry.getKey().getReaction().isPresent()).toList(), entry -> {
            CompoundTag resultTag = new CompoundTag();
            resultTag.putString("Result", entry.getKey().getReaction().get().getFullId());
            resultTag.putInt("Count", entry.getValue());
            return resultTag;
        }));

        CompoundTag fluidTag = new CompoundTag();
        evaporatedFluid.writeToNBT(fluidTag);
        nbt.put("EvaporatedFluid", fluidTag);
	};

    @SubscribeEvent
    public static void onAttachBasinBehaviours(BlockEntityBehaviourEvent<BasinBlockEntity> event) {
        event.attach(new ExtendedBasinBehaviour(event.getBlockEntity()));
    };
    
};
