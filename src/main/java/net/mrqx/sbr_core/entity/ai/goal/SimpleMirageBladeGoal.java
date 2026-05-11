package net.mrqx.sbr_core.entity.ai.goal;

import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.enchantment.Enchantments;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.utils.MrqxSummonedSwordArts;

import javax.annotation.Nullable;

/**
 * 远程幻影剑攻击 AI 目标。
 * <p>
 * 允许持有拔刀剑的 {@link PathfinderMob} 周期性地向目标发射基础幻影剑、
 * 螺旋幻剑阵、怒风幻剑阵、急袭幻剑阵、暴雨幻剑阵等远程剑技。
 * 各剑技独立冷却，由子类可重写的方法提供冷却时间与剑数量参数。
 */
public class SimpleMirageBladeGoal<T extends PathfinderMob & ISlashBladeEntity & RangedAttackMob> extends RangedAttackGoal {
    public final T entity;
    protected boolean canUseBaseSummonedSword;
    protected boolean canUseSpiralSword;
    protected boolean canUseStormSword;
    protected boolean canUseBlisteringSword;
    protected boolean canUseHeavyRainSword;
    protected int baseSummonedSwordCounter = 0;
    protected int spiralSwordCounter = 0;
    protected int stormSwordCounter = 0;
    protected int blisteringSwordCounter = 0;
    protected int heavyRainSwordCounter = 0;
    @Nullable
    public LivingEntity target;
    
    /**
     * @param canUseBaseSummonedSword 是否启用基础幻影剑
     * @param canUseSpiralSword       是否启用环形幻剑阵
     * @param canUseStormSword        是否启用怒风幻剑阵
     * @param canUseBlisteringSword   是否启用急袭幻剑阵
     * @param canUseHeavyRainSword    是否启用暴雨幻剑阵
     */
    public SimpleMirageBladeGoal(T rangedAttackMob, double speedModifier,
                                 boolean canUseBaseSummonedSword, boolean canUseSpiralSword, boolean canUseStormSword,
                                 boolean canUseBlisteringSword, boolean canUseHeavyRainSword) {
        super(rangedAttackMob, speedModifier, 0, 0);
        this.entity = rangedAttackMob;
        this.canUseBaseSummonedSword = canUseBaseSummonedSword;
        this.canUseSpiralSword = canUseSpiralSword;
        this.canUseStormSword = canUseStormSword;
        this.canUseBlisteringSword = canUseBlisteringSword;
        this.canUseHeavyRainSword = canUseHeavyRainSword;
    }
    
    /**
     * 此目标不可被打断。
     */
    @Override
    public boolean isInterruptable() {
        return false;
    }
    
    /**
     * 当实体存在有效目标时可启用。
     */
    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.entity.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            this.target = livingentity;
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 只要目标仍然有效则继续运行。
     */
    @Override
    public boolean canContinueToUse() {
        return this.canUse() || (this.target != null && this.target.isAlive());
    }
    
    /**
     * 每个 tick 递减各剑技冷却计数器，并在冷却归零时按配置发射对应剑技。
     */
    @Override
    public void tick() {
        baseSummonedSwordCounter--;
        spiralSwordCounter--;
        stormSwordCounter--;
        blisteringSwordCounter--;
        heavyRainSwordCounter--;
        
        if (this.target != null) {
            double enchantPower = this.getPowerLevel();
            this.entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
                if (canUseBaseSummonedSword) {
                    if (baseSummonedSwordCounter <= 0) {
                        MrqxSummonedSwordArts.BASE_SUMMONED_SWORD.accept(this.entity, this.target, enchantPower);
                        baseSummonedSwordCounter = getBaseSummonedSwordCooldown();
                    }
                }
                if (canUseSpiralSword) {
                    if (spiralSwordCounter <= 0) {
                        MrqxSummonedSwordArts.SPIRAL_SWORD.accept(this.entity, enchantPower, getSpiralSwordCount());
                        spiralSwordCounter = getSpiralSwordCooldown();
                    }
                }
                if (canUseStormSword) {
                    if (stormSwordCounter <= 0) {
                        MrqxSummonedSwordArts.STORM_SWORD.accept(this.entity, this.target, enchantPower, getStormSwordCount());
                        stormSwordCounter = getStormSwordCooldown();
                    }
                }
                if (canUseBlisteringSword) {
                    if (blisteringSwordCounter <= 0) {
                        MrqxSummonedSwordArts.BLISTERING_SWORD.accept(this.entity, this.target, enchantPower, getBlisteringSwordCount());
                        blisteringSwordCounter = getBlisteringSwordCooldown();
                    }
                }
                if (canUseHeavyRainSword) {
                    if (heavyRainSwordCounter <= 0) {
                        MrqxSummonedSwordArts.HEAVY_RAIN_SWORD.accept(this.entity, this.target, enchantPower, getHeavyRainSwordCount());
                        heavyRainSwordCounter = getHeavyRainSwordCooldown();
                    }
                }
            });
        }
    }
    
    /**
     * 获取当前主手武器上的力量（Power）附魔等级，用于计算幻影剑伤害。
     */
    public double getPowerLevel() {
        return this.entity.getMainHandItem().getEnchantmentLevel(Enchantments.POWER_ARROWS);
    }
    
    /**
     * 基础幻影剑的发射冷却（单位：tick，默认 20）。
     */
    public int getBaseSummonedSwordCooldown() {
        return 20;
    }
    
    /**
     * 环形幻剑阵的发射冷却（默认 200）。
     */
    public int getSpiralSwordCooldown() {
        return 200;
    }
    
    /**
     * 怒风幻剑阵的发射冷却（默认 200）。
     */
    public int getStormSwordCooldown() {
        return 200;
    }
    
    /**
     * 急袭幻剑阵的发射冷却（默认 400）。
     */
    public int getBlisteringSwordCooldown() {
        return 400;
    }
    
    /**
     * 暴雨幻剑阵的发射冷却（默认 600）。
     */
    public int getHeavyRainSwordCooldown() {
        return 600;
    }
    
    /**
     * 环形幻剑阵的生成数量（S 级及以上评价为 8，否则 6）。
     */
    public int getSpiralSwordCount() {
        return IConcentrationRank.ConcentrationRanks.S.level <= this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
            .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) ? 8 : 6;
    }
    
    /**
     * 怒风幻剑阵的生成数量（S 级及以上评价为 8，否则 6）。
     */
    public int getStormSwordCount() {
        return IConcentrationRank.ConcentrationRanks.S.level <= this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
            .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) ? 8 : 6;
    }
    
    /**
     * 急袭幻剑阵的生成数量（S 级及以上评价为 8，否则 6）。
     */
    public int getBlisteringSwordCount() {
        return IConcentrationRank.ConcentrationRanks.S.level <= this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
            .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) ? 8 : 6;
    }
    
    /**
     * 暴雨幻剑阵的生成数量，基于等级评价计算。
     */
    public int getHeavyRainSwordCount() {
        return (9 + Math.min(this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
            .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) - 1, 0)) * 2;
    }
}
