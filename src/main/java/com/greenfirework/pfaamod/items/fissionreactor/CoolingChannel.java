package com.greenfirework.pfaamod.items.fissionreactor;

import com.greenfirework.pfaamod.Utility;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CoolingChannel extends Item {
	public static final String name = "coolingChannel";
	
	public CoolingChannel() {
		setUnlocalizedName(name);
		setTextureName(Utility.TextureName(name));
		setCreativeTab(CreativeTabs.tabRedstone);
		
		GameRegistry.registerItem(this, name);
	}
}
