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
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.Nullable;
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
    /**
     * 默认的 Alex 模型 PMD 实例。
     */
    @Nullable
    public static final MmdPmdModelMc ALEX;
    
    /**
     * 全局 MMD 动作播放器。
     */
    @Nullable
    public static final MmdMotionPlayerGL2 MOTION_PLAYER;
    
    static {
        MmdPmdModelMc tmpAlex = null;
        try {
            tmpAlex = new MmdPmdModelMc(ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/pa/alex.pmd"));
        } catch (IOException | MmdException e) {
            MrqxSlashBladeCore.LOGGER.warn("Failed to new jp.nyatla.nymmd.MmdPmdModelMc", e);
        }
        ALEX = tmpAlex;
        
        MmdMotionPlayerGL2 tmpMp = null;
        if (ALEX != null) {
            tmpMp = new MmdMotionPlayerGL2();
            try {
                tmpMp.setPmd(ALEX);
            } catch (MmdException e) {
                MrqxSlashBladeCore.LOGGER.warn("Failed to setPmd for MotionPlayer", e);
            }
        }
        MOTION_PLAYER = tmpMp;
    }
    
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
    
    
    /**
     * @param loc   动画资源路径
     * @param start 起始帧（ms）
     * @param end   结束帧（ms）
     * @param loop  是否循环
     */
    public VanillaConvertedVmdAnimation(ResourceLocation loc, double start, double end, boolean loop) {
        this.loc = loc;
        this.start = start;
        this.end = end;
        
        this.span = TimeValueHelper.getTicksFromFrames((float) Math.abs(end - start));
        
        this.loop = loop;
        
        currentTick = 0;
    }
    
    /**
     * 克隆当前动画（每个实体实例应有独立的动画状态）。
     */
    public VanillaConvertedVmdAnimation getClone() {
        VanillaConvertedVmdAnimation tmp = new VanillaConvertedVmdAnimation(this.loc, this.start, this.end, this.loop);
        
        tmp.setBlendArms(this.blendArms);
        tmp.setBlendLegs(this.blendLegs);
        return tmp;
    }
    
    /**
     * 设置帧插值并更新动画状态。
     */
    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
        this.setupAnim();
    }
    
    /**
     * 设置是否混合手臂（不覆盖手臂动画）。
     */
    public VanillaConvertedVmdAnimation setBlendArms(boolean blend) {
        blendArms = blend;
        return this;
    }
    
    /**
     * 设置是否混合腿部（不覆盖腿部动画）。
     */
    public VanillaConvertedVmdAnimation setBlendLegs(boolean blend) {
        blendLegs = blend;
        return this;
    }
    
    /**
     * 推进一帧，并在动画播放完毕后停止。
     */
    public void tick() {
        if (this.isRunning) {
            this.currentTick++;
            this.loop = false;
            if (span <= currentTick) {
                this.stop();
            }
        }
    }
    
    /**
     * 从头开始播放动画。
     */
    public void play() {
        this.currentTick = 0;
        this.isRunning = true;
    }
    
    /**
     * 停止播放动画。
     */
    public void stop() {
        this.isRunning = false;
    }
    
    /**
     * 获取当前播放进度（tick）。
     */
    public int getCurrentTick() {
        return currentTick;
    }
    
    /**
     * 动画是否正在播放。
     */
    public boolean isActive() {
        return this.isRunning;
    }
    
    /**
     * 将 VMD 动画的位置/旋转数据应用至指定的 ModelPart。
     */
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
    
    /**
     * 获取指定骨骼在 VMD 当前位置/旋转下的 3D 变换值。
     * <p>
     * 若该骨骼被禁用混合则返回原始值。
     */
    public Vec3f get3DTransform(String boneName, TransformType type, Vec3f value0) {
        this.setupAnim();
        
        float finalizeScale = 2.0f;
        
        Vector3f blend = new Vector3f(value0.getX(), value0.getY(), value0.getZ());
        
        boolean b = (!this.blendArms && ARMS.contains(boneName)) || (!this.blendLegs && LEGS.contains(boneName));
        if (type != TransformType.POSITION && b) {
            blend.mul(0);
        }
        
        if (MOTION_PLAYER == null) {
            return value0;
        }
        
        PmdBone bone = MOTION_PLAYER.getBoneByName(boneName);
        
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
    
    /**
     * 将四元数转换为 ZYX 欧拉角。
     */
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
    
    
    /**
     * 根据当前 tick 更新 MMD 动作播放器的帧状态。
     */
    public void setupAnim() {
        if (MOTION_PLAYER == null) {
            return;
        }
        
        double eofTime = 0;
        MmdVmdMotionMc motion = BladeMotionManager.getInstance().getMotion(loc);
        try {
            MOTION_PLAYER.setVmd(motion);
            eofTime = TimeValueHelper.getMSecFromFrames(motion.getMaxFrame());
        } catch (Exception e) {
            MrqxSlashBladeCore.LOGGER.error("Failed to set up VMD Motion Animation", e);
        }
        
        double time = TimeValueHelper.getMSecFromTicks(currentTick + this.tickDelta);
        time = Math.min(eofTime, time);
        time = TimeValueHelper.getMSecFromFrames((float) start) + time;
        
        try {
            MOTION_PLAYER.updateMotion((float) time);
        } catch (MmdException e) {
            MrqxSlashBladeCore.LOGGER.error("Failed to update VMD Motion Animation", e);
        }
    }
}