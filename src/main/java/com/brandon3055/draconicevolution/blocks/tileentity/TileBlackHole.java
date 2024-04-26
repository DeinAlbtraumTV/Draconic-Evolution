package com.brandon3055.draconicevolution.blocks.tileentity;

import com.brandon3055.brandonscore.blocks.TileBCore;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class TileBlackHole extends TileBCore {
	private int tick = 0;
	private List<BlockPos> blockIntrusions = new ArrayList<>();

	public TileBlackHole(BlockPos pos, BlockState state) {
		super(DEContent.tile_black_hole, pos, state);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide) {
			damageCollidingEntities();
			removeIntrudingBlocks();
		}

	}

	private static DamageSource blackHole = new DamageSource("draconicevolution.black_hole").bypassArmor().bypassMagic().setMagic();

	private void damageCollidingEntities() {
		List<Entity> entities = level.getEntities(null, new AABB(getBlockPos().offset(-10, -10, -10), getBlockPos().offset(10, 10, 10)));
		for (Entity entity : entities){
			double distance = Math.min(Utils.getDistance(new Vec3D(entity).add(0, entity.getEyeHeight(), 0), Vec3D.getCenter(worldPosition)), Utils.getDistance(new Vec3D(entity), Vec3D.getCenter(worldPosition)));
			if (distance < (8F / 2) + 0.5 && !entity.isSpectator()) {
				entity.hurt(blackHole, (float) ((5F - distance) * 10F));
			}
		}
	}

	//This are basically the same as in the TileReactorCore
	public void removeIntrudingBlocks() {
		if (!(level instanceof ServerLevel)) {
			return;
		}

		if (tick % 100 == 0) {
			double rad = (8 * 1.05) / 2;
			Iterable<BlockPos> inRange = BlockPos.betweenClosed(worldPosition.offset(-rad, -rad, -rad), worldPosition.offset(rad + 1, rad + 1, rad + 1));

			for (BlockPos p : inRange) {
				if (p.equals(worldPosition) || Utils.getDistance(p.getX(), p.getY(), p.getZ(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) - 0.5 >= rad) {
					continue;
				}

				if (!level.isEmptyBlock(p) && !blockIntrusions.contains(p)) {
					blockIntrusions.add(p.immutable());
				}
			}
		}

		if (blockIntrusions.size() > 0) {
			Iterator<BlockPos> i = blockIntrusions.iterator();

			while (i.hasNext()) {
				BlockPos blockPos = i.next();
				final Vec3D iPos = new Vec3D(blockPos);

				((ServerLevel) level).sendParticles(ParticleTypes.FLAME, iPos.x + 0.5, iPos.y + 0.5, iPos.z + 0.5, 5, 0.5, 0.5, 0.5, 0.01D);

				i.remove();
				level.levelEvent(2001, blockPos, Block.getId(level.getBlockState(blockPos)));
				level.removeBlock(blockPos, false);
			}

			if (tick % 20 == 0 || level.random.nextInt(40) == 0) {
				level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 0.9F + level.random.nextFloat() * 0.2F);
			}
		}
	}
}
