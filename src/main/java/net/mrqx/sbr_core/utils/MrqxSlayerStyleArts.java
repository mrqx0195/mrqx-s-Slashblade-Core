package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.ability.SlayerStyleArts;
import mods.flammpfeil.slashblade.ability.Untouchable;
import mods.flammpfeil.slashblade.capability.mobeffect.CapabilityMobEffect;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.NBTHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import net.mrqx.sbr_core.entity.EntityAirTrickSummonedSword;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber
public class MrqxSlayerStyleArts {
    public final static int TRICK_ACTION_UNTOUCHABLE_TIME = 10;
    public static final String AVOID_TRICK_UP_KEY = "sb.avoid.trickup";
    public static final String AVOID_COUNTER_KEY = "sb.avoid.counter";
    public static final String AVOID_VEC_KEY = "sb.avoid.vec";
    public static final String AIR_TRICK_COUNTER_KEY = "sb.airtrick.counter";
    public static final String AIR_TRICK_TARGET_KEY = "sb.airtrick.target";

    public static final TriFunction<LivingEntity, Boolean, Boolean, Boolean> TRICK_UP = (livingEntity, shouldUntouchable, ignoreAvoid) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(state -> {
                if (!ignoreAvoid && livingEntity.getPersistentData().getInt(AVOID_TRICK_UP_KEY) != 0) {
                    return false;
                }
                if (shouldUntouchable) {
                    Untouchable.setUntouchable(livingEntity, TRICK_ACTION_UNTOUCHABLE_TIME);
                }
                Vec3 motion = new Vec3(0, 0.8, 0);
                livingEntity.move(MoverType.SELF, motion);
                livingEntity.getPersistentData().putInt(AVOID_TRICK_UP_KEY, 2);
                livingEntity.setOnGround(false);
                livingEntity.getPersistentData().putInt(AVOID_COUNTER_KEY, 2);
                NBTHelper.putVector3d(livingEntity.getPersistentData(), AVOID_VEC_KEY, livingEntity.position());
                AdvancementHelper.grantCriterion(livingEntity, SlayerStyleArts.ADVANCEMENT_TRICK_UP);
                livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.5f, 1.2f);

                return true;
            }).orElse(false);

    public static final BiFunction<LivingEntity, Boolean, Boolean> AIR_TRICK = (livingEntity, shouldUntouchable) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(state -> {
                Entity tmpTarget = state.getTargetEntity(livingEntity.level());
                Entity target;
                if (tmpTarget != null && tmpTarget.getParts() != null && tmpTarget.getParts().length > 0) {
                    target = tmpTarget.getParts()[0];
                } else {
                    target = tmpTarget;
                }
                if (target == null) {
                    return false;
                }

                if (target == livingEntity.getLastHurtMob()
                        && livingEntity.tickCount < livingEntity.getLastHurtMobTimestamp() + 100) {
                    LivingEntity hitEntity = livingEntity.getLastHurtMob();
                    if (hitEntity != null) {
                        doAirTrickTeleport(livingEntity, hitEntity);
                    }
                } else {
                    EntityAirTrickSummonedSword airTrickSummonedSword = new EntityAirTrickSummonedSword(MrqxSlashBladeCore.RegistryEvents.AirTrickSummonedSword, livingEntity.level());

                    airTrickSummonedSword.setOwner(livingEntity);
                    airTrickSummonedSword.setTarget(target);
                    airTrickSummonedSword.setShouldUntouchable(shouldUntouchable);

                    Vec3 lastPos = livingEntity.getEyePosition(1.0f);
                    airTrickSummonedSword.xOld = lastPos.x;
                    airTrickSummonedSword.yOld = lastPos.y;
                    airTrickSummonedSword.zOld = lastPos.z;

                    Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2.0, 0)
                            .add(livingEntity.getLookAngle().scale(-2.0));
                    airTrickSummonedSword.setPos(targetPos.x, targetPos.y, targetPos.z);

                    Vec3 dir = livingEntity.getLookAngle();
                    airTrickSummonedSword.shoot(dir.x, dir.y, dir.z, 1.0f, 0);

                    airTrickSummonedSword.setOwner(livingEntity);

                    airTrickSummonedSword.setDamage(0.01f);

                    airTrickSummonedSword.setColor(state.getColorCode());

                    airTrickSummonedSword.getPersistentData().putBoolean("doForceHit", true);

                    livingEntity.level().addFreshEntity(airTrickSummonedSword);
                    livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 0.2F, 1.45F);
                }

                return true;
            }).orElse(false);

    public static final BiFunction<LivingEntity, Boolean, Boolean> TRICK_DOWN = (livingEntity, shouldUntouchable) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(state -> {
                Vec3 oldPos = livingEntity.position();
                Vec3 motion = new Vec3(0.0F, -512.0F, 0.0F);
                livingEntity.move(MoverType.SELF, motion);
                if (livingEntity.onGround()) {
                    if (shouldUntouchable) {
                        Untouchable.setUntouchable(livingEntity, TRICK_ACTION_UNTOUCHABLE_TIME);
                    }
                    livingEntity.getPersistentData().putInt("sb.avoid.counter", 2);
                    NBTHelper.putVector3d(livingEntity.getPersistentData(), "sb.avoid.vec", livingEntity.position());
                    state.updateComboSeq(livingEntity, ComboStateRegistry.NONE.getId());
                    AdvancementHelper.grantCriterion(livingEntity, SlayerStyleArts.ADVANCEMENT_TRICK_DOWN);
                    livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.5F, 1.2F);
                } else {
                    livingEntity.setPos(oldPos);
                }
                return true;
            }).orElse(false);

    public static final PropertyDispatch.QuadFunction<LivingEntity, Boolean, Boolean, Vec3, Boolean> TRICK_DODGE = (livingEntity, shouldUntouchable, ignoreAvoid, position) ->
            livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(state -> {
                Level level = livingEntity.level();
                if (ignoreAvoid || 0 < livingEntity.getCapability(CapabilityMobEffect.MOB_EFFECT).map(ef -> ef.doAvoid(level.getGameTime())).orElse(0)) {
                    if (shouldUntouchable) {
                        Untouchable.setUntouchable(livingEntity, TRICK_ACTION_UNTOUCHABLE_TIME);
                    }
                    livingEntity.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.5f, 1.2f);
                    livingEntity.moveTo(position);
                    livingEntity.getPersistentData().putInt("sb.avoid.counter", 2);
                    NBTHelper.putVector3d(livingEntity.getPersistentData(), "sb.avoid.vec", livingEntity.position());
                    AdvancementHelper.grantCriterion(livingEntity, SlayerStyleArts.ADVANCEMENT_TRICK_DODGE);
                    state.updateComboSeq(livingEntity, state.getComboRoot());
                    return true;
                }
                return false;
            }).orElse(false);

    public static void doAirTrickTeleport(Entity entityIn, LivingEntity target) {
        entityIn.getPersistentData().putInt(AIR_TRICK_COUNTER_KEY, 3);
        entityIn.getPersistentData().putInt(AIR_TRICK_TARGET_KEY, target.getId());

        if (entityIn instanceof ServerPlayer) {
            AdvancementHelper.grantCriterion((ServerPlayer) entityIn, SlayerStyleArts.ADVANCEMENT_AIR_TRICK);
            Vec3 motion = target.getPosition(1.0f).subtract(entityIn.getPosition(1.0f)).scale(0.5f);
            ((ServerPlayer) entityIn).connection.send(new ClientboundSetEntityMotionPacket(entityIn.getId(), motion));
        }
    }

    private static void executeTeleport(LivingEntity entityIn, LivingEntity target) {
        if (!(entityIn.level() instanceof ServerLevel worldIn)) {
            return;
        }

        entityIn.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.75F, 1.25F);

        entityIn.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> state.updateComboSeq(entityIn, state.getComboRoot()));

        Untouchable.setUntouchable(entityIn, TRICK_ACTION_UNTOUCHABLE_TIME);

        Vec3 teleportPos = target.position().add(0, target.getBbHeight() / 2.0, 0).add(entityIn.getLookAngle().scale(-2.0));

        double x = teleportPos.x;
        double y = teleportPos.y;
        double z = teleportPos.z;
        float yaw = entityIn.getYRot();
        float pitch = entityIn.getXRot();

        Set<RelativeMovement> relativeList = Collections.emptySet();
        BlockPos blockpos = new BlockPos((int) x, (int) y, (int) z);
        if (Level.isInSpawnableBounds(blockpos)) {
            if (entityIn instanceof ServerPlayer serverPlayer) {
                ChunkPos chunkpos = new ChunkPos(blockpos);
                worldIn.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entityIn.getId());
                entityIn.stopRiding();
                if (serverPlayer.isSleeping()) {
                    serverPlayer.stopSleepInBed(true, true);
                }

                if (worldIn == entityIn.level()) {
                    serverPlayer.connection.teleport(x, y, z, yaw, pitch, relativeList);
                } else {
                    serverPlayer.teleportTo(worldIn, x, y, z, yaw, pitch);
                }

                entityIn.setYHeadRot(yaw);
            } else {
                float f1 = Mth.wrapDegrees(yaw);
                float f = Mth.wrapDegrees(pitch);
                f = Mth.clamp(f, -90.0F, 90.0F);
                if (worldIn != entityIn.level()) {
                    entityIn.unRide();
                    LivingEntity entity = (LivingEntity) entityIn.getType().create(worldIn);
                    if (entity == null) {
                        return;
                    }

                    entity.restoreFrom(entityIn);
                    entity.moveTo(x, y, z, f1, f);
                    entity.setYHeadRot(f1);
                } else {
                    entityIn.moveTo(x, y, z, f1, f);
                    entityIn.setYHeadRot(f1);
                }
            }

            if (!entityIn.isFallFlying()) {
                entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
                entityIn.setOnGround(false);
            }

            if (entityIn instanceof PathfinderMob) {
                ((PathfinderMob) entityIn).getNavigation().stop();
            }

        }
    }

    @SubscribeEvent
    public static void onLivingTickEvent(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }

        Vec3 deltaMovement;

        Vec3 input = new Vec3(entity.xxa, entity.yya, entity.zza);
        double scale = 1.0;
        float yRot = entity.getYRot();
        double d0 = input.lengthSqr();
        if (d0 < 1.0E-7D) {
            deltaMovement = Vec3.ZERO;
        } else {
            Vec3 vec3 = (d0 > 1.0D ? input.normalize() : input).scale(scale);
            float f = Mth.sin(yRot * ((float) Math.PI / 180F));
            float f1 = Mth.cos(yRot * ((float) Math.PI / 180F));
            deltaMovement = new Vec3(vec3.x * (double) f1 - vec3.z * (double) f, vec3.y,
                    vec3.z * (double) f1 + vec3.x * (double) f);
        }


        boolean doStepUpBoost = true;

        Vec3 offset = deltaMovement.normalize().scale(0.5f).add(0, 0.25, 0);
        BlockPos offsetPos = new BlockPos(VectorHelper.f2i(entity.position().add(offset))).below();
        BlockState blockState = entity.level().getBlockState(offsetPos);
        if (blockState.liquid()) {
            doStepUpBoost = false;
        }
        AttributeInstance stepHeightAttribute = entity.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepHeightAttribute != null) {
            AttributeModifier stepUpBonus = new AttributeModifier(UUID.fromString("c8158a43-a96f-4db9-8858-57294fbe0ebb"), "StepUp Bonus", 0.5, AttributeModifier.Operation.ADDITION);
            stepHeightAttribute.removeModifier(stepUpBonus);
            if (doStepUpBoost && (entity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).isPresent())) {
                stepHeightAttribute.addPermanentModifier(stepUpBonus);
            }
        }

        if (entity.onGround() && 0 < entity.getPersistentData().getInt(AVOID_TRICK_UP_KEY)) {

            int count = entity.getPersistentData().getInt(AVOID_TRICK_UP_KEY);
            count--;

            if (count <= 0) {
                entity.getPersistentData().remove(AVOID_TRICK_UP_KEY);

                if (entity instanceof ServerPlayer) {
                    ((ServerPlayer) entity).hasChangedDimension();
                }
            } else {
                entity.getPersistentData().putInt(AVOID_TRICK_UP_KEY, count);
            }
        }

        if (entity.getPersistentData().contains(AVOID_COUNTER_KEY)) {
            int count = entity.getPersistentData().getInt(AVOID_COUNTER_KEY);
            count--;

            if (count <= 0) {
                if (entity.getPersistentData().contains(AVOID_VEC_KEY)) {
                    Vec3 pos = NBTHelper.getVector3d(entity.getPersistentData(), AVOID_VEC_KEY);
                    entity.moveTo(pos);
                    entity.level().broadcastEntityEvent(entity, (byte) 46);
                }

                entity.getPersistentData().remove(AVOID_COUNTER_KEY);
                entity.getPersistentData().remove(AVOID_VEC_KEY);

                if (entity instanceof ServerPlayer) {
                    ((ServerPlayer) entity).hasChangedDimension();
                }
            } else {
                entity.getPersistentData().putInt(AVOID_COUNTER_KEY, count);
            }
        }

        if (entity.getPersistentData().contains(AIR_TRICK_COUNTER_KEY)) {
            int count = entity.getPersistentData().getInt(AIR_TRICK_COUNTER_KEY);
            count--;

            if (count <= 0) {
                if (entity.getPersistentData().contains(AIR_TRICK_TARGET_KEY)) {
                    int id = entity.getPersistentData().getInt(AIR_TRICK_TARGET_KEY);

                    if (entity.level().getEntity(id) instanceof LivingEntity living) {
                        executeTeleport(entity, living);
                    }
                }

                entity.getPersistentData().remove(AIR_TRICK_COUNTER_KEY);
                entity.getPersistentData().remove(AIR_TRICK_TARGET_KEY);
                if (entity instanceof ServerPlayer) {
                    ((ServerPlayer) entity).hasChangedDimension();
                }
            } else {
                entity.getPersistentData().putInt(AIR_TRICK_COUNTER_KEY, count);
            }
        }


    }
}
