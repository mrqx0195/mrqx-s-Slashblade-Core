package net.mrqx.sbr_core.events;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.ArrayList;
import java.util.List;

public class ExEnchantmentRegistryEvent extends Event implements IModBusEvent {
    private final List<Enchantment> oldExEnchantments;
    private final List<Enchantment> newExEnchantments = new ArrayList<>();

    public ExEnchantmentRegistryEvent(List<Enchantment> oldExEnchantment) {
        this.oldExEnchantments = ImmutableList.copyOf(oldExEnchantment);
        this.newExEnchantments.addAll(oldExEnchantment);
    }

    public List<Enchantment> getOldExEnchantments() {
        return oldExEnchantments;
    }

    public List<Enchantment> getNewExEnchantments() {
        return newExEnchantments;
    }
}
