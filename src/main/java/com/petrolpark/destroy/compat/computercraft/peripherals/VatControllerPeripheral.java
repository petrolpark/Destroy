package com.petrolpark.destroy.compat.computercraft.peripherals;

import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity;
import com.simibubi.create.Create;
import com.simibubi.create.compat.computercraft.implementation.CreateLuaTable;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VatControllerPeripheral implements IPeripheral {

    VatControllerBlockEntity vcbe;

    public VatControllerPeripheral(VatControllerBlockEntity vcbe) {
        this.vcbe = vcbe;
    }

    @LuaFunction(mainThread = true)
    public final float getPressure() throws LuaException {
        checkForVat();
        return this.vcbe.getPressure();
    }

    @LuaFunction(mainThread = true)
    public final float getTemperature() throws LuaException {
        checkForVat();
        return this.vcbe.getTemperature();
    }

    @LuaFunction(mainThread = true)
    public final int getCapacity() throws LuaException {
        checkForVat();
        return this.vcbe.getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final float getUVStrength() throws LuaException {
        checkForVat();
        return this.vcbe.getUVPower();
    }

    @LuaFunction(mainThread = true)
    public final float getFluidLevel() throws LuaException {
        checkForVat();
        return this.vcbe.getFluidLevel();
    }

    @LuaFunction(mainThread = true)
    public final CreateLuaTable getMixture() throws LuaException {
        checkForVat();

        CreateLuaTable contentsTable = new CreateLuaTable();
        ReadOnlyMixture mixture = this.vcbe.getCombinedReadOnlyMixture();

        mixture.getContents(false).forEach((molecule) -> {
            contentsTable.put(molecule.getFullID(), mixture.getConcentrationOf(molecule));
        });

        return contentsTable;
    }

    @Override
    public String getType() {
        return "vat_controller";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof VatControllerPeripheral o && vcbe == o.vcbe;
    }

    private void checkForVat() throws LuaException {
        if (vcbe.getVatOptional().isEmpty())
            throw new LuaException("Could not execute function, vat is not assembled");
    }
}
