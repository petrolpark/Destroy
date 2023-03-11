package com.petrolpark.destroy.behaviour;

import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ChargingBehaviour extends BeltProcessingBehaviour {

    public static final int CHARGING_TIME = 240; // The total length of time it takes to charge
	public static final int ENTITY_SCAN_TIME = 10; // After many ticks the charger scans for Entities to be charged

    public ChargingBehaviourSpecifics specifics; // The charging Block Entity ("charger"), usually a Dynamo
	public int runningTicks; // How long the charger has been charging
	public boolean running; // Whether the charger is currently charging
	public boolean finished; // Whether the charger has finished charging
	public Mode mode; // Whether the charger is charging an Item Stack in a Basin, on a Belt or Depot, or in the world

    int entityScanCooldown; // How long to the next Item Entity scan

    public interface ChargingBehaviourSpecifics {

        public boolean tryProcessInBasin(boolean simulate);

		public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate);

		public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate);

		public boolean canProcessInBulk();

		public void onChargingCompleted();

		public float getKineticSpeed();
    };

    public <T extends SmartTileEntity & ChargingBehaviourSpecifics> ChargingBehaviour(T te) {
        super(te);
        this.specifics = te;
        this.mode = Mode.WORLD;
        entityScanCooldown = ENTITY_SCAN_TIME;
        whenItemEnters((s, i) -> BeltChargingCallbacks.onItemReceived(s, i, this)); // What to do with the Item Stack when it arrives beneath the charger
		whileItemHeld((s, i) -> BeltChargingCallbacks.whenItemHeld(s, i, this)); // What to do with the Item Stack while we're keeping it underneath the charger
    };

    @Override
    public void read(CompoundTag tag, boolean clientPacket) {
        running = tag.getBoolean("Running");
        mode = Mode.values()[tag.getInt("Mode")];
        finished = tag.getBoolean("Finished");
        runningTicks = tag.getInt("Ticks");
        super.read(tag, clientPacket);
    };

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        tag.putBoolean("Running", running);
        tag.putInt("Mode", mode.ordinal());
        tag.putBoolean("Finished", finished);
        tag.putInt("Ticks", runningTicks);
        super.write(tag, clientPacket);
    }

    public void start(Mode mode) {
		this.mode = mode;
		running = true;
		runningTicks = 0;
		tileEntity.sendData();
	};

    @Override
    public void tick() {
        super.tick();
        
        Level level = getWorld();
        BlockPos pos = getPos();

        if (level == null) return;
        if (!running) {
            if (level.isClientSide()) {
                if (specifics.getKineticSpeed() == 0) return; // Don't do anything if the Charger isn't 'on'.
                if (entityScanCooldown > 0) // Decrement Item Entity scanning
					entityScanCooldown--;
				if (entityScanCooldown <= 0) { // If this is the tick to search for Entities
					entityScanCooldown = ENTITY_SCAN_TIME;

					if (TileEntityBehaviour.get(level, pos.below(2), TransportedItemStackHandlerBehaviour.TYPE) != null) // If the charger is above a Belt or Depot...
						return; // ...don't start anything, as this is already handled in BeltChargingCallbacks
					if (AllBlocks.BASIN.has(level.getBlockState(pos.below(2)))) // If the charger is above a Basin...
						return; // ...don't start anything, as this is already handled in DynamoTileEntity

					for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.below()).deflate(.125f))) { // Check all Item Entities in the block below
						if (!itemEntity.isAlive() || !itemEntity.isOnGround()) continue; // Ignore Item Entities marked for removal
						if (!specifics.tryProcessInWorld(itemEntity, true)) continue; // Ignore if the Item Stack cannot be processed
						start(Mode.WORLD); // At the first chargeable Item we get, stop looking for any more and start processing
						return;
					}
				}
            } else { // If the charger isn't running and this is the client side
                return;
            };
        };

        if (runningTicks == CHARGING_TIME && specifics.getKineticSpeed() != 0) { // If this is the tick where the charger should actually process
            switch (mode) {
                case BASIN: {
                    applyOnBasin();
                } case WORLD: {
                    applyInWorld();
                } case BELT: {
                    // This is handled in BeltChargingCallback
                };
                //TODO sounds
                if (!level.isClientSide()) {
                    tileEntity.sendData();
                };
            }
        };

        if (!level.isClientSide && runningTicks >= CHARGING_TIME) { // If the charging is finished
			finished = true;
			running = false;
			specifics.onChargingCompleted();
			tileEntity.sendData();
			return;
		};

        runningTicks += getRunningTickSpeed(); // Increment the charging timer
        if (runningTicks >= CHARGING_TIME) {
            runningTicks = CHARGING_TIME;
        };
    };

    /*
     * Charges all possible Item Entities in the Basin two Blocks below this charger.
     */
    protected void applyOnBasin() {
		Level level = getWorld();
		if (level.isClientSide()) return;
		if (specifics.tryProcessInBasin(false)) { // If we successfully charged the Item Stack
			tileEntity.sendData();
        };
	};

    /*
     * Charges all possible Item Entities below this charger.
     */
	protected void applyInWorld() {
		Level level = getWorld();
		BlockPos pos = getPos();
		AABB bb = new AABB(pos.below(1));
		boolean bulk = specifics.canProcessInBulk(); // If we can process multiple Item Stack Entities at once

		if (level.isClientSide()) return;

		for (Entity entity : level.getEntities(null, bb)) {
			if (!(entity instanceof ItemEntity itemEntity)) continue; // Ignore non-Item Entities
			if (!entity.isAlive() || !entity.isOnGround()) continue; // Ignore Item Entities marked for removal

			entityScanCooldown = 0; // Reset the Item Entity scanner
			if (specifics.tryProcessInWorld(itemEntity, false)) tileEntity.sendData(); // If successfully charged the Item Stack
			if (!bulk) break; // If we can only charge one Item Stack Entity, give up now
		}
	};

	public int getRunningTickSpeed() {
		float speed = specifics.getKineticSpeed();
		if (speed == 0) return 0;
		return (int) Mth.lerp(Mth.clamp(Math.abs(speed) / 512f, 0, 1), 1, 60);
	};

    public enum Mode {
        WORLD, BELT, BASIN;
    };
    
}
