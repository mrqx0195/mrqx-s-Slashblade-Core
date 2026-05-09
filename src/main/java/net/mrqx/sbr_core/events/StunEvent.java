package net.mrqx.sbr_core.events;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class StunEvent extends LivingEvent implements ICancellableEvent {
    private long duration;
    
    public StunEvent(LivingEntity entity, long duration) {
        super(entity);
        this.duration = duration;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
