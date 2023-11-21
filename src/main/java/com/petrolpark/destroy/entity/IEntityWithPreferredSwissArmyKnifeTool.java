package com.petrolpark.destroy.entity;

import com.petrolpark.destroy.item.SwissArmyKnifeItem;

public interface IEntityWithPreferredSwissArmyKnifeTool {
    /**
     * Get the tool to which a Swiss Army Knife should switch if this entity is targeted.
     * This is only called client-side.
     * @param shiftDown Whether the Swiss Army Knife's user has shift down
     */
    public SwissArmyKnifeItem.Tool getToolForSwissArmyKnife(boolean shiftDown);
};
