package net.mrqx.sbr_core.events;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Use {@link mods.flammpfeil.slashblade.event.SlashBladeEvent.ChargeActionEvent} instead.
 */
@Cancelable
@Deprecated
public class ChargeActionEvent extends Event {
    private final LivingEntity entityLiving;
    private final int elapsed;
    private final ISlashBladeState state;
    private ResourceLocation comboState;
    private final SlashArts.ArtsType type;

    public ChargeActionEvent(LivingEntity entityLiving, int elapsed, ISlashBladeState state, ResourceLocation comboState, SlashArts.ArtsType type) {
        this.entityLiving = entityLiving;
        this.elapsed = elapsed;
        this.state = state;
        this.comboState = comboState;
        this.type = type;
    }

    public LivingEntity getEntityLiving() {
        return entityLiving;
    }

    public int getElapsed() {
        return elapsed;
    }

    public ISlashBladeState getState() {
        return state;
    }

    public ResourceLocation getComboState() {
        return comboState;
    }

    public void setComboState(ResourceLocation comboState) {
        this.comboState = comboState;
    }

    public SlashArts.ArtsType getType() {
        return type;
    }
}
