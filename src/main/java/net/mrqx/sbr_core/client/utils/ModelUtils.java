package net.mrqx.sbr_core.client.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.MathHelper;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.model.ISlashBladeEntityModel;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 模型渲染工具类。
 * <p>
 * 提供拔刀剑实体的模型姿态变换、人形模型缩放渲染以及 VMD 动画驱动等静态方法。
 */
public class ModelUtils {
    /**
     * 应用实体模型的身体旋转姿态后执行渲染操作。
     */
    public static void processSlashModel(PoseStack poseStack, ISlashBladeEntityModel model, Consumer<PoseStack> renderAction) {
        poseStack.pushPose();
        poseStack.mulPose(model.getBodyRot());
        renderAction.accept(poseStack);
        poseStack.popPose();
    }
    
    /**
     * 处理幼年人形模型的缩放与位置偏移，分别渲染头部和身体。
     */
    public static void processYoungHumanoidModel(PoseStack poseStack, Consumer<PoseStack> headRenderAction, Consumer<PoseStack> bodyRenderAction) {
        poseStack.pushPose();
        float f = 0.75F;
        poseStack.scale(f, f, f);
        poseStack.translate(0.0F, 1.0f, 0.0F);
        headRenderAction.accept(poseStack);
        poseStack.popPose();
        poseStack.pushPose();
        float f1 = 0.5F;
        poseStack.scale(f1, f1, f1);
        poseStack.translate(0.0F, 1.5F, 0.0F);
        bodyRenderAction.accept(poseStack);
        poseStack.popPose();
    }
    
    /**
     * 驱动 VMD 动画：更新模型各部件的位置/旋转，并设置身体旋转角度。
     */
    public static <T extends LivingEntity & ISlashBladeEntity,
        U extends EntityModel<T> & ISlashBladeEntityModel> void processAnimation(T entity, U model) {
        VanillaConvertedVmdAnimation currentAnimation = entity.getCurrentAnimation();
        if (currentAnimation != null) {
            for (Map.Entry<ModelPart, String> entry : model.getPartMap().entrySet()) {
                currentAnimation.updatePart(entry.getValue(), entry.getKey());
                Vec3f rot = currentAnimation.get3DTransform("body", TransformType.ROTATION, new Vec3f(
                    MathHelper.clampToRadian(model.getBody().xRot),
                    MathHelper.clampToRadian(model.getBody().yRot),
                    MathHelper.clampToRadian(model.getBody().zRot)));
                model.setBodyRotX(rot.getX());
                model.setBodyRotY(rot.getY());
                model.setBodyRotZ(rot.getZ());
            }
        } else {
            model.setBodyRotX(0);
            model.setBodyRotY(0);
            model.setBodyRotZ(0);
        }
    }
}
