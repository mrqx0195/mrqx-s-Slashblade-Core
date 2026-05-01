//package net.mrqx.sbr_core.mixin.compat.playeranimation;
//
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//import jp.nyatla.nymmd.MmdMotionPlayerGL2;
//import mods.flammpfeil.slashblade.SlashBlade;
//import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
//import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
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
//@Mixin(VmdAnimation.class)
//public class MixinVmdAnimation implements ISlashBladeAnimation {
//    @Shadow(remap = false)
//    @Final
//    static LazyOptional<MmdMotionPlayerGL2> motionPlayer;
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
//    @WrapOperation(method = "get3DTransform(Ljava/lang/String;Ldev/kosmx/playerAnim/api/TransformType;FLdev/kosmx/playerAnim/core/util/Vec3f;)Ldev/kosmx/playerAnim/core/util/Vec3f;",
//        at = @At(
//            value = "FIELD",
//            target = "Lmods/flammpfeil/slashblade/compat/playerAnim/VmdAnimation;motionPlayer:Lnet/minecraftforge/common/util/LazyOptional;",
//            opcode = Opcodes.GETSTATIC,
//            remap = false
//        ),
//        remap = false
//    )
//    private LazyOptional<MmdMotionPlayerGL2> wrapGetMotionPlayer(Operation<LazyOptional<MmdMotionPlayerGL2>> original) {
//        ISlashBladeState state = mrqx_s_Slashblade_Core$getSlashBladeState();
//        if (!mrqx_s_Slashblade_Core$getMotionPlayerMap().containsKey(SlashBlade.prefix("standby").toString())) {
//            mrqx_s_Slashblade_Core$getMotionPlayerMap().put(SlashBlade.prefix("standby").toString(), motionPlayer);
//        }
//        if (state != null) {
//            return mrqx_s_Slashblade_Core$getMotionPlayerMap().getOrDefault(state.getComboRoot().toString(), motionPlayer);
//        }
//        return motionPlayer;
//    }
//}
