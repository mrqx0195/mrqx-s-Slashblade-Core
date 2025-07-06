package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;

public class MrqxSummonedSwordArts {
    public static final TriConsumer<LivingEntity, LivingEntity, Double> BASE_SUMMONED_SWORD = (livingEntity, target, damage) -> {
        AdvancementHelper.grantCriterion(livingEntity, SummonedSwordArts.ADVANCEMENT_SUMMONEDSWORDS);
        Level worldIn = livingEntity.level();
        Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ());
        EntityAbstractSummonedSword summonedSword = new EntityAbstractSummonedSword(SlashBlade.RegistryEvents.SummonedSword, worldIn);
        Vec3 pos = livingEntity.getEyePosition(1.0f).add(VectorHelper.getVectorForRotation(0.0f, livingEntity.getViewYRot(0) + 90).scale(livingEntity.level().random.nextDouble() > 0.5 ? 1 : -1));
        summonedSword.setPos(pos.x, pos.y, pos.z);
        summonedSword.setDamage(damage);
        summonedSword.setOwner(livingEntity);
        livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> summonedSword.setColor(state.getColorCode()));
        summonedSword.setRoll(livingEntity.getRandom().nextFloat() * 360.0f);
        Vec3 dir = targetPos.subtract(pos).normalize();
        summonedSword.shoot(dir.x, dir.y, dir.z, 3.0f, 0.0f);
        worldIn.addFreshEntity(summonedSword);
        livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
    };

    public static final TriConsumer<LivingEntity, Double, Integer> SPIRAL_SWORD = (livingEntity, damage, count) -> {
        boolean alreadySummoned = livingEntity.getPassengers().stream().anyMatch(e -> e instanceof EntitySpiralSwords);
        if (alreadySummoned) {
            List<Entity> list = livingEntity.getPassengers().stream().filter(e -> e instanceof EntitySpiralSwords).toList();
            list.forEach(e -> ((EntitySpiralSwords) e).doFire());
        } else {
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                AdvancementHelper.grantCriterion(livingEntity, SummonedSwordArts.ADVANCEMENT_SPIRAL_SWORDS);
                Level worldIn = livingEntity.level();
                for (int i = 0; i < count; i++) {
                    EntitySpiralSwords spiralSwords = new EntitySpiralSwords(SlashBlade.RegistryEvents.SpiralSwords, worldIn);
                    spiralSwords.setPos(livingEntity.position());
                    spiralSwords.setOwner(livingEntity);
                    spiralSwords.setColor(state.getColorCode());
                    spiralSwords.setRoll(0);
                    spiralSwords.setDamage(damage);
                    spiralSwords.startRiding(livingEntity, true);
                    spiralSwords.setDelay(360 / count * i);
                    worldIn.addFreshEntity(spiralSwords);
                    livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
                }
            });
        }
    };

    public static final QuadConsumer<LivingEntity, LivingEntity, Double, Integer> STORM_SWORD = (livingEntity, target, damage, count) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                Level worldIn = livingEntity.level();
                if (!target.isAlive() || target.isRemoved()) {
                    return;
                }
                AdvancementHelper.grantCriterion(livingEntity, SummonedSwordArts.ADVANCEMENT_STORM_SWORDS);
                for (int i = 0; i < count; i++) {
                    EntityStormSwords stormSwords = new EntityStormSwords(SlashBlade.RegistryEvents.StormSwords, worldIn);
                    stormSwords.setPos(livingEntity.position());
                    stormSwords.setOwner(livingEntity);
                    stormSwords.setColor(state.getColorCode());
                    stormSwords.setRoll(0);
                    stormSwords.setDamage(damage);
                    stormSwords.startRiding(target, true);
                    stormSwords.setDelay(360 / count * i);
                    worldIn.addFreshEntity(stormSwords);
                    livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
                }
            });

    public static final QuadConsumer<LivingEntity, LivingEntity, Double, Integer> BLISTERING_SWORD = (livingEntity, target, damage, count) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                Level worldIn = livingEntity.level();
                if (!target.isAlive() || target.isRemoved()) {
                    return;
                }
                AdvancementHelper.grantCriterion(livingEntity, SummonedSwordArts.ADVANCEMENT_BLISTERING_SWORDS);
                for (int i = 0; i < count; i++) {
                    EntityBlisteringSwords blisteringSwords = new EntityBlisteringSwords(SlashBlade.RegistryEvents.BlisteringSwords, worldIn);
                    blisteringSwords.setPos(livingEntity.position());
                    blisteringSwords.setOwner(livingEntity);
                    blisteringSwords.setColor(state.getColorCode());
                    blisteringSwords.setRoll(0);
                    blisteringSwords.setDamage(damage);
                    blisteringSwords.startRiding(livingEntity, true);
                    blisteringSwords.setDelay(i);
                    worldIn.addFreshEntity(blisteringSwords);
                    livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
                }
            });

    public static final QuadConsumer<LivingEntity, LivingEntity, Double, Integer> HEAVY_RAIN_SWORD = (livingEntity, target, damage, count) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent((state) -> {
                Level worldIn = livingEntity.level();
                AdvancementHelper.grantCriterion(livingEntity, SummonedSwordArts.ADVANCEMENT_HEAVY_RAIN_SWORDS);
                int rank = livingEntity.getCapability(CapabilityConcentrationRank.RANK_POINT).map(r -> r.getRank(worldIn.getGameTime()).level).orElse(0);
                Vec3 basePos;
                if (target != null) {
                    basePos = target.position();
                } else {
                    Vec3 forwardDir = calculateViewVector(0, livingEntity.getYRot());
                    basePos = livingEntity.getPosition(0).add(forwardDir.scale(5));
                }
                float yOffset = 7;
                basePos = basePos.add(0, yOffset, 0);
                EntityHeavyRainSwords rainSwords = new EntityHeavyRainSwords(SlashBlade.RegistryEvents.HeavyRainSwords, worldIn);
                rainSwords.setOwner(livingEntity);
                rainSwords.setColor(state.getColorCode());
                rainSwords.setRoll(0);
                rainSwords.setDamage(damage);
                rainSwords.startRiding(livingEntity, true);
                rainSwords.setDelay(0);
                rainSwords.setPos(basePos);
                rainSwords.setXRot(-90);
                worldIn.addFreshEntity(rainSwords);
                for (int i = 0; i < count; i++) {
                    EntityHeavyRainSwords heavyRainSwords = new EntityHeavyRainSwords(SlashBlade.RegistryEvents.HeavyRainSwords, worldIn);
                    heavyRainSwords.setOwner(livingEntity);
                    heavyRainSwords.setColor(state.getColorCode());
                    heavyRainSwords.setRoll(0);
                    heavyRainSwords.setDamage(damage);
                    heavyRainSwords.startRiding(livingEntity, true);
                    heavyRainSwords.setDelay(i);
                    heavyRainSwords.setSpread(basePos);
                    heavyRainSwords.setXRot(-90);
                    worldIn.addFreshEntity(heavyRainSwords);
                    livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
                }

            });

    public static Vec3 calculateViewVector(float x, float y) {
        float f = x * ((float) Math.PI / 180F);
        float f1 = -y * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }
}
