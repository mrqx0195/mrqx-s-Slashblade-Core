package net.mrqx.sbr_core.client.model;

import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;

import java.util.Map;

public interface ISlashBladeEntityModel {
    ModelPart getBody();
    
    float getBodyRotX();
    
    float getBodyRotY();
    
    float getBodyRotZ();
    
    void setBodyRotX(float bodyRotX);
    
    void setBodyRotY(float bodyRotY);
    
    void setBodyRotZ(float bodyRotZ);
    
    Map<ModelPart, String> getPartMap();
    
    default Quaternionf getBodyRot() {
        return new Quaternionf().rotationZYX(this.getBodyRotZ(), this.getBodyRotY(), this.getBodyRotX());
    }
    
    default void resetBodyRot() {
        this.setBodyRotX(0.0F);
        this.setBodyRotY(0.0F);
        this.setBodyRotZ(0.0F);
    }
}
