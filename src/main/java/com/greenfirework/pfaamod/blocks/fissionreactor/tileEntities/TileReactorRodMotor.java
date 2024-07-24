package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.fissionreactor.FissionReactorController;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorRodMotor extends PFAATileEntityBase {

	public boolean isMaster = false;
	public FissionReactorController MasterController = null;

	public FissionReactorController GetMaster() {
		
		// If we haven't memoized the master controller, search for it, memoize the result..
		if (MasterController == null) {		
			Object te = world.getTileEntity(xCoord - 1, yCoord, zCoord);
			if (!(te instanceof TileReactorRodMotor)) {
				te = world.getTileEntity(xCoord, yCoord, zCoord - 1);
			}
		
			if (te instanceof TileReactorRodMotor) {
				MasterController = ((TileReactorRodMotor)te).GetMaster();
			}			
		}

		return MasterController;
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
