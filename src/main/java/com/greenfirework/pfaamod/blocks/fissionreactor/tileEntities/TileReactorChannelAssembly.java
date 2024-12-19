package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import com.greenfirework.pfaamod.pfaamod;
import com.greenfirework.pfaamod.blocks.PFAATileEntityBase;
import com.greenfirework.pfaamod.components.InventoryComponent;
import com.greenfirework.pfaamod.fissionreactor.IReactorComponent;
import com.greenfirework.pfaamod.structures.IAssembleable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileReactorChannelAssembly extends PFAATileEntityBase implements IAssembleable {

	protected InventoryComponent channels = new InventoryComponent(4);
	public int[] channelCaps = new int[4];
	
	public boolean isAssembled = false;
	
	public TileReactorChannelAssembly(World world, int meta) {
		super(world, meta);
	}

	public TileReactorChannelAssembly() {
		super();
	}
	
	public ItemStack getChannel(int idx) {
		return channels.getStackInSlot(idx);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		channels.readFromNBT(nbt, "Channels");
		isAssembled = nbt.getBoolean("isAssembled");
		channelCaps = nbt.getIntArray("caps");
		if (channelCaps.length == 0)
			channelCaps = new int[4];
		channels.setSizeInventory(4);
	}

	public void updateDisplayCaps() {
		pfaamod.LOG.info("Updating display caps");
		for (int idx = 0; idx < 4; idx++) {
			channelCaps[idx] = channels.getStackInSlot(idx) == null ? 0 : ((IReactorComponent)channels.getStackInSlot(idx).getItem()).getCapIndex();
		}

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt, boolean isDescription) {
		channels.writeToNBT(nbt, "Channels");
		nbt.setBoolean("isAssembled", isAssembled);		
		nbt.setIntArray("caps", channelCaps);
	}

	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (side != ForgeDirection.UP.ordinal())
			return false;
		
		int cIdx = (subX >= 0.5F ? 1 : 0) | (subZ >= 0.5F ? 2 : 0);
		ItemStack holding = player.getCurrentEquippedItem();
		
		if (holding == null) {
			ItemStack stack;
			if ((stack = channels.getStackInSlot(cIdx)) != null) {
				channels.setInventorySlotContents(cIdx, null);
				player.setCurrentItemOrArmor(0, stack);
				updateDisplayCaps();
				return true;
			}
		}
		else if (holding.getItem() instanceof IReactorComponent) {
			if (channels.getStackInSlot(cIdx) == null) {
				ItemStack stack = holding.copy();
				stack.stackSize = 1;
				holding.stackSize--;
				if (holding.stackSize == 0)
					holding = null;
				player.setCurrentItemOrArmor(0, holding);				
				channels.setInventorySlotContents(cIdx, stack);
				updateDisplayCaps();
				return true;
			}
			
		}
		return false;
	}

	public void brokenOrCovered() {
		channels.ejectWorld(world, xCoord, yCoord, zCoord);
		updateDisplayCaps();
	}
	
	@Override
	public void assemble() {
		isAssembled = true;
	}

	@Override
	public void disassemble() {
		isAssembled = false;
	}

	@Override
	public void assemblyComplete() {
		// Nothing to do
	}

}
