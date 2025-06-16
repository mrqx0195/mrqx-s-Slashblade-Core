package net.mrqx.sbr_core.mixin.common;

import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.event.RefineHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mrqx.sbr_core.events.MrqxSlashBladeEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(RefineHandler.class)
public abstract class MixinRefineHandler {
    @Overwrite(remap = false)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAnvilUpdateEvent(AnvilUpdateEvent event) {
        if (!event.getOutput().isEmpty())
            return;

        ItemStack base = event.getLeft();
        ItemStack material = event.getRight();

        if (base.isEmpty())
            return;
        if (!(base.getCapability(ItemSlashBlade.BLADESTATE).isPresent()))
            return;

        if (material.isEmpty())
            return;

        boolean isRepairable = base.getItem().isValidRepairItem(base, material);
        if (!isRepairable)
            return;

        int level = material.getEnchantmentValue();

        if (level < 0)
            return;

        ItemStack result = base.copy();

        int refineLimit = Math.max(10, level);

        int materialCost = 0;
        int levelCostBase = SlashBladeConfig.REFINE_LEVEL_COST.get();
        int costResult = levelCostBase * materialCost;
        AtomicInteger refineResult = new AtomicInteger(0);
        result.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(s -> {
            refineResult.set(s.getRefine());
        });

        while (materialCost < material.getCount()) {

            MrqxSlashBladeEvents.RefineProgressEvent e = new MrqxSlashBladeEvents.RefineProgressEvent(result,
                    result.getCapability(ItemSlashBlade.BLADESTATE).resolve().get(), materialCost + 1, levelCostBase,
                    costResult, refineResult.get(), event);

            MinecraftForge.EVENT_BUS.post(e);
            if (e.isCanceled()) {
                break;
            }

            refineResult.set(e.getRefineResult());

            materialCost = e.getMaterialCost();
            costResult = e.getCostResult() + e.getLevelCost();

            boolean refineable = !event.getPlayer().getAbilities().instabuild
                    && event.getPlayer().experienceLevel < costResult;

            if (refineable)
                break;
        }

        if (result.getCapability(ItemSlashBlade.BLADESTATE).isPresent()) {
            SlashBladeState state = (SlashBladeState) result.getCapability(ItemSlashBlade.BLADESTATE).resolve().get();
            MrqxSlashBladeEvents.RefineSettlementEvent e2 = new MrqxSlashBladeEvents.RefineSettlementEvent(result,
                    state, materialCost, costResult, refineResult.get(), event);

            MinecraftForge.EVENT_BUS.post(e2);
            if (e2.isCanceled()) {
                return;
            }

            state.setProudSoulCount(state.getProudSoulCount() + Math.min(5000, level * 10));
            if (state.getRefine() < refineLimit) {
                if (state.getRefine() + e2.getRefineResult() < 200) {
                    state.setMaxDamage(state.getMaxDamage() + e2.getRefineResult());
                } else if (state.getRefine() < 200) {
                    state.setMaxDamage(state.getMaxDamage() + Math.min(state.getRefine() + e2.getRefineResult(), 200)
                            - state.getRefine());
                }
                state.setRefine(e2.getRefineResult());
            }

            result.setDamageValue(result.getDamageValue() - Math.max(1, level / 2));
            result.getOrCreateTag().put("bladeState", state.serializeNBT());

            materialCost = e2.getMaterialCost();
            costResult = e2.getCostResult();
        }

        event.setMaterialCost(materialCost);
        event.setCost(costResult);
        event.setOutput(result);
    }
}
