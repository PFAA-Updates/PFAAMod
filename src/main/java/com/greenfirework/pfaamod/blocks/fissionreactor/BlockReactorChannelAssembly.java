package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockTESRProviderBase;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TESRReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorChannelAssembly;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockReactorChannelAssembly extends PFAABlockTESRProviderBase<TileReactorChannelAssembly, TESRReactorChannelAssembly> {

	private static final String name = "channelAssembly";

	public BlockReactorChannelAssembly() {
		super(name, TileReactorChannelAssembly.class, TESRReactorChannelAssembly.class);	
	}

	@Override
	public TileReactorChannelAssembly createNewTileEntity(World world, int meta) {
		return new TileReactorChannelAssembly(world, meta);
	}
	
	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
		if (worldIn.getBlock(x, y + 1, z) != Blocks.air)
			getMyTileEntity(worldIn, x, y, z, 0, TileReactorChannelAssembly.class).brokenOrCovered();
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
		super.onBlockDestroyedByExplosion(worldIn, x, y, z, explosionIn);
		getMyTileEntity(worldIn, x, y, z, 0, TileReactorChannelAssembly.class).brokenOrCovered();
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
		super.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
		getMyTileEntity(worldIn, x, y, z, 0, TileReactorChannelAssembly.class).brokenOrCovered();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		return getMyTileEntity(worldIn, x, y, z, 0, TileReactorChannelAssembly.class).onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
	}
}
