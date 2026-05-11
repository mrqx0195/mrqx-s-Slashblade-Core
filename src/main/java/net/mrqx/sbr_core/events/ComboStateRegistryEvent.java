package net.mrqx.sbr_core.events;

import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * 连段状态注册事件，在 Mod 总线派发。
 * <p>
 * 当 {@link ComboState.Builder} 构建完成 {@link ComboState} 后触发，
 * 允许其他模组在该事件中修改连段的行为（如添加 Tick 动作）。
 */
public class ComboStateRegistryEvent extends Event implements IModBusEvent {
    private final ComboState.Builder builder;
    private final ComboState combo;
    
    public ComboStateRegistryEvent(ComboState.Builder builder, ComboState combo) {
        this.builder = builder;
        this.combo = combo;
    }
    
    /**
     * 获取用于修改连段行为的 Builder。
     */
    public ComboState.Builder getBuilder() {
        return this.builder;
    }
    
    /**
     * 获取被构建的连段状态。
     */
    public ComboState getCombo() {
        return this.combo;
    }
}
