package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockTESRProviderBase;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TESRReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorChannelAssembly;

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
	
}
