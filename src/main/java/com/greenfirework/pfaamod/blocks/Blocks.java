package com.greenfirework.pfaamod.blocks;
import com.greenfirework.pfaamod.blocks.fissionreactor.BlockReactorPressureCasing;

public class Blocks {
	
	public static BlockReactorPressureCasing PressureCasing;
	
	
	public static void init() {
		PressureCasing = new BlockReactorPressureCasing();
		
	}
}
