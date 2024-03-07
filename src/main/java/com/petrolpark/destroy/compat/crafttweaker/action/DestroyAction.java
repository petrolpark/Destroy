package com.petrolpark.destroy.compat.crafttweaker.action;

import com.blamejared.crafttweaker.api.action.base.IUndoableAction;

public abstract class DestroyAction implements IUndoableAction {

    @Override
    public String describe() {
        return "An internal Destroy action";
    }

    @Override
    public String systemName() {
        return "Destroy";
    }
}
