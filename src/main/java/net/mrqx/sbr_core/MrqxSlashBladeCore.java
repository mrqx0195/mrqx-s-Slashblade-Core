package net.mrqx.sbr_core;

import com.google.common.base.CaseFormat;
import com.mojang.logging.LogUtils;
import mods.flammpfeil.slashblade.client.renderer.entity.SummonedSwordRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.mrqx.sbr_core.entity.EntityAirTrickSummonedSword;
import org.slf4j.Logger;

@Mod(MrqxSlashBladeCore.MODID)
public class MrqxSlashBladeCore {
    public static final String MODID = "sbr_core";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MrqxSlashBladeCore() {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        public static final ResourceLocation ENTITY_AIR_TRICK_SUMMONED_SWORD_RESOURCE_LOCATION = new ResourceLocation("sbr_core", classToString(EntityAirTrickSummonedSword.class));
        public static EntityType<EntityAirTrickSummonedSword> AirTrickSummonedSword;

        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(ForgeRegistries.Keys.ENTITY_TYPES, (entityTypeRegisterHelper) -> {
                AirTrickSummonedSword = EntityType.Builder.of((EntityAirTrickSummonedSword::new), MobCategory.MISC).sized(0.5F, 0.5F).setTrackingRange(4).setUpdateInterval(20).setCustomClientFactory(EntityAirTrickSummonedSword::createInstance).build(ENTITY_AIR_TRICK_SUMMONED_SWORD_RESOURCE_LOCATION.toString());
                entityTypeRegisterHelper.register(ENTITY_AIR_TRICK_SUMMONED_SWORD_RESOURCE_LOCATION, AirTrickSummonedSword);
            });
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(AirTrickSummonedSword, SummonedSwordRenderer::new);
        }

        private static String classToString(Class<? extends Entity> entityClass) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", "");
        }
    }
}
