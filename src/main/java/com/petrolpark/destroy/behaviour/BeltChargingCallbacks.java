package com.petrolpark.destroy.behaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.world.item.ItemStack;

public class BeltChargingCallbacks {
    
    static ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, ChargingBehaviour behaviour) {
        if (behaviour.specifics.getKineticSpeed() == 0) return ProcessingResult.PASS; // If the charger isn't 'on'
		if (behaviour.running) return ProcessingResult.HOLD; // If the charger is charging an Item Stack so we want to charge this one afterwards
		if (!behaviour.specifics.tryProcessOnBelt(transported, null, true)) return ProcessingResult.PASS; // If this Item Stack cannot be charged

		behaviour.start(ChargingBehaviour.Mode.BELT, handler.getWorldPositionOf(transported));
		return ProcessingResult.HOLD;
    };

    static ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, ChargingBehaviour behaviour) {
        if (behaviour.specifics.getKineticSpeed() == 0) return ProcessingResult.PASS; // If the charger isn't 'on', stop trying to process
		if (!behaviour.running) return ProcessingResult.PASS; // If the charger isn't charging, stop trying to process
		if (behaviour.runningTicks != ChargingBehaviour.CHARGING_TIME) return ProcessingResult.HOLD; // If this isn't the tick where the charger should process the Item Stack, stop trying to process

        ArrayList<ItemStack> results = new ArrayList<>(); // Results of charging the Item Stack
        if (!behaviour.specifics.tryProcessOnBelt(transported, results, false)) return ProcessingResult.PASS; // If the Item Stack cannot be charged, let it pass on

        boolean chargeAll = behaviour.specifics.canProcessInBulk() || transported.stack.getCount() == 1; // Whether every Item in the Item Stack should be charged at once

        List<TransportedItemStack> collect = results.stream() // Results of charging the Item Stack in the form of Transported Item Stacks
			.map(stack -> {
				TransportedItemStack copy = transported.copy();
				copy.stack = stack;
				copy.locked = true;
				return copy;
			})
			.collect(Collectors.toList());

        if (chargeAll) { // If we should charge the whole Stack into charged Items
            if (collect.isEmpty()) {
                handler.handleProcessingOnItem(transported, TransportedResult.removeItem()); // Remove the Item Stack if there is no result
            } else {
                handler.handleProcessingOnItem(transported, TransportedResult.convertTo(collect)); // Change the Item Stack into the processed results
            };

        } else { // If we should only charge one Item
            TransportedItemStack left = transported.copy();
            left.stack.shrink(1);
            if (collect.isEmpty()) {
                handler.handleProcessingOnItem(transported, TransportedResult.convertTo(left)); // Remove one Item if there is no result
            } else {
                handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(collect, left)); // Remove one Item and add the results
            };
        };

        behaviour.blockEntity.sendData();
		return ProcessingResult.HOLD;
    };




};
