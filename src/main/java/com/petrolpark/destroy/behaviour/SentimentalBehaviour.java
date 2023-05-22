package com.petrolpark.destroy.behaviour;

import javax.annotation.Nullable;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A Behaviour for Tile Entities which have an 'owner' and which should make that owner cry if destroyed.
 */
public class SentimentalBehaviour extends BlockEntityBehaviour {

    public static BehaviourType<SentimentalBehaviour> TYPE = new BehaviourType<>();

    public LivingEntity owner;

    public SentimentalBehaviour(SmartBlockEntity be) {
        super(be);
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
        blockEntity.sendData();
    };

    /**
     * Cause the owner of this sentimental Block to cry.
     * @param state The Block State which was destroyed
     * @param player The Player (if any) who destroyed it
     */
    public void onRemove(BlockState state, @Nullable Player player) {
        if (hasOwner() && getPos().distToCenterSqr(owner.getX(), owner.getY(), owner.getZ()) < 16) {
            owner.addEffect(new MobEffectInstance(DestroyMobEffects.CRYING.get(), 600, 0, false, false));

            // For Villagers
            if (owner instanceof Villager villager && player != null) {
                villager.gossips.add(villager.getUUID(), GossipType.MINOR_NEGATIVE, 20); // Decrease the reputation of the Player that did this
                if (villager.isBaby()) DestroyAdvancements.JUMP_ON_SAND_CASTLE.award(owner.getLevel(), player);
            };
        };
    };

    @Override
    public void read(CompoundTag tag, boolean clientPacket) {
        if (!clientPacket && getWorld() instanceof ServerLevel server) {
            server.getEntity(tag.getUUID("BabyOwner"));
        };
        super.read(tag, clientPacket);
    };

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        if (hasOwner() && !clientPacket) {
            tag.putUUID("BabyOwner", owner.getUUID());
        };
        super.write(tag, clientPacket);
    };

    private boolean hasOwner() {
        return owner != null && owner.isAlive();
    };
    
};
