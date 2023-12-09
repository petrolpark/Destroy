package com.petrolpark.destroy.advancement;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;

@MoveToPetrolparkLibrary
public class BadgesAdvancementTab extends AdvancementTab {

    public BadgesAdvancementTab(Minecraft pMinecraft, AdvancementsScreen pScreen, AdvancementTabType pType, int pIndex, Advancement pAdvancement, DisplayInfo pDisplay) {
        super(pMinecraft, pScreen, pType, pIndex, pAdvancement, pDisplay);
    };
    
};
