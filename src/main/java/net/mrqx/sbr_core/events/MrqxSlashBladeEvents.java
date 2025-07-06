package net.mrqx.sbr_core.events;

import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Deprecated
public class MrqxSlashBladeEvents extends SlashBladeEvent {
    public MrqxSlashBladeEvents(ItemStack blade, ISlashBladeState state) {
        super(blade, state);
    }


    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.BladeChangeSpecialEffectEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class BladeChangeSpecialEffectEvent extends MrqxSlashBladeEvents {
        private ResourceLocation SEKey;
        private int shrinkCount = 0;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;

        public BladeChangeSpecialEffectEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                             SlashBladeEvent.BladeStandAttackEvent originalEvent) {
            super(blade, state);
            this.SEKey = SEKey;
            this.originalEvent = originalEvent;
        }

        public ResourceLocation getSEKey() {
            return SEKey;
        }

        public ResourceLocation setSEKey(ResourceLocation SEKey) {
            this.SEKey = SEKey;
            return SEKey;
        }

        public int getShrinkCount() {
            return shrinkCount;
        }

        public int setShrinkCount(int shrinkCount) {
            this.shrinkCount = shrinkCount;
            return this.shrinkCount;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.BladeChangeSpecialAttackEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class BladeChangeSpecialAttackEvent extends MrqxSlashBladeEvents {
        private ResourceLocation SAKey;
        private int shrinkCount = 0;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;

        public BladeChangeSpecialAttackEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SAKey,
                                             SlashBladeEvent.BladeStandAttackEvent originalEvent) {
            super(blade, state);
            this.SAKey = SAKey;
            this.originalEvent = originalEvent;
        }

        public ResourceLocation getSAKey() {
            return SAKey;
        }

        public ResourceLocation setSAKey(ResourceLocation SAKey) {
            this.SAKey = SAKey;
            return SAKey;
        }

        public int getShrinkCount() {
            return shrinkCount;
        }

        public int setShrinkCount(int shrinkCount) {
            this.shrinkCount = shrinkCount;
            return this.shrinkCount;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.PreCopySpecialEffectFromBladeEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class PreCopySpecialEffectFromBladeEvent extends MrqxSlashBladeEvents {
        private ResourceLocation SEKey;
        private int shrinkCount = 0;
        private boolean isRemovable;
        private boolean isCopiable;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;

        public PreCopySpecialEffectFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                                  SlashBladeEvent.BladeStandAttackEvent originalEvent, boolean isRemovable, boolean isCopiable) {
            super(blade, state);
            this.SEKey = SEKey;
            this.isRemovable = isRemovable;
            this.isCopiable = isCopiable;
            this.originalEvent = originalEvent;
        }

        public ResourceLocation getSEKey() {
            return SEKey;
        }

        public ResourceLocation setSEKey(ResourceLocation SEKey) {
            this.SEKey = SEKey;
            return SEKey;
        }

        public int getShrinkCount() {
            return shrinkCount;
        }

        public int setShrinkCount(int shrinkCount) {
            this.shrinkCount = shrinkCount;
            return this.shrinkCount;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }

        public boolean isRemovable() {
            return isRemovable;
        }

        public boolean setRemovable(boolean isRemovable) {
            this.isRemovable = isRemovable;
            return isRemovable;
        }

        public boolean isCopiable() {
            return isCopiable;
        }

        public boolean setCopiable(boolean isCopiable) {
            this.isCopiable = isCopiable;
            return isCopiable;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.CopySpecialEffectFromBladeEvent} instead.
     */
    @Deprecated
    public static class CopySpecialEffectFromBladeEvent extends MrqxSlashBladeEvents {
        private final ResourceLocation SEKey;
        private final boolean isRemovable;
        private final boolean isCopiable;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;
        private final ItemStack orb;
        private final ItemEntity itemEntity;

        public CopySpecialEffectFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SEKey,
                                               SlashBladeEvent.BladeStandAttackEvent originalEvent, boolean isRemovable, boolean isCopiable,
                                               ItemStack orb, ItemEntity itemEntity) {
            super(blade, state);
            this.SEKey = SEKey;
            this.isRemovable = isRemovable;
            this.isCopiable = isCopiable;
            this.originalEvent = originalEvent;
            this.orb = orb;
            this.itemEntity = itemEntity;
        }

        public CopySpecialEffectFromBladeEvent(PreCopySpecialEffectFromBladeEvent pe, ItemStack orb,
                                               ItemEntity itemEntity) {
            this(pe.getBlade(), pe.getSlashBladeState(), pe.getSEKey(), pe.getOriginalEvent(), pe.isRemovable(),
                    pe.isCopiable(), orb, itemEntity);
        }

        public ResourceLocation getSEKey() {
            return SEKey;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }

        public boolean isRemovable() {
            return isRemovable;
        }

        public boolean isCopiable() {
            return isCopiable;
        }

        public ItemStack getOrb() {
            return orb;
        }

        public ItemEntity getItemEntity() {
            return itemEntity;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.PreCopySpecialAttackFromBladeEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class PreCopySpecialAttackFromBladeEvent extends MrqxSlashBladeEvents {
        private ResourceLocation SAKey;
        private int shrinkCount = 0;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;

        public PreCopySpecialAttackFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SAKey,
                                                  SlashBladeEvent.BladeStandAttackEvent originalEvent) {
            super(blade, state);
            this.SAKey = SAKey;
            this.originalEvent = originalEvent;
        }

        public ResourceLocation getSAKey() {
            return SAKey;
        }

        public ResourceLocation setSAKey(ResourceLocation SAKey) {
            this.SAKey = SAKey;
            return SAKey;
        }

        public int getShrinkCount() {
            return shrinkCount;
        }

        public int setShrinkCount(int shrinkCount) {
            this.shrinkCount = shrinkCount;
            return this.shrinkCount;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.CopySpecialAttackFromBladeEvent} instead.
     */
    @Deprecated
    public static class CopySpecialAttackFromBladeEvent extends MrqxSlashBladeEvents {
        private final ResourceLocation SAKey;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;
        private final ItemStack orb;
        private final ItemEntity itemEntity;

        public CopySpecialAttackFromBladeEvent(ItemStack blade, ISlashBladeState state, ResourceLocation SAKey,
                                               SlashBladeEvent.BladeStandAttackEvent originalEvent,
                                               ItemStack orb, ItemEntity itemEntity) {
            super(blade, state);
            this.SAKey = SAKey;
            this.originalEvent = originalEvent;
            this.orb = orb;
            this.itemEntity = itemEntity;
        }

        public CopySpecialAttackFromBladeEvent(PreCopySpecialAttackFromBladeEvent pe, ItemStack orb,
                                               ItemEntity itemEntity) {
            this(pe.getBlade(), pe.getSlashBladeState(), pe.getSAKey(), pe.getOriginalEvent(), orb, itemEntity);
        }

        public ResourceLocation getSAKey() {
            return SAKey;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }

        public ItemStack getOrb() {
            return orb;
        }

        public ItemEntity getItemEntity() {
            return itemEntity;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.bladestand.ProudSoulEnchantmentEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class ProudSoulEnchantmentEvent extends MrqxSlashBladeEvents {
        private int totalShrinkCount;
        private float probability;
        private Enchantment enchantment;
        private int enchantLevel;
        private boolean tryNextEnchant;
        private final SlashBladeEvent.BladeStandAttackEvent originalEvent;

        public ProudSoulEnchantmentEvent(ItemStack blade, ISlashBladeState state,
                                         Enchantment enchantment, int enchantLevel, boolean tryNextEnchant, float probability,
                                         int totalShrinkCount, SlashBladeEvent.BladeStandAttackEvent originalEvent) {
            super(blade, state);
            this.enchantment = enchantment;
            this.enchantLevel = enchantLevel;
            this.tryNextEnchant = tryNextEnchant;
            this.probability = probability;
            this.totalShrinkCount = totalShrinkCount;
            this.originalEvent = originalEvent;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public Enchantment setEnchantment(Enchantment enchantment) {
            this.enchantment = enchantment;
            return enchantment;
        }

        public int getEnchantLevel() {
            return enchantLevel;
        }

        public int setEnchantLevel(int enchantLevel) {
            this.enchantLevel = enchantLevel;
            return this.enchantLevel;
        }

        public boolean willTryNextEnchant() {
            return tryNextEnchant;
        }

        public boolean setWillTryNextEnchant(boolean tryNextEnchant) {
            this.tryNextEnchant = tryNextEnchant;
            return tryNextEnchant;
        }

        public int getTotalShrinkCount() {
            return totalShrinkCount;
        }

        public int setTotalShrinkCount(int totalShrinkCount) {
            this.totalShrinkCount = totalShrinkCount;
            return this.totalShrinkCount;
        }

        public float getProbability() {
            return probability;
        }

        public float setProbability(float probability) {
            this.probability = probability;
            return this.probability;
        }

        public @Nullable SlashBladeEvent.BladeStandAttackEvent getOriginalEvent() {
            return originalEvent;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.RefineProgressEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class RefineProgressEvent extends MrqxSlashBladeEvents {
        private final AnvilUpdateEvent originalEvent;
        private int materialCost;
        private int levelCost;
        private final int costResult;
        private int refineResult;

        public RefineProgressEvent(ItemStack blade, ISlashBladeState state, int materialCost,
                                   int levelCost, int costResult, int refineResult, AnvilUpdateEvent originalEvent) {
            super(blade, state);
            this.materialCost = materialCost;
            this.levelCost = levelCost;
            this.costResult = costResult;
            this.refineResult = refineResult;
            this.originalEvent = originalEvent;
        }

        public @Nullable AnvilUpdateEvent getOriginalEvent() {
            return originalEvent;
        }

        public int getMaterialCost() {
            return materialCost;
        }

        public int setMaterialCost(int materialCost) {
            this.materialCost = materialCost;
            return this.materialCost;
        }

        public int getLevelCost() {
            return levelCost;
        }

        public int setLevelCost(int levelCost) {
            this.levelCost = levelCost;
            return this.levelCost;
        }

        public int getCostResult() {
            return costResult;
        }

        public int getRefineResult() {
            return refineResult;
        }

        public int setRefineResult(int refineResult) {
            this.refineResult = refineResult;
            return this.refineResult;
        }
    }

    /**
     * Use {@link mods.flammpfeil.slashblade.event.RefineSettlementEvent} instead.
     */
    @Cancelable
    @Deprecated
    public static class RefineSettlementEvent extends MrqxSlashBladeEvents {
        private final AnvilUpdateEvent originalEvent;
        private int materialCost;
        private int costResult;
        private int refineResult;

        public RefineSettlementEvent(ItemStack blade, ISlashBladeState state, int materialCost, int costResult,
                                     int refineResult, AnvilUpdateEvent originalEvent) {
            super(blade, state);
            this.materialCost = materialCost;
            this.costResult = costResult;
            this.refineResult = refineResult;
            this.originalEvent = originalEvent;
        }

        public @Nullable AnvilUpdateEvent getOriginalEvent() {
            return originalEvent;
        }

        public int getMaterialCost() {
            return materialCost;
        }

        public int setMaterialCost(int materialCost) {
            this.materialCost = materialCost;
            return this.materialCost;
        }

        public int getCostResult() {
            return costResult;
        }

        public int setCostResult(int costResult) {
            this.costResult = costResult;
            return this.costResult;
        }

        public int getRefineResult() {
            return refineResult;
        }

        public int setRefineResult(int refineResult) {
            this.refineResult = refineResult;
            return this.refineResult;
        }
    }
}
