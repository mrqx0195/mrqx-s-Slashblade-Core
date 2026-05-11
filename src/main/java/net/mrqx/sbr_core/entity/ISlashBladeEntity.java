package net.mrqx.sbr_core.entity;

import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 拔刀剑实体的能力接口。
 * <p>
 * 定义所有可以使用拔刀剑进行战斗的实体（包括自定义生物、Boss 等）所需实现的功能，
 * 包括连段推进判定、可用连段校验、攻击目标列表筛选、VMD 动画状态管理等。
 */
public interface ISlashBladeEntity {
    /**
     * 获取当前播放的 VMD 动画。
     */
    @Nullable
    default VanillaConvertedVmdAnimation getCurrentAnimation() {
        return null;
    }
    
    /**
     * 设置当前播放的 VMD 动画。
     */
    default void setCurrentAnimation(@Nullable VanillaConvertedVmdAnimation currentAnimation) {
    }
    
    /**
     * 判定当前连段是否可以从 current 推进至 next 状态。
     * <p>
     * 用于在连段推进前进行自定义逻辑拦截（如目标距离、位置等条件检查）。
     */
    boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next);
    
    /**
     * 判断实体是否可以使用指定的连段。
     *
     * @param combo 连段 ID
     */
    boolean canUseCombo(ResourceLocation combo);
    
    /**
     * 获取此实体可以攻击的实体类型集合。
     * <p>
     * 在 {@link #processTargetList} 中用于过滤目标列表，只保留允许攻击的类型。
     */
    Set<Class<? extends Entity>> getAttackableEntities();
    
    /**
     * 处理攻击目标列表：合并原始列表、多部件实体、攻击方目标等，
     * 根据 {@link #getAttackableEntities()} 过滤并去重（排除己方、所有者等）。
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    default List<Entity> processTargetList(Level world, LivingEntity attacker, AABB aabb, double reach, List<Entity> originalTargetList) {
        List<Entity> targetList = new ArrayList<>(originalTargetList);
        targetList.addAll(world.getEntitiesOfClass(LivingEntity.class, aabb.inflate(5), IEntityExtension::isMultipartEntity).stream()
            .flatMap(e -> (e.isMultipartEntity()) ? Stream.of(e.getParts()) : Stream.of(e)).filter(t -> {
                boolean result = false;
                if (t instanceof LivingEntity living) {
                    result = attacker.canAttack(living);
                } else if (t instanceof PartEntity<?> part) {
                    if (part.getParent() instanceof LivingEntity living) {
                        result = attacker.canAttack(living) && part.distanceToSqr(attacker) < (reach * reach);
                    }
                }
                return result;
            }).toList());
        
        targetList.addAll(world.getEntitiesOfClass(LivingEntity.class, aabb).stream()
            .flatMap(e -> (e.isMultipartEntity()) ? Stream.of(e.getParts()) : Stream.of(e)).filter(t -> {
                boolean result = false;
                if (t instanceof LivingEntity living) {
                    result = attacker.canAttack(living);
                } else if (t instanceof PartEntity<?> part) {
                    if (part.getParent() instanceof LivingEntity living) {
                        result = attacker.canAttack(living) && part.distanceToSqr(attacker) < (reach * reach);
                    }
                }
                return result;
            }).toList());
        
        targetList.removeIf(entity -> this.getAttackableEntities().stream().noneMatch(clazz ->
            clazz.isInstance(entity)));
        
        BladeStateAccess.of(attacker.getMainHandItem()).ifPresent(state -> {
            Entity target = state.getTargetEntity(world);
            if (target != null) {
                targetList.add(target);
            }
        });
        if (attacker instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                targetList.add(target);
            }
        }
        
        targetList.removeIf(entity -> entity.equals(this));
        targetList.removeIf(entity -> entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null
            && ownable.getOwnerUUID().equals(attacker.getUUID()));
        targetList.removeIf(entity -> attacker instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null
            && ownable.getOwnerUUID().equals(entity.getUUID()));
        targetList.removeIf(entity -> entity instanceof OwnableEntity ownable && attacker instanceof OwnableEntity ownableAttacker
            && ownable.getOwnerUUID() != null && ownableAttacker.getOwnerUUID() != null
            && ownable.getOwnerUUID().equals(ownableAttacker.getOwnerUUID()));
        return targetList;
    }
    
    /**
     * 此实体在发动上斩后是否跟进跃升斩。
     */
    @SuppressWarnings("SameReturnValue")
    default boolean useUpperSlashJump() {
        return false;
    }
    
    /**
     * 命中效果回调，在成功攻击目标时调用。
     */
    @SuppressWarnings("EmptyMethod")
    default void hitEffect(LivingEntity enemy) {
    }
}
