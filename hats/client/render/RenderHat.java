package hats.client.render;

import java.awt.image.BufferedImage;

import hats.client.core.ClientProxy;
import hats.client.gui.GuiHatSelection;
import hats.client.model.ModelHat;
import hats.common.Hats;
import hats.common.core.HatHandler;
import hats.common.entity.EntityHat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import org.lwjgl.opengl.GL11;

public class RenderHat extends Render 
{

	public RenderHat()
	{
		shadowSize = 0.0F;
	}
	
    public void renderHat(EntityHat hat, double par2, double par4, double par6, float par8, float par9)
    {
    	if(!hat.hatName.equalsIgnoreCase("") && hat.parent != null && !hat.parent.isPlayerSleeping() && hat.parent.isEntityAlive())
    	{
    		boolean firstPerson = (hat.parent == Minecraft.getMinecraft().renderViewEntity && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative || Minecraft.getMinecraft().currentScreen instanceof GuiHatSelection) && RenderManager.instance.playerViewY == 180.0F));
    		
    		if((Hats.renderInFirstPerson == 1 && firstPerson || !firstPerson) && !hat.parent.getHasActivePotion())
    		{
		    	ModelHat model = ClientProxy.models.get(hat.hatName);
		    	
		    	if(model != null)
		    	{
			        GL11.glPushMatrix();
			        
		            GL11.glEnable(GL11.GL_BLEND);
		            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			        
			        GL11.glTranslatef((float)par2 - (float)(HatHandler.getHorizontalRenderOffset(hat.parent) * Math.sin(Math.toRadians(hat.parent.prevRotationYawHead + (hat.parent.rotationYawHead - hat.parent.prevRotationYawHead) * par9))), (float)par4 + HatHandler.getVerticalRenderOffset(hat.parent), (float)par6 + (float)(HatHandler.getHorizontalRenderOffset(hat.parent) * Math.cos(Math.toRadians(hat.parent.prevRotationYawHead + (hat.parent.rotationYawHead - hat.parent.prevRotationYawHead) * par9))));
			        GL11.glRotatef(180.0F - par8, 0.0F, 1.0F, 0.0F);
			        
			        GL11.glRotatef(hat.parent.prevRotationPitch + (hat.parent.rotationPitch - hat.parent.prevRotationPitch) * par9, -1.0F, 0.0F, 0.0F);
			        
			        if(hat.reColour > 0)
			        {
			        	float diffR = hat.getR() - hat.prevR;
			        	float diffG = hat.getG() - hat.prevG;
			        	float diffB = hat.getB() - hat.prevB;
			        	
			        	float rendTick = par9;
			        	if(rendTick > 1.0F)
			        	{
			        		rendTick = 1.0F;
			        	}
			        	
			        	diffR *= (float)(hat.reColour - rendTick) / 20F;
			        	diffG *= (float)(hat.reColour - rendTick) / 20F;
			        	diffB *= (float)(hat.reColour - rendTick) / 20F;
			        	
			        	GL11.glColor4f((float)(hat.getR() - diffR) / 255.0F, (float)(hat.getG() - diffG) / 255.0F, (float)(hat.getB() - diffB) / 255.0F, 1.0F);
			        }
			        else
			        {
			        	GL11.glColor4f((float)hat.getR() / 255.0F, (float)hat.getG() / 255.0F, (float)hat.getB() / 255.0F, 1.0F);
			        }
			        
			        BufferedImage image = ClientProxy.bufferedImages.get(hat.hatName);
			        
			    	if(Minecraft.getMinecraft().thePlayer.username.equalsIgnoreCase("ohaiiChun") && hat.parent instanceof EntityPlayer && ((EntityPlayer)hat.parent).username.equalsIgnoreCase("Notch"))
			    	{
			    		//debug
			    	}
			        
			        if (image != null)
			        {
			            if (ClientProxy.bufferedImageID.get(image) == -1)
			            {
			            	ClientProxy.bufferedImageID.put(image, Minecraft.getMinecraft().renderEngine.allocateAndSetupTexture(image));
			            }
	
			            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ClientProxy.bufferedImageID.get(image));
			            Minecraft.getMinecraft().renderEngine.resetBoundTexture();
			        }
	
			        GL11.glScalef(-1.0F, -1.0F, 1.0F);
			        
			        model.render(hat, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			        
			        GL11.glDisable(GL11.GL_BLEND);
			        
			        GL11.glPopMatrix();
		    	}
		    	else if(!HatHandler.reloadingHats)
		    	{
		    		if(!Hats.proxy.tickHandlerClient.requestedHats.contains(hat.hatName))
		    		{
		    			HatHandler.requestHat(hat.hatName, null);
		    			Hats.proxy.tickHandlerClient.requestedHats.add(hat.hatName);
		    		}
		    	}
    		}
    	}
    }
	
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderHat((EntityHat)par1Entity, par2, par4, par6, par8, par9);
    }

}
