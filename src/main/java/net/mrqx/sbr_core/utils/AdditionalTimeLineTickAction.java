package net.mrqx.sbr_core.utils;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.function.Consumer;

public class AdditionalTimeLineTickAction implements Consumer<LivingEntity> {
    public static Builder getBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        Map<Integer, Consumer<LivingEntity>> timeLine = Maps.newHashMap();
        
        public Builder put(int ticks, Consumer<LivingEntity> action) {
            timeLine.put(ticks, action);
            return this;
        }
        
        public AdditionalTimeLineTickAction build() {
            return new AdditionalTimeLineTickAction(timeLine);
        }
    }
    
    Map<Integer, Consumer<LivingEntity>> timeLine = Maps.newHashMap();
    
    public AdditionalTimeLineTickAction(Map<Integer, Consumer<LivingEntity>> timeLine) {
        this.timeLine.putAll(timeLine);
    }
    
    @Override
    public void accept(LivingEntity livingEntity) {
        long elapsed = ComboState.getElapsed(livingEntity);
        int adjustElapsed = (int) elapsed;
        
        BladeStateAccess.of(livingEntity.getMainHandItem()).ifPresent(state -> {
            if (state.getLastProcessedComboTick() != adjustElapsed) {
                return;
            }
            
            Consumer<LivingEntity> action = timeLine.get(adjustElapsed);
            if (action != null) {
                action.accept(livingEntity);
            }
        });
    }
}
