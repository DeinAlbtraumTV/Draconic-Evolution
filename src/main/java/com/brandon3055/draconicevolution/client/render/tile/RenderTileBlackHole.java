package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.BlockEntityRendererTransparent;
import com.brandon3055.brandonscore.client.render.RenderUtils;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileBlackHole;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static com.brandon3055.draconicevolution.client.render.tile.RenderTileReactorCore.REACTOR_CORE_TYPE;

public class RenderTileBlackHole implements BlockEntityRendererTransparent<TileBlackHole> {
	private static CCModel model = null;

	public RenderTileBlackHole(BlockEntityRendererProvider.Context context) {
		if (model == null) {
			Map<String, CCModel> map = new OBJParser(new ResourceLocation(DraconicEvolution.MODID, "models/block/reactor/reactor_core.obj")).quads().ignoreMtl().parse();
			model = CCModel.combine(map.values());
		}
	}

	@Override
	public void renderTransparent(TileBlackHole tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		Matrix4 mat = new Matrix4(poseStack);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.brightness = packedLight;
		ccrs.overlay = packedOverlay;

		mat.translate(0.5, 0.5, 0.5);
		mat.scale(8);
		mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, Vector3.Y_POS);

		ccrs.bind(REACTOR_CORE_TYPE, buffers);
		model.render(ccrs, mat);
		RenderUtils.endBatch(buffers);
	}

	@Override
	public void render(TileBlackHole te, float partialTicks, PoseStack mStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
		Matrix4 mat = new Matrix4(mStack);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.brightness = packedLight;
		ccrs.overlay = packedOverlay;

		mat.translate(0.5, 0.5, 0.5);
		mat.scale(8);
		mat.rotate((ClientEventHandler.elapsedTicks + partialTicks) / 400F, Vector3.Y_POS);

		ccrs.bind(REACTOR_CORE_TYPE, buffers);
		model.render(ccrs, mat);
		RenderUtils.endBatch(buffers);
	}
}
