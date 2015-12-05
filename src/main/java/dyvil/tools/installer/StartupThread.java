package dyvil.tools.installer;

import dyvil.collection.Entry;
import dyvil.collection.List;
import dyvil.collection.Map;
import dyvil.collection.mutable.ArrayList;
import dyvil.collection.mutable.HashMap;
import dyvil.tools.dpf.Parser;
import dyvil.tools.installer.util.Expandable;
import dyvil.tools.installer.util.MapConverter;
import dyvil.tools.parsing.marker.MarkerList;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class StartupThread extends Thread
{
	public StartupThread()
	{
		super("Startup");
	}
	
	@Override
	public void run()
	{
		DyvilInstaller.gui.setInstallMessage("Determining Installation Directory...");
		DyvilInstaller.gui.setInstallDirectory(getInstallDirectory());
		
		DyvilInstaller.gui.setInstallMessage("Fetching versions...");
		try
		{
			URL url = new URL("https://raw.githubusercontent.com/Dyvil/Dyvil/master/versions/versions.dyp");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			
			ByteBuffer bytebuf = ByteBuffer.allocate(1024);
			rbc.read(bytebuf);
			String content = new String(bytebuf.array(), 0, bytebuf.position(), StandardCharsets.UTF_8);
			
			int versions = readVersionFile(content);
			
			if (versions == 1)
			{
				DyvilInstaller.gui.setInstallMessage("Fetched 1 version");
			}
			else
			{
				DyvilInstaller.gui.setInstallMessage("Fetched " + versions + " versions");
			}
		}
		catch (Exception ex)
		{
			DyvilInstaller.gui.setInstallMessage("Could not fetch version list");
			ex.printStackTrace();
		}
		
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException ex)
		{
		}
		
		DyvilInstaller.gui.setInstallMessage("");
	}
	
	private static int readVersionFile(String content)
	{
		Parser parser = new Parser(new MarkerList(), content);
		Map<String, Object> map = new HashMap<String, Object>();
		MapConverter converter = new MapConverter(map);
		parser.accept(converter);
		
		Map<String, Object> versionFiles = ((Map<String, Object>) map.get("versionFiles"));
		List<Version> versions = new ArrayList<>();
		
		for (Entry<String, Object> entry : versionFiles)
		{
			Object value = entry.getValue();
			if (value instanceof Expandable)
			{
				value = ((Expandable) value).expand(map);
			}
			versions.add(new Version(entry.getKey(), value.toString()));
		}
		
		for (Version version : versions.sorted())
		{
			DyvilInstaller.gui.addVersion(version);
		}
		return versions.size();
	}
	
	private static String getInstallDirectory()
	{
		String os = System.getProperty("os.name").toUpperCase();
		if (os.contains("WIN"))
		{
			return System.getenv("ProgramFiles") + "\\Dyvil";
		}
		else if (os.contains("MAC"))
		{
			return System.getProperty("user.home") + "/Library/Dyvil";
		}
		else if (os.contains("NUX"))
		{
			return System.getProperty("user.home") + "/Dyvil";
		}
		return System.getProperty("user.dir") + File.separator + "Dyvil";
	}
}
