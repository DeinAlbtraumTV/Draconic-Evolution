package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;

import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 20/05/2016.
 */
public class RenderTileEnergyPylon extends TESRBase<TileEnergyPylon> {

    private static CCModel model;

    public RenderTileEnergyPylon() {
        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/pylon_sphere.obj")); //Note dont generate the model evey render frame move this to constructor
        model = CCModel.combine(map.values());
        model.apply(new Scale(-0.35, -0.35, -0.35));
        model.computeNormals();
    }


    @Override
    public void render(TileEnergyPylon te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!te.structureValid.get()) {
            return;
        }
//        Map<String, CCModel> map = OBJParser.parseModels(ResourceHelperDE.getResource("models/pylon_sphere.obj")); //Note dont generate the model evey render frame move this to constructor
//        model = CCModel.combine(map.values());
//        model.apply(new Scale(-0.35, -0.35, -0.35));
//        model.computeNormals();

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();

        TextureUtils.bindBlockTexture();
        GlStateManager.pushMatrix();
        Vector3 translateVector = new Vector3(x + 0.5, y + (te.sphereOnTop.get() ? 1.5 : -0.5), z + 0.5);
        translateVector.translation().glApply();
        IconTransformation iconTransform = new IconTransformation(DETextures.getDETexture("models/pylon_sphere_texture"));
        setLighting(200F);

        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        Rotation rotY = new Rotation(((ClientEventHandler.elapsedTicks + partialTicks) * 2F) * MathHelper.torad, new Vector3(0, 1, 0.5).normalize());
        model.render(ccrs, iconTransform, rotY);
        ccrs.draw();

        float f = ((ClientEventHandler.elapsedTicks + partialTicks) % 30F) / 30F;

        if (te.isOutputMode.get()) {
            f = 1F - f;
        }

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
        GlStateManager.color4f(1F, 1F, 1F, 1F - f);

        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        model.render(ccrs, iconTransform, new Scale(1 + f, 1 + f, 1 + f).with(rotY));
        ccrs.draw();

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
    }
}
