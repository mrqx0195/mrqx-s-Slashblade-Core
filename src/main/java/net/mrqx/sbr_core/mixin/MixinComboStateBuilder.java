package net.mrqx.sbr_core.mixin;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import net.mrqx.sbr_core.events.ComboStateRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComboState.Builder.class)
public abstract class MixinComboStateBuilder {
    @Inject(method = "build()Lmods/flammpfeil/slashblade/registry/combo/ComboState;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void injectBuild(CallbackInfoReturnable<ComboState> ci) {
//        MinecraftForge.EVENT_BUS.post(new ComboStateRegistryEvent((ComboState.Builder) (Object) this,
//                AccessorComboState.createComboState((ComboState.Builder) (Object) this)));
        ModLoader.get().postEventWrapContainerInModOrder(new ComboStateRegistryEvent((ComboState.Builder) (Object) this,
                AccessorComboState.createComboState((ComboState.Builder) (Object) this)));
    }
}
