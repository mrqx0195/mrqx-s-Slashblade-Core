package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.AttackManager;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.ClientAnimations;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SlashBladeAttackUtils {
    public static final String VOID_SLASH_COUNTER_KEY = "sbr_core.voidSlashCounter";
    public static final String SUPER_JUDGEMENT_CUT_COUNTER_KEY = "sbr_core.superJudgementCutCounter";

    public static boolean isHoldingSlashBlade(LivingEntity livingEntity) {
        return !livingEntity.getMainHandItem().isEmpty() && livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).isPresent();
    }

    public static boolean canInterruptCombo(LivingEntity livingEntity, boolean powerful) {
        return livingEntity.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).map(state -> {
            ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
            ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
            if (current != null) {
                ComboState next = ComboStateRegistry.REGISTRY.get().getValue(current.getNextOfTimeout(livingEntity));
                if (powerful) {
                    return !ADVANCE_UNINTERRUPTIBLE_COMBO.contains(current) && !ADVANCE_UNINTERRUPTIBLE_COMBO.contains(next);
                }
                return !UNINTERRUPTIBLE_COMBO.contains(current) && !UNINTERRUPTIBLE_COMBO.contains(next);
            }
            return true;
        }).orElse(false);
    }

    public static void tryAerialCleave(LivingEntity livingEntity, ISlashBladeState state) {
        if (livingEntity.onGround()) {
            return;
        }
        state.updateComboSeq(livingEntity, ComboStateRegistry.AERIAL_CLEAVE.getId());
    }

    public static void groundAttack(LivingEntity livingEntity, ISlashBladeState state) {
        if (livingEntity.onGround()) {
            state.updateComboSeq(livingEntity, ComboStateRegistry.COMBO_A1.getId());
        } else {
            tryAerialCleave(livingEntity, state);
        }
    }

    public static void airAttack(LivingEntity livingEntity, ISlashBladeState state, boolean canRapidSlash) {
        if (!state.resolvCurrentComboState(livingEntity).equals(ComboStateRegistry.UPPERSLASH.getId())) {
            if (livingEntity.onGround()) {
                if (canRapidSlash && !livingEntity.isPassenger()) {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.RAPID_SLASH.getId());
                } else {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.UPPERSLASH.getId());
                }
            } else {
                state.updateComboSeq(livingEntity, ComboStateRegistry.AERIAL_RAVE_A1.getId());
            }
        }
    }

    public static void rapidSlashAttack(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target) {
        ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        livingEntity.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null) {
            ResourceLocation next = current.getNext(livingEntity);
            if (currentLoc.equals(ComboStateRegistry.NONE.getId()) || next.equals(ComboStateRegistry.NONE.getId())) {
                if (livingEntity.onGround()) {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.RAPID_SLASH.getId());
                }
            }
        }
    }

    public static void doSlashArts(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target, boolean isJust) {
        livingEntity.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        int elapsed;
        SlashArts.ArtsType type;
        if (isJust) {
            elapsed = 10;
            type = SlashArts.ArtsType.Jackpot;
        } else {
            elapsed = 20;
            type = SlashArts.ArtsType.Success;
        }
        ResourceLocation comboLoc = state.getSlashArts().doArts(type, livingEntity);
        SlashBladeEvent.ChargeActionEvent event = new SlashBladeEvent.ChargeActionEvent(livingEntity, elapsed, state, comboLoc, type);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            comboLoc = event.getComboState();
            ComboState combo = ComboStateRegistry.REGISTRY.get().getValue(comboLoc);
            if (combo != null && !Objects.equals(comboLoc, ComboStateRegistry.NONE.getId())) {
                state.updateComboSeq(livingEntity, comboLoc);
            }
        }
    }

    public static Boolean trySlashArts(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target, boolean isJust, boolean powerful) {
        if (JustSlashArtManager.getJustCooldown(livingEntity) > 0) {
            return false;
        }
        ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        if (current != null) {
            ComboState next = ComboStateRegistry.REGISTRY.get().getValue(current.getNextOfTimeout(livingEntity));
            if (powerful && ADVANCE_CHARGE_COMBO.contains(current)) {
                doSlashArts(livingEntity, state, target, isJust);
                return true;
            } else if (isJust && QUICK_CHARGE_COMBO.contains(current)) {
                doSlashArts(livingEntity, state, target, true);
                return true;
            } else if (CHARGE_COMBO.contains(current)) {
                doSlashArts(livingEntity, state, target, isJust);
                return true;
            } else {
                return isJust && QUICK_CHARGE_COMBO.contains(next);
            }
        }
        return false;
    }

    public static void voidSlash(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target, boolean powerful) {
        if (powerful) {
            SlashBladeMovementUtils.tryTrickToTarget(livingEntity, target);
        }
        state.updateComboSeq(livingEntity, ComboStateRegistry.VOID_SLASH.getId());
    }

    public static void normalSlashBladeAttack(LivingEntity livingEntity, ISlashBladeState state, LivingEntity target,
                                              boolean canRapidSlash, boolean preferAirAttack, boolean canVoidSlash, boolean powerful) {
        ResourceLocation currentLoc = state.resolvCurrentComboState(livingEntity);
        ComboState current = ComboStateRegistry.REGISTRY.get().getValue(currentLoc);
        CompoundTag data = livingEntity.getPersistentData();
        livingEntity.lookAt(EntityAnchorArgument.Anchor.FEET, target.position());
        if (current != null) {
            ResourceLocation nextLoc = current.getNext(livingEntity);
            if (currentLoc.equals(ComboStateRegistry.NONE.getId()) || nextLoc.equals(ComboStateRegistry.NONE.getId())) {
                JustSlashArtManager.resetJustCount(livingEntity);
                if (canVoidSlash && livingEntity.onGround() && data.getInt(VOID_SLASH_COUNTER_KEY) <= 0) {
                    voidSlash(livingEntity, state, target, powerful);
                    data.putInt(VOID_SLASH_COUNTER_KEY, 1000);
                } else {
                    if (preferAirAttack) {
                        airAttack(livingEntity, state, canRapidSlash);
                    } else {
                        groundAttack(livingEntity, state);
                    }
                }
            } else if (current.equals(ComboStateRegistry.RAPID_SLASH.get()) && preferAirAttack && canInterruptCombo(livingEntity, powerful)) {
                List<Entity> hits = AttackManager.areaAttack(livingEntity, KnockBacks.toss.action, 0.44f, true, true, true);
                if (!hits.isEmpty()) {
                    state.updateComboSeq(livingEntity, ComboStateRegistry.RISING_STAR.getId());
                    AdvancementHelper.grantCriterion(livingEntity, AdvancementHelper.ADVANCEMENT_RISING_STAR);
                }
            } else {
                if (!nextLoc.equals(currentLoc) && livingEntity instanceof ISlashBladeEntity slashBladeEntity
                        && slashBladeEntity.canProgressCombo(target, currentLoc, nextLoc)) {
                    state.progressCombo(livingEntity);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static TriConsumer<Long, Integer, String> syncClientEntity() {
        return (point, entityId, combo) -> {
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);
                if (entity instanceof ISlashBladeEntity slashBladeEntity) {
                    entity.getCapability(CapabilityConcentrationRank.RANK_POINT).ifPresent(cr -> {
                        long time = entity.level().getGameTime();
                        IConcentrationRank.ConcentrationRanks oldRank = cr.getRank(time);
                        cr.setRawRankPoint(point);
                        cr.setLastUpdte(time);
                        if (oldRank.level < cr.getRank(time).level) {
                            cr.setLastRankRise(time);
                        }
                    });
                    ComboState state = ComboStateRegistry.REGISTRY.get().getValue(ResourceLocation.tryParse(combo));
                    if (state == null) {
                        return;
                    }
                    ResourceLocation animation = ComboState.getRegistryKey(state);
                    if (animation != null) {
                        VanillaConvertedVmdAnimation vmdAnimation = ClientAnimations.ANIMATION.get(animation);
                        if (vmdAnimation != null) {
                            slashBladeEntity.setCurrentAnimation(vmdAnimation.getClone());
                        }
                    }
                }
            }
        };
    }

    public static final Set<ComboState> CHARGE_COMBO = Set.of(
            ComboStateRegistry.COMBO_C.get(),
            ComboStateRegistry.COMBO_A4.get(),
            ComboStateRegistry.COMBO_A5.get(),
            ComboStateRegistry.COMBO_B7.get(),
            ComboStateRegistry.COMBO_B_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_A3.get(),
            ComboStateRegistry.AERIAL_RAVE_B4.get(),
            ComboStateRegistry.UPPERSLASH.get(),
            ComboStateRegistry.UPPERSLASH_JUMP.get(),
            ComboStateRegistry.AERIAL_CLEAVE_LANDING.get(),
            ComboStateRegistry.RAPID_SLASH_END.get(),
            ComboStateRegistry.RISING_STAR.get()
    );

    public static final Set<ComboState> ADVANCE_CHARGE_COMBO = Set.of(
            ComboStateRegistry.JUDGEMENT_CUT_SLASH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.get(),
            ComboStateRegistry.JUDGEMENT_CUT_END.get()

    );

    public static final Set<ComboState> QUICK_CHARGE_COMBO = Set.of(
            ComboStateRegistry.COMBO_A1_END.get(),
            ComboStateRegistry.COMBO_A2_END.get(),
            ComboStateRegistry.COMBO_C_END.get(),
            ComboStateRegistry.COMBO_A3_END3.get(),
            ComboStateRegistry.COMBO_A4_END.get(),
            ComboStateRegistry.COMBO_A4_EX_END2.get(),
            ComboStateRegistry.COMBO_A5_END.get(),
            ComboStateRegistry.COMBO_B7_END3.get(),
            ComboStateRegistry.COMBO_B_END3.get(),
            ComboStateRegistry.AERIAL_RAVE_A1_END.get(),
            ComboStateRegistry.AERIAL_RAVE_A2_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_A3_END.get(),
            ComboStateRegistry.AERIAL_RAVE_B3_END.get(),
            ComboStateRegistry.AERIAL_RAVE_B4_END.get(),
            ComboStateRegistry.UPPERSLASH_END.get(),
            ComboStateRegistry.UPPERSLASH_JUMP_END.get(),
            ComboStateRegistry.AERIAL_CLEAVE_END.get(),
            ComboStateRegistry.RAPID_SLASH_QUICK.get(),
            ComboStateRegistry.RAPID_SLASH_END2.get(),
            ComboStateRegistry.RISING_STAR_END.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SHEATH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SHEATH_AIR.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SHEATH_JUST.get()
    );

    public static final Set<ComboState> UNINTERRUPTIBLE_COMBO = Set.of(
            ComboStateRegistry.COMBO_A1.get(),
            ComboStateRegistry.COMBO_A2.get(),
            ComboStateRegistry.COMBO_C.get(),
            ComboStateRegistry.COMBO_A3.get(),
            ComboStateRegistry.COMBO_A4.get(),
            ComboStateRegistry.COMBO_A4_EX.get(),
            ComboStateRegistry.COMBO_A5.get(),
            ComboStateRegistry.COMBO_B1.get(),
            ComboStateRegistry.COMBO_B7.get(),
            ComboStateRegistry.COMBO_B_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_A1.get(),
            ComboStateRegistry.AERIAL_RAVE_A2.get(),
            ComboStateRegistry.AERIAL_RAVE_A3.get(),
            ComboStateRegistry.AERIAL_RAVE_B3.get(),
            ComboStateRegistry.AERIAL_RAVE_B4.get(),
            ComboStateRegistry.UPPERSLASH.get(),
            ComboStateRegistry.UPPERSLASH_JUMP.get(),
            ComboStateRegistry.AERIAL_CLEAVE.get(),
            ComboStateRegistry.RISING_STAR.get(),
            ComboStateRegistry.JUDGEMENT_CUT.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.get(),
            ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST2.get(),
            ComboStateRegistry.JUDGEMENT_CUT_END.get()
    );

    public static final Set<ComboState> ADVANCE_UNINTERRUPTIBLE_COMBO = Set.of(
            ComboStateRegistry.COMBO_C.get(),
            ComboStateRegistry.COMBO_A4_EX.get(),
            ComboStateRegistry.COMBO_A5.get(),
            ComboStateRegistry.COMBO_B7.get(),
            ComboStateRegistry.COMBO_B_END2.get(),
            ComboStateRegistry.AERIAL_RAVE_B3.get(),
            ComboStateRegistry.UPPERSLASH.get(),
            ComboStateRegistry.AERIAL_CLEAVE.get(),
            ComboStateRegistry.RISING_STAR.get(),
            ComboStateRegistry.JUDGEMENT_CUT.get(),
            ComboStateRegistry.JUDGEMENT_CUT_END.get()
    );
}
