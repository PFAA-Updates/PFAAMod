package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockTEProviderBase;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorRodMotor;

import net.minecraft.world.World;

public class BlockReactorRodMotor extends PFAABlockTEProviderBase<TileReactorRodMotor> {

	private static final String name = "reactorRodMotor";
	
	public BlockReactorRodMotor() {
		super(name, TileReactorRodMotor.class);
	}

	@Override
	public TileReactorRodMotor createNewTileEntity(World world, int meta) {
		return new TileReactorRodMotor(world, meta);
	}
	
	@Override
	public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta) {
		super.onBlockPreDestroy(worldIn, x, y, z, meta);
		getMyTileEntity(worldIn, x, y, z, meta, TileReactorRodMotor.class).broken();
	}

}
