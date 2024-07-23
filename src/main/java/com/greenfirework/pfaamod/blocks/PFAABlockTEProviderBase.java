package com.greenfirework.pfaamod.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.world.World;

public abstract class PFAABlockTEProviderBase<T extends PFAATileEntityBase> extends PFAABlockBase implements ITileEntityProvider {

    public PFAABlockTEProviderBase(String name) {
		super(name);
	}

	public abstract T createNewTileEntity(World world, int meta);
}
