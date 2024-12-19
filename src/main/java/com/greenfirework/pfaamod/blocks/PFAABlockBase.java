package com.greenfirework.pfaamod.blocks;

import com.greenfirework.pfaamod.Utility;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class PFAABlockBase extends Block {
	
	public PFAABlockBase(String name) {
		super(Material.iron);
		setBlockName(name);
		setBlockTextureName(Utility.textureName(name));
		setCreativeTab(CreativeTabs.tabRedstone);
		
		GameRegistry.registerBlock(this,  name);
	}

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
    
	
}
