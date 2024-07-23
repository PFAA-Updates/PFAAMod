package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.fissionreactor.FissionReactorController;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorRodMotor extends PFAATileEntityBase {

	public FissionReactorController MasterController = null;

	private FissionReactorController GetMaster() {
		return null;
	}
	
	public TileReactorRodMotor(World world, int meta) {
		super(world, meta);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

}
