package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.Blocks;
import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.fissionreactor.FissionReactor;
import com.greenfirework.pfaamod.structures.IAssembleable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileReactorFluidPort extends PFAATileEntityBase implements IAssembleable, IFluidHandler {

	public boolean isInlet;
	public boolean isOutlet;
	public boolean isAssembled;
	public byte connectionFlags;
	
	public int masterPosition[];
	
	public FissionReactor master;
	
	public TileReactorFluidPort(World world, int meta) {
		super(world, meta);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		isAssembled = nbt.getBoolean("isAssembled");
		if (!isDescription) {
			isInlet = nbt.getBoolean("isInlet");
			isOutlet = nbt.getBoolean("isOutlet");
			masterPosition = nbt.getIntArray("masterPosition");
			connectionFlags = nbt.getByte("connections");
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		nbt.setBoolean("isAssembled", isAssembled);
		if (!isDescription) {
			nbt.setBoolean("isInlet", isInlet);
			nbt.setBoolean("isOutlet", isOutlet);
			nbt.setIntArray("masterPosition", masterPosition);
			nbt.setByte("connections", connectionFlags);
		}
		
	}
	
	/**
	 * On neighboring block change (we don't know which one), scan all neighboring blocks for fluid handlers and add them to an outlets list on the master.  Or remove them if they're not there anymore.
	 */
	public void onNeighborBlockChange() {
		if (isOutlet && isAssembled & master != null) {
			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
				boolean existingConnection = (connectionFlags & (1 << direction.ordinal())) > 0;
				
				TileEntity rawTE = world.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
				if (rawTE instanceof IFluidHandler && !(rawTE instanceof TileReactorFluidPort) ) {
					if (existingConnection) 
						continue;
					connectionFlags |= 1 << direction.ordinal();
					master.addOutlet(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, direction.getOpposite());
				}
				else if (existingConnection) {
					connectionFlags &= ~(1 << direction.ordinal());
					master.removeOutlet(connectionFlags, connectionFlags, connectionFlags, direction);
				}
			}
		}
	}
	
	// Search for the master controller.  This works for now, but later on should be redone.
	private FissionReactor getMaster() {
		if (master == null && isAssembled) {
			int[] search;
			
			if 		(world.getBlock(xCoord + 1, yCoord + 2, zCoord) == Blocks.CHANNEL_ASSEMBLY)
				search = new int[] {xCoord + 1, yCoord + 2, zCoord};
			
			else if (world.getBlock(xCoord - 1, yCoord + 2, zCoord) == Blocks.CHANNEL_ASSEMBLY)
				search = new int[] {xCoord - 1, yCoord + 2, zCoord};
			
			else if (world.getBlock(xCoord, yCoord + 2, zCoord + 1) == Blocks.CHANNEL_ASSEMBLY)
				search = new int[] {xCoord, yCoord + 2, zCoord + 1};
			
			else
				search = new int[] {xCoord, yCoord + 2, zCoord - 1};
			
			while(world.getBlock(search[0], search[1], search[2]) != Blocks.ROD_MOTOR)
				search[1]++;
			
			TileEntity rawTE = world.getTileEntity(search[0], search[1], search[2]);
			
			if (rawTE instanceof TileReactorRodMotor)
				master = ((TileReactorRodMotor)rawTE).getMaster();
		}
		return master;
	}
	
	public void broken() {
		if (isAssembled)
		{
			// TODO trigger structure break
		}
	}

	@Override
	public void assemble() {
		isAssembled = true;
		isInlet = false;
		isOutlet = false;
		connectionFlags = 0;
	}

	@Override
	public void disassemble() {
		isAssembled = false;
		connectionFlags = 0;
		isInlet = false;
		isOutlet = false;
		master = null;
	}
	
	public void setIsOutlet(boolean isOutlet) {
		this.isOutlet = isOutlet;
		this.isInlet = !isOutlet;
	}
	
	@Override
	public void assemblyComplete() {
		getMaster();
		
		world.notifyBlockChange(xCoord, yCoord, zCoord, blockType);
		markDirty();
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!canFill(from, resource.getFluid()))
			return 0;
		return getMaster().coolant.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!canDrain(from, resource.getFluid()) || (getMaster().coolant.getFluidAmount() > 0 && resource.getFluidID() != getMaster().coolant.getFluid().getFluidID())) {
			return null;
		}
		return getMaster().coolant.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!canDrain(from, getMaster().coolant.getFluid().getFluid()))
			return null;
		return getMaster().coolant.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return isAssembled && isInlet && getMaster().coolant.getFluid().getFluidID() == fluid.getID();
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return isAssembled && getMaster().coolant.getFluid().getFluidID() == fluid.getID();
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (!isAssembled)
			return null;
		
		return new FluidTankInfo[] { getMaster().coolant.getInfo() };
	}

}
