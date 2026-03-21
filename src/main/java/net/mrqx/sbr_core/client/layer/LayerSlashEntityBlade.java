package net.mrqx.sbr_core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.CapabilitySlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import org.joml.Matrix4f;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class LayerSlashEntityBlade<T extends LivingEntity, M extends EntityModel<T>> extends LayerMainBlade<T, M> {
    protected final LazyOptional<MmdPmdModelMc> bladeHolder = LazyOptional.of(() -> {
        try {
            return new MmdPmdModelMc(SlashBlade.prefix("model/bladeholder.pmd"));
        } catch (MmdException | IOException e) {
            throw new RuntimeException("Failed to load blade model!", e);
        }
    });
    protected final LazyOptional<MmdMotionPlayerGL2> motionPlayer = LazyOptional.of(() -> {
        MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();
        this.bladeHolder.ifPresent((pmd) -> {
            try {
                mmp.setPmd(pmd);
            } catch (MmdException e) {
                throw new RuntimeException(e);
            }
        });
        return mmp;
    });

    public LayerSlashEntityBlade(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Override
    public void render(PoseStack matrixStack, MultiBufferSource bufferIn, int lightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderOffhandItem(matrixStack, bufferIn, lightIn, entity);
        float motionYOffset = 1.5F;
        double motionScale = 0.125;
        double modelScaleBase = 0.0078125;
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty()) {
            LazyOptional<ISlashBladeState> state = stack.getCapability(CapabilitySlashBlade.BLADESTATE);
            state.ifPresent((s) -> this.motionPlayer.ifPresent((mmp) -> {
                ComboState combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboSeq()) != null
                        ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboSeq())
                        : ComboStateRegistry.NONE.get();

                double time;
                for (time = TimeValueHelper.getMSecFromTicks((float) Math.max(0L, entity.level().getGameTime() - s.getLastActionTime()) + partialTicks); combo != ComboStateRegistry.NONE.get() && combo != null && (double) combo.getTimeoutMS() < time; combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(combo.getNextOfTimeout(entity)) != null ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(combo.getNextOfTimeout(entity)) : ComboStateRegistry.NONE.get()) {
                    time -= combo.getTimeoutMS();
                }

                if (combo == ComboStateRegistry.NONE.get()) {
                    combo = ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboRoot()) != null ? (ComboState) ((IForgeRegistry<?>) ComboStateRegistry.REGISTRY.get()).getValue(s.getComboRoot()) : ComboStateRegistry.STANDBY.get();
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
                    SlashBlade.LOGGER.warn(e);
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
                    SlashBlade.LOGGER.warn(e);
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
                        if (0 <= idx) {
                            float[] buf = new float[16];
                            mmp._skinning_mat[idx].getValue(buf);
                            Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
                            matrixStack.scale(-1.0F, 1.0F, 1.0F);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().mul(mat);
                            matrixStack.scale(-1.0F, 1.0F, 1.0F);
                        }

                        float modelScale = (float) (modelScaleBase * (1.0 / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);
                        String part;
                        if (s.isBroken()) {
                            part = "blade_damaged";
                        } else {
                            part = "blade";
                        }

                        BladeRenderState.renderOverrided(stack, obj, part, textureLocation, matrixStack, bufferIn, lightIn);
                        BladeRenderState.renderOverridedLuminous(stack, obj, part + "_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                    }

                    try (MSAutoCloser ignored2 = MSAutoCloser.pushMatrix(matrixStack)) {
                        int idx = mmp.getBoneIndexByName("hardpointB");
                        if (0 <= idx) {
                            float[] buf = new float[16];
                            mmp._skinning_mat[idx].getValue(buf);
                            Matrix4f mat = VectorHelper.matrix4fFromArray(buf);
                            matrixStack.scale(-1.0F, 1.0F, 1.0F);
                            PoseStack.Pose entry = matrixStack.last();
                            entry.pose().mul(mat);
                            matrixStack.scale(-1.0F, 1.0F, 1.0F);
                        }

                        float modelScale = (float) (modelScaleBase * (1.0 / motionScale));
                        matrixStack.scale(modelScale, modelScale, modelScale);
                        BladeRenderState.renderOverrided(stack, obj, "sheath", textureLocation, matrixStack, bufferIn, lightIn);
                        BladeRenderState.renderOverridedLuminous(stack, obj, "sheath_luminous", textureLocation, matrixStack, bufferIn, lightIn);
                        if (s.isCharged(entity)) {
                            float f = (float) entity.tickCount + partialTicks;
                            BladeRenderState.renderChargeEffect(stack, f, obj, "effect", ResourceLocation.parse("textures/entity/creeper/creeper_armor.png"), matrixStack, bufferIn, lightIn);
                        }
                    }
                }
            }));
        }
    }

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
