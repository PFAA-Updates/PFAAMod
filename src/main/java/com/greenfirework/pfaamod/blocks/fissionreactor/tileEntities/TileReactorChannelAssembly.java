package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.components.InventoryComponent;
import com.greenfirework.pfaamod.structures.IAssembleable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorChannelAssembly extends PFAATileEntityBase implements IAssembleable {

	protected InventoryComponent Channels = new InventoryComponent(4);
	public boolean isAssembled = false;
	
	
	public TileReactorChannelAssembly(World world, int meta) {
		super(world, meta);
	}

	public TileReactorChannelAssembly() {
		super();
	}
	
	public ItemStack getChannel(int idx) {
		return Channels.getStackInSlot(idx);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		Channels.readFromNBT(nbt, "Channels");
		isAssembled = nbt.getBoolean("isAssembled");		
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		Channels.writeToNBT(nbt, "Channels");
		nbt.setBoolean("isAssembled", isAssembled);		
	}

	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		
		if (side != 1)
			return false;
		
		return false;
	}

	@Override
	public void assemble() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disassemble() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assemblyComplete() {
		// TODO Auto-generated method stub
		
	}

}
