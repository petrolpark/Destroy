package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.DynamiteBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DynamiteBlock extends Block implements IBE<DynamiteBlockEntity> {

    public DynamiteBlock(Properties properties) {
        super(properties);
    };

    @Override
    public Class<DynamiteBlockEntity> getBlockEntityClass() {
        return DynamiteBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends DynamiteBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.DYNAMITE.get();
    };
    
};
