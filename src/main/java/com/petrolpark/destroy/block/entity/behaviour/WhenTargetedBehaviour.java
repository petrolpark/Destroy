package com.petrolpark.destroy.block.entity.behaviour;

import java.util.function.BiConsumer;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Client-side behaviour for this Block Entity when the Player looks at it.
 */
public class WhenTargetedBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<WhenTargetedBehaviour> TYPE = new BehaviourType<>();

    private final BiConsumer<LocalPlayer, BlockHitResult> callback;

    public WhenTargetedBehaviour(SmartBlockEntity be, BiConsumer<LocalPlayer, BlockHitResult> whenTargeted) {
        super(be);
        callback = whenTargeted;
    };

    public void target(LocalPlayer player, BlockHitResult result) {
        callback.accept(player, result);
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};
