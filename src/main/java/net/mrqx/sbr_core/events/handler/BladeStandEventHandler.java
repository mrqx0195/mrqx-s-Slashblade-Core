package net.mrqx.sbr_core.events.handler;

import java.util.Set;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.mrqx.sbr_core.events.MrqxSlashBladeEvents;

@SuppressWarnings("null")
@EventBusSubscriber()
public class BladeStandEventHandler {
    @SubscribeEvent
    public static void copySAEnchantmentCheck(MrqxSlashBladeEvents.PreCopySpecialAttackFromBladeEvent event) {
        SlashBladeEvent.BladeStandAttackEvent oriEvent = event.getOriginalEvent();
        Player player = (Player) oriEvent.getDamageSource().getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack blade = event.getBlade();
        Set<Enchantment> enchantments = EnchantmentHelper.getEnchantments(stack).keySet();
        boolean flag = false;
        for (Enchantment e : enchantments) {
            if (EnchantmentHelper.getTagEnchantmentLevel(e, blade) >= e.getMaxLevel()) {
                flag = true;
            }
        }
        if (!flag) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void proudSoulEnchantmentProbabilityCheck(MrqxSlashBladeEvents.ProudSoulEnchantmentEvent event) {
        SlashBladeEvent.BladeStandAttackEvent oriEvent = event.getOriginalEvent();
        Player player = (Player) oriEvent.getDamageSource().getEntity();
        Level world = player.level();
        RandomSource random = world.getRandom();

        if (random.nextFloat() > event.getProbability()) {
            event.setCanceled(true);
        }
    }
}
