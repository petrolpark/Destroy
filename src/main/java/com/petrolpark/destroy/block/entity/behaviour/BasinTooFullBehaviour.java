package com.petrolpark.destroy.block.entity.behaviour;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;

public class BasinTooFullBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<BasinTooFullBehaviour> TYPE = new BehaviourType<>();

    public boolean tooFullToReact;

    public BasinTooFullBehaviour(SmartBlockEntity be) {
        super(be);
        tooFullToReact = false;
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    public void read(CompoundTag nbt, boolean clientPacket) {
        tooFullToReact = nbt.getBoolean("TooFullToReact");
	};

	public void write(CompoundTag nbt, boolean clientPacket) {
        nbt.putBoolean("TooFullToReact", tooFullToReact);
	};
    
};
