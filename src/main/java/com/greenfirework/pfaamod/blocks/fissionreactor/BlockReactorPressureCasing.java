package com.greenfirework.pfaamod.blocks.fissionreactor;

import com.greenfirework.pfaamod.blocks.PFAABlockBase;
import com.greenfirework.pfaamod.structures.IMetaValueAssembleable;

import net.minecraft.world.World;

public class BlockReactorPressureCasing extends PFAABlockBase implements IMetaValueAssembleable {

	private static final String name = "pressureCasing";
	
	public BlockReactorPressureCasing() {
		super(name);
	}

	@Override
	public int getAssembledBitIndex() {
		return 3;
	}
	
	@Override
	public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta) {
		super.onBlockPreDestroy(worldIn, x, y, z, meta);
		if ((meta & 8) > 0) {
			// TODO trigger structure disassembly
		}
	}

	// TODO this one needs to change textures based on assembled status and connected blocks...  later.
}
