package com.greenfirework.pfaamod.fissionreactor;

public interface IReaction {
	public void addHeat(int amt);
	
	public void reflect();
	
	public void flux(int range);
}
