package net.mrqx.sbr_core.mixin.common;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.item.enchantment.Enchantment;
import net.mrqx.sbr_core.events.ExEnchantmentRegistryEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemSlashBlade.class)
public abstract class MixinItemSlashBlade {
    @Shadow(remap = false)
    @Final
    @Mutable
    public static List<Enchantment> EX_ENCHANTMENTS;
    
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injectClinit(CallbackInfo ci) {
        if (EX_ENCHANTMENTS != null) {
            EX_ENCHANTMENTS = NeoForge.EVENT_BUS.post(new ExEnchantmentRegistryEvent(EX_ENCHANTMENTS)).getNewExEnchantments();
        }
    }
}
