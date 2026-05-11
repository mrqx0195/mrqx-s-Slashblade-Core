package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 拔刀剑攻击工具集。
 * <p>
 * 提供连段判断、攻击类型分发（地面/空中）、蓄力斩判定、终结技触发等核心战斗逻辑。
 * 通过 {@link ComboState} 状态机构管理连段推进与打断规则。
 */
public class SlashBladeAttackUtils {
    public static final String VOID_SLASH_COUNTER_KEY = "sbr_core.voidSlashCounter";
    public static final String SUPER_JUDGEMENT_CUT_COUNTER_KEY = "sbr_core.superJudgementCutCounter";
    
    /**
     * 判断实体主手是否持有拔刀剑。
     */
    public static boolean isHoldingSlashBlade(LivingEntity livingEntity) {
        return !livingEntity.getMainHandItem().isEmpty() && BladeStateAccess.of(livingEntity.getMainHandItem()).isPresent();
    }
    
    /**
     * 判断当前连段是否允许被打断以执行其他动作。
     */
    public static boolean canInterruptCombo(LivingEntity livingEntity, boolean powerful) {
        return BladeStateAccess.of(livingEntity.getMainHandItem()).map(state -> {
            ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
            ComboState current = ComboStateRegistry.REGISTRY.get(currentLoc);
            if (current != null) {
                ComboState next = ComboStateRegistry.REGISTRY.get(current.getNextOfTimeout(livingEntity));
                if (powerful) {
                    return !ADVANCE_UNINTERRUPTIBLE_COMBO.contains(current) && !ADVANCE_UNINTERRUPTIBLE_COMBO.contains(next);
                }
                return !UNINTERRUPTIBLE_COMBO.contains(current) && !UNINTERRUPTIBLE_COMBO.contains(next);
            }
            return true;
        }).orElse(false);
    }
    
    /**
     * 尝试使用空中下劈斩（Aerial Cleave）。
     */
    public static void tryAerialCleave(LivingEntity livingEntity, ISlashBladeState state) {
        if (livingEntity.onGround()) {
            return;
        }
        state.updateComboSeq(livingEntity, ComboStateRegistry.AERIAL_CLEAVE.getId());
    }
    
    /**
     * 地面攻击：根据是否在地面进入连段 A 或空中下劈斩。
     */
    public static void groundAttack(LivingEntity livingEntity, ISlashBladeState state) {
        if (livingEntity.onGround()) {
            state.updateComboSeq(livingEntity, ComboStateRegistry.COMBO_A1.getId());
        } else {
            tryAerialCleave(livingEntity, state);
        }
    }
    
    /**
     * 空中攻击：根据是否在地面、是否允许迅冲斩来分发连段。
     */
    public static void airAttack(LivingEntity livingEntity, ISlashBladeState state, boolean canRapidSlash) {
        if (!state.resolvCurrentComboState(livingEntity).equals(ComboStateRegistry.UPPERSLASH.getId())) {
            if (livingEntity.onGround()) {
                if (canRapidSlash && !livingEntity.isPassenger()) {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.RAPID_SLASH.getId());
                } else {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.UPPERSLASH.getId());
                }
            } else {
                state.updateComboSeq(livingEntity, ComboStateRegistry.AERIAL_RAVE_A1.getId());
            }
        }
    }
    
    /**
     * 迅冲斩攻击：面向目标并切换至迅冲斩连段。
     */
    public static void rapidSlashAttack(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target) {
        ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
        ComboState current = ComboStateRegistry.REGISTRY.get(currentLoc);
        livingEntity.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null) {
            ResourceLocation next = current.getNext(livingEntity);
            if (currentLoc.equals(ComboStateRegistry.NONE.getId()) || next.equals(ComboStateRegistry.NONE.getId())) {
                if (livingEntity.onGround()) {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.RAPID_SLASH.getId());
                }
            }
        }
    }
    
    /**
     * 执行剑技（SlashArts），根据是否 Just 判定使用不同蓄力时间与类型。
     */
    public static boolean doSlashArts(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target, boolean isJust) {
        livingEntity.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        int elapsed;
        SlashArts.ArtsType type;
        if (isJust) {
            elapsed = 10;
            type = SlashArts.ArtsType.Jackpot;
        } else {
            elapsed = 20;
            type = SlashArts.ArtsType.Success;
        }
        ResourceLocation comboLoc = state.getSlashArts().doArts(type, livingEntity);
        SlashBladeEvent.PerformSlashArtEvent event = new SlashBladeEvent.PerformSlashArtEvent(livingEntity, elapsed, state, comboLoc, type);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            comboLoc = event.getComboState();
            ComboState combo = ComboStateRegistry.REGISTRY.get(comboLoc);
            if (combo != null && !Objects.equals(comboLoc, ComboStateRegistry.NONE.getId())) {
                state.updateComboSeq(livingEntity, comboLoc);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 尝试触发剑技。根据当前连段判定是否允许蓄力斩击，并区分普通/高级/快速蓄力组合。
     */
    public static boolean trySlashArts(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target, boolean isJust, boolean powerful) {
        if (JustSlashArtManager.getJustCooldown(livingEntity) > 0) {
            return false;
        }
        ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
        ComboState current = ComboStateRegistry.REGISTRY.get(currentLoc);
        if (current != null) {
            ComboState next = ComboStateRegistry.REGISTRY.get(current.getNextOfTimeout(livingEntity));
            if (powerful && ADVANCE_CHARGE_COMBO.contains(current)) {
                return doSlashArts(livingEntity, state, target, isJust);
            } else if (isJust && QUICK_CHARGE_COMBO.contains(current)) {
                return doSlashArts(livingEntity, state, target, true);
            } else if (CHARGE_COMBO.contains(current)) {
                return doSlashArts(livingEntity, state, target, isJust);
            } else {
                return isJust && QUICK_CHARGE_COMBO.contains(next) && doSlashArts(livingEntity, state, target, true);
            }
        }
        return false;
    }
    
    /**
     * 发动虚无刀界（Void Slash）：若为强力版本则先瞬移至目标附近。
     */
    public static void voidSlash(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target, boolean powerful) {
        if (powerful) {
            SlashBladeMovementUtils.tryTrickToTarget(livingEntity, target);
        }
        state.updateComboSeq(livingEntity, ComboStateRegistry.VOID_SLASH.getId());
    }
    
    /**
     * 普通拔刀剑攻击的主分发逻辑。根据当前连段状态、位置、设置等信息选择具体的攻击方式。
     */
    public static void normalSlashBladeAttack(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target,
                                              boolean canRapidSlash, boolean preferAirAttack, boolean canVoidSlash, boolean powerful) {
        ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
        ComboState current = ComboStateRegistry.REGISTRY.get(currentLoc);
        CompoundTag data = livingEntity.getPersistentData();
        livingEntity.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null) {
            ResourceLocation nextLoc = current.getNext(livingEntity);
            if (currentLoc.equals(ComboStateRegistry.NONE.getId()) || nextLoc.equals(ComboStateRegistry.NONE.getId())) {
                JustSlashArtManager.resetJustCount(livingEntity);
                if (canVoidSlash && livingEntity.onGround() && data.getInt(VOID_SLASH_COUNTER_KEY) <= 0) {
                    voidSlash(livingEntity, state, target, powerful);
                    data.putInt(VOID_SLASH_COUNTER_KEY, 1000);
                } else {
                    if (preferAirAttack) {
                        airAttack(livingEntity, state, canRapidSlash);
                    } else {
                        groundAttack(livingEntity, state);
                    }
                }
            } else if (current.equals(ComboStateRegistry.RAPID_SLASH.get()) && preferAirAttack && canInterruptCombo(livingEntity, powerful)) {
                List<Entity> hits = AttackManager.areaAttack(livingEntity, KnockBacks.toss.action, 0.44f, true, true, true);
                if (!hits.isEmpty()) {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.RISING_STAR.getId());
                    AdvancementHelper.grantCriterion(livingEntity, AdvancementHelper.ADVANCEMENT_RISING_STAR);
                }
            } else {
                if (!nextLoc.equals(currentLoc) && livingEntity instanceof ISlashBladeEntity slashBladeEntity
                    && slashBladeEntity.canProgressCombo(target, currentLoc, nextLoc)) {
                    state.progressCombo(livingEntity);
                }
            }
        }
    }
    
    public static final Set<ComboState> CHARGE_COMBO = new HashSet<>(Set.of(
        ComboStateRegistry.COMBO_C.get(),
        ComboStateRegistry.COMBO_A4.get(),
        ComboStateRegistry.COMBO_A5.get(),
        ComboStateRegistry.COMBO_B7.get(),
        ComboStateRegistry.COMBO_B_END2.get(),
        ComboStateRegistry.AERIAL_RAVE_A3.get(),
        ComboStateRegistry.AERIAL_RAVE_B4.get(),
        ComboStateRegistry.UPPERSLASH.get(),
        ComboStateRegistry.UPPERSLASH_JUMP.get(),
        ComboStateRegistry.AERIAL_CLEAVE_LANDING.get(),
        ComboStateRegistry.RAPID_SLASH_END.get(),
        ComboStateRegistry.RISING_STAR.get()
    ));
    
    public static final Set<ComboState> ADVANCE_CHARGE_COMBO = new HashSet<>(Set.of(
        ComboStateRegistry.JUDGEMENT_CUT_SLASH.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.get(),
        ComboStateRegistry.JUDGEMENT_CUT_END.get()
    
    ));
    
    public static final Set<ComboState> QUICK_CHARGE_COMBO = new HashSet<>(Set.of(
        ComboStateRegistry.COMBO_A1_END.get(),
        ComboStateRegistry.COMBO_A2_END.get(),
        ComboStateRegistry.COMBO_C_END.get(),
        ComboStateRegistry.COMBO_A3_END3.get(),
        ComboStateRegistry.COMBO_A4_END.get(),
        ComboStateRegistry.COMBO_A4_EX_END2.get(),
        ComboStateRegistry.COMBO_A5_END.get(),
        ComboStateRegistry.COMBO_B7_END3.get(),
        ComboStateRegistry.COMBO_B_END3.get(),
        ComboStateRegistry.AERIAL_RAVE_A1_END.get(),
        ComboStateRegistry.AERIAL_RAVE_A2_END2.get(),
        ComboStateRegistry.AERIAL_RAVE_A3_END.get(),
        ComboStateRegistry.AERIAL_RAVE_B3_END.get(),
        ComboStateRegistry.AERIAL_RAVE_B4_END.get(),
        ComboStateRegistry.UPPERSLASH_END.get(),
        ComboStateRegistry.UPPERSLASH_JUMP_END.get(),
        ComboStateRegistry.AERIAL_CLEAVE_END.get(),
        ComboStateRegistry.RAPID_SLASH_QUICK.get(),
        ComboStateRegistry.RAPID_SLASH_END2.get(),
        ComboStateRegistry.RISING_STAR_END.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SHEATH.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SHEATH_AIR.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SHEATH_JUST.get()
    ));
    
    public static final Set<ComboState> UNINTERRUPTIBLE_COMBO = new HashSet<>(Set.of(
        ComboStateRegistry.COMBO_A1.get(),
        ComboStateRegistry.COMBO_A2.get(),
        ComboStateRegistry.COMBO_C.get(),
        ComboStateRegistry.COMBO_A3.get(),
        ComboStateRegistry.COMBO_A4.get(),
        ComboStateRegistry.COMBO_A4_EX.get(),
        ComboStateRegistry.COMBO_A5.get(),
        ComboStateRegistry.COMBO_B1.get(),
        ComboStateRegistry.COMBO_B7.get(),
        ComboStateRegistry.COMBO_B_END2.get(),
        ComboStateRegistry.AERIAL_RAVE_A1.get(),
        ComboStateRegistry.AERIAL_RAVE_A2.get(),
        ComboStateRegistry.AERIAL_RAVE_A3.get(),
        ComboStateRegistry.AERIAL_RAVE_B3.get(),
        ComboStateRegistry.AERIAL_RAVE_B4.get(),
        ComboStateRegistry.UPPERSLASH.get(),
        ComboStateRegistry.UPPERSLASH_JUMP.get(),
        ComboStateRegistry.AERIAL_CLEAVE.get(),
        ComboStateRegistry.RISING_STAR.get(),
        ComboStateRegistry.JUDGEMENT_CUT.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.get(),
        ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.get(),
        ComboStateRegistry.JUDGEMENT_CUT_END.get()
    ));
    
    public static final Set<ComboState> ADVANCE_UNINTERRUPTIBLE_COMBO = new HashSet<>(Set.of(
        ComboStateRegistry.COMBO_C.get(),
        ComboStateRegistry.COMBO_A4_EX.get(),
        ComboStateRegistry.COMBO_A5.get(),
        ComboStateRegistry.COMBO_B7.get(),
        ComboStateRegistry.COMBO_B_END2.get(),
        ComboStateRegistry.AERIAL_RAVE_B3.get(),
        ComboStateRegistry.UPPERSLASH.get(),
        ComboStateRegistry.AERIAL_CLEAVE.get(),
        ComboStateRegistry.RISING_STAR.get(),
        ComboStateRegistry.JUDGEMENT_CUT.get(),
        ComboStateRegistry.JUDGEMENT_CUT_END.get()
    ));
}
