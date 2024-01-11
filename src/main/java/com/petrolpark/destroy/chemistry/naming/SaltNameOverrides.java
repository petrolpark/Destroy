package com.petrolpark.destroy.chemistry.naming;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Molecule;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class SaltNameOverrides {

    public static final Map<Molecule, SaltNameOverrides> ALL_OVERRIDES = new HashMap<>();

    public static final Manager MANAGER = new Manager();

    /**
     * The translation key of the name to be used for this Molecule when it is part of a salt.
     */
    @Nullable
    public String genericOverrideKey;

    /**
     * The translation keys of names of salts this Molecule forms with other Molecules.
     */
    public Map<Molecule, String> specificOverrideKeys;

    public SaltNameOverrides() {
        specificOverrideKeys = new HashMap<>();
    };

    private static final Gson GSON = new Gson();

    public static void loadOverridesFromJson(InputStream inputStream) {
        try {
            JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
            jsonObject.entrySet().forEach(moleculeEntry -> {
                String moleculeId = moleculeEntry.getKey();
                Molecule molecule = Molecule.getMolecule(moleculeId);
                if (molecule == null) throw new IllegalStateException("Unknown molecule in overrides: "+moleculeId);

                SaltNameOverrides overrides = ALL_OVERRIDES.get(molecule);
                if (overrides == null) overrides = new SaltNameOverrides();

                JsonObject overridesObject = moleculeEntry.getValue().getAsJsonObject();
                if (overridesObject.has("generic")) overrides.genericOverrideKey = overridesObject.get("generic").getAsString();
                if (overridesObject.has("specifics")) {
                    JsonObject specificsObject = overridesObject.getAsJsonObject("specifics");
                    for (Entry<String, JsonElement> overrideEntry : specificsObject.entrySet()) {
                        String partnerId = overrideEntry.getKey();
                        Molecule partner = Molecule.getMolecule(partnerId);
                        if (partner == null) throw new IllegalStateException("Unknown molecule in overrides of "+moleculeId+": "+partnerId);
                        overrides.specificOverrideKeys.put(partner, overrideEntry.getValue().getAsString());
                    };
                };

                ALL_OVERRIDES.put(molecule, overrides);
            });
        } catch (Throwable e) {
            Destroy.LOGGER.error("Error loading salt name overrides.", e);
        };
    }

    public static void loadOverrideFiles(ResourceManager resourceManager) {
        Minecraft minecraft = Minecraft.getInstance();
        ALL_OVERRIDES.clear();
        for (String namespace : resourceManager.getNamespaces()) {
            Destroy.LOGGER.info("Loading salt name overrides for mod "+namespace);
            ResourceLocation location = new ResourceLocation(namespace, "lang/salt_name_overrides/"+minecraft.getLanguageManager().getSelected()+".json");
            Optional<Resource> resource = resourceManager .getResource(location);
            if (resource.isPresent()) {
                try (InputStream inputStream = resource.get().open()) {
                    loadOverridesFromJson(inputStream);
                } catch (IOException e) {
                    Destroy.LOGGER.error("Failed to read salt name overrides: " + location, e);
                };
            };
        }; 
    };

    public static class Manager implements ResourceManagerReloadListener {
        
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            loadOverrideFiles(resourceManager);
        };

    };
};
