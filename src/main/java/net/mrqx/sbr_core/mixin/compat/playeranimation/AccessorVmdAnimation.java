package net.mrqx.sbr_core.mixin.compat.playeranimation;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VmdAnimation.class)
public interface AccessorVmdAnimation {
    @Accessor(value = "nameMap", remap = false)
    static Map<String, String> getNameMap() {
        return Maps.newHashMap();
    }
}
