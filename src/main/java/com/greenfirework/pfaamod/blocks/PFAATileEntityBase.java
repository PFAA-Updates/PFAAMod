package com.greenfirework.pfaamod.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class PFAATileEntityBase extends TileEntity {
	protected World world;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.readCustomNBT(nbt, false);
	}
	
	public abstract void readCustomNBT(NBTTagCompound nbt, boolean isDescription);
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.writeCustomNBT(nbt, false);
	}
	public abstract void writeCustomNBT(NBTTagCompound nbt, boolean isDescription);

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeCustomNBT(nbttagcompound, true);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbttagcompound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
		this.readCustomNBT(pkt.func_148857_g(), true);
    }
	
	public void receiveMessageFromClient(NBTTagCompound message)
	{
	}
	
	public PFAATileEntityBase(World world, int meta) {
		this.world = world;
	}
	
	public PFAATileEntityBase() {
		this.world = null;
	}
}
