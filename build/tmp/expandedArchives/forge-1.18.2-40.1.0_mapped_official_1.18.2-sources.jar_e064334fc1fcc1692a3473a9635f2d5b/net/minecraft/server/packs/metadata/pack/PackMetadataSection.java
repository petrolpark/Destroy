package net.minecraft.server.packs.metadata.pack;

import net.minecraft.network.chat.Component;

public class PackMetadataSection {
   public static final PackMetadataSectionSerializer SERIALIZER = new PackMetadataSectionSerializer();
   private final Component description;
   private final int packFormat;

   public PackMetadataSection(Component p_10371_, int p_10372_) {
      this.description = p_10371_;
      this.packFormat = p_10372_;
   }

   public Component getDescription() {
      return this.description;
   }

   public int getPackFormat() {
      return this.packFormat;
   }
}