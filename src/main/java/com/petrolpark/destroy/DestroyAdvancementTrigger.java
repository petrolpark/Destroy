package com.petrolpark.destroy;

import com.petrolpark.data.advancement.SimpleAdvancementTrigger;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.ReactionResult;
import com.petrolpark.destroy.chemistry.legacy.reactionresult.DestroyAdvancementReactionResult;
import net.createmod.catnip.lang.Lang;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/*
 * Class for easily triggering Advancements.
 * An Advancement which references the trigger must still be created in the data folder.
 */
public enum DestroyAdvancementTrigger {

    ACETONE,
    ADDITION_POLYMER,
    OPEN_AGING_BARREL("open_aging_barrel", "aging_barrel"),
    AIBN,
    ANDRUSSOW_PROCESS,
    ARC_FURNACE,
    TAKE_BABY_BLUE("take_baby_blue", "baby_blue", "baby_blue_high"),
    SHOOT_HEFTY_BEETROOT("beetroot_potato_cannon"),
    BLOWPIPE,
    CAPTURE_STRAY,
    CATALYTIC_CONVERTER,
    USE_CENTRIFUGE("use_centrifuge", "centrifuge"),
    COLORIMETER,
    COMPLETE_SEISMOGRAPH,
    CUT_ONIONS,
    DISTILL("distill", "distillations"),
    CHARGE_WITH_DYNAMO("charge_with_dynamo", "dynamo_charging"),
    ELECTROLYZE_WITH_DYNAMO("electrolyze_with_dynamo", "electrolyze_with_dynamo"),
    ETHYLANTHRAQUINONE,
    EXTRUDE,
    FILL_SEISMOGRAPH,
    FIREPROOF_FLINT_AND_STEEL,
    DETONATE("detonate", "explosive"),
    HABER_PROCESS,
    HANGOVER,
    CURE_HANGOVER("cure_hangover", "hangover_cured"),
    HYDRAZINE,
    HYDROGEN_PEROXIDE,
    HYPERACCUMULATE,
    USE_KEYPUNCH("keypunch"),
    KEYPUNCH_FIVE,
    TRY_TO_MAKE_METH("try_to_make_meth", "meth"),
    MECHANICAL_SIEVE,
    OSTWALD_PROCESS,
    PERIODIC_TABLE,
    PROPANOL,
    USE_PUMPJACK("use_pumpjack", "pumpjack"),
    JUMP_ON_SAND_CASTLE("jump_on_sand_castle", "sand_castle"),
    USE_SEISMOMETER,
    SIPHON,
    STEAM_REFORMATION,
    TAP_TREE,
    COLLECT_TEARS("collect_tears", "tear_bottle"),
    URINATE,
    USE_VAT("use_vat", "vat"),
    VERY_DRUNK("very_drunk", "drunk");

    private String triggerId;
    private String[] advancementIds;
    private SimpleAdvancementTrigger trigger;

    DestroyAdvancementTrigger() {
        triggerId = Lang.asId(name());
        advancementIds = new String[]{Lang.asId(name())};
        trigger = new SimpleAdvancementTrigger(Destroy.asResource(triggerId));
    };

    DestroyAdvancementTrigger(String triggerAndAdvancementId) {
        this(triggerAndAdvancementId, triggerAndAdvancementId);
    };

    DestroyAdvancementTrigger(String triggerId, String ...advancementIds) {
        this.triggerId = triggerId;
        this.advancementIds = advancementIds;
        trigger = new SimpleAdvancementTrigger(Destroy.asResource(triggerId));
    };

    public void award(Level level, Player player) {
        if (level.isClientSide()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            trigger.trigger(serverPlayer);
        } else {
            Destroy.LOGGER.warn("Could not award Destroy Advancement "+triggerId+" to client-side Player.");
        };
    };

    public boolean isAlreadyAwardedTo(LivingEntity player) {
		if (!(player instanceof ServerPlayer sp)) return true;
        for (String advancementId : advancementIds) {
            Advancement advancement = sp.getServer().getAdvancements().getAdvancement(Destroy.asResource(advancementId));
            if (advancement == null || sp.getAdvancements().getOrStartProgress(advancement).isDone()) return true;
        };
        return false;
	};

    public ReactionResult asReactionResult(Float moles, LegacyReaction reaction) {
        return new DestroyAdvancementReactionResult(moles, reaction, this);
    };

    public static void register() {
        for (DestroyAdvancementTrigger e : values()) {
            CriteriaTriggers.register(e.trigger);
        };
    };
}
