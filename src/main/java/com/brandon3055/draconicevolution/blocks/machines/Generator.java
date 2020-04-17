package com.brandon3055.draconicevolution.blocks.machines;

import com.brandon3055.brandonscore.blocks.BlockBCore;
import com.brandon3055.draconicevolution.blocks.tileentity.TileGenerator;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Created by Brandon on 23/07/2014.
 * Block for DE Generator
 */
public class Generator extends BlockBCore/* implements ITileEntityProvider, IRenderOverride*/ {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public Generator(Properties properties) {
        super(properties);

        this.setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(ACTIVE, false));
    }

//    @Override
//    public boolean uberIsBlockFullCube() {
//        return true;
//    }


    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return super.getRenderType(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    //    @Override
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.CUTOUT;
//    }
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void registerRenderer(Feature feature) {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(feature.getRegistryName(), "inventory"));
//        ClientRegistry.bindTileEntitySpecialRenderer(TileGenerator.class, new AnimationTESR<>());
//    }


//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(FACING).getIndex();
//    }
//
//    @Override
//    public BlockState withRotation(BlockState state, Rotation rot) {
//        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
//    }
//
//    @Override
//    public BlockState withMirror(BlockState state, Mirror mirrorIn) {
//        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
//    }
//

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    //endregion


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileGenerator();
    }

    @Override
    public int getLightValue(BlockState state) {
        return state.get(ACTIVE) ? 13 : 0;
    }
}


