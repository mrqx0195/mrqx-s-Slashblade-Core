package net.mrqx.sbr_core.config;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.mrqx.sbr_core.MrqxSlashBladeCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MrqxSlashBladeCoreConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NON_EX_EFFECT_ENCHANTMENT;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

        NON_EX_EFFECT_ENCHANTMENT = commonBuilder.comment("Example: ‘minecraft:multishot’. This prevents a specific enchantment from being added by this mod to SlashBlade's obtainable enchantment list (i.e., you cannot obtain Tiny Proud Souls with that enchantment, nor can you add it to SlashBlade via an enchantment table or anvil). This does not affect the extra effects of that enchantment from functioning, but it may prevent other mods from adding it to SlashBlade's obtainable enchantment list, and it might also affect enchantments that are already applicable to SlashBlade.")
                .defineList("non_ex_effect_enchantment", new ArrayList<>(), o -> o instanceof String);

        COMMON_CONFIG = commonBuilder.build();
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        ItemSlashBlade.exEnchantment.removeIf(enchantment -> {
            String key = Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)).toString();
            if (MrqxSlashBladeCoreConfig.NON_EX_EFFECT_ENCHANTMENT.get()
                    .contains(key)) {
                MrqxSlashBladeCore.LOGGER.debug("remove exEnchantment: {}", key);
                return true;
            }
            return false;
        });
    }
}
