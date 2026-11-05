package com.titammods.hephaestus_tools.mixin.client;

import com.titammods.hephaestus_tools.client.workbench.WorkbenchAnimation;
import com.titammods.hephaestus_tools.client.workbench.WorkbenchSession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    @Shadow public ModelPart head;
    @Shadow public ModelPart hat;
    @Shadow public ModelPart body;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart leftLeg;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void hephaestusTools$workbenchPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                float ageInTicks, float netHeadYaw, float headPitch,
                                                CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (entity != mc.player) {
            if (com.titammods.hephaestus_tools.client.workbench.RemoteCraft.isActive(entity.getId())
                    && !com.titammods.hephaestus_tools.client.workbench.RemoteCraft.isAnimated(entity.getId())) {
                com.titammods.hephaestus_tools.client.workbench.RemoteCraft.applyPose(entity.getId(), head, body, rightArm, leftArm);
                hat.copyFrom(head);
            }
            return;
        }

        float weight = WorkbenchSession.isRunning() ? WorkbenchAnimation.poseWeight() : 0.0f;
        if (weight <= 0.0f) {
            WorkbenchAnimation.restoreAnchors(head, body, rightArm, leftArm);
            hat.copyFrom(head);
            return;
        }
        WorkbenchAnimation.applyProceduralPose(head, body, rightArm, leftArm, rightLeg, leftLeg,
                weight, WorkbenchSession.sceneSeconds());

        hat.copyFrom(head);
    }
}
