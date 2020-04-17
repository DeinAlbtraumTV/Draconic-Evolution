package com.brandon3055.draconicevolution.network;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static com.brandon3055.draconicevolution.network.DraconicNetwork.CHANNEL;


/**
 * Created by brandon3055 on 29/11/2016.
 * Update batcher for the Energy Net Beam effects.
 */
public class CrystalUpdateBatcher {

    public static final Map<Integer, BlockPos> ID_CRYSTAL_MAP = new HashMap<>();
    private static final Map<ServerPlayerEntity, List<BatchedCrystalUpdate>> batchQue = new HashMap<>();

    public CrystalUpdateBatcher() {}

    public static void queData(BatchedCrystalUpdate update, ServerPlayerEntity target) {
        if (!batchQue.containsKey(target)) {
            batchQue.put(target, new ArrayList<>());
        }

        batchQue.get(target).add(update);
    }

    public static void tickEnd() {
        if (!batchQue.isEmpty()) {
            for (ServerPlayerEntity playerMP : batchQue.keySet()) {
                List<BatchedCrystalUpdate> playerData = batchQue.get(playerMP);
                if (!playerData.isEmpty()) {
                    PacketCustom packet = new PacketCustom(CHANNEL, DraconicNetwork.C_CRYSTAL_UPDATE);
                    packet.writeVarInt(playerData.size());
                    playerData.forEach(update -> update.writeData(packet));
                    packet.sendToPlayer(playerMP);
                }
            }
            batchQue.clear();
        }
    }

    public static void handleBatchedData(MCDataInput input) {
        int count = input.readVarInt();
        for (int i = 0; i < count; i++) {
            BatchedCrystalUpdate update = new BatchedCrystalUpdate();
            update.readData(input);
            if (!ID_CRYSTAL_MAP.containsKey(update.crystalID)) {
                continue;
            }

            TileEntity tile = BrandonsCore.proxy.getClientWorld().getTileEntity(ID_CRYSTAL_MAP.get(update.crystalID));
            if (tile instanceof TileCrystalBase && !tile.isRemoved()) {
                ((TileCrystalBase) tile).receiveBatchedUpdate(update);
            }
        }

    }

    public static class BatchedCrystalUpdate {
        public int crystalID;
        public long crystalCapacity;
        public Map<Byte, Byte> indexToFlowMap = new LinkedHashMap<>();

        public BatchedCrystalUpdate() {}

        public BatchedCrystalUpdate(int crystalID, long crystalCapacity) {
            this.crystalID = crystalID;
            this.crystalCapacity = crystalCapacity;
        }

        public void writeData(MCDataOutput output) {
            output.writeVarInt(crystalID);
            output.writeVarLong(crystalCapacity);
            output.writeByte(indexToFlowMap.size());
            for (Byte index : indexToFlowMap.keySet()) {
                output.writeByte(index);
                output.writeByte(indexToFlowMap.get(index));
            }
        }

        public void readData(MCDataInput input) {
            crystalID = input.readVarInt();
            crystalCapacity = input.readVarLong();
            byte count = input.readByte();
            for (int i = 0; i < count; i++) {
                byte index = input.readByte();
                indexToFlowMap.put(index, input.readByte());
            }
        }
    }
}
