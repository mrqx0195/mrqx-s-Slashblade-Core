package net.mrqx.sbr_core.config;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * SBR Core 的通用配置类。
 * <p>
 * 提供对拔刀剑可获取附魔列表的黑名单配置，
 * 在配置加载时自动从 {@link ItemSlashBlade#EX_ENCHANTMENTS} 中移除被禁用的附魔。
 */
@EventBusSubscriber
public class MrqxSlashBladeCoreConfig {
    public static final ModConfigSpec COMMON_CONFIG;
    
    public static final ModConfigSpec.ConfigValue<List<? extends String>> NON_EX_EFFECT_ENCHANTMENT;
    
    static {
        ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();
        
        NON_EX_EFFECT_ENCHANTMENT = commonBuilder.comment("Example: ‘minecraft:multishot’. This prevents a specific enchantment from being added by this mod to SlashBlade's obtainable enchantment list (i.e., you cannot obtain Tiny Proud Souls with that enchantment, nor can you add it to SlashBlade via an enchantment table or anvil). This does not affect the extra effects of that enchantment from functioning, but it may prevent other mods from adding it to SlashBlade's obtainable enchantment list, and it might also affect enchantments that are already applicable to SlashBlade.")
            .defineList("non_ex_effect_enchantment", new ArrayList<>(), () -> "", o -> o instanceof String);
        
        COMMON_CONFIG = commonBuilder.build();
    }
    
    /**
     * 配置加载时，从 {@link ItemSlashBlade#EX_ENCHANTMENTS} 中移除黑名单附魔。
     */
    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        ItemSlashBlade.EX_ENCHANTMENTS.removeIf(enchantment -> {
            String key = enchantment.toString();
            if (MrqxSlashBladeCoreConfig.NON_EX_EFFECT_ENCHANTMENT.get()
                .contains(key)) {
                MrqxSlashBladeCore.LOGGER.debug("remove exEnchantment: {}", key);
                return true;
            }
            return false;
        });
    }
}
