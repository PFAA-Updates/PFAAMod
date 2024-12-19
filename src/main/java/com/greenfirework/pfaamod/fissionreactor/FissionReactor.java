package com.greenfirework.pfaamod.fissionreactor;

import java.util.Vector;

import com.greenfirework.pfaamod.Config;
import com.greenfirework.pfaamod.pfaamod;
import com.greenfirework.pfaamod.blocks.Blocks;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorChannelAssembly;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorFluidPort;
import com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities.TileReactorRodMotor;
import com.greenfirework.pfaamod.components.InventoryComponent;
import com.greenfirework.pfaamod.items.fissionreactor.ItemReactorFuelChannel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;

public class FissionReactor {

	public static final int T_0_C = 27315;
	public static final int T_20_C = T_0_C + 2000;
	public static final int T_360_C = T_0_C + 36000;
	public static final int T_400_C = T_0_C + 40000;
	public static final int T_1000_C = T_0_C + 100000;

	public static final int T_1_K = 100;
	public static final int T_TWENTIETH_K = 5;
	
	private World world;
	public FluidTank coolant;
	public int coolantTemperature = T_20_C;
	private int coolantLast; // Used to help reconstruct how much coolant was inserted by external sources each tick 
	private int vesselHeight;
	
	// Eventually this should be instanced and based on the input coolant.
	// e.g. hotCoolant for coolant (PWR), steam for distilled water (BWR, RBMK)
	private static FluidStack hotCoolant; 
	
	private static final ReactorEmptyChannel EMPTY = new ReactorEmptyChannel();
	
	public static void postInit() {
		hotCoolant = FluidRegistry.getFluidStack("hotCoolant", 1);
		hotCoolant.tag.setBoolean("pressurized", true); // Compat trick for IE pipes to get higher flow
	}
	
	public Vector<int[]> outletConnections;
	
	/**
	 * Assembly temperatures.  Measured in hundredths Kelvin.
	 */
	public int[] assemblyTemperatures;
	
	/**
	 *  Alternate temperature store for A/B swapping in fullTick()
	 */
	public int[] alternateTemperatures;
	
	/**
	 * Surface area measurements.  Measured in meters squared.
	 */
	public int[] assemblyThermalXfer;
	
	/**
	 * Thermal generation in Kelvin per Tick
	 */
	public int[] assemblyHeatGenerate;
	
	/**
	 * Size of the channel assembly block
	 */
	private int[] assemblyDimensions;
	
	/**
	 * Position of the master rod motor (most northwest)
	 */
	public int[] masterPosition;
	
	/**
	 * Fluid outlet block position
	 */
	public int[] outletPosition;
	
	public int[] rodPositions;
	
	public int[] assemblyDecay;

	public int[] channelTypes; // 0, 1, 2, 3 = empty, fuel, cooling, reflector
	
	public int[] columnFlux;
	public int[] columnFluxReflected;
	
	public InventoryComponent[] fuelStores; 
		
	public static final int[][] FLUX_SPREAD = new int[][] 	{ 
		new int[] { 0,  0, 0}, // Affect self
		
		new int[] { 1,  0, 1}, // Affect 4-way touching
		new int[] {-1,  0, 1},
		new int[] { 0,  1, 1},
		new int[] { 0, -1, 1},
		
		new int[] { 2,  0, 2}, // Affect 4-way touching, one step further
		new int[] {-2,  0, 2},
		new int[] { 0,  2, 2},
		new int[] { 0, -2, 2},		
		new int[] { 1,  1, 2},
		new int[] {-1,  1, 2},
		new int[] { 1, -1, 2},
		new int[] {-1, -1, 2}
	};
	
	public static final int[][] FLUX_VERTICAL = new int[][] {
		// No "self effect" because that one is done in FLUX_SPREAD
		new int[] {  1, 1}, 
		new int[] { -1, 1},
		new int[] {  2, 2}, 
		new int[] { -2, 2}
	};
	
	/**
	 * Core offset - how many blocks down the top-north-west-most channel assembly block is.
	 */
	public int coreOffset;
	private boolean isDeadFixMePlease = false;
	
	private int tickCounter = -1;
	
	/**
	 * 2D plane for all of the reactor channels
	 */
	public IReactorComponent channels[];
	
	private boolean needsInitialComponentConfig;
	
	public int assemblyIndex(int x, int y, int z) {
		return x + y * assemblyDimensions[0] + z * assemblyDimensions[0] * assemblyDimensions[1];
	}
	
	public int channelIndex(int xBlock, int zBlock, int xSub, int zSub) {
		return xSub + (xBlock << 1 + (assemblyDimensions[0] * zSub + zBlock << 1) << 1);
	}
	
	public int channelIndex(int xChn, int zChn) {
		return xChn + zChn * assemblyDimensions[0] * 2;
	}
	
	public int assemblyColumnIndex(int x, int z) {
		return x + z * assemblyDimensions[0];
	}
	
	public FissionReactor(World world) {
		this.world = world;
	}
	
	public static int findCoreOffset(World world, int[] startPosition) {
		int offset = 0;
		while (world.getBlock(startPosition[0], startPosition[1]+offset, startPosition[2]) != Blocks.CHANNEL_ASSEMBLY)
			offset--;
		
		return offset;
	}
	
	public static int[] findCoreSize(World world, int[] startPosition) {
		int[] size = new int[3];

		while (world.getBlock(startPosition[0] + size[0], startPosition[1], startPosition[2]) == Blocks.CHANNEL_ASSEMBLY)
			size[0]++;
		
		while (world.getBlock(startPosition[0], startPosition[1] + size[1], startPosition[2]) == Blocks.CHANNEL_ASSEMBLY)
			size[1]++;
		
		while (world.getBlock(startPosition[0], startPosition[1], startPosition[2] + size[2]) == Blocks.CHANNEL_ASSEMBLY)
			size[2]++;
		
		return size;
	}
	
	/**
	 * Assemble a new reactor.  Reactor construction should have been validated by the structure manager.
	 * @param size
	 * @param position
	 * @param coreOffset
	 */
	public void assemble(int[] size, int[] position, int coreOffset) {
		if (size.length != 3 || position.length != 3)
			return;
		
		// Get the Core Offset from Rod Motor to Channel Assembly.  At most -2 blocks (RM, Casing, Assembly).
		this.coreOffset = coreOffset;
		
		assemblyDimensions = size;
		masterPosition = position;

		int stride = size[0] * size[1] * size[2];
		assemblyTemperatures = new int[stride];
		assemblyThermalXfer = new int[stride];
		assemblyHeatGenerate = new int[stride];

		columnFlux = new int[assemblyDimensions[1]];
		columnFluxReflected = new int[assemblyDimensions[1]];
				
		for (int y = 0; y < size[1]; y++) {			
			// North and South edges of reactor each have 1m² of surface area
			for (int idx = 0; idx < size[0]; idx++) {
				assemblyThermalXfer[assemblyIndex(idx, y, 0)] += 1;
				assemblyThermalXfer[assemblyIndex(idx, y, size[2]-1)] += 1;
			}
		
			// East and West edges of reactor each have 1m² of surface area
			for (int idx = 0; idx < size[2]; idx++) {
				assemblyThermalXfer[assemblyIndex(0, y, idx)] += 1;
				assemblyThermalXfer[assemblyIndex(size[0]-1, y, idx)] += 1;
			}
		}
		for (int idx = 0; idx < stride; idx++) {
			assemblyTemperatures[idx] = T_20_C; // 20.00 °C in hundredths K
			assemblyThermalXfer[idx] *= Config.reactorHeatXferCoeff; // Precalculate heat transfer coefficient
		}
		
		// Flag that we still need to add the cooling channels (or filled channels) to this.
		needsInitialComponentConfig = true;
		
		// Determine the inner height of the vessel.  Determine the volume of the space
		vesselHeight = coreOffset;
		
		while ((position[1] + vesselHeight >= 0) && world.getBlock(position[0], position[1] + vesselHeight, position[2]) != Blocks.PRESSURE_CASING)
			vesselHeight--;
		
		// vesselHeight needs to be converted from a negative value to positive.
		//We also need to slice off the top/bottom values so we don't count the pressure casing as fluid-containing space
		vesselHeight = -vesselHeight - 2;
		
		// Now set up the coolant storage
		coolant = new FluidTank(vesselHeight * size[0] * size[2] * Config.reactorCoolantPerBlock);
		coolantTemperature = T_20_C;
		
		// Now scan down the boundaries of the structure, and set the fluid ports to inlets or outlets.
		// All outlets are on the same layer, and are on the highest later with fluid ports.
		// All other fluid ports are inlets.
		
		boolean setOutlets = true;
		for(int yOffset = 2; yOffset <= vesselHeight; yOffset++) {
			boolean foundOutlet = false;
			
			// TODO this is 4x repeated code, clean this up
			for (int xOffset = -1; xOffset <= assemblyDimensions[0]; xOffset++) {
				TileEntity rawTE = world.getTileEntity(masterPosition[0] + xOffset, masterPosition[1] - yOffset, masterPosition[2] - 1);
				if (rawTE instanceof TileReactorFluidPort) {
					foundOutlet = true;
					((TileReactorFluidPort)rawTE).setIsOutlet(setOutlets);
					if (setOutlets)
						((TileReactorFluidPort)rawTE).onNeighborBlockChange();;
				}
				
				rawTE = world.getTileEntity(masterPosition[0] + xOffset, masterPosition[1] - yOffset, masterPosition[2] + assemblyDimensions[0]);
				if (rawTE instanceof TileReactorFluidPort) {
					foundOutlet = true;
					((TileReactorFluidPort)rawTE).setIsOutlet(setOutlets);
					if (setOutlets)
						((TileReactorFluidPort)rawTE).onNeighborBlockChange();;
				}
			}
			for (int zOffset = -1; zOffset <= assemblyDimensions[0]; zOffset++) {
				
				TileEntity rawTE = world.getTileEntity(masterPosition[0] - 1, masterPosition[1] - yOffset, masterPosition[2] + zOffset);
				if (rawTE instanceof TileReactorFluidPort) {
					foundOutlet = true;
					((TileReactorFluidPort)rawTE).setIsOutlet(setOutlets);
					if (setOutlets)
						((TileReactorFluidPort)rawTE).onNeighborBlockChange();;
				}
				
				rawTE = world.getTileEntity(masterPosition[0] + assemblyDimensions[0], masterPosition[1] - yOffset, masterPosition[2] + zOffset);
				if (rawTE instanceof TileReactorFluidPort) {
					foundOutlet = true;
					((TileReactorFluidPort)rawTE).setIsOutlet(setOutlets);
					if (setOutlets)
						((TileReactorFluidPort)rawTE).onNeighborBlockChange();;
				}
			}
			
			if (foundOutlet)
				setOutlets = false;
		}
	}
	
	/**
	 * Get the highest redstone signal into the rod motors from the sides or from above
	 * @return
	 */
	public int getMaxRedstoneInput() {
		int maxVal = 0;
		for (int xBlk = 0; xBlk < assemblyDimensions[0]; xBlk++) {
			for (int zBlk = 0; zBlk < assemblyDimensions[2]; zBlk++) {
				maxVal = Math.max(maxVal, world.getBlockPowerInput(xBlk + masterPosition[0], masterPosition[1], zBlk + masterPosition[2]));
			}	
		}
		return maxVal;
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
		
		// Check if we need to rebuild the channel map for simulation
		// This happens on the first tick after the reactor is assembled or loaded from NBT.
		if (channels == null) 
		{
			channels = new IReactorComponent[assemblyDimensions[0] * assemblyDimensions[2] * 4];
			rodPositions = new int[assemblyDimensions[0] * assemblyDimensions[2] * 4];
			fuelStores = new InventoryComponent[assemblyDimensions[0] * assemblyDimensions[2]];
			channelTypes = new int[assemblyDimensions[0] * assemblyDimensions[2]];

			// Loop through the channel assemblies and pull the channel objects in.
			// We rebuild this every load so we're not trying to store the reactor contents twice.
			int yPos = masterPosition[1] + coreOffset;
			for (int xPos = masterPosition[0]; xPos < masterPosition[0] + assemblyDimensions[0]; xPos++) {
				for (int zPos = masterPosition[0]; zPos < masterPosition[2] + assemblyDimensions[2]; zPos++) {
					TileEntity rawTE = world.getTileEntity(xPos, yPos, zPos);
					if (!(rawTE instanceof TileReactorChannelAssembly)) {
						pfaamod.LOG.error("Fission Reactor FAILED to locate channel assembly TE at {} {} {}", xPos, yPos, zPos);
						isDeadFixMePlease = true;
						return; // If the core doesn't exist we just need to bail.
					}
					TileReactorChannelAssembly assembly = (TileReactorChannelAssembly)rawTE;
					
					// Load the channels array based on the items actually installed in the channel assemble
					for(int idx = 0; idx < 4; idx++) {
						int cIdx = channelIndex(xPos, zPos, idx & 1, idx >> 1);
						channels[cIdx] = assembly.getChannel(idx) == null ? EMPTY : (IReactorComponent)assembly.getChannel(idx).getItem();
						channelTypes[cIdx] = channels[cIdx].getCapIndex();
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
							
							surfaceAdj += channels[cIdx].getOpenSurfaceArea();	
							fuelMask |= channels[cIdx] instanceof ItemReactorFuelChannel ? 1 << maskBit++ : 0;
						}
						
						for(int yBlk = 0; yBlk < assemblyDimensions[1]; yBlk++) {
							assemblyThermalXfer[assemblyIndex(xBlk, yBlk, zBlk)] += surfaceAdj * Config.reactorHeatXferCoeff;
							
							// Sanity cap - limit our max thermal transfer per block.  We don't need to invoke Maxwell's Demon.
							assemblyThermalXfer[assemblyIndex(xBlk, yBlk, zBlk)] = Math.min(assemblyThermalXfer[assemblyIndex(xBlk, yBlk, zBlk)], 128);
						}
						
						TileReactorRodMotor rodMotor = (TileReactorRodMotor)world.getTileEntity(xBlk + masterPosition[0], masterPosition[1], zBlk + masterPosition[2]);
						rodMotor.setFuelConfiguration(fuelMask, assemblyDimensions[1]);
						fuelStores[assemblyColumnIndex(xBlk, zBlk)] = rodMotor.fuelRods;
					}
				}
			}
		}
		
		tickCounter = ++tickCounter % 20;
		if (tickCounter == 0)
			fluxTick();
		thermalTick();
	}
	
	/**
	 * Simple reactor process (each tick)
	 */
	private void thermalTick() {
		
		int coolantAmount = coolant.getFluidAmount();
		// First, check if any coolant was added to the core.  If so, we need to drop coolant temperature to maintain thermal mass
		if (coolantAmount > 0) {	
			coolantTemperature = (coolantTemperature * coolantLast + T_20_C * (coolantAmount - coolantLast)) / coolantAmount;	
		}
		
		// Get the coolant level.  First, get it in absolute height within the reactor.  Then translate that into being relative to the channel assemblies where 0 = no core coverage.
		int coolantLevelBlocks = (coolantAmount * vesselHeight / coolant.getCapacity()) - (assemblyDimensions[2] - coreOffset - 1);

		// Now convert this into the array index after which water covers the channel assembly blocks
		coolantLevelBlocks = Math.max(0, (assemblyDimensions[2] - coolantLevelBlocks) * assemblyDimensions[0] * assemblyDimensions[1]);
		
		// Heat up the channel assemblies
		int addCoolantHeat = 0;
		int size = assemblyDimensions[0] * assemblyDimensions[1] * assemblyDimensions[2];
		
		for (int idx = 0; idx < size; idx++) {
			assemblyTemperatures[idx] += assemblyHeatGenerate[idx];
			int deltaT = 0;
			
			// If the water is covering this level of the core, do heat transfer to it
			if (idx >= coolantLevelBlocks) {
				deltaT = (assemblyTemperatures[idx] - coolantTemperature) * assemblyThermalXfer[idx];
				deltaT >>= 8; // This bit shift is like dividing by 256, giving me some leeway for nuance in reactorHeatXferCoeff.
			
				addCoolantHeat += deltaT;
			}
			
			assemblyTemperatures[idx] = Math.min(assemblyTemperatures[idx] - deltaT,  T_400_C); // Cap assembly temps at 400 °C for now.  In future we'll handle meltdowns instead.
		}
		
		// Now that we have a rough estimate of thermal energy going into the core, add it to the coolant
		coolantTemperature = Math.min(coolantTemperature + addCoolantHeat * Config.reactorAssemblySpecificHeat / (coolantAmount * Config.reactorCoolantSpecificHeat),  T_360_C); // Cap coolant temp at 360 °C.  In future we'll handle steam explosions instead.

		// Now determine how much coolant could evaporate (convert to hot coolant, not really evaporate) this tick.
		// TODO limit this to however many mB are touching the core?
		int exported = Math.min((coolantTemperature - Config.reactorCoolantBoilTemperature) * Config.reactorCoolantSpecificHeat / Config.reactorCoolantExportHeat, coolantAmount);
		
		// Now try to actually push that much hot coolant out - we may not be able to export all of it from the reactor.
		exported = exportHotCoolant(exported);
		
		// Now try to export up to that much, and subtract the equivalent thermal energy from the coolant
		if (exported < coolantAmount)
			coolantTemperature = ((coolantAmount * Config.reactorCoolantSpecificHeat) - (exported * Config.reactorCoolantExportHeat)) / ((coolantAmount - exported) * Config.reactorCoolantSpecificHeat);
		else
			coolantTemperature = T_20_C;
		
		// Remove from our coolant store
		coolant.drain(exported, true);
		coolantLast = coolantAmount - exported;
	}
	
	/**
	 * Full reactor process (each second)
	 */
	private void fluxTick() {
		
		// First zero heat generation
		int size = assemblyDimensions[0] * assemblyDimensions[1] * assemblyDimensions[2];	
		
		// Clear some arrays
		for (int idx = 0; idx < size; idx++) {
			assemblyHeatGenerate[idx] = 0;
			alternateTemperatures[idx] = 0;
		}
		
		int desiredRodPos = getMaxRedstoneInput();
		
		for(int idx = 0; idx < rodPositions.length; idx++) {
			if (rodPositions[idx] < desiredRodPos) {
				rodPositions[idx]++;
			}
			if (rodPositions[idx] > desiredRodPos) {
				rodPositions[idx]--;
			}
		}
		
		// Do thermal conduction within the core
		shareHeat();
		
		// Now do "neutron flux" (ha, ha) processing in the core	
		int radiation = doFlux();
		
		// TODO check the radiation level and then apply radiation effects
	}
	
	private void shareHeat() {
		// Share heat between channel assemblies.
		// Each assembly divides its heat into 8 sections.  Residual stays with the assembly.  2/8ths of the heat stays in the assembly.
		// The other 6/8 of the heat is exchanged, 1/8th with each of the 6 adjacent assemblies. 
		// If it can't be exchanged in a direction then retain.
		int yStride = assemblyDimensions[0];
		int zStride = assemblyDimensions[0] * assemblyDimensions[1];
		
		int idx = 0; 
		for(int zBlk = 0; zBlk < assemblyDimensions[2]; zBlk++) {
			boolean zCap = zBlk == assemblyDimensions[2] - 1;
			
			for(int yBlk = 0; yBlk < assemblyDimensions[1]; yBlk++) {
				boolean yCap = yBlk == assemblyDimensions[1] - 1;
				
				for(int xBlk = 0; xBlk < assemblyDimensions[0]; xBlk++) {
					int myHeat = (assemblyTemperatures[idx] & 3) + (assemblyTemperatures[idx] >> 2);
					int heatPart = assemblyTemperatures[idx] >> 3;
					
					// If we couldn't be shared with from down/north/west, then preserve the relevant heat
					if (xBlk == 0)
						myHeat += heatPart;	
		
					if (yBlk == 0)
						myHeat += heatPart;	
		
					if (zBlk == 0)
						myHeat += heatPart;
					
					if (xBlk == assemblyDimensions[0] - 1) 
						myHeat += heatPart;						
					else {
						int deltaT = ((heatPart) + (assemblyTemperatures[idx + 1] >> 3));
						int half = deltaT >> 1;
			
						myHeat += half + (deltaT & 1);	
						alternateTemperatures[idx + 1] += half;	
					}			
					if (yCap) 
						myHeat += heatPart;						
					else {
						int deltaT = ((heatPart) + (assemblyTemperatures[idx + yStride] >> 3));
						int half = deltaT >> 1;
			
						deltaT &= 1;
						myHeat += half + (deltaT & 1);	
						alternateTemperatures[idx + assemblyDimensions[0]] += half;	
					}			
					if (zCap)
						myHeat += heatPart;						
					else {
						int deltaT = ((heatPart) + (assemblyTemperatures[idx + zStride] >> 3));
						int half = deltaT >> 1;
		
						myHeat += half + (deltaT & 1);	
						alternateTemperatures[idx + zStride] += half;	
					}					
					alternateTemperatures[idx] += myHeat;					
					idx++;
				}	
			}
		}
		
		// Swap arrays for next tick
		int[] temp = assemblyTemperatures;
		assemblyTemperatures = alternateTemperatures;
		alternateTemperatures = temp;
	}
	
	private int doFlux() {
		// TODO move these allocations to assemble();
		
		int totalFlux = 0;
		
		// Loop through the channels.  For each channel that provides a neutron source, apply it to the nearby channels.
		for (int zIdx = 0; zIdx < assemblyDimensions[2] * 2; zIdx ++) {
			for (int xIdx = 0; xIdx < assemblyDimensions[0] * 2; xIdx ++) {
				if (channelTypes[channelIndex(xIdx, zIdx)] == 1) {
					// Fuel channel - we need to process flux being emitted from (and reflected to) this channel
					// This is a 3D simulation as well, which complicates it.  However, we have an ace:
					// 90% of the time we only need to do 2D operations on arrays.  We only care about 3D when we reflect off another fuel channel
					// Or just for checking internal reflections.
					
					// Start by getting some reference offsets
					int xBlk = xIdx >> 1;
					int zBlk = zIdx >> 1;
					
					// Now access the fuel rods for this column
					InventoryComponent fuel = fuelStores[assemblyColumnIndex(xBlk, zBlk)];
					int fIdx = ((xIdx & 1) + (zIdx & 1) << 1) * assemblyDimensions[1];
					
					int rodPos = rodPositions[assemblyColumnIndex(xBlk, zBlk)];
					boolean any = false;
					
					// For each rod, get its output flux given its current temperature
					for (int yIdx = 0; yIdx < assemblyDimensions[1]; yIdx++) {
						ItemStack fuelStack = fuel.getStackInSlot(fIdx++);
						if (fuelStack == null)
							columnFlux[yIdx] = 0;
						else {
							columnFlux[yIdx] = (rodPos + 1) * ((IReactorFuel)fuelStack.getItem()).getSourcedFlux(fuelStack, assemblyTemperatures[assemblyIndex(xBlk,  yIdx,  zBlk)]);
							columnFlux[yIdx] >>= 4;
							columnFluxReflected[yIdx] = 0;
							totalFlux += columnFlux[yIdx];
							if (columnFlux[yIdx] > 0)
								any = true;
						}
					}
					
					// Only bother with the flux calcs if we're getting flux
					if (any) {
						
						// First, loop through FLUX_VERTICAL to handle flux interactions within the fuel channel
						fIdx = ((xIdx & 1) + (zIdx & 1) << 1) * assemblyDimensions[1];						
						for(int yIdx = 0; yIdx < assemblyDimensions[1]; yIdx++) {
							for (int spIdx = 0; spIdx < FLUX_VERTICAL.length; spIdx++) {
								final int[] spread = FLUX_VERTICAL[spIdx];
								if (yIdx + spread[0] < 0 || yIdx + spread[0] >= assemblyDimensions[1])
									continue;
								
								ItemStack fuelStack = fuel.getStackInSlot(fIdx + yIdx + spread[0]);
								if (fuelStack == null)
									continue;
								
								columnFluxReflected[yIdx] += ((IReactorFuel)fuelStack.getItem()).getReflectedFlux(fuelStack, columnFlux[yIdx] >> spread[1]);
							}
							
						}
						
						// Then loop through FLUX_SPREAD to handle flux interactions with other channels horizontally
						for (int spIdx = 0; spIdx < FLUX_SPREAD.length; spIdx++) {
							final int[] spread = FLUX_SPREAD[spIdx];
							int xOff = xIdx + spread[0];
							int zOff = zIdx + spread[1];
						
							// If trying to spread outside of the reactor core, just don't.
							if (xOff < 0 || xOff >= assemblyDimensions[0] * 2 || zOff < 0 || zOff  >= assemblyDimensions[2] * 2)
								continue;
						
							switch(channelTypes[channelIndex(xOff, zOff)]) {
								case 0: // Empty Channel
								case 2: // Cooling Channel
									continue;
								case 1:// Fuel Channel
									fIdx = ((xOff & 1) + (zOff & 1) << 1) * assemblyDimensions[1];
									for (int yIdx = 0; yIdx < assemblyDimensions[1]; yIdx++) {
										if (columnFlux[yIdx] > 0) {
											ItemStack fuelStack = fuel.getStackInSlot(fIdx++);
											if (fuelStack == null)
											continue;
											columnFluxReflected[yIdx] += ((IReactorFuel)fuelStack.getItem()).getReflectedFlux(fuelStack, columnFlux[yIdx] >> spread[2]);
										}
									}
									break;
								case 3: // Reflector
									for (int yIdx = 0; yIdx < assemblyDimensions[1]; yIdx++) {
										columnFluxReflected[yIdx] += channels[channelIndex(xOff, zOff)].getReflection(columnFlux[yIdx] >> spread[2]);
									}
							}
						}
					}
					
					// Now that we have our sum total reflected flux, convert that to heat energy generation
					fIdx = ((xIdx & 1) + (zIdx & 1) << 1) * assemblyDimensions[1];
					for (int yIdx = 0; yIdx < assemblyDimensions[1]; yIdx++) {
						ItemStack fuelStack = fuel.getStackInSlot(fIdx++);
						if (fuelStack == null)
							continue;
						
						int aIdx = assemblyIndex(xBlk, yIdx, zBlk);
						assemblyHeatGenerate[aIdx] = T_TWENTIETH_K * ((IReactorFuel)fuelStack.getItem()).absorbFlux(fuelStack, columnFluxReflected[yIdx]) / Config.reactorAssemblySpecificHeat;
						
						if (assemblyDecay[aIdx] < assemblyHeatGenerate[aIdx])
							assemblyDecay[aIdx]++;
						else if (assemblyDecay[aIdx] > 0) 
							assemblyDecay[aIdx]--;
						
						assemblyHeatGenerate[aIdx] += assemblyDecay[aIdx];
					}
				}
			}
		}
		
		return totalFlux;
	}
	
	public void removeOutlet(int x, int y, int z, ForgeDirection From) {
		outletConnections.removeIf(c -> c[0] == x && c[1] == y && c[2] == z && c[3] == From.ordinal());
	}
	
	
	public void addOutlet(int x, int y, int z, ForgeDirection From) {
		outletConnections.removeIf(c -> c[0] == x && c[1] == y && c[2] == z && c[3] == From.ordinal());
		outletConnections.add(new int[] {x, y, z, From.ordinal()});
	}
	
	
	private int exportHotCoolant(int maxAmt) {
		hotCoolant.amount = maxAmt;
		maxAmt = 0;
		
		// Loop through our list of outlet connections.  For each one we push what we can for hot coolant
		// If we have leftover, we try the next outlet until we run out of outlets.
		for (int idx = 0; idx < outletConnections.size(); idx++) {
			int[] connection = outletConnections.get(idx);
			IFluidHandler receiver = (IFluidHandler)world.getTileEntity(connection[0],  connection[1],  connection[2]);
		
			// Check if it can accept hot coolant
			if (receiver.canFill(ForgeDirection.getOrientation(connection[3]), hotCoolant.getFluid())) {
				
				// If so fill it, and note down how much was filled
				int amt = receiver.fill(ForgeDirection.getOrientation(connection[3]), hotCoolant, true);
				maxAmt += amt;
				hotCoolant.amount -= amt;
				if (hotCoolant.amount <= 0)
					break;
			}
			
		}
		
		// At the very end, return how much hot coolant was actually exported
		return maxAmt;
	}
	

	/**
	 * Persist reactor state to NBT
	 * @param nbt
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("coreSize", assemblyDimensions);
		nbt.setIntArray("position", masterPosition);
		nbt.setIntArray("temperatures", assemblyTemperatures);
		nbt.setIntArray("surfaces", assemblyThermalXfer);
		nbt.setIntArray("heat", assemblyHeatGenerate);
		nbt.setIntArray("decay", assemblyDecay);
		
		nbt.setInteger("coreOffset", coreOffset);
		nbt.setInteger("tickCounter", tickCounter);
		nbt.setInteger("fluidVolume", coolant.getCapacity());
		nbt.setInteger("fluidTemperature", coolantTemperature);
		nbt.setInteger("vesselHeight", vesselHeight);
		
		coolant.writeToNBT(nbt);
		
		int[] storedOutlets = new int[outletConnections.size() * 4];
		for(int idx = 0; idx < outletConnections.size(); idx++) {
			int[] conn = outletConnections.get(idx);
			storedOutlets[idx << 2] = conn[0];
			storedOutlets[(idx << 2) + 1] = conn[1];
			storedOutlets[(idx << 2) + 2] = conn[2];
			storedOutlets[(idx << 2) + 3] = conn[3];
		}
		nbt.setIntArray("outlets", storedOutlets);
		
	}
	
	

	/**
	 * Retrieve reactor state from NBT.  If this is called instead of assemble(), we need to ensure anything set in assemble() is also set here.
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		assemblyDimensions = nbt.getIntArray("coreSize");
		masterPosition = nbt.getIntArray("position");
		assemblyTemperatures = nbt.getIntArray("temperatures");
		assemblyThermalXfer = nbt.getIntArray("surfaces");
		assemblyHeatGenerate = nbt.getIntArray("heat");
		assemblyDecay = nbt.getIntArray("decay");
		
		coreOffset = nbt.getInteger("coreOffset");
		tickCounter = nbt.getInteger("tickCounter");
		coolantTemperature = nbt.getInteger("fluidTemperature");
		vesselHeight = nbt.getInteger("vesselHeight");
		
		coolant = new FluidTank(nbt.getInteger("fluidVolume"));
		coolant.readFromNBT(nbt);
		
		columnFlux = new int[assemblyDimensions[1]];
		columnFluxReflected = new int[assemblyDimensions[1]];
		
		channels = null;
		needsInitialComponentConfig = false;
		
		int[] storedOutlets = nbt.getIntArray("outlets");
		outletConnections.setSize(storedOutlets.length >> 2);
		
		for(int idx = 0; idx < storedOutlets.length; idx += 4) {
			outletConnections.set(idx >> 2, new int[] {storedOutlets[idx], storedOutlets[idx + 1], storedOutlets[idx + 2], storedOutlets[idx + 3]});
		}
		
	}
}
