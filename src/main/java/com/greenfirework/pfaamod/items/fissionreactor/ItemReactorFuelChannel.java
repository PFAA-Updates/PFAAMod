package com.greenfirework.pfaamod.items.fissionreactor;

import com.greenfirework.pfaamod.fissionreactor.IReactorComponent;
import com.greenfirework.pfaamod.items.PFAAItemBase;

public class ItemReactorFuelChannel extends PFAAItemBase implements IReactorComponent {

	public static String name = "fuelChannel";
	
	public ItemReactorFuelChannel() {
		super(name);
	}

	@Override
	public int getOpenSurfaceArea() {
		return 0;
	}

	@Override
	public int getCapIndex() {
		return 1;
	}

	@Override
	public int getReflection(int incoming) {
		return incoming;
	}

}
