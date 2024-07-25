package com.greenfirework.pfaamod.items;

import com.greenfirework.pfaamod.fissionreactor.FissionReactor;
import com.greenfirework.pfaamod.fissionreactor.IReactorComponent;

public class ItemReactorFuelChannel extends PFAAItemBase implements IReactorComponent {

	public static String name = "fuelChannel";
	
	public ItemReactorFuelChannel() {
		super(name);
	}

	@Override
	public void Source(FissionReactor Context, int channelX, int channelZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Recieve(FissionReactor Context, int sourceChannelX, int sourceChannelZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnConstruct(FissionReactor Context, int channelX, int channelZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpenSurfaceArea() {
		return 0;
	}

}
