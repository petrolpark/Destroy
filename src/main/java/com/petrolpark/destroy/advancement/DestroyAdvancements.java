package com.petrolpark.destroy.advancement;

import com.petrolpark.destroy.Destroy;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/*
 * Class for easily triggering Advancements.
 * An Advancement which references the trigger must still be created in the data folder.
 */
public enum DestroyAdvancements {

    OPEN_AGING_BARREL("open_aging_barrel"),
    TAKE_BABY_BLUE("take_baby_blue"),
    TAKE_BABY_BLUE_HIGH("take_baby_blue_high"),
    SHOOT_HEFTY_BEETROOT("shoot_hefty_beetroot"),
    CAPTURE_STRAY("capture_stray"),
    USE_CENTRIFUGE("use_centrifuge"),
    LUBRICATE_CENTRIFUGE("lubricate_centrifuge"),
    DISTILL("distill"),
    CHARGE_WITH_DYNAMO("charge_with_dynamo"),
    ELECTROLYZE_WITH_DYNAMO("electrolyze_with_dynamo"),
    HANGOVER("hangover"),
    CURE_HANGOVER("cure_hangover"),
    HYPERACCUMULATE("hyperaccumulate"),
    MECHANICAL_HANDS("mechanical_hands"),
    OTSWALD_PROCESS("otswald_process"),
    COLLECT_TEARS("collect_tears"),
    URINATE("urinate"),
    VERY_DRUNK("very_drunk");

    private String id;
    private SimpleDestroyTrigger trigger;

    DestroyAdvancements(String id) {
        this.id = id;
        trigger = new SimpleDestroyTrigger(id);
    };

    public void award(Level level, Player player) {
        if (level.isClientSide()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            trigger.trigger(serverPlayer);
        } else {
            Destroy.LOGGER.warn("Could not award Destroy Advancement "+id+" to client-side Player.");
        };
    };

    public static void register() {
        for (DestroyAdvancements e : values()) {
            CriteriaTriggers.register(e.trigger);
        };
    };
}
