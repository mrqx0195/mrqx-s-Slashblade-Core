package net.mrqx.sbr_core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.client.renderer.layers.LayerMainBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.client.renderer.model.obj.WavefrontObject;
import mods.flammpfeil.slashblade.client.renderer.util.BladeRenderState;
import mods.flammpfeil.slashblade.client.renderer.util.MSAutoCloser;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

/**
 * 支持 MMD 骨骼驱动的拔刀剑渲染层。
 * <p>
 * 重写了原版的 {@link LayerMainBlade}，使用 MMD 骨骼动画（VMD）
 * 渲染刀与鞘，并支持充能特效显示。
 */
public class LayerSlashEntityBlade<T extends LivingEntity, M extends EntityModel<T>> extends LayerMainBlade<T, M> {
    @Nullable
    private MmdPmdModelMc cachedBladeholder;
    @Nullable
    private MmdMotionPlayerGL2 cachedMotionPlayer;
    
    /**
     * 获取或创建刀挂（bladeholder）的 PMD 模型。
     */
    public Optional<MmdPmdModelMc> getBladeholder() {
        if (cachedBladeholder == null) {
            try {
                cachedBladeholder = new MmdPmdModelMc(ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/bladeholder.pmd"));
            } catch (IOException | MmdException e) {
                MrqxSlashBladeCore.LOGGER.warn("Failed to new jp.nyatla.nymmd.MmdPmdModelMc", e);
            }
        }
        return Optional.ofNullable(cachedBladeholder);
    }
    
    /**
     * 获取或创建 MMD 动作播放器，并绑定刀挂模型。
     */
    public Optional<MmdMotionPlayerGL2> getMotionPlayer() {
        if (cachedMotionPlayer == null) {
            cachedMotionPlayer = new MmdMotionPlayerGL2();
            this.getBladeholder().ifPresent(bladeHolder -> {
                try {
                    cachedMotionPlayer.setPmd(bladeHolder);
                } catch (MmdException e) {
                    MrqxSlashBladeCore.LOGGER.warn("Failed to setPmd for MotionPlayer", e);
                }
            });
        }
        return Optional.ofNullable(cachedMotionPlayer);
    }
    
    /**
     * @param entityRendererIn 父渲染器
     */
    public LayerSlashEntityBlade(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }
    
    /**
     * 渲染主方法：渲染副手物品，计算当前连段的动画帧并驱动 MMD 骨骼模型渲染刀与鞘。
     */
    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderOffhandItem(matrixStack, bufferIn, lightIn, entity);
        float motionYOffset = 1.5F;
        double motionScale = 0.125;
        double modelScaleBase = 0.0078125;
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty()) {
            BladeStateAccess.of(stack).ifPresent((s) -> this.getMotionPlayer().ifPresent(mmp -> {
                ComboState combo = ComboStateRegistry.REGISTRY.get(s.getComboSeq()) != null
                    ? ComboStateRegistry.REGISTRY.get(s.getComboSeq())
                    : ComboStateRegistry.NONE.get();
                
                double time;
                for (time = TimeValueHelper.getMSecFromTicks((float) Math.max(0L, entity.level().getGameTime() - s.getLastActionTime()) + partialTicks); combo != ComboStateRegistry.NONE.get() && combo != null && (double) combo.getTimeoutMS() < time; combo = ComboStateRegistry.REGISTRY.get(combo.getNextOfTimeout(entity)) != null ? ComboStateRegistry.REGISTRY.get(combo.getNextOfTimeout(entity)) : ComboStateRegistry.NONE.get()) {
                    time -= combo.getTimeoutMS();
                }
                
                if (combo == ComboStateRegistry.NONE.get()) {
                    combo = ComboStateRegistry.REGISTRY.get(s.getComboRoot()) != null ? ComboStateRegistry.REGISTRY.get(s.getComboRoot()) : ComboStateRegistry.STANDBY.get();
                }
                
                MmdVmdMotionMc motion = null;
                if (combo != null) {
                    motion = BladeMotionManager.getInstance().getMotion(combo.getMotionLoc());
                }
                
                double maxSeconds = 0.0;
                
                try {
                    mmp.setVmd(motion);
                    if (motion != null) {
                        maxSeconds = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
                    }
                } catch (Exception e) {
                    MrqxSlashBladeCore.LOGGER.warn("Error while rendering LayerSlashEntityBlade", e);
                }
                
                double start = 0.0;
                if (combo != null) {
                    start = TimeValueHelper.getMSecFromFrames(combo.getStartFrame());
                }
                
                double end = 0.0;
                if (combo != null) {
                    end = TimeValueHelper.getMSecFromFrames(combo.getEndFrame());
                }
                
                double span = Math.abs(end - start);
                span = Math.min(maxSeconds, span);
                if (combo != null && combo.getLoop()) {
                    time %= span;
                }
                
                time = Math.min(span, time);
                time = start + time;
                
                try {
                    mmp.updateMotion((float) time);
                } catch (MmdException e) {
                    MrqxSlashBladeCore.LOGGER.warn("Error while rendering LayerSlashEntityBlade", e);
                }
                
                try (MSAutoCloser ignored = MSAutoCloser.pushMatrix(matrixStack)) {
                    this.setUserPose(matrixStack, entity, partialTicks);
                    matrixStack.translate(0.0F, motionYOffset, 0.0F);
                    matrixStack.scale((float) motionScale, (float) motionScale, (float) motionScale);
                    matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    ResourceLocation textureLocation = s.getTexture().orElse(DefaultResources.resourceDefaultTexture);
                    WavefrontObject obj = BladeModelManager.getInstance().getModel(s.getModel().orElse(DefaultResources.resourceDefaultModel));
                    
                    try (MSAutoCloser ignored1 = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointA");
                        String part;
                        if (s.isBroken()) {
                            part = "blade_damaged";
                        } else {
                            part = "blade";
                        }
                        renderPart(matrixStack, bufferIn, lightIn, motionScale, modelScaleBase, stack, mmp, textureLocation, obj, idx, part);
                    }
                    
                    try (MSAutoCloser ignored2 = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointB");
                        String part = "sheath";
                        renderPart(matrixStack, bufferIn, lightIn, motionScale, modelScaleBase, stack, mmp, textureLocation, obj, idx, part);
                        if (s.isCharged(entity)) {
                            float f = (float) entity.tickCount + partialTicks;
                            BladeRenderState.renderChargeEffect(stack, f, obj, "effect", ResourceLocation.parse("textures/entity/creeper/creeper_armor.png"), matrixStack, bufferIn, lightIn);
                        }
                    }
                }
            }));
        }
    }
    
    /**
     * 渲染 MMD 骨骼的指定部件（刀/鞘/特效）。
     */
    private void renderPart(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, double motionScale, double modelScaleBase, ItemStack stack, MmdMotionPlayerGL2 mmp, ResourceLocation textureLocation, WavefrontObject obj, int idx, String part) {
        if (0 <= idx) {
            float[] buf = new float[16];
            mmp._skinning_mat[idx].getValue(buf);
            Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
            matrixStack.scale(-1.0F, 1.0F, 1.0F);
            PoseStack.Pose entry = matrixStack.last();
            entry.pose().mul(mat);
            matrixStack.scale(-1.0F, 1.0F, 1.0F);
        }
        
        float modelScale = (float) (modelScaleBase * ((double) 1.0F / motionScale));
        matrixStack.scale(modelScale, modelScale, modelScale);
        
        BladeRenderState.renderOverrided(stack, obj, part, textureLocation, matrixStack, bufferIn, lightIn);
        BladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
    }
    
    /**
     * 设置用户的姿态：如果存在 VMD 动画，则更新其帧插值。
     */
    @Override
    public void setUserPose(PoseStack matrixStack, T entity, float partialTicks) {
        if (entity instanceof ISlashBladeEntity slashBladeEntity) {
            VanillaConvertedVmdAnimation animationPlayer = slashBladeEntity.getCurrentAnimation();
            if (animationPlayer != null) {
                animationPlayer.setTickDelta(partialTicks);
            }
        }
    }
}
