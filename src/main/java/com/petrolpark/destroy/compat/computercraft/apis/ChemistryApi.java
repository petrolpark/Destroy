package com.petrolpark.destroy.compat.computercraft.apis;

import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReaction;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyReactions;
import com.simibubi.create.Create;
import com.simibubi.create.compat.computercraft.implementation.CreateLuaTable;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import org.jetbrains.annotations.Nullable;

public class ChemistryApi implements ILuaAPI {

    @LuaFunction(mainThread = true)
    public final CreateLuaTable getMoleculeInfos(String id) {
        LegacySpecies molecule = LegacySpecies.getMolecule(id);
        CreateLuaTable infos = new CreateLuaTable();

        infos.putDouble("mass", molecule.getMass());
        infos.putDouble("boilingPoint", molecule.getBoilingPoint());
        infos.putDouble("density", molecule.getDensity());
        infos.putDouble("charge", molecule.getCharge());
        infos.putString("FROWNS", molecule.getFROWNSCode());

        return infos;
    }

    @LuaFunction(mainThread = true)
    public final CreateLuaTable getReactionsFor(String id) {
        CreateLuaTable resultTable = new CreateLuaTable();
        LegacySpecies molecule = LegacySpecies.getMolecule(id);

        LegacyReaction.REACTIONS.forEach((rId, reaction) -> {
            if ( reaction.getProducts().contains(molecule)) {
                resultTable.putDouble(rId, reaction.getProductMolarRatio(molecule));
            }
        });

        return resultTable;
    }

    @LuaFunction(mainThread = true)
    public final CreateLuaTable getReactantsFor(String reactionId) {
        CreateLuaTable resultTable = new CreateLuaTable();
        LegacyReaction reaction = LegacyReaction.REACTIONS.get(reactionId);

        reaction.getReactants().forEach((reactant) -> {
            resultTable.putDouble(reactant.getFullID(), reaction.getReactantMolarRatio(reactant));
        });

        return resultTable;
    }

    @LuaFunction(mainThread = true)
    public final CreateLuaTable getProductsFor(String reactionId) {
        CreateLuaTable resultTable = new CreateLuaTable();
        LegacyReaction reaction = LegacyReaction.REACTIONS.get(reactionId);

        reaction.getProducts().forEach((product) -> {
            resultTable.putDouble(product.getFullID(), reaction.getReactantMolarRatio(product));
        });

        return resultTable;
    }

    @Override
    public String[] getNames() {
       return new String[0];
    }

    @Override
    public @Nullable String getModuleName() {
        return "destroy.chemistry";
    }
}
