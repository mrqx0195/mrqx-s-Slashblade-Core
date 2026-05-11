package net.mrqx.sbr_core.events;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 拔刀剑额外附魔注册事件，在 Mod 总线派发。
 * <p>
 * 允许其他模组在此事件中向拔刀剑的可获取附魔列表中添加或移除附魔。
 */
public class ExEnchantmentRegistryEvent extends Event implements IModBusEvent {
    private final List<Enchantment> oldExEnchantments;
    private final List<Enchantment> newExEnchantments = new ArrayList<>();
    
    public ExEnchantmentRegistryEvent(List<Enchantment> oldExEnchantment) {
        this.oldExEnchantments = ImmutableList.copyOf(oldExEnchantment);
        this.newExEnchantments.addAll(oldExEnchantment);
    }
    
    /**
     * 获取旧（初始）的额外附魔集合。
     */
    public List<Enchantment> getOldExEnchantments() {
        return oldExEnchantments;
    }
    
    /**
     * 获取新的额外附魔集合（可修改）。
     */
    public List<Enchantment> getNewExEnchantments() {
        return newExEnchantments;
    }
}
