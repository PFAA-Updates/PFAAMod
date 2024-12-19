package com.greenfirework.pfaamod.fissionreactor;

import com.greenfirework.pfaamod.components.InventoryComponent;

public class ReactorEmptyChannel implements IReactorComponent {



	@Override
	public int getOpenSurfaceArea() {
		return 1;
	}

	@Override
	public int getCapIndex() {
		return 0;
	}

	@Override
	public int getReflection(int incoming) {
		return 0;
	}

}
