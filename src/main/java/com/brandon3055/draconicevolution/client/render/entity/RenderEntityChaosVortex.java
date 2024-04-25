package com.brandon3055.draconicevolution.client.render.entity;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.entity.ChaosImplosionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by brandon3055 on 3/10/2015.
 */
public class RenderEntityChaosVortex extends EntityRenderer<ChaosImplosionEntity> {
    private static final ResourceLocation ORB_TEXTURE = new ResourceLocation(DraconicEvolution.MODID, "textures/particles/white_orb.png");

    public RenderEntityChaosVortex(EntityRendererProvider.Context context) {
        super(context);
//        super(manager);
//        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/reactor_core_model.obj"));
//        model = CCModel.combine(map.values());
    }

    @Override
    public void render(ChaosImplosionEntity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
//        if (entity.ticksExisted < 100) {
//            return;
//        }
//        RenderSystem.pushMatrix();
//        RenderSystem.translate(x, y, z);
//        RenderSystem.disableLighting();
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200F, 200F);
//        //ResourceHandler.bindResource("textures/models/white.png");
//
//        float scale = ((float) entity.ticksExisted - 100F) + tick;
//        scale /= 500F;
//        if (scale > 0.5F) scale = 0.5F;
//
//        RenderSystem.scale(scale, scale, scale);
//        //uvSphere.renderAll();
//
//        RenderSystem.enableLighting();
//        RenderSystem.popMatrix();
    }


    @Override
    public ResourceLocation getTextureLocation(ChaosImplosionEntity entity) {
        return ORB_TEXTURE;
    }

}
