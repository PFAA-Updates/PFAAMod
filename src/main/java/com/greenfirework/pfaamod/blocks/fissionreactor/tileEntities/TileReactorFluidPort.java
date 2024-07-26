package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.fissionreactor.FissionReactor;
import com.greenfirework.pfaamod.structures.IAssembleable;

import net.minecraft.nbt.NBTTagCompound;
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
	public int masterPosition[];
	
	public FissionReactor master;
	
	public TileReactorFluidPort(World world, int meta) {
		super(world, meta);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		isAssembled = nbt.getBoolean("isAssembled");
		if (!isDescription) {
			isInlet = nbt.getBoolean("isInlet");
			isOutlet = nbt.getBoolean("isOutlet");
			masterPosition = nbt.getIntArray("masterPosition");
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		nbt.setBoolean("isAssembled", isAssembled);
		if (!isDescription) {
			nbt.setBoolean("isInlet", isInlet);
			nbt.setBoolean("isOutlet", isOutlet);
			nbt.setIntArray("masterPosition", masterPosition);
		}
		
	}
	
	private FissionReactor getMaster() {
		if (master == null && isAssembled) {
			
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
	}

	@Override
	public void disassemble() {
		isAssembled = false;
		master = null;
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
		return getMaster().Coolant.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (!canDrain(from, resource.getFluid()) || (getMaster().Coolant.getFluidAmount() > 0 && resource.getFluidID() != getMaster().Coolant.getFluid().getFluidID())) {
			return null;
		}
		return getMaster().Coolant.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!canDrain(from, getMaster().Coolant.getFluid().getFluid()))
			return null;
		return getMaster().Coolant.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return isAssembled && isInlet;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return isAssembled && getMaster().Coolant.getFluid().getFluidID() == fluid.getID();
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (!isAssembled)
			return null;
		
		return new FluidTankInfo[] { getMaster().Coolant.getInfo() };
	}

}
