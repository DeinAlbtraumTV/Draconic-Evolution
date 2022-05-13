package com.brandon3055.draconicevolution.api.modules.lib;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.IOPStorageModifiable;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Created by brandon3055 on 19/4/20.
 */
public class TileModuleContext extends ModuleContext {
    private BlockEntity tile;

    public TileModuleContext(BlockEntity tile) {
        super();
        this.tile = tile;
    }

    @Override
    public IOPStorageModifiable getOpStorage() {
        LazyOptional<IOPStorage> optional = tile.getCapability(DECapabilities.OP_STORAGE);
        if (optional.isPresent()) {
            return (IOPStorageModifiable) optional.orElseThrow(IllegalStateException::new);
        }
        return null;
    }

    @Override
    public Type getType() {
        return Type.TILE_ENTITY;
    }

    /**
     * @return the tile entity this module is installed in.
     */
    public BlockEntity getTile() {
        return tile;
    }
}
