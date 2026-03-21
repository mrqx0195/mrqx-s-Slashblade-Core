package net.mrqx.sbr_core.entity.ai.goal;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
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

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
            this.entity.swing(InteractionHand.MAIN_HAND);
            this.doSlashBladeAttack(enemy);
            this.entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state ->
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

    @Override
    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.canRapidSlash ? this.entity.getMeleeAttackRangeSqr(attackTarget) * 3 : this.entity.getMeleeAttackRangeSqr(attackTarget);
    }

    protected void doSlashBladeAttack(LivingEntity target) {
        this.entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
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

    @Override
    public boolean canUse() {
        return super.canUse() && SlashBladeAttackUtils.isHoldingSlashBlade(mob);
    }

    @Nullable
    public Consumer<SimpleSlashGoal<T>> getAfterSlashConsumer() {
        return afterSlashConsumer;
    }

    public SimpleSlashGoal<T> setAfterSlashConsumer(@Nullable Consumer<SimpleSlashGoal<T>> afterSlashConsumer) {
        this.afterSlashConsumer = afterSlashConsumer;
        return this;
    }

    @Nullable
    public ResourceLocation getLastComboStateLocation() {
        return lastComboStateLocation;
    }

    protected void setLastComboStateLocation(ResourceLocation lastComboStateLocation) {
        this.lastComboStateLocation = lastComboStateLocation;
    }
}
