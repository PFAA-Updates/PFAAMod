package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockTEProviderBase;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorChannelAssembly;

import net.minecraft.world.World;

public class BlockReactorChannelAssembly extends PFAABlockTEProviderBase<TileReactorChannelAssembly> {

	private static final String name = "channelAssembly";

	protected BlockReactorChannelAssembly() {
		super(name);	
	}

	@Override
	public TileReactorChannelAssembly createNewTileEntity(World world, int meta) {
		return new TileReactorChannelAssembly(world, meta);
	}
	
}
