package net.mrqx.sbr_core.events;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.HashSet;
import java.util.Set;

public class ExEnchantmentRegistryEvent extends Event implements IModBusEvent {
    private final Set<ResourceKey<Enchantment>> oldExEnchantments;
    private final Set<ResourceKey<Enchantment>> newExEnchantments = new HashSet<>();
    
    public ExEnchantmentRegistryEvent(Set<ResourceKey<Enchantment>> oldExEnchantment) {
        this.oldExEnchantments = ImmutableSet.copyOf(oldExEnchantment);
        this.newExEnchantments.addAll(oldExEnchantment);
    }
    
    public Set<ResourceKey<Enchantment>> getOldExEnchantments() {
        return oldExEnchantments;
    }
    
    public Set<ResourceKey<Enchantment>> getNewExEnchantments() {
        return newExEnchantments;
    }
}
