package com.greenfirework.pfaamod.fissionreactor;

import com.greenfirework.pfaamod.pfaamod;
import com.greenfirework.pfaamod.blocks.Blocks;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorRodMotor;
import com.greenfirework.pfaamod.items.ItemReactorFuelChannel;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

public class FissionReactor {
	
	private World worldInst;
	private FluidTank Coolant;

	private static ReactorEmptyChannel Empty = new ReactorEmptyChannel();
	
	/**
	 * Assembly temperatures.  Measured in hundredths Kelvin.
	 */
	public int assemblyTemperatures[];
	
	/**
	 * Surface area measurements.  Measured in meters squared.
	 */
	public int assemblySurfaceAreas[];
	
	/**
	 * Thermal generation in Kelvin per Tick
	 */
	public int assemblyHeatGenerate[];
	
	/**
	 * Size of the channel assembly block
	 */
	private int assemblyDimensions[];
	
	/**
	 * Position of the master rod motor (most northwest)
	 */
	public int masterPosition[];
	
	/**
	 * Core offset - how many blocks down the top-north-west-most channel assembly block is.
	 */
	public int coreOffset;
	private boolean isDeadFixMePlease = false;
	
	private int tickCounter = -1;
	
	/**
	 * 2D plane for all of the reactor channels
	 */
	public IReactorComponent Channels[];
	
	private boolean needsInitialComponentConfig;
	
	public int assemblyIndex(int x, int y, int z) {
		return x + y * assemblyDimensions[0] + z * assemblyDimensions[0] * assemblyDimensions[1];
	}
	
	public int channelIndex(int xBlock, int zBlock, int xSub, int zSub) {
		return xSub + (xBlock << 1 + (assemblyDimensions[0] * zSub + zBlock << 1) << 1);
	}
	
	public FissionReactor(World world) {
		worldInst = world;
	}
	
	public void assemble(int[] size, int[] position) {
		if (size.length != 3 || position.length != 3)
			return;
		
		// Get the Core Offset from Rod Motor to Channel Assembly.  At most -2 blocks (RM, Casing, Assembly).
		for(coreOffset = -2; (position[1] + coreOffset >= 0) && worldInst.getBlock(position[0], position[1] + coreOffset, position[2]) != Blocks.ChannelAssembly; coreOffset--) ;
		
		assemblyDimensions = size;
		masterPosition = position;

		int stride = size[0]*size[1]*size[2];
		assemblyTemperatures = new int[stride]; // x y z
		assemblySurfaceAreas = new int[stride]; // x y z
		assemblyHeatGenerate = new int[stride]; // x y z

		
		for (int idx = 0; idx < stride; idx++) {
			assemblyTemperatures[idx] = 29315; // 20.00 °C in hundredths K
			assemblySurfaceAreas[idx] = 0; // Init surface areas to 0 m²
		}
		
		
		for (int y = 0; y < size[1]; y++) {			
			// North and South edges of reactor each have 1m² of surface area
			for (int idx = 0; idx < size[0]; idx++) {
				assemblySurfaceAreas[assemblyIndex(idx, y, 0)] += 1;
				assemblySurfaceAreas[assemblyIndex(idx, y, size[2]-1)] += 1;
			}
		
			// East and West edges of reactor each have 1m² of surface area
			for (int idx = 0; idx < size[2]; idx++) {
				assemblySurfaceAreas[assemblyIndex(0, y, idx)] += 1;
				assemblySurfaceAreas[assemblyIndex(size[0]-1, y, idx)] += 1;
			}
		}
		// Flag that we still need to add the cooling channels (or filled channels) to this.
		needsInitialComponentConfig = true;
		
		// Determine the inner height of the vessel.  Determine the volume of the space
		int vesselHeight ;
		for(vesselHeight = coreOffset; (position[1] + vesselHeight >= 0) && worldInst.getBlock(position[0], position[1] + vesselHeight, position[2]) != Blocks.PressureCasing; vesselHeight--) ;
		vesselHeight = -2 - vesselHeight;
		
		Coolant = new FluidTank(vesselHeight * size[0] * size[2] * 16000);
	}
	
	/**
	 * Call when the reactor pressure casing (or other block) is broken by a player or other means.
	 */
	public void broken() {
		// TODO if the reactor is hot (fuel loaded + reacting), explode.
	}
	
	/**
	 * Process reactor simulation
	 */
	public void tick() {
		if (isDeadFixMePlease)
			return;
		
		if (Channels == null) // Check if we need to rebuild the channel map for simulation
		{
			Channels = new IReactorComponent[assemblyDimensions[0] * assemblyDimensions[2] * 4];

			// Loop through the channel assemblies and pull the channel objects in.
			// We rebuild this every load so we're not trying to store the reactor contents twice.
			int yPos = masterPosition[1] + coreOffset;
			for (int xPos = masterPosition[0]; xPos < masterPosition[0]+assemblyDimensions[0]; xPos++) {
				for (int zPos = masterPosition[0]; zPos < masterPosition[2]+assemblyDimensions[2]; zPos++) {
					TileEntity rawTE = worldInst.getTileEntity(xPos, yPos, zPos);
					if (rawTE == null || !(rawTE instanceof TileReactorChannelAssembly)) {
						pfaamod.LOG.error("Fission Reactor FAILED to locate channel assembly TE at {0} {1} {2}", xPos, yPos, zPos);
						isDeadFixMePlease = true;
						return; // If the core doesn't exist we just need to bail.
					}
					TileReactorChannelAssembly assembly = (TileReactorChannelAssembly)rawTE;
					
					for(int idx = 0; idx < 4; idx++) {
						Channels[channelIndex(xPos, zPos, idx & 1, idx >> 1)] = assembly.getChannel(idx) == null ? Empty : (IReactorComponent)assembly.getChannel(idx).getItem();
					}
				}
			}
			
			// If we're doing the initial build (not just a rebuild of references)
			// Then update the surface area data based on the channel types, and also configure the Rod Motors for each column
			if (needsInitialComponentConfig) {
				for(int xBlk = 0; xBlk < assemblyDimensions[0]; xBlk++) {
					for(int zBlk = 0; zBlk < assemblyDimensions[2]; zBlk++) {						
						int maskBit = 0;						
						int surfaceAdj = 0;
						int fuelMask = 0;
						
						for(int idx = 0; idx < 4; idx++) {
							int cIdx = channelIndex(xBlk, zBlk, idx & 1, idx >> 1);
							
							surfaceAdj += Channels[cIdx].getOpenSurfaceArea();	
							fuelMask |= Channels[cIdx] instanceof ItemReactorFuelChannel ? 1 << maskBit : 0;
							maskBit++;
						}
						
						for(int yBlk = 0; yBlk < assemblyDimensions[1]; yBlk++) {
							assemblySurfaceAreas[assemblyIndex(xBlk, yBlk, zBlk)] += surfaceAdj;
						}
						
						TileEntity rawTE = worldInst.getTileEntity(xBlk + masterPosition[0], masterPosition[1], zBlk + masterPosition[2]);
						if (rawTE instanceof TileReactorRodMotor)
							((TileReactorRodMotor)rawTE).setFuelConfiguration(fuelMask, assemblyDimensions[1]);
					}
				}
			}
		}
		
		tickCounter = ++tickCounter % 20;
		if (tickCounter == 0)
			fullTick();
		smallTick();
	}
	
	/**
	 * Simple reactor process (each tick)
	 */
	private void smallTick() {
		
	}
	
	/**
	 * Full reactor process (each second)
	 */
	private void fullTick() {
		
		// First zero heat generation
		int stride = assemblyDimensions[0];
		int size = stride*assemblyDimensions[2];
		
		for (int idx=0; idx<size; idx++) {
			assemblyHeatGenerate[idx] = 0;
		}
		
		for (int idx=0; idx<size; idx++) {
			
		}
		
		
	}
	
	/**
	 * Persist reactor state to NBT
	 * @param nbt
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("coreSize", assemblyDimensions);
		nbt.setIntArray("position", masterPosition);
		nbt.setIntArray("temperatures", assemblyTemperatures);
		nbt.setIntArray("surfaces", assemblySurfaceAreas);
		nbt.setIntArray("heat", assemblyHeatGenerate);
		
		nbt.setInteger("coreOffset", coreOffset);
		nbt.setInteger("tickCounter", tickCounter);
		nbt.setInteger("fluidVolume", Coolant.getCapacity());
		
		Coolant.writeToNBT(nbt);
	}
	
	/**
	 * Retrieve reactor state from NBT
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {

		assemblyDimensions = nbt.getIntArray("coreSize");
		masterPosition = nbt.getIntArray("position");
		assemblyTemperatures = nbt.getIntArray("temperatures");
		assemblySurfaceAreas = nbt.getIntArray("surfaces");
		assemblyHeatGenerate = nbt.getIntArray("heat");
		
		coreOffset = nbt.getInteger("coreOffset");
		tickCounter = nbt.getInteger("tickCounter");
		int capacity = nbt.getInteger("fluidVolume");
		
		
		Coolant = new FluidTank(capacity);
		Coolant.readFromNBT(nbt);
	}
		
}
