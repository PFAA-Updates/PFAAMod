package com.greenfirework.pfaamod;

import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TESRReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorChannelAssembly;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ClientRegistry.bindTileEntitySpecialRenderer(TileReactorChannelAssembly.class, new TESRReactorChannelAssembly());
	}
}
