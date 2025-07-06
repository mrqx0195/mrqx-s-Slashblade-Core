package net.mrqx.sbr_core.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class StunEvent extends LivingEvent {
    private final long duration;

    public StunEvent(LivingEntity entity, long duration) {
        super(entity);
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }
}
