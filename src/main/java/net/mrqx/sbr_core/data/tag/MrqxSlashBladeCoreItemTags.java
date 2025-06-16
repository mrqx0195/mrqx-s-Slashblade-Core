package net.mrqx.sbr_core.data.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.mrqx.sbr_core.MrqxSlashBladeCore;

public class MrqxSlashBladeCoreItemTags {
    public static final TagKey<Item> CAN_COPY_SA = ItemTags.create(new ResourceLocation(MrqxSlashBladeCore.MODID, "can_copy_sa"));
    public static final TagKey<Item> CAN_COPY_SE = ItemTags.create(new ResourceLocation(MrqxSlashBladeCore.MODID, "can_copy_se"));
    public static final TagKey<Item> CAN_CHANGE_SA = ItemTags.create(new ResourceLocation(MrqxSlashBladeCore.MODID, "can_change_sa"));
    public static final TagKey<Item> CAN_CHANGE_SE = ItemTags.create(new ResourceLocation(MrqxSlashBladeCore.MODID, "can_change_se"));
}
