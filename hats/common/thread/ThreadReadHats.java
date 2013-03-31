package hats.common.thread;

import hats.client.model.ModelHat;
import hats.common.Hats;
import hats.common.core.CommonProxy;
import hats.common.core.HatHandler;

import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class ThreadReadHats extends Thread 
{
	
	public File hatsFolder;
	
	public CommonProxy proxy;

	public ThreadReadHats(File dir, CommonProxy prox)
	{
		setName("Hats Mod Hat Hunting Thread");
		setDaemon(true);
		
		hatsFolder = dir;
		proxy = prox;
	}
	
	@Override
	public void run()
	{
		if(!hatsFolder.exists())
		{
			return;
		}
		
		int hatCount = 0;
		
		File[] files = hatsFolder.listFiles();
		for(File file : files)
		{
			if(HatHandler.readHatFromFile(file))
			{
				hatCount++;	
			}
		}

		Hats.console("Loaded " + Integer.toString(hatCount) + (hatCount == 1 ? " hat" : " hats"));
	}
}