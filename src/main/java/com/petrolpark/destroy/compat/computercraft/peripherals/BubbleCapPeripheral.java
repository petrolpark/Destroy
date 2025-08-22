package com.petrolpark.destroy.compat.computercraft.peripherals;

import com.petrolpark.destroy.content.processing.distillation.BubbleCapBlockEntity;
import com.petrolpark.destroy.content.processing.distillation.DistillationTower;
import dan200.computercraft.api.detail.ForgeDetailRegistries;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BubbleCapPeripheral implements IPeripheral {

    BubbleCapBlockEntity bcbe;

    public BubbleCapPeripheral(BubbleCapBlockEntity bcbe) {
        this.bcbe = bcbe;
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Map<String, ?>> tanks() {
        Level level = bcbe.getLevel();

        DistillationTower tower = bcbe.getDistillationTower();
        BlockPos startPos = tower.getControllerPos();
        int height = tower.getHeight();

        Map<Integer, Map<String, ?>> result = new HashMap();

        for (int i = 0; i < height; i++) {
            BubbleCapBlockEntity be = ( BubbleCapBlockEntity ) level.getBlockEntity(startPos.offset(0, i, 0));
            result.put(i + 1, getTank(be.getTank()));
        }

        return result;
    }

    private Map<String, ?> getTank(IFluidHandler fluids) {
        FluidStack stack = fluids.getFluidInTank(0);

        if (!stack.isEmpty()) {
            Map<String, Object> details = ForgeDetailRegistries.FLUID_STACK.getBasicDetails(stack);
            if (details.get("name").equals("destroy:mixture")) {
                Map<Integer, Map<String, ?>> contentsTable = new HashMap<>();
                CompoundTag data = new CompoundTag();
                stack.writeToNBT(data);
                ListTag contents = data
                        .getCompound("Tag")
                        .getCompound("Mixture")
                        .getList("Contents", 10);
                contents.forEach(tag -> {
                    Map<String, Object> molecule = new HashMap<>();
                    CompoundTag moleculeTag = (CompoundTag) tag;
                    molecule.put("id", moleculeTag.getString("Molecule"));
                    molecule.put("concentration", moleculeTag.getFloat("Concentration"));
                    contentsTable.put(contentsTable.size() + 1, molecule);
                });
                details.put("contents", contentsTable);
            }
            return details;
        }

        return new HashMap();
    }

    @Override
    public String getType() {
        return "bubble_cap";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof BubbleCapPeripheral o && bcbe == o.bcbe;
    }
}
