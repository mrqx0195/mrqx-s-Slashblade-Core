package net.mrqx.sbr_core.mixin.common;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComboState.class)
public interface AccessorComboState {
    @SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
    @Invoker("<init>")
    static ComboState createComboState(ComboState.Builder builder) {
        throw new UnsupportedOperationException();
    }
}
