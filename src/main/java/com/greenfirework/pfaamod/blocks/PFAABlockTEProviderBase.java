package com.greenfirework.pfaamod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class PFAABlockTEProviderBase<T extends PFAATileEntityBase> extends PFAABlockBase implements ITileEntityProvider {

    public PFAABlockTEProviderBase(String name, Class<T> C) {
		super(name);
		GameRegistry.registerTileEntity(C, name);
	}

    @SuppressWarnings("unchecked")
	protected T getMyTileEntity(World world, int x, int y, int z, int meta, Class<T> C) {
    	TileEntity rawTE = world.getTileEntity(x, y, z);
    	if (!(rawTE.getClass().isAssignableFrom(C)))
    		return createNewTileEntity(world, meta);
    	return (T)rawTE;
    }
    
	public abstract T createNewTileEntity(World world, int meta);
}
