package com.greenfirework.pfaamod.items.fissionreactor;

import com.greenfirework.pfaamod.fissionreactor.IReactorComponent;
import com.greenfirework.pfaamod.items.PFAAItemBase;

public class ItemReactorReflectorChannel extends PFAAItemBase implements IReactorComponent {
	public static final String name = "reflectorChannel";
	
	public ItemReactorReflectorChannel() {
		super(name);
	}

	@Override
	public int getOpenSurfaceArea() {
		return 0;
	}

	@Override
	public int getCapIndex() {
		return 3;
	}

	@Override
	public int getReflection(int incoming) {
		return incoming;
	}
}
