package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.entity.GuardianCrystalEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardianCrystalRenderer extends EntityRenderer<GuardianCrystalEntity> {
   private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(ENDER_CRYSTAL_TEXTURES);
   private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));
   private final ModelRenderer cube;
   private final ModelRenderer glass;
   private final ModelRenderer base;

   public GuardianCrystalRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowRadius = 0.5F;
      this.glass = new ModelRenderer(64, 32, 0, 0);
      this.glass.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.cube = new ModelRenderer(64, 32, 32, 0);
      this.cube.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.base = new ModelRenderer(64, 32, 0, 16);
      this.base.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
   }

   @Override
   public void render(GuardianCrystalEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      matrixStackIn.pushPose();
      float f = getY(entityIn, partialTicks);
      float f1 = ((float)entityIn.innerRotation + partialTicks) * 3.0F;
      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RENDER_TYPE);
      matrixStackIn.pushPose();
      matrixStackIn.scale(2.0F, 2.0F, 2.0F);
      matrixStackIn.translate(0.0D, -0.5D, 0.0D);
      int i = OverlayTexture.NO_OVERLAY;
      if (entityIn.shouldShowBottom()) {
         this.base.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      }

      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
      matrixStackIn.translate(0.0D, (double)(1.5F + f / 2.0F), 0.0D);
      matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      this.glass.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      float f2 = 0.875F;
      matrixStackIn.scale(0.875F, 0.875F, 0.875F);
      matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.glass.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      matrixStackIn.scale(0.875F, 0.875F, 0.875F);
      matrixStackIn.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
      this.cube.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
      matrixStackIn.popPose();
      matrixStackIn.popPose();
      BlockPos blockpos = entityIn.getBeamTarget();
      if (blockpos != null) {
         float f3 = (float)blockpos.getX() + 0.5F;
         float f4 = (float)blockpos.getY() + 0.5F;
         float f5 = (float)blockpos.getZ() + 0.5F;
         float f6 = (float)((double)f3 - entityIn.getX());
         float f7 = (float)((double)f4 - entityIn.getY());
         float f8 = (float)((double)f5 - entityIn.getZ());
         matrixStackIn.translate((double)f6, (double)f7, (double)f8);
         DraconicGuardianRenderer.renderBeam(-f6, -f7 + f, -f8, partialTicks, entityIn.innerRotation, matrixStackIn, bufferIn, packedLightIn);
      }

      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   public static float getY(GuardianCrystalEntity p_229051_0_, float p_229051_1_) {
      float f = (float)p_229051_0_.innerRotation + p_229051_1_;
      float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      f1 = (f1 * f1 + f1) * 0.4F;
      return f1 - 1.4F;
   }

   @Override
   public ResourceLocation getTextureLocation(GuardianCrystalEntity entity) {
      return ENDER_CRYSTAL_TEXTURES;
   }

   @Override
   public boolean shouldRender(GuardianCrystalEntity livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
      return super.shouldRender(livingEntityIn, camera, camX, camY, camZ) || livingEntityIn.getBeamTarget() != null;
   }
}
