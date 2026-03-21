package net.mrqx.sbr_core.entity;

import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface ISlashBladeEntity {
    @Nullable
    VanillaConvertedVmdAnimation getCurrentAnimation();

    void setCurrentAnimation(@Nullable VanillaConvertedVmdAnimation currentAnimation);

    boolean canProgressCombo(LivingEntity target, ResourceLocation current, ResourceLocation next);

    boolean canUseCombo(ResourceLocation combo);

    Set<Class<? extends Entity>> getAttackableEntities();

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    default List<Entity> processTargetList(Level world, LivingEntity attacker, AABB aabb, double reach, List<Entity> originalTargetList) {
        List<Entity> targetList = new ArrayList<>(originalTargetList);
        targetList.addAll(world.getEntitiesOfClass(LivingEntity.class, aabb.inflate(5), IForgeEntity::isMultipartEntity).stream()
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

        attacker.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
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
        return targetList.stream().distinct().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @SuppressWarnings("SameReturnValue")
    default boolean useUpperSlashJump() {
        return false;
    }

    @SuppressWarnings("EmptyMethod")
    default void hitEffect(LivingEntity enemy) {
    }
}
