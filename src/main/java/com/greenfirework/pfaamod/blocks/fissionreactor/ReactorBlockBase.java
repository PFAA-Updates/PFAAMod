package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.Utility;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.IBlockAccess;

public abstract class ReactorBlockBase extends Block {
	
	public ReactorBlockBase(String name) {
		super(Material.iron);
		setBlockName(name);
		setBlockTextureName(Utility.TextureName(name));
		setCreativeTab(CreativeTabs.tabRedstone);
		
		GameRegistry.registerBlock(this,  name);
	}

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
	
}
