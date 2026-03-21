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

    @Override
    public boolean isInterruptable() {
        return false;
    }

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

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || (this.target != null && this.target.isAlive());
    }

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

    public double getPowerLevel() {
        return this.entity.getMainHandItem().getEnchantmentLevel(Enchantments.POWER_ARROWS);
    }

    public int getBaseSummonedSwordCooldown() {
        return 20;
    }

    public int getSpiralSwordCooldown() {
        return 200;
    }

    public int getStormSwordCooldown() {
        return 200;
    }

    public int getBlisteringSwordCooldown() {
        return 400;
    }

    public int getHeavyRainSwordCooldown() {
        return 600;
    }

    public int getSpiralSwordCount() {
        return IConcentrationRank.ConcentrationRanks.S.level <= this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) ? 8 : 6;
    }

    public int getStormSwordCount() {
        return IConcentrationRank.ConcentrationRanks.S.level <= this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) ? 8 : 6;
    }

    public int getBlisteringSwordCount() {
        return IConcentrationRank.ConcentrationRanks.S.level <= this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) ? 8 : 6;
    }

    public int getHeavyRainSwordCount() {
        return (9 + Math.min(this.entity.getCapability(CapabilityConcentrationRank.RANK_POINT)
                .map(r -> r.getRank(this.entity.level().getGameTime()).level).orElse(0) - 1, 0)) * 2;
    }
}
