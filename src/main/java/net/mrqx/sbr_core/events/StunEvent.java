package net.mrqx.sbr_core.events;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * 眩晕事件，在实体被击晕时触发。
 * <p>
 * 可取消，支持修改眩晕持续时间。
 */
public class StunEvent extends LivingEvent implements ICancellableEvent {
    private long duration;
    
    public StunEvent(LivingEntity entity, long duration) {
        super(entity);
        this.duration = duration;
    }
    
    /**
     * 获取眩晕持续时间（单位：tick）。
     */
    public long getDuration() {
        return duration;
    }
    
    /**
     * 设置眩晕持续时间。
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
