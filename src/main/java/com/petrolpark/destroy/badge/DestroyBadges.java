package com.petrolpark.destroy.badge;

import static com.petrolpark.destroy.Destroy.PETROLPARK_REGISTRATE;
import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.item.crafting.Ingredient;

public class DestroyBadges {

    private static final Ingredient GOLD_SHEET_INGREDIENT = Ingredient.of(AllTags.forgeItemTag("plates/gold"));

    public static final RegistryEntry<Badge>

    BETA_TESTER = PETROLPARK_REGISTRATE.badge("beta_tester")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    BESTIE = PETROLPARK_REGISTRATE.badge("bestie")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    COMPETITION_WINNER = PETROLPARK_REGISTRATE.badge("competition_winner")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    CONTENT_CREATOR = PETROLPARK_REGISTRATE.badge("content_creator")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    DEVELOPER = PETROLPARK_REGISTRATE.badge("developer")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    EARLY_BIRD = PETROLPARK_REGISTRATE.badge("early_bird")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    NITRO = PETROLPARK_REGISTRATE.badge("nitro")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    SUGGESTION = PETROLPARK_REGISTRATE.badge("suggestion")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    TRANSLATOR = PETROLPARK_REGISTRATE.badge("translator")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),

    PATREON_1 = REGISTRATE.badge("patreon_1")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    PATREON_2 = REGISTRATE.badge("patreon_2")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register(),
    PATREON_3 = REGISTRATE.badge("patreon_3")
        .duplicationIngredient(GOLD_SHEET_INGREDIENT)
        .register();

    public static void register() {};
};
