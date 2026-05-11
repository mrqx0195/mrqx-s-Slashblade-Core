package net.mrqx.sbr_core.client.model;

import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;

import java.util.Map;

/**
 * 拔刀剑实体模型的接口。
 * 实现此接口的模型支持身体旋转姿态调整，用于配合 VMD 动画。
 */
public interface ISlashBladeEntityModel {
    /** 获取身体 ModelPart */
    ModelPart getBody();
    
    /** 获取身体绕 X 轴旋转角度（弧度） */
    float getBodyRotX();
    
    /** 获取身体绕 Y 轴旋转角度（弧度） */
    float getBodyRotY();
    
    /** 获取身体绕 Z 轴旋转角度（弧度） */
    float getBodyRotZ();
    
    /** 设置身体绕 X 轴旋转角度 */
    void setBodyRotX(float bodyRotX);
    
    /** 设置身体绕 Y 轴旋转角度 */
    void setBodyRotY(float bodyRotY);
    
    /** 设置身体绕 Z 轴旋转角度 */
    void setBodyRotZ(float bodyRotZ);
    
    /** 获取 ModelPart 到骨骼名称的映射，用于驱动 VMD 动画 */
    Map<ModelPart, String> getPartMap();
    
    /**
     * 以 ZYX 顺序组合身体旋转并返回四元数。
     */
    default Quaternionf getBodyRot() {
        return new Quaternionf().rotationZYX(this.getBodyRotZ(), this.getBodyRotY(), this.getBodyRotX());
    }
    
    /** 重置身体旋转角度为零 */
    default void resetBodyRot() {
        this.setBodyRotX(0.0F);
        this.setBodyRotY(0.0F);
        this.setBodyRotZ(0.0F);
    }
}
