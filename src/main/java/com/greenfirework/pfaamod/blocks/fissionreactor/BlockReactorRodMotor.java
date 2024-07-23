package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockTEProviderBase;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorRodMotor;

import net.minecraft.world.World;

public class BlockReactorRodMotor extends PFAABlockTEProviderBase<TileReactorRodMotor> {

	private static final String name = "reactorFluidPort";
	
	public BlockReactorRodMotor() {
		super(name);
	}

	@Override
	public TileReactorRodMotor createNewTileEntity(World world, int meta) {
		return new TileReactorRodMotor(world, meta);
	}

}
