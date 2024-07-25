package com.greenfirework.pfaamod.fissionreactor;

public interface IReactorComponent {
	/**
	 * Called when the component is getting ticked as part of the reactor.  Should fire off other neutron sources
	 * @param Context
	 * @param channelX
	 * @param channelZ
	 */
	public void Source(FissionReactor Context, int channelX, int channelZ);
	/**
	 * Called when the component should react to getting ticked by other neutron sources
	 * @param Context
	 * @param sourceChannelX
	 * @param sourceChannelZ
	 */
	public void Recieve(FissionReactor Context, int sourceChannelX, int sourceChannelZ);
	/**
	 * Fired when the reactor is constructing itself.  Useful for reactor metadata (cooling surface area, etc)
	 * @param Context
	 */
	public void OnConstruct(FissionReactor Context, int channelX, int channelZ);
	
	public int getOpenSurfaceArea();
}
