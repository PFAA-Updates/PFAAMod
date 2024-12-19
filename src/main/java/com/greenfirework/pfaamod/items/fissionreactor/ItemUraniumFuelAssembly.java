package com.greenfirework.pfaamod.items.fissionreactor;

import com.greenfirework.pfaamod.Config;
import com.greenfirework.pfaamod.fissionreactor.IReactorFuel;
import com.greenfirework.pfaamod.items.PFAAItemBase;

import net.minecraft.item.ItemStack;

public class ItemUraniumFuelAssembly extends PFAAItemBase implements IReactorFuel {
	public static final String name = "itemUraniumFuelRod";
	
	public ItemUraniumFuelAssembly() {
		super(name);
		setMaxDamage(Config.fuelRodMaxDurability);
	}

	@Override
	public int getSourcedFlux(ItemStack stack, int temperature) {
		if (stack.getItemDamage() == 0)
			return 0;
		
		return 0;
	}

	@Override
	public int getReflectedFlux(ItemStack stack, int incomingFlux) {
		return incomingFlux;
	}

	@Override
	public int absorbFlux(ItemStack stack, int incomingFlux) {
		int flux = incomingFlux + stack.getTagCompound().getInteger("flux");
		stack.getTagCompound().setInteger("flux", flux % Config.fuelRodFluxDurability);
		flux /= Config.fuelRodFluxDurability;
		setDamage(stack,  Math.max(stack.getItemDamage() - flux, 0));
		return incomingFlux * Config.fuelRodFluxHeat;
	}

}
