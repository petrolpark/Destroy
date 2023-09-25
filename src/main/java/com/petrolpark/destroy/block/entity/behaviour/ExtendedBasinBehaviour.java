package com.petrolpark.destroy.block.entity.behaviour;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class ExtendedBasinBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<ExtendedBasinBehaviour> TYPE = new BehaviourType<>();

    public boolean tooFullToReact;
    private Map<ReactionResult, Integer> reactionResults;
    private Mixture mixture;
    private int ticksToResults;

    public ExtendedBasinBehaviour(SmartBlockEntity be) {
        super(be);
        tooFullToReact = false;
        reactionResults = new HashMap<>();
        ticksToResults = -1;
    };

    public void setReactionResults(Map<ReactionResult, Integer> results, Mixture mixture, int time) {
        this.reactionResults = results;
        this.mixture = mixture;
        ticksToResults = time;
    };

    @Override
    public void tick() {
        if (!(blockEntity instanceof BasinBlockEntity basin) || mixture == null || basin.getLevel().isClientSide() || reactionResults.isEmpty() || ticksToResults == -1) return;
        ticksToResults--;
        if (ticksToResults == 0) {
            enactReactionResults(basin);
        };
    };

    public void enactReactionResults(BasinBlockEntity basin) {
        for (ReactionResult result : reactionResults.keySet()) {
            for (int i = 0; i < reactionResults.get(result); i++) result.onBasinReaction(basin.getLevel(), basin, mixture);
        };
        reactionResults.clear();
        mixture = null;
        ticksToResults = -1;
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
            ReactionResult result = Reaction.get(resultTag.getString("Result")).getResult();
            int number = resultTag.getInt("Count");
            if (result == null) return;
            reactionResults.put(result, number);
        });
        
        mixture = null;
        if (nbt.contains("MixtureForResults", Tag.TAG_COMPOUND)) mixture = Mixture.readNBT(nbt.getCompound("MixtureForResults"));

        ticksToResults = nbt.getInt("TicksUntilResults");
	};

    @Override
	public void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putBoolean("TooFullToReact", tooFullToReact);

        nbt.put("Results", NBTHelper.writeCompoundList(reactionResults.entrySet(), entry -> {
            CompoundTag resultTag = new CompoundTag();
            resultTag.putString("Result", entry.getKey().getReaction().getFullId());
            resultTag.putInt("Count", entry.getValue());
            return resultTag;
        }));

        if (mixture != null) nbt.put("MixtureForResults", mixture.writeNBT());

        nbt.putInt("TicksUntilResults", ticksToResults);
	};
    
};
