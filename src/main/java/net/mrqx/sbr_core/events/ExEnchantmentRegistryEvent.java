package net.mrqx.sbr_core.events;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * 拔刀剑额外附魔注册事件，在 Mod 总线派发。
 * <p>
 * 允许其他模组在此事件中向拔刀剑的可获取附魔列表中添加或移除附魔。
 */
public class ExEnchantmentRegistryEvent extends Event implements IModBusEvent {
    private final Set<ResourceKey<Enchantment>> oldExEnchantments;
    private final Set<ResourceKey<Enchantment>> newExEnchantments = new HashSet<>();
    
    public ExEnchantmentRegistryEvent(Set<ResourceKey<Enchantment>> oldExEnchantment) {
        this.oldExEnchantments = ImmutableSet.copyOf(oldExEnchantment);
        this.newExEnchantments.addAll(oldExEnchantment);
    }
    
    /**
     * 获取旧（初始）的额外附魔集合。
     */
    public Set<ResourceKey<Enchantment>> getOldExEnchantments() {
        return oldExEnchantments;
    }
    
    /**
     * 获取新的额外附魔集合（可修改）。
     */
    public Set<ResourceKey<Enchantment>> getNewExEnchantments() {
        return newExEnchantments;
    }
}
