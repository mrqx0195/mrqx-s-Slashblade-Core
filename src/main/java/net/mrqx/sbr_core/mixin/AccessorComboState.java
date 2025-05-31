package net.mrqx.sbr_core.mixin;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComboState.class)
public interface AccessorComboState {
    @Invoker("<init>")
    public static ComboState createComboState(ComboState.Builder builder) {
        throw new UnsupportedOperationException();
    }
}
