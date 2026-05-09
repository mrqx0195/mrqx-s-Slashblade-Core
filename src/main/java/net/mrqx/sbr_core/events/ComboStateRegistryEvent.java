package net.mrqx.sbr_core.events;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class ComboStateRegistryEvent extends Event implements IModBusEvent {
    private final ComboState.Builder builder;
    private final ComboState combo;
    
    public ComboStateRegistryEvent(ComboState.Builder builder, ComboState combo) {
        this.builder = builder;
        this.combo = combo;
    }
    
    public ComboState.Builder getBuilder() {
        return this.builder;
    }
    
    public ComboState getCombo() {
        return this.combo;
    }
}
