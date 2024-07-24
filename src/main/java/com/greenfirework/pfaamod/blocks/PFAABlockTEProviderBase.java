package com.greenfirework.pfaamod.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.world.World;

public abstract class PFAABlockTEProviderBase<T extends PFAATileEntityBase> extends PFAABlockBase implements ITileEntityProvider {

    public PFAABlockTEProviderBase(String name, Class<T> C) {
		super(name);
		GameRegistry.registerTileEntity(C, name);
	}

	public abstract T createNewTileEntity(World world, int meta);
}
