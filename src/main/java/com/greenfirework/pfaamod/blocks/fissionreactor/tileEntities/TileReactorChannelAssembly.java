package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.components.InventoryComponent;
import com.greenfirework.pfaamod.fissionreactor.ChannelTypes;
import com.greenfirework.pfaamod.fissionreactor.IReactorComponent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileReactorChannelAssembly extends PFAATileEntityBase implements ISidedInventory {

	protected InventoryComponent Channels = new InventoryComponent(4);
	public boolean isAssembled = false;

    private static final int[] slotsList = new int[] {0, 1, 2, 3};
    private static final int[] slotsNone = new int[] {};
	
	public TileReactorChannelAssembly(World world, int meta) {
		super(world, meta);
	}

	public TileReactorChannelAssembly() {
		super();
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		Channels.readFromNBT(nbt, "Channels");
		isAssembled = nbt.getBoolean("isAssembled");		
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		Channels.writeToNBT(nbt, "Channels");
		nbt.setBoolean("isAssembled", isAssembled);		
	}

	@Override
	public int getSizeInventory() {
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return Channels.getStackInSlot(slotIn);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return Channels.decrStackSize(index, count);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return Channels.getStackInSlotOnClosing(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		Channels.setInventorySlotContents(index, stack);
	}

	@Override
	public String getInventoryName() {
		return "Paging one Dorothy Gale to the UI";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this &&
				player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() { // Not used as players can't open the inventory
	}

	@Override
	public void closeInventory() { // Not used as players can't open the inventory
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem().getClass().isAssignableFrom(IReactorComponent.class);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if (side == 1)
			return slotsList;		
		return slotsNone;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStack, int side) {
        return side == 1 && this.isItemValidForSlot(index, itemStack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack itemStack, int side) {
		return side == 1;
	}

}
