package net.mrqx.sbr_core.mixin.common;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.minecraftforge.fml.ModLoader;
import net.mrqx.sbr_core.events.ComboStateRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComboState.Builder.class)
public abstract class MixinComboStateBuilder {
    @Inject(method = "build()Lmods/flammpfeil/slashblade/registry/combo/ComboState;", at = @At(value = "HEAD"), remap = false)
    public void injectBuild(CallbackInfoReturnable<ComboState> ci) {
        ModLoader.get().postEventWrapContainerInModOrder(new ComboStateRegistryEvent((ComboState.Builder) (Object) this,
                AccessorComboState.createComboState((ComboState.Builder) (Object) this)));
    }
}
