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

	private static final IModelCustom baseModel = AdvancedModelLoader.loadModel(new ResourceLocation(pfaamod.MODID, "obj/channelAssembly.obj"));
	private static final IModelCustom[] capModels = new IModelCustom[] {null,
																		AdvancedModelLoader.loadModel(new ResourceLocation(pfaamod.MODID, "obj/fuelChannelCap.obj")),
																		AdvancedModelLoader.loadModel(new ResourceLocation(pfaamod.MODID, "obj/coolingChannelCap.obj")),
																		AdvancedModelLoader.loadModel(new ResourceLocation(pfaamod.MODID, "obj/reflectorChannelCap.obj"))};
	
	
	private static final ResourceLocation texture = new ResourceLocation(pfaamod.MODID, "textures/blocks/channelAssembly.png");
	
	
	
	private static final float[][] capPositions = new float[][] {new float[] {0.15625F, 0.15625F},
																 new float[] {0.53125F, 0.15625F},
																 new float[] {0.15625F, 0.53125F},
																 new float[] {0.53125F, 0.53125F}};
	
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z,
			float par4) {
		TileReactorChannelAssembly TE = (TileReactorChannelAssembly)entity;
		
		if (TE.isAssembled)
			return;

        bindTexture(texture);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

        baseModel.renderAll();
        
        // Render channel caps
        for(int idx = 0; idx < 4; idx++) {
        	if (TE.channelCaps[idx] > 0) {
        		GL11.glPushMatrix();
        		
                GL11.glTranslatef(capPositions[idx][0], 0.0F, capPositions[idx][1]);
                capModels[TE.channelCaps[idx]].renderAll();
        		
        		GL11.glPopMatrix();
        	}
        }
        
        GL11.glPopMatrix();
	}

}
