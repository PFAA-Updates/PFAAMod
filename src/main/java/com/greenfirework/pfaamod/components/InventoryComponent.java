package com.greenfirework.pfaamod.components;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class InventoryComponent {
    private final static Random rand = new Random();

    private ItemStack[] itemStacks;
    
    public InventoryComponent(int capacitySlots) {
    	itemStacks = new ItemStack[Math.max(1, capacitySlots)];
    }

    public void setSizeInventory(int capacitySlots) {
    	ItemStack[] tempItems = new ItemStack[Math.max(1, capacitySlots)];
    	for (int idx = 0; idx < Math.min(capacitySlots, itemStacks.length); idx++) {
    		tempItems[idx] = itemStacks[idx];
    	}
    	itemStacks = tempItems;
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

    public void readFromNBT(NBTTagCompound nbt) {
    	readFromNBT(nbt, "Items");
	}
	
	public void readFromNBT(NBTTagCompound nbt, String itemKey) {
		NBTTagList nbttaglist = nbt.getTagList(itemKey, 10);
        this.itemStacks = new ItemStack[nbt.getInteger(itemKey+"Count")];

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
	
    public void writeToNBT(NBTTagCompound nbt) {
    	writeToNBT(nbt, "Items");
	}

    public void writeToNBT(NBTTagCompound nbt, String itemKey) {
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
        nbt.setTag(itemKey, nbttaglist);
        nbt.setInteger(itemKey+"Count", itemStacks.length);
	}
	
    public void ejectWorld(World world, int x, int y, int z) {
    	if (world.isRemote)
    		return; // No, not on clients thanks.
    	
    	for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack stack = itemStacks[i];

            if (stack != null) {
            	EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
            	item.setVelocity((rand.nextDouble() - 0.5) * 0.25, rand.nextDouble() * 0.5 * 0.25, (rand.nextDouble() - 0.5) * 0.25);
            	world.spawnEntityInWorld(item);
            }
            
            itemStacks[i] = null;
        }
    }
}
