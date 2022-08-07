package net.minecraft.world.item;

import net.minecraft.ChatFormatting;

public enum Rarity implements net.minecraftforge.common.IExtensibleEnum {
   COMMON(ChatFormatting.WHITE),
   UNCOMMON(ChatFormatting.YELLOW),
   RARE(ChatFormatting.AQUA),
   EPIC(ChatFormatting.LIGHT_PURPLE);

   public final ChatFormatting color;

   private Rarity(ChatFormatting p_43028_) {
      this.color = p_43028_;
   }

   public static Rarity create(String name, ChatFormatting p_43028_) {
      throw new IllegalStateException("Enum not extended");
   }
}
