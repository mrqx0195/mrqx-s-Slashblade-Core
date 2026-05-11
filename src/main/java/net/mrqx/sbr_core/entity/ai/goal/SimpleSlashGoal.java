package net.mrqx.sbr_core.entity.ai.goal;

import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.mixin.common.AccessorMeleeAttackGoal;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * 近战拔刀剑攻击 AI 目标。
 * <p>
 * 允许持有拔刀剑的 {@link PathfinderMob} 使用完整的拔刀剑连段系统进行攻击，
 * 支持地面连段、空中连段、迅冲斩、虚无刀界、剑技触发等操作。
 * 可由子类通过构造参数配置是否启用快速连段、空中优先、剑技等特性。
 */
public class SimpleSlashGoal<T extends PathfinderMob & ISlashBladeEntity> extends MeleeAttackGoal {
    protected final T entity;
    protected final int attackCooldown;
    protected boolean canRapidSlash;
    protected boolean preferAirAttack;
    protected boolean canVoidSlash;
    protected boolean canDoSlashArts;
    protected boolean canDoJustSlashArts;
    protected boolean powerful;
    @Nullable
    private ResourceLocation lastComboStateLocation;
    @Nullable
    private Consumer<SimpleSlashGoal<T>> afterSlashConsumer = null;
    
    /**
     * 简化构造器，所有高级特性默认关闭。
     */
    public SimpleSlashGoal(T mob, double speedModifier, int attackCooldown, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.entity = mob;
        this.attackCooldown = attackCooldown;
        this.canRapidSlash = false;
        this.preferAirAttack = false;
        this.canVoidSlash = false;
        this.canDoSlashArts = false;
        this.canDoJustSlashArts = false;
        this.powerful = false;
    }
    
    /**
     * @param canRapidSlash      是否启用迅冲斩
     * @param preferAirAttack    是否优先空中攻击（如跃升斩）
     * @param canVoidSlash       是否启用虚无刀界作为起手
     * @param canDoSlashArts     是否允许触发剑技
     * @param canDoJustSlashArts 是否允许使用 Just 剑技（如完美次元斩）
     * @param powerful           是否为强力版本（影响连段打断判定和瞬步冷却）
     */
    public SimpleSlashGoal(T mob, double speedModifier, int attackCooldown, boolean followingTargetEvenIfNotSeen,
                           boolean canRapidSlash, boolean preferAirAttack, boolean canVoidSlash,
                           boolean canDoSlashArts, boolean canDoJustSlashArts, boolean powerful) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.entity = mob;
        this.attackCooldown = attackCooldown;
        this.canRapidSlash = canRapidSlash;
        this.preferAirAttack = preferAirAttack;
        this.canVoidSlash = canVoidSlash;
        this.canDoSlashArts = canDoSlashArts;
        this.canDoJustSlashArts = canDoJustSlashArts;
        this.powerful = powerful;
    }
    
    /**
     * 在攻击范围内时执行一次拔刀剑攻击，推进连段并重置冷却。
     */
    @Override
    protected void checkAndPerformAttack(LivingEntity enemy) {
        if (this.entity.isWithinMeleeAttackRange(enemy) && this.getTicksUntilNextAttack() <= 0) {
            this.entity.swing(InteractionHand.MAIN_HAND);
            this.doSlashBladeAttack(enemy);
            BladeStateAccess.of(this.entity.getMainHandItem()).ifPresent(state ->
                this.setLastComboStateLocation(state.getComboSeq()));
            if (this instanceof AccessorMeleeAttackGoal accessor) {
                accessor.sbr_core$setLastCanUseCheck(this.entity.level().getGameTime() - (20 - this.attackCooldown));
                accessor.sbr_core$setTicksUntilNextAttack(this.attackCooldown);
            }
            if (this.getAfterSlashConsumer() != null) {
                this.getAfterSlashConsumer().accept(this);
            }
        }
    }
    
    /**
     * 执行拔刀剑攻击的分发逻辑，根据距离、位置与配置选择连段/剑技。
     */
    protected void doSlashBladeAttack(LivingEntity target) {
        BladeStateAccess.of(this.entity.getMainHandItem()).ifPresent(state -> {
            state.setTargetEntityId(this.entity.getTarget());
            if (this.entity.distanceTo(target) <= TargetSelector.getResolvedReach(this.entity)) {
                if (canDoSlashArts) {
                    if (SlashBladeAttackUtils.trySlashArts(this.entity, state, target, canDoJustSlashArts, powerful)) {
                        return;
                    }
                }
                SlashBladeAttackUtils.normalSlashBladeAttack(this.entity, state, target, canRapidSlash, preferAirAttack, canVoidSlash, powerful);
            } else {
                if (!this.entity.onGround() && this.entity.getY() - target.getY() > 5) {
                    SlashBladeAttackUtils.tryAerialCleave(this.entity, state);
                }
                if (canRapidSlash && SlashBladeAttackUtils.canInterruptCombo(this.entity, powerful) && !this.entity.isPassenger()) {
                    SlashBladeAttackUtils.rapidSlashAttack(this.entity, state, target);
                } else if (canDoSlashArts) {
                    SlashBladeAttackUtils.trySlashArts(this.entity, state, target, canDoJustSlashArts, powerful);
                }
            }
        });
    }
    
    @Override
    public void resetAttackCooldown() {
        super.resetAttackCooldown();
    }
    
    /**
     * 只有在主手持拔刀剑时才启用此目标。
     */
    @Override
    public boolean canUse() {
        return super.canUse() && SlashBladeAttackUtils.isHoldingSlashBlade(mob);
    }
    
    /**
     * 获取攻击后回调消费者。
     */
    @Nullable
    public Consumer<SimpleSlashGoal<T>> getAfterSlashConsumer() {
        return afterSlashConsumer;
    }
    
    /**
     * 设置攻击后回调消费者，可用于链式调用。
     */
    public SimpleSlashGoal<T> setAfterSlashConsumer(@Nullable Consumer<SimpleSlashGoal<T>> afterSlashConsumer) {
        this.afterSlashConsumer = afterSlashConsumer;
        return this;
    }
    
    /**
     * 获取上一次攻击后的连段状态 ID。
     */
    @Nullable
    public ResourceLocation getLastComboStateLocation() {
        return lastComboStateLocation;
    }
    
    /**
     * 记录上一次攻击后的连段状态 ID。
     */
    protected void setLastComboStateLocation(ResourceLocation lastComboStateLocation) {
        this.lastComboStateLocation = lastComboStateLocation;
    }
}
