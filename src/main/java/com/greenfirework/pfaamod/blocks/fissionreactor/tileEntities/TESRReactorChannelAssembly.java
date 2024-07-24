package com.greenfirework.pfaamod.blocks.fissionreactor.tileEntities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.greenfirework.pfaamod.pfaamod;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class TESRReactorChannelAssembly extends TileEntitySpecialRenderer {

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation(pfaamod.MODID, "obj/channelAssembly.obj"));
	
	ResourceLocation texture = new ResourceLocation(pfaamod.MODID, "textures/blocks/channelAssembly.png");
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z,
			float par4) {
		

        bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.0F, (float) z + 0.5F);
        GL11.glScalef(0.09375F, 0.09375F, 0.09375F);

        model.renderAll();
        GL11.glPopMatrix();
	}

}
