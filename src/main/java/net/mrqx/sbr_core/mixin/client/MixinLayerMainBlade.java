//package net.mrqx.sbr_core.mixin.client;
//
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//import com.llamalad7.mixinextras.sugar.Local;
//import jp.nyatla.nymmd.MmdMotionPlayerGL2;
//import mods.flammpfeil.slashblade.SlashBlade;
//import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
//import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
//import net.minecraft.client.model.EntityModel;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraftforge.common.util.LazyOptional;
//import net.mrqx.sbr_core.client.layer.ISlashBladeAnimation;
//import org.jetbrains.annotations.NotNull;
//import org.objectweb.asm.Opcodes;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
//@Mixin(LayerMainBlade.class)
//public abstract class MixinLayerMainBlade<T extends LivingEntity, M extends EntityModel<T>> implements ISlashBladeAnimation {
//    @Shadow(remap = false)
//    @Final
//    LazyOptional<MmdMotionPlayerGL2> motionPlayer;
//
//    @Unique
//    private final Map<String, LazyOptional<MmdMotionPlayerGL2>> mrqx_s_Slashblade_Core$motionPlayerMap = new HashMap<>();
//
//    @Unique
//    private ISlashBladeState mrqx_s_Slashblade_Core$state;
//
//    @Override
//    public @NotNull Map<String, LazyOptional<MmdMotionPlayerGL2>> mrqx_s_Slashblade_Core$getMotionPlayerMap() {
//        return mrqx_s_Slashblade_Core$motionPlayerMap;
//    }
//
//    @Override
//    public ISlashBladeState mrqx_s_Slashblade_Core$getSlashBladeState() {
//        return this.mrqx_s_Slashblade_Core$state;
//    }
//
//    @Override
//    public void mrqx_s_Slashblade_Core$setSlashBladeState(ISlashBladeState state) {
//        this.mrqx_s_Slashblade_Core$state = state;
//    }
//
//    @WrapOperation(method = "lambda$render$5",
//        at = @At(
//            value = "FIELD",
//            target = "Lmods/flammpfeil/slashblade/client/renderer/layers/LayerMainBlade;motionPlayer:Lnet/minecraftforge/common/util/LazyOptional;",
//            opcode = Opcodes.GETFIELD,
//            remap = false
//        ),
//        remap = false
//    )
//    private LazyOptional<MmdMotionPlayerGL2> wrapGetMotionPlayer(LayerMainBlade<T, M> instance, Operation<LazyOptional<MmdMotionPlayerGL2>> original,
//                                                                 @Local(argsOnly = true) ISlashBladeState state) {
//        this.mrqx_s_Slashblade_Core$setSlashBladeState(state);
//        if (!mrqx_s_Slashblade_Core$getMotionPlayerMap().containsKey(SlashBlade.prefix("standby").toString())) {
//            mrqx_s_Slashblade_Core$getMotionPlayerMap().put(SlashBlade.prefix("standby").toString(), motionPlayer);
//        }
//        return mrqx_s_Slashblade_Core$getMotionPlayerMap().getOrDefault(state.getComboRoot().toString(), motionPlayer);
//    }
//}
