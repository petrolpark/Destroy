package com.petrolpark.destroy.compat.computercraft;

import com.petrolpark.destroy.Destroy;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;
import java.util.function.Function;

// From the cc:tweaked documentation
// A {@link ICapabilityProvider} that lazily creates an {@link IPeripheral} when required.
public final class DestroyPeripheralProvider<O extends BlockEntity> implements ICapabilityProvider {
    public static final Capability<IPeripheral> CAPABILITY_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static final ResourceLocation PERIPHERAL = ResourceLocation.fromNamespaceAndPath(Destroy.MOD_ID, "peripheral");

    private final O blockEntity;
    private final Function<O, IPeripheral> factory;
    private @Nullable LazyOptional<IPeripheral> peripheral;

    public DestroyPeripheralProvider(O blockEntity, Function<O, IPeripheral> factory) {
        this.blockEntity = blockEntity;
        this.factory = factory;
    }

    public static <O extends BlockEntity> void attach(AttachCapabilitiesEvent<BlockEntity> event, O blockEntity, Function<O, IPeripheral> factory) {
        var provider = new DestroyPeripheralProvider<>(blockEntity, factory);
        event.addCapability(PERIPHERAL, provider);
        event.addListener(provider::invalidate);
    }

    public void invalidate() {
        if (peripheral != null) peripheral.invalidate();
        peripheral = null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        if (capability != CAPABILITY_PERIPHERAL) return LazyOptional.empty();
        if (blockEntity.isRemoved()) return LazyOptional.empty();

        var peripheral = this.peripheral;
        return (peripheral == null ? (this.peripheral = LazyOptional.of(() -> factory.apply(blockEntity))) : peripheral).cast();
    }
}
