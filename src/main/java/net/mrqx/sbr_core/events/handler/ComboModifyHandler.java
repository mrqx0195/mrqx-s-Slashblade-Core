package net.mrqx.sbr_core.events.handler;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import net.minecraft.resources.ResourceLocation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.events.ComboStateRegistryEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 连段修改事件处理器。
 * <p>
 * 监听 {@link ComboStateRegistryEvent}，为特定的连段添加自定义 Tick 动作，
 * 例如为上斩添加跃升斩过渡逻辑。
 */
@EventBusSubscriber
public class ComboModifyHandler {
    private static final ResourceLocation UPPER_SLASH_NAME = SlashBlade.prefix("upperslash_jump");
    
    /**
     * 在连段状态注册时，为上斩（Upper Slash）添加跃升斩（Upper Slash Jump）的过渡 Tick 动作。
     */
    @SubscribeEvent
    public static void onComboStateRegistryEvent(ComboStateRegistryEvent event) {
        ComboState.Builder builder = event.getBuilder();
        ComboState combo = event.getCombo();
        if (combo.getStartFrame() == ComboMovementModifiers.UPPER_SLASH.startFrame
            && combo.getEndFrame() == ComboMovementModifiers.UPPER_SLASH.endFrame
            && combo.getPriority() == ComboMovementModifiers.UPPER_SLASH.priority) {
            builder.addTickAction(ComboState.TimeLineTickAction.getBuilder().put(9, livingEntity -> {
                if (livingEntity instanceof ISlashBladeEntity slashBladeEntity && slashBladeEntity.useUpperSlashJump()) {
                    BladeStateAccess.of(livingEntity.getMainHandItem()).ifPresent(state -> {
                        state.updateComboSeq(livingEntity, UPPER_SLASH_NAME);
                        AdvancementHelper.grantCriterion(livingEntity, AdvancementHelper.ADVANCEMENT_UPPERSLASH_JUMP);
                    });
                }
            }).build());
        }
    }
    
    @SuppressWarnings({"SameParameterValue"})
    private enum ComboMovementModifiers {
        UPPER_SLASH(1600, 1659, 90);
        
        public final int startFrame;
        public final int endFrame;
        public final int priority;
        
        ComboMovementModifiers(int startFrame, int endFrame, int priority) {
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            this.priority = priority;
        }
    }
}
