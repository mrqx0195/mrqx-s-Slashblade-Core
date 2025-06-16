package net.mrqx.sbr_core.events;

import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

public class SlashBladePlayerAnimationRegistryEvent extends Event {
    private final Map<ResourceLocation, VmdAnimation> animation;

    public SlashBladePlayerAnimationRegistryEvent(Map<ResourceLocation, VmdAnimation> animation) {
        this.animation = animation;
    }

    public Map<ResourceLocation, VmdAnimation> getAnimation() {
        return this.animation;
    }


    private static boolean hasInit = false;

    public static boolean hasInit() {
        return hasInit;
    }

    public static void setInit() {
        hasInit = true;
    }
}
