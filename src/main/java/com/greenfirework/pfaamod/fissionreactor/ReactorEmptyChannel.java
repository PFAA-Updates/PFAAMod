package com.greenfirework.pfaamod.fissionreactor;

public class ReactorEmptyChannel implements IReactorComponent {

	@Override
	public void Source(FissionReactor Context, int channelX, int channelZ) {
		// Do Nothing
	}

	@Override
	public void Recieve(FissionReactor Context, int sourceChannelX, int sourceChannelZ) {
		// Do Nothing
	}

	@Override
	public void OnConstruct(FissionReactor Context, int channelX, int channelZ) {
		// Do Nothing
	}

	@Override
	public int getOpenSurfaceArea() {
		return 1;
	}

}
