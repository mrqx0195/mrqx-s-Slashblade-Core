package net.mrqx.sbr_core.entity;

import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.entity.Projectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.mrqx.sbr_core.utils.MrqxSlayerStyleArts;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 隔空瞬步专用的幻影剑实体。
 * <p>
 * 碰撞目标后触发隔空瞬步传送，并在首次 Tick 强制命中指定目标以实现位置同步。
 */
public class EntityAirTrickSummonedSword extends EntityAbstractSummonedSword {
    @Nullable
    private Entity target;
    private boolean shouldUntouchable;
    
    public EntityAirTrickSummonedSword(EntityType<? extends Projectile> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    /**
     * 获取此剑预设的目标实体。
     */
    public @Nullable Entity getTarget() {
        return target;
    }
    
    /**
     * 设置此剑预设的目标实体。
     */
    public void setTarget(Entity target) {
        this.target = target;
    }
    
    /**
     * 获取传送后是否启用无敌帧。
     */
    public boolean isShouldUntouchable() {
        return shouldUntouchable;
    }
    
    /**
     * 设置传送后是否启用无敌帧。
     */
    public void setShouldUntouchable(boolean shouldUntouchable) {
        this.shouldUntouchable = shouldUntouchable;
    }
    
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (getOwner() instanceof LivingEntity living) {
            LivingEntity target = living.getLastHurtMob();
            if (target != null && Objects.equals(this.getHitEntity(), target)) {
                MrqxSlayerStyleArts.doAirTrickTeleport(living, target);
            }
        }
    }
    
    @Override
    public void tick() {
        if (this.target != null && this.getPersistentData().getBoolean("doForceHit")) {
            this.doForceHitEntity(this.target);
            this.getPersistentData().remove("doForceHit");
        }
        super.tick();
    }
}
