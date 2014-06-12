package hats.client.render;

import hats.client.core.ClientProxy;
import hats.client.core.HatInfoClient;
import hats.client.model.ModelHat;
import hats.common.Hats;
import hats.common.core.HatHandler;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HatRendererHelper 
{
	public static void renderHat(HatInfoClient info, float alpha, float hatScale, float mobRenderScaleX, float mobRenderScaleY, float mobRenderScaleZ, float renderYawOffset, float rotationYaw, float rotationPitch, float rotationRoll, float rotatePointVert, float rotatePointHori, float rotatePointSide, float offsetVert, float offsetHori, float offsetSide, boolean isPlayer, boolean bindTexture, float renderTick)
	{
		if(info == null)//hat names are lower case
		{
			return;
		}
    	ModelHat model = ClientProxy.models.get(info.hatName);
    	
    	if(model != null)
    	{
    		if(Hats.proxy.tickHandlerClient.currentHatRenders >= Hats.config.getInt("maxHatRenders") && !isPlayer)
    		{
    			return;
    		}
    		
    		GL11.glPushMatrix();
    		
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glScalef(1.001F, 1.001F, 1.001F);
            GL11.glScalef(mobRenderScaleX, mobRenderScaleY, mobRenderScaleZ);

            GL11.glRotatef(renderYawOffset, 0.0F, -1.0F, 0.0F);
            
            GL11.glTranslatef(-rotatePointSide, 0.0F, rotatePointHori);
            
            GL11.glRotatef(renderYawOffset, 0.0F, 1.0F, 0.0F);
            
            GL11.glTranslatef(0.0F, rotatePointVert, 0.0F);

            GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
            
            GL11.glRotatef(rotationYaw, 0.0F, -1.0F, 0.0F);
            GL11.glRotatef(rotationPitch, -1.0F, 0.0F, 0.0F);
            GL11.glRotatef(rotationRoll, 0.0F, 0.0F, 1.0F);

            GL11.glTranslatef(0.0F, 1F, 0.0F);
            
            GL11.glTranslatef(offsetSide, offsetVert, -offsetHori);
            
            GL11.glTranslatef(0.0F, -1F, 0.0F);
            GL11.glScalef(hatScale, hatScale, hatScale);
            
            if(bindTexture)
            {
		        if(info.recolour > 0)
		        {
		        	float diffR = info.colourR - info.prevColourR;
		        	float diffG = info.colourG - info.prevColourG;
		        	float diffB = info.colourB - info.prevColourB;
		        	
		        	diffR *= (float)(info.recolour - renderTick) / 20F;
		        	diffG *= (float)(info.recolour - renderTick) / 20F;
		        	diffB *= (float)(info.recolour - renderTick) / 20F;
		        	
		        	GL11.glColor4f((float)(info.colourR - diffR) / 255.0F, (float)(info.colourG - diffG) / 255.0F, (float)(info.colourB - diffB) / 255.0F, MathHelper.clamp_float(alpha, 0.0F, 1.0F));
		        }
		        else
		        {
		        	GL11.glColor4f((float)info.colourR / 255.0F, (float)info.colourG / 255.0F, (float)info.colourB / 255.0F, MathHelper.clamp_float(alpha, 0.0F, 1.0F));
		        }
		        
		        BufferedImage image = ClientProxy.bufferedImages.get(info.hatName);
		        
		        if (image != null)
		        {
		            if (ClientProxy.bufferedImageID.get(image) == -1)
		            {
		            	ClientProxy.bufferedImageID.put(image, TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), image));
		            }
		            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ClientProxy.bufferedImageID.get(image));
		        }
            }
            else
            {
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, MathHelper.clamp_float(alpha, 0.0F, 1.0F));
            }

	        GL11.glScalef(-1.0F, -1.0F, 1.0F);
	        model.render(0.0625F);

	        GL11.glDisable(GL11.GL_BLEND);
    		
    		GL11.glPopMatrix();
    		
    		Hats.proxy.tickHandlerClient.currentHatRenders++;
    	}
    	else if(!HatHandler.reloadingHats)
    	{
    		if(!Hats.proxy.tickHandlerClient.requestedHats.contains(info.hatName))
    		{
    			HatHandler.requestHat(info.hatName, null);
    			Hats.proxy.tickHandlerClient.requestedHats.add(info.hatName);
    		}
    	}
	}
}
