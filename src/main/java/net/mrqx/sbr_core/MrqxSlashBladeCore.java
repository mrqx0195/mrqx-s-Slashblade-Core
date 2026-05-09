package net.mrqx.sbr_core;

import com.google.common.base.CaseFormat;
import com.mojang.logging.LogUtils;
import mods.flammpfeil.slashblade.client.renderer.entity.SummonedSwordRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.mrqx.sbr_core.config.MrqxSlashBladeCoreConfig;
import net.mrqx.sbr_core.entity.EntityAirTrickSummonedSword;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(MrqxSlashBladeCore.MODID)
public class MrqxSlashBladeCore {
    public static final String MODID = "sbr_core";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static ResourceLocation prefix(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }
    
    public MrqxSlashBladeCore(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, MrqxSlashBladeCoreConfig.COMMON_CONFIG);
    }
    
    @EventBusSubscriber
    public static class RegistryEvents {
        public static final ResourceLocation ENTITY_AIR_TRICK_SUMMONED_SWORD_RESOURCE_LOCATION = MrqxSlashBladeCore.prefix(classToString(EntityAirTrickSummonedSword.class));
        @SuppressWarnings("NotNullFieldNotInitialized")
        public static EntityType<EntityAirTrickSummonedSword> AirTrickSummonedSword;
        
        @SubscribeEvent
        public static void register(RegisterEvent event) {
            event.register(Registries.ENTITY_TYPE, (entityTypeRegisterHelper) -> {
                AirTrickSummonedSword = EntityType.Builder.of((EntityAirTrickSummonedSword::new), MobCategory.MISC).sized(0.5F, 0.5F).setTrackingRange(4).setUpdateInterval(20).build(ENTITY_AIR_TRICK_SUMMONED_SWORD_RESOURCE_LOCATION.toString());
                entityTypeRegisterHelper.register(ENTITY_AIR_TRICK_SUMMONED_SWORD_RESOURCE_LOCATION, AirTrickSummonedSword);
            });
        }
        
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(AirTrickSummonedSword, SummonedSwordRenderer::new);
        }
        
        @SuppressWarnings("SameParameterValue")
        public static String classToString(Class<? extends Entity> entityClass) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityClass.getSimpleName()).replace("entity_", "");
        }
    }
}
