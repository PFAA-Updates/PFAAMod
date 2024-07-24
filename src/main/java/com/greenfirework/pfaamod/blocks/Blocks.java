package com.greenfirework.pfaamod.blocks;
import com.greenfirework.pfaamod.blocks.fissionreactor.BlockReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.BlockReactorPressureCasing;

public class Blocks {

	public static BlockReactorPressureCasing PressureCasing;
	public static BlockReactorChannelAssembly ChannelAssembly;
	
	
	public static void init() {
		PressureCasing = new BlockReactorPressureCasing();
		ChannelAssembly = new BlockReactorChannelAssembly();
		
	}
}
