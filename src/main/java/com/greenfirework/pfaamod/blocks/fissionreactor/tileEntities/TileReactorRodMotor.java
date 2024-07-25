package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.components.InventoryComponent;
import com.greenfirework.pfaamod.fissionreactor.FissionReactor;
import com.greenfirework.pfaamod.structures.IAssembleable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorRodMotor extends PFAATileEntityBase implements IAssembleable {

	public boolean isMaster = false;
	public FissionReactor MasterController = null;
	public InventoryComponent fuelRods = new InventoryComponent(1);
	public int fuelMask = 0;
	public boolean isAssembled = false;
	
	// Graphical stuff
	public float rodActualPosition;
	public float rodTargetPosition;
	
	public FissionReactor GetMaster() {	
		if (!isAssembled)
			return null;
		
		// If we haven't memoized the master controller, search for it, memoize the result..
		if (MasterController == null) {		
			Object te = world.getTileEntity(xCoord - 1, yCoord, zCoord);
			if (!(te instanceof TileReactorRodMotor)) {
				te = world.getTileEntity(xCoord, yCoord, zCoord - 1);
			}
		
			if (te instanceof TileReactorRodMotor) {
				MasterController = ((TileReactorRodMotor)te).GetMaster();
			} else {
				isMaster = true;
				MasterController = new FissionReactor(world);
			}
		}

		return MasterController;
	}
	
	public void broken() {
		if (isAssembled) {
			// TODO trigger structure disassembly.
		}
	}
	
	
	public void setFuelConfiguration(int maskBits, int height) {
		fuelRods.setSizeInventory(height << 2);
		fuelMask = maskBits;
	}
	
	public TileReactorRodMotor(World world, int meta) {
		super(world, meta);
	}

	@Override
	public void updateEntity() {
		if (world.isRemote)
			return;
		
		if (isMaster)
			MasterController.tick();
		
		if (rodActualPosition > rodTargetPosition) {
			rodActualPosition -= 0.003125;
		}
		if (rodActualPosition < rodTargetPosition) {
			rodActualPosition += 0.003125;
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		if (!isDescription) {
			fuelMask = nbt.getInteger("fuelMask");
			isAssembled = nbt.getBoolean("isAssembled");
			isMaster = nbt.getBoolean("isMaster");
			
			if (isMaster)
			{
				MasterController = new FissionReactor(world);
				MasterController.readFromNBT(nbt);
			}
			fuelRods.readFromNBT(nbt);	
		}
		
		rodActualPosition = nbt.getFloat("rodActual");
		rodTargetPosition = nbt.getFloat("rodTarget");	
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		if (!isDescription) {
			nbt.setInteger("fuelMask", fuelMask);
			nbt.setBoolean("isAssembled", isAssembled);
			nbt.setBoolean("isMaster", isMaster);
			
			if (isMaster)
				MasterController.writeToNBT(nbt);
		
			fuelRods.writeToNBT(nbt);
		}

		nbt.setFloat("rodActual", rodActualPosition);
		nbt.setFloat("rodTarget", rodTargetPosition);
	}

	@Override
	public void assemble() {
		isAssembled = true;
	}

	@Override
	public void disassemble() {
		if (isAssembled) {
			if (isMaster)
				MasterController.broken();
			MasterController = null;
			isMaster = false;
			
			fuelRods.ejectWorld(worldObj, xCoord, yCoord + 1, zCoord);
		}
		isAssembled = false;
	}

	@Override
	public void assemblyComplete() {
		GetMaster();
	}

}
