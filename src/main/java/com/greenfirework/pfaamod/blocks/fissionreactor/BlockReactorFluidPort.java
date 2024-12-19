package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockTEProviderBase;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorFluidPort;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockReactorFluidPort extends PFAABlockTEProviderBase<TileReactorFluidPort> {

	private static final String name = "reactorFluidPort";
	
	public BlockReactorFluidPort() {
		super(name, TileReactorFluidPort.class);
	}
	
	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
		getMyTileEntity(worldIn, x, y, z, 0, TileReactorFluidPort.class).onNeighborBlockChange();
	}

	@Override
	public TileReactorFluidPort createNewTileEntity(World world, int meta) {
		return new TileReactorFluidPort(world, meta);
	}
}
