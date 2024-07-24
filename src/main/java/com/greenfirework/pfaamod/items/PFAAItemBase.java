package com.greenfirework.pfaamod.items;

import com.greenfirework.pfaamod.Utility;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class PFAAItemBase extends Item {
	public PFAAItemBase(String name) {
		setUnlocalizedName(name);
		setTextureName(Utility.TextureName(name));
		setCreativeTab(CreativeTabs.tabRedstone);
		
		GameRegistry.registerItem(this, name);
	}
}
