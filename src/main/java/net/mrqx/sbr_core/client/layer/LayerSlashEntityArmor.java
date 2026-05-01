package net.mrqx.sbr_core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.client.model.ISlashBladeEntityModel;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;

@OnlyIn(Dist.CLIENT)
public class LayerSlashEntityArmor<T extends LivingEntity & ISlashBladeEntity, M extends HumanoidModel<T> & ISlashBladeEntityModel, A extends HumanoidModel<T> & ISlashBladeEntityModel> extends HumanoidArmorLayer<T, M, A> {
    public LayerSlashEntityArmor(RenderLayerParent<T, M> renderer, A innerModel, A outerModel, ModelManager modelManager) {
        super(renderer, innerModel, outerModel, modelManager);
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        poseStack.pushPose();
        poseStack.mulPose(this.getParentModel().getBodyRot());
        super.render(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        poseStack.popPose();
    }
}
