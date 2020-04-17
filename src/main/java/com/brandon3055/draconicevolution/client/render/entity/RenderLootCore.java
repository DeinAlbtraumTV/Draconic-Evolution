package com.brandon3055.draconicevolution.client.render.entity;

import codechicken.lib.colour.ColourARGB;
import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.entity.EntityLootCore;
import com.brandon3055.draconicevolution.utils.ResourceHelperDE;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * Created by Brandon on 21/11/2014.
 */
public class RenderLootCore extends EntityRenderer<EntityLootCore> {

    public RenderLootCore(EntityRendererManager renderManager) {
        super(renderManager);
    }


    @Override
    public void doRender(EntityLootCore entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ResourceHelperDE.bindTexture("textures/items/loot_core.png");

        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        float yOffset = MathHelper.sin(((float) ClientEventHandler.elapsedTicks + partialTicks) / 10.0F) * 0.1F + 0.1F;
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(x, y + (entity.getHeight() / 2) + yOffset, z), new Rotation(((float) (ClientEventHandler.elapsedTicks + entity.timeOffset) + partialTicks) / 30F, new Vector3(entity.rotX, entity.rotY, 0).normalize()), 0.1);
        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
        ccrs.draw();

        entity.isLooking = Minecraft.getInstance().getRenderManager().pointedEntity == entity;

        if (entity.lookAnimation > 0F) {
            float f = this.renderManager.playerViewY;
            float f1 = this.renderManager.playerViewX;
            boolean flag1 = this.renderManager.options.thirdPersonView == 2;
            renderLabel(entity, this.getFontRendererFromRenderManager(), (float) x, (float) y, (float) z, f, f1, flag1);
        }
    }

    public void renderLabel(EntityLootCore lootCore, FontRenderer renderer, float x, float y, float z, float viewY, float viewX, boolean thirdPerson) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-viewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) (thirdPerson ? -1 : 1) * viewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepthTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int rows = lootCore.displayMap.size();

        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        double scale = lootCore.lookAnimation;
        double heightScale = Math.min(scale * 2, 1);
        double widthScale = Math.max((scale - 0.5) * 2, 0.0);
        double height = (rows * 8 * heightScale) + 1;
        double width = widthScale * 40;
        double xPos = 10 - width / 2;
        double yPos = -4 - height / 2;

        renderBox(vertexbuffer, xPos - 1, yPos, 1, height, 0x60AAAAAA);
        renderBox(vertexbuffer, xPos + width, yPos, 1, height, 0x60AAAAAA);
        renderBox(vertexbuffer, xPos - 1, yPos - 1, width + 2, 1, 0x60AAAAAA);
        renderBox(vertexbuffer, xPos - 1, yPos + height, width + 2, 1, 0x60AAAAAA);
        renderBox(vertexbuffer, xPos, yPos, width, height, 0x60000000);

        tessellator.draw();
        GlStateManager.enableTexture();

        if (lootCore.lookAnimation >= 1F) {

            GlStateManager.pushMatrix();
            GlStateManager.translated(-5, 0, 0);
            GlStateManager.scaled(9, 9, 9);
            GlStateManager.rotated(180, 1, 0, 0);
            GlStateManager.popMatrix();
            String name = I18n.format("entity.draconicevolution:lootCore.name");
            int w = renderer.getStringWidth(name);
            renderer.drawString(name, 11 - (w / 2F), (int) yPos - 10, -1);

            int row = 0;
            for (ItemStack stack : lootCore.displayMap.keySet()) {
                int rowY = (int) yPos + row * 8 + 4;
                GlStateManager.pushMatrix();
                GlStateManager.translated(-5, rowY, 0);
                GlStateManager.scaled(9, 9, 9);
                GlStateManager.rotated(180, 1, 0, 0);
                Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
                renderer.drawString("x" + lootCore.displayMap.get(stack), 0, -4 + rowY, -1);
                row++;
            }
        }


        GlStateManager.depthMask(true);
        GlStateManager.enableDepthTest();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private void renderBox(BufferBuilder buffer, double x, double y, double width, double height, int colour) {
        ColourARGB colourARGB = new ColourARGB(colour);
        double zLevel = 0;
        buffer.pos(x, y + height, zLevel).color(colourARGB.r / 255F, colourARGB.g / 255F, colourARGB.b / 255F, colourARGB.a / 255F).endVertex();
        buffer.pos(x + width, y + height, zLevel).color(colourARGB.r / 255F, colourARGB.g / 255F, colourARGB.b / 255F, colourARGB.a / 255F).endVertex();
        buffer.pos(x + width, y, zLevel).color(colourARGB.r / 255F, colourARGB.g / 255F, colourARGB.b / 255F, colourARGB.a / 255F).endVertex();
        buffer.pos(x, y, zLevel).color(colourARGB.r / 255F, colourARGB.g / 255F, colourARGB.b / 255F, colourARGB.a / 255F).endVertex();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLootCore entity) {
        return ResourceHelperDE.getResource("textures/items/loot_core.png");
    }

}
