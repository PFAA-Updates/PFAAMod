package com.greenfirework.pfaamod.blocks;
import com.greenfirework.pfaamod.blocks.fissionreactor.BlockReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.BlockReactorPressureCasing;
import com.greenfirework.pfaamod.blocks.fissionreactor.BlockReactorRodMotor;

public class Blocks {

	public static BlockReactorPressureCasing PRESSURE_CASING;
	public static BlockReactorChannelAssembly CHANNEL_ASSEMBLY;
	public static BlockReactorRodMotor ROD_MOTOR;
	
	
	public static void init() {
		PRESSURE_CASING = new BlockReactorPressureCasing();
		CHANNEL_ASSEMBLY = new BlockReactorChannelAssembly();
		ROD_MOTOR = new BlockReactorRodMotor();
		
	}
}
