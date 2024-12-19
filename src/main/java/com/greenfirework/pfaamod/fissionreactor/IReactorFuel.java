package com.greenfirework.pfaamod.fissionreactor;

import net.minecraft.item.ItemStack;

public interface IReactorFuel {
	public int getSourcedFlux(ItemStack stack, int temperature);
	
	public int getReflectedFlux(ItemStack stack, int incomingFlux);
	
	/**
	 * Get the amount of thermal energy to produce per tick based on the incoming radiation.
	 * This function should apply fuel burnup to the fuel rod item.
	 * 
	 * @param stack The itemStack of nuclear fuel accepting the incoming flux
	 * @param incomingFlux the incoming neutron flux amount
	 * @return How much heat energy to deliver to the channel assembly holding this fuel rod
	 */
	
	public int absorbFlux(ItemStack stack, int incomingFlux);
}
