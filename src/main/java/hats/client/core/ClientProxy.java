package hats.client.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import hats.client.gui.GuiHatSelection;
import hats.client.model.ModelHat;
import hats.client.render.RenderHat;
import hats.common.core.CommonProxy;
import hats.common.core.HatHandler;
import hats.common.entity.EntityHat;
import hats.common.thread.ThreadHatsReader;
import net.minecraft.client.Minecraft;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClientProxy extends CommonProxy
{

    @Override
    public void initRenderersAndTextures()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityHat.class, new RenderHat());
    }

    @Override
    public void initTickHandlers()
    {
        super.initTickHandlers();
        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);
    }

    @Override
    public void getHatsAndOpenGui()
    {
        ((Thread)new ThreadHatsReader(HatHandler.hatsFolder, false, true)).start();
    }

    @Override
    public void clearAllHats()
    {
        super.clearAllHats();
        models.clear();
        bufferedImages.clear();
        bufferedImageID.clear();
    }

    @Override
    public void remap(String duplicate, String original)
    {
        super.remap(duplicate, original);
        models.put(duplicate, models.get(original));
        bufferedImages.put(duplicate, bufferedImages.get(original));
    }

    @Override
    public void openHatsGui()
    {
        FMLClientHandler.instance().displayGuiScreen(Minecraft.getMinecraft().thePlayer, new GuiHatSelection(Minecraft.getMinecraft().thePlayer));
        //		FMLClientHandler.instance().displayGuiScreen(Minecraft.getMinecraft().thePlayer, new GuiTradeWindow("Kihira"));
    }

    @Override
    public void loadHatFile(File file)
    {
        super.loadHatFile(file);

        String hatName = file.getName().substring(0, file.getName().length() - 4).toLowerCase();

        try
        {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();

            while(entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if(!entry.isDirectory())
                {
                    if(entry.getName().endsWith(".png"))
                    {
                        InputStream stream = zipFile.getInputStream(entry);
                        BufferedImage image = ImageIO.read(stream);

                        bufferedImages.put(hatName, image);
                        bufferedImageID.put(image, -1);

                    }
                    if(entry.getName().endsWith(".xml"))
                    {
                        InputStream stream = zipFile.getInputStream(entry);

                        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

                        Document doc = builder.parse(stream);

                        models.put(hatName, new ModelHat(doc));
                    }
                }
            }

            zipFile.close();
        }
        catch (Exception e1)
        {
            //an exception would have been thrown before this in the thread, so no output.
        }
    }

    public static HashMap<String, BufferedImage> bufferedImages = new HashMap<String, BufferedImage>();
    public static HashMap<BufferedImage, Integer> bufferedImageID = new HashMap<BufferedImage, Integer>();
    public static HashMap<String, ModelHat> models = new HashMap<String, ModelHat>();

}
