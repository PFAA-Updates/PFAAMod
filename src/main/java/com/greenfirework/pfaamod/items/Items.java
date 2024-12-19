package com.greenfirework.pfaamod.items;

import com.greenfirework.pfaamod.items.fissionreactor.ItemReactorCoolingChannel;
import com.greenfirework.pfaamod.items.fissionreactor.ItemReactorFuelChannel;
import com.greenfirework.pfaamod.items.fissionreactor.ItemReactorReflectorChannel;
import com.greenfirework.pfaamod.items.fissionreactor.ItemUraniumFuelAssembly;

public class Items {
	public static ItemReactorCoolingChannel coolingChannel;
	public static ItemReactorFuelChannel fuelChannel;
	public static ItemReactorReflectorChannel reflectorChannel;
	
	public static ItemUraniumFuelAssembly fuelAssembly;
	
	public static void preInit() {
		coolingChannel = new ItemReactorCoolingChannel();
		fuelChannel = new ItemReactorFuelChannel();
		reflectorChannel = new ItemReactorReflectorChannel();
		
		fuelAssembly = new ItemUraniumFuelAssembly();		
		
	}
}
