package com.greenfirework.pfaamod.blocks;

import java.lang.reflect.Constructor;

import com.greenfirework.pfaamod.PFAAModelBlockInventoryRenderer;
import com.greenfirework.pfaamod.pfaamod;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;

public abstract class PFAABlockTESRProviderBase<T extends PFAATileEntityBase, U extends TileEntitySpecialRenderer> extends PFAABlockTEProviderBase<T> {

	private Class<U> TESRClass;
	private Class<T> TEClass;
	
	public PFAABlockTESRProviderBase(String name, Class<T> TEClass, Class<U> TESRClass) {
		super(name, TEClass);
		this.TESRClass = TESRClass;
		this.TEClass = TEClass;
		
		RegisterTESR();
	}

	protected void RegisterTESR() {
		Constructor<?> ctor;
		TileEntitySpecialRenderer TESRInstance;
		try {
			ctor = TESRClass.getConstructor();
			TESRInstance = (TileEntitySpecialRenderer) ctor.newInstance(new Object[] {});
		} catch (Exception e) {
			pfaamod.LOG.debug("We failed to get the TESR instance.");
			e.printStackTrace();
			return;
		}	
		ClientRegistry.bindTileEntitySpecialRenderer(TEClass, TESRInstance);		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(this), new PFAAModelBlockInventoryRenderer(TESRInstance, createNewTileEntity(null, 0)));
	}
	
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
}
