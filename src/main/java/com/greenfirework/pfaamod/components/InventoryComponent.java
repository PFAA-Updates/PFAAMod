package com.greenfirework.pfaamod.components;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryComponent {

    private ItemStack[] itemStacks;
    
    public InventoryComponent(int capacitySlots) {
    	itemStacks = new ItemStack[capacitySlots];
    }

	public int getSizeInventory() {
		return itemStacks.length;
	}

	public ItemStack getStackInSlot(int slotIn) {
		return itemStacks[slotIn];
	}

	public ItemStack decrStackSize(int index, int count) {
		if (this.itemStacks[index] != null)
        {
            ItemStack itemstack;

            if (this.itemStacks[index].stackSize <= count)
            {
                itemstack = this.itemStacks[index];
                this.itemStacks[index] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.itemStacks[index].splitStack(count);

                if (this.itemStacks[index].stackSize == 0)
                {
                    this.itemStacks[index] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}

	public ItemStack getStackInSlotOnClosing(int index) {
		if (this.itemStacks[index] != null)
        {
            ItemStack itemstack = this.itemStacks[index];
            this.itemStacks[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		this.itemStacks[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
	}

	public int getInventoryStackLimit() {
		return 64;
	}

    public void readFromNBT(NBTTagCompound compound) {
    	readFromNBT(compound, "Items");
	}
	
	public void readFromNBT(NBTTagCompound compound, String ItemKey) {
		NBTTagList nbttaglist = compound.getTagList(ItemKey, 10);
        this.itemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.itemStacks.length)
            {
                this.itemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
	}
	
    public void writeToNBT(NBTTagCompound compound) {
    	writeToNBT(compound, "Items");
	}

    public void writeToNBT(NBTTagCompound compound, String ItemKey) {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.itemStacks.length; ++i)
        {
            if (this.itemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.itemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        compound.setTag(ItemKey, nbttaglist);
	}
	
}
