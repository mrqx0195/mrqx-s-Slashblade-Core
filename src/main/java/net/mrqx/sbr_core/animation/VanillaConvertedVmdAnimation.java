package net.mrqx.sbr_core.animation;

import com.google.common.collect.Lists;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.MathHelper;
import dev.kosmx.playerAnim.core.util.Vec3f;
import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModelMc;
import jp.nyatla.nymmd.MmdVmdMotionMc;
import jp.nyatla.nymmd.core.PmdBone;
import jp.nyatla.nymmd.types.MmdVector3;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeMotionManager;
import mods.flammpfeil.slashblade.util.TimeValueHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.List;

/**
 * This class is based on the original work from SlashIllager by baguchi.
 * <p>
 * Original source: <a href="https://github.com/baguchi/SlashIllager/blob/master/src/main/java/baguchan/slash_illager/animation/VanillaConvertedVmdAnimation.java">baguchi/SlashIllager/.../VanillaConvertedVmdAnimation.java</a>
 * <p>
 * License: <a href="https://github.com/baguchi/SlashIllager/blob/master/LICENSE.md">MIT License</a>
 *
 * @author baguchi
 */
public class VanillaConvertedVmdAnimation {
    static final LazyOptional<MmdPmdModelMc> ALEX =
        LazyOptional.of(() -> {
            try {
                return new MmdPmdModelMc(SlashBlade.prefix("model/pa/alex.pmd"));
            } catch (MmdException | IOException e) {
                throw new RuntimeException("Error loading PmdModelMc", e);
            }
        });
    
    static final LazyOptional<MmdMotionPlayerGL2> MOTION_PLAYER =
        LazyOptional.of(() -> {
            MmdMotionPlayerGL2 mmp = new MmdMotionPlayerGL2();
            ALEX.ifPresent(pmd -> {
                try {
                    mmp.setPmd(pmd);
                } catch (MmdException e) {
                    MrqxSlashBladeCore.LOGGER.error("Error loading PMD Model", e);
                }
            });
            return mmp;
        });
    
    int currentTick;
    
    private float tickDelta = 0f;
    
    final ResourceLocation loc;
    final double start;
    final double end;
    final double span;
    boolean loop;
    
    private boolean isRunning = true;
    
    private boolean blendArms = false;
    private boolean blendLegs = true;
    
    static final List<String> ARMS = Lists.newArrayList("left arm", "right arm");
    static final List<String> LEGS = Lists.newArrayList("left leg", "right leg");
    
    
    public VanillaConvertedVmdAnimation(ResourceLocation loc, double start, double end, boolean loop) {
        this.loc = loc;
        this.start = start;
        this.end = end;
        
        this.span = TimeValueHelper.getTicksFromFrames((float) Math.abs(end - start));
        
        this.loop = loop;
        
        currentTick = 0;
    }
    
    public VanillaConvertedVmdAnimation getClone() {
        VanillaConvertedVmdAnimation tmp = new VanillaConvertedVmdAnimation(this.loc, this.start, this.end, this.loop);
        
        tmp.setBlendArms(this.blendArms);
        tmp.setBlendLegs(this.blendLegs);
        return tmp;
    }
    
    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
        this.setupAnim();
    }
    
    public VanillaConvertedVmdAnimation setBlendArms(boolean blend) {
        blendArms = blend;
        return this;
    }
    
    public VanillaConvertedVmdAnimation setBlendLegs(boolean blend) {
        blendLegs = blend;
        return this;
    }
    
    public void tick() {
        if (this.isRunning) {
            this.currentTick++;
            this.loop = false;
            if (span <= currentTick) {
                this.stop();
            }
        }
    }
    
    public void play() {
        this.currentTick = 0;
        this.isRunning = true;
    }
    
    public void stop() {
        this.isRunning = false;
    }
    
    public int getCurrentTick() {
        return currentTick;
    }
    
    public boolean isActive() {
        return this.isRunning;
    }
    
    public void updatePart(String partName, ModelPart part) {
        Vec3f pos = this.get3DTransform(partName, TransformType.POSITION, new Vec3f(part.x, part.y, part.z));
        part.x = pos.getX();
        part.y = pos.getY();
        part.z = pos.getZ();
        Vec3f rot = this.get3DTransform(partName, TransformType.ROTATION, new Vec3f(
            MathHelper.clampToRadian(part.xRot),
            MathHelper.clampToRadian(part.yRot),
            MathHelper.clampToRadian(part.zRot)));
        part.setRotation(rot.getX(), rot.getY(), rot.getZ());
    }
    
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public Vec3f get3DTransform(String boneName, TransformType type, Vec3f value0) {
        this.setupAnim();
        
        float finalizeScale = 2.0f;
        
        Vector3f blend = new Vector3f(value0.getX(), value0.getY(), value0.getZ());
        
        boolean b = (!this.blendArms && ARMS.contains(boneName)) || (!this.blendLegs && LEGS.contains(boneName));
        if (type != TransformType.POSITION && b) {
            blend.mul(0);
        }
        
        if (!MOTION_PLAYER.isPresent()) {
            return value0;
        }
        MmdMotionPlayerGL2 mmp = MOTION_PLAYER.orElseThrow(() -> new IllegalStateException("No MOTION_PLAYER present"));
        
        PmdBone bone = mmp.getBoneByName(boneName);
        
        if (bone != null) {
            switch (type) {
                case POSITION: {
                    MmdVector3 org = bone.m_vec3Position;
                    Vector3f tmp = new Vector3f(org.x, org.y, org.z);
                    tmp = tmp.mul(1, -1, 1);
                    tmp.mul(finalizeScale).add(blend);
                    return new Vec3f(tmp.x, tmp.y, tmp.z);
                }
                case ROTATION: {
                    Quaterniond qt = new Quaterniond(bone.m_vec4Rotate.x, bone.m_vec4Rotate.y, bone.m_vec4Rotate.z, bone.m_vec4Rotate.w);
                    Vector3d tmp = quaternionToEulerZYX(qt);
                    tmp = tmp.mul(-1, 1, -1);
                    tmp.add(blend);
                    return new Vec3f((float) tmp.x, (float) tmp.y, (float) tmp.z);
                }
                default:
                    break;
            }
        }
        
        return value0;
    }
    
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    Vector3d quaternionToEulerZYX(Quaterniond qt) {
        Vector3d tmp = new Vector3d();
        Quaterniond normalizedQt = qt.normalize();
        double wx = normalizedQt.w * normalizedQt.x;
        double wy = normalizedQt.w * normalizedQt.y;
        double wz = normalizedQt.w * normalizedQt.z;
        double xx = normalizedQt.x * normalizedQt.x;
        double xy = normalizedQt.x * normalizedQt.y;
        double xz = normalizedQt.x * normalizedQt.z;
        double yy = normalizedQt.y * normalizedQt.y;
        double yz = normalizedQt.y * normalizedQt.z;
        double zz = normalizedQt.z * normalizedQt.z;
        double m00 = 1.0 - 2.0 * (yy + zz);
        double m01 = 2.0 * (xy + wz);
        double m02 = 2.0 * (xz - wy);
        double m12 = 2.0 * (yz + wx);
        double m22 = 1.0 - 2.0 * (xx + yy);
        tmp.z = Math.atan2(m01, m00);
        tmp.y = Math.asin(-m02);
        tmp.x = Math.atan2(m12, m22);
        return tmp;
    }
    
    
    public void setupAnim() {
        if (!MOTION_PLAYER.isPresent()) {
            return;
        }
        
        MmdMotionPlayerGL2 mmp = MOTION_PLAYER.orElseThrow(() -> new IllegalStateException("MOTION_PLAYER is not present"));
        
        double eofTime = 0;
        MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(loc);
        try {
            mmp.setVmd(motion);
            eofTime = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
        } catch (Exception e) {
            MrqxSlashBladeCore.LOGGER.error("Failed to set up VMD Motion Animation", e);
        }
        
        double time = TimeValueHelper.getMSecFromTicks(currentTick + this.tickDelta);
        time = Math.min(eofTime, time);
        time = TimeValueHelper.getMSecFromFrames((float) start) + time;
        
        try {
            mmp.updateMotion((float) time);
        } catch (MmdException e) {
            MrqxSlashBladeCore.LOGGER.error("Failed to update VMD Motion Animation", e);
        }
    }
}