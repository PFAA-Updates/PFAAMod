package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.fissionreactor.ChannelTypes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorChannelAssembly extends PFAATileEntityBase {

	public ChannelTypes Channels[] = new ChannelTypes[4];
	
	public TileReactorChannelAssembly(World world, int meta) {
		super(world, meta);
	}

	public TileReactorChannelAssembly() {
		super();
	}
	
	
	private static ChannelTypes Conv[] = { ChannelTypes.Empty, ChannelTypes.CoolingChannel, ChannelTypes.FuelChannel, ChannelTypes.ReflectorChannel};
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		
		//int[] chan = nbt.getIntArray("channels");
		//for (int i=0;i<4;i++)
			//Channels[i] = Conv[chan[i]];
		
		
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		
		
	}

}
