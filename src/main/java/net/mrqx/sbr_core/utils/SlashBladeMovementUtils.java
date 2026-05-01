package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class SlashBladeMovementUtils {
    public static final String TRICK_COOL_DOWN = "sbr_core.trickCooldown";
    
    /**
     * 检查是否可发动瞬步类技能
     */
    public static boolean canTrick(LivingEntity maid, boolean powerful) {
        CompoundTag data = maid.getPersistentData();
        return data.getInt(SlashBladeMovementUtils.TRICK_COOL_DOWN) <= 0
            && SlashBladeAttackUtils.canInterruptCombo(maid, powerful)
            && maid.getVehicle() == null;
    }
    
    /**
     * 尝试使用 隔空瞬步
     */
    public static Boolean airTrickCheck(LivingEntity livingEntity, Float distance, Double reach) {
        if (distance > reach) {
            return MrqxSlayerStyleArts.AIR_TRICK.apply(livingEntity, true);
        }
        return false;
    }
    
    /**
     * 尝试使用 瞬步退行
     */
    public static void trickDownCheck(LivingEntity livingEntity) {
        if (livingEntity.fallDistance > 2) {
            livingEntity.fallDistance = 0;
            MrqxSlayerStyleArts.TRICK_DOWN.apply(livingEntity, true);
        }
    }
    
    /**
     * 尝试使用闪避
     */
    public static void tryTrickDodge(LivingEntity livingEntity, @Nullable Entity target) {
        RandomSource random = livingEntity.level().random;
        double oldX = livingEntity.position().x;
        double oldY = livingEntity.position().y;
        double oldZ = livingEntity.position().z;
        if (!livingEntity.level().isClientSide() && livingEntity.isAlive()) {
            for (int i = 0; i < 16; ++i) {
                double x = livingEntity.getX() + (random.nextDouble() - 0.5) * 16;
                double y = target != null ? target.getY() : livingEntity.getY();
                double z = livingEntity.getZ() + (random.nextDouble() - 0.5) * 16;
                if (livingEntity.randomTeleport(x, y, z, false)
                    && MrqxSlayerStyleArts.TRICK_DODGE.apply(livingEntity, true, false, livingEntity.position())) {
                    livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 46);
                    break;
                } else {
                    livingEntity.teleportTo(oldX, oldY, oldZ);
                }
            }
        }
    }
    
    /**
     * 尝试瞬移至目标
     */
    public static void tryTrickToTarget(LivingEntity livingEntity, Entity target) {
        RandomSource random = livingEntity.level().random;
        double oldX = livingEntity.position().x;
        double oldY = livingEntity.position().y;
        double oldZ = livingEntity.position().z;
        if (!livingEntity.level().isClientSide() && livingEntity.isAlive()) {
            for (int i = 0; i < 16; ++i) {
                double reach = TargetSelector.getResolvedReach(livingEntity);
                reach *= reach;
                double x = target.getX() + (random.nextDouble() - 0.5) * reach * 0.8;
                double y = target.getY();
                double z = target.getZ() + (random.nextDouble() - 0.5) * reach * 0.8;
                if (livingEntity.randomTeleport(x, y, z, false)
                    && MrqxSlayerStyleArts.TRICK_DODGE.apply(livingEntity, true, false, livingEntity.position())
                    && TargetSelector.getTargettableEntitiesWithinAABB(livingEntity.level(), livingEntity).contains(target)) {
                    livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 46);
                    break;
                } else {
                    livingEntity.teleportTo(oldX, oldY, oldZ);
                }
            }
        }
    }
    
    public static void tickSlashBladeTrick(LivingEntity livingEntity, Entity target,
                                           boolean canAirTrick, boolean canTrickDown, boolean canTrickDodge, boolean powerful) {
        float distance = livingEntity.distanceTo(target);
        double reach = TargetSelector.getResolvedReach(livingEntity);
        CompoundTag data = livingEntity.getPersistentData();
        data.putInt(TRICK_COOL_DOWN, data.getInt(TRICK_COOL_DOWN) - (powerful ? 2 : 1));
        
        boolean canTrick = SlashBladeMovementUtils.canTrick(livingEntity, powerful);
        boolean hasTrick = false;
        
        if (canAirTrick && distance > reach) {
            if (!SlashBladeMovementUtils.airTrickCheck(livingEntity, distance, reach)) {
                SlashBladeMovementUtils.tryTrickToTarget(livingEntity, target);
            }
            hasTrick = true;
            data.putInt(TRICK_COOL_DOWN, 60);
            livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 46);
        }
        
        if (canTrick && !hasTrick) {
            if (canTrickDown && !livingEntity.onGround()) {
                SlashBladeMovementUtils.trickDownCheck(livingEntity);
                data.putInt(TRICK_COOL_DOWN, 60);
                livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 46);
            } else if (canTrickDodge) {
                SlashBladeMovementUtils.tryTrickDodge(livingEntity, target);
                data.putInt(TRICK_COOL_DOWN, 60);
                livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 46);
            }
        }
    }
}
