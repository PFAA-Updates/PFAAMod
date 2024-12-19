package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.components.InventoryComponent;
import com.greenfirework.pfaamod.fissionreactor.FissionReactor;
import com.greenfirework.pfaamod.fissionreactor.IReactorFuel;
import com.greenfirework.pfaamod.structures.IAssembleable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorRodMotor extends PFAATileEntityBase implements IAssembleable, ISidedInventory {

	public boolean isMaster = false;
	public boolean isAssembled = false;
	
	public int fuelMask = 0;
	
	public FissionReactor masterController = null;
	public InventoryComponent fuelRods = new InventoryComponent(1);
	
	// Graphical stuff
	public float rodActualPosition;
	public float rodTargetPosition;
	
	public FissionReactor getMaster() {	
		if (world.isRemote)
			return null;
		
		if (!isAssembled)
			return null;
		
		// If we haven't memoized the master controller, search for it, memoize the result..
		if (masterController == null) {		
			Object te = world.getTileEntity(xCoord - 1, yCoord, zCoord);
			if (!(te instanceof TileReactorRodMotor)) {
				te = world.getTileEntity(xCoord, yCoord, zCoord - 1);
			}
		
			if (te instanceof TileReactorRodMotor) {
				masterController = ((TileReactorRodMotor)te).getMaster();
			} else {
				isMaster = true;
				masterController = new FissionReactor(world);
			}
		}

		return masterController;
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
		
		// Do the simulation of the rods actually moving on the client as well as the server
		if (rodActualPosition > rodTargetPosition) {
			rodActualPosition -= 0.003125;
		}
		if (rodActualPosition < rodTargetPosition) {
			rodActualPosition += 0.003125;
		}
		if (Math.abs(rodActualPosition - rodTargetPosition) < 0.003125)
			rodActualPosition = rodTargetPosition;
		
		if (world.isRemote || !isMaster)
			return;
		
		masterController.tick();
		
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		if (!isDescription) {
			fuelMask = nbt.getInteger("fuelMask");
			isAssembled = nbt.getBoolean("isAssembled");
			isMaster = nbt.getBoolean("isMaster");
			
			if (isMaster)
			{
				masterController = new FissionReactor(world);
				masterController.readFromNBT(nbt);
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
				masterController.writeToNBT(nbt);
		
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
				masterController.broken();
			masterController = null;
			isMaster = false;
			
			fuelRods.ejectWorld(worldObj, xCoord, yCoord, zCoord);
		}
		isAssembled = false;
	}

	@Override
	public void assemblyComplete() {
		getMaster();
		if (isMaster) {
			int offset = FissionReactor.findCoreOffset(worldObj, new int[] {xCoord, yCoord, zCoord});
			int[] size = FissionReactor.findCoreSize(worldObj, new int[] {xCoord, yCoord + offset, zCoord});
			getMaster().assemble(size, new int[] {xCoord, yCoord, zCoord}, offset);
		}
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getInventoryName() {
		return "fuelAccessInventory";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof IReactorFuel;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		// TODO need to list the accessible slots based on the fuel mask
		return null;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
		// TODO Auto-generated method stub
		return false;
	}

}
