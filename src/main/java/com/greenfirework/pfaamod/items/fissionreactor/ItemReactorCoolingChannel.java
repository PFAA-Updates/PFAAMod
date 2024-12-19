package com.greenfirework.pfaamod.items.fissionreactor;

import com.greenfirework.pfaamod.fissionreactor.IReactorComponent;
import com.greenfirework.pfaamod.items.PFAAItemBase;

public class ItemReactorCoolingChannel extends PFAAItemBase implements IReactorComponent {
	public static final String name = "coolingChannel";
	
	public ItemReactorCoolingChannel() {
		super(name);
	}

	@Override
	public int getOpenSurfaceArea() {
		return 2;
	}

	@Override
	public int getCapIndex() {
		return 2;
	}

	@Override
	public int getReflection(int incoming) {
		return 0;
	}
}
