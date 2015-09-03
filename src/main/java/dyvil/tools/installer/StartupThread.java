package dyvil.tools.installer;

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
		DyvilInstaller.setInstallMessage("Determining Installation Directory...");
		DyvilInstaller.setInstallDirectory(getInstallDirectory());
		
		DyvilInstaller.setInstallMessage("Fetching versions...");
		try
		{
			URL url = new URL("https://raw.githubusercontent.com/Dyvil/Dyvil/master/versions.txt");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			
			ByteBuffer bytebuf = ByteBuffer.allocate(1024);
			rbc.read(bytebuf);
			String content = new String(bytebuf.array(), 0, bytebuf.position(), StandardCharsets.UTF_8);
			
			int versions = readVersionFile(content);
			
			if (versions == 0)
			{
				DyvilInstaller.setInstallMessage("Fetched 1 version");
			}
			else
			{
				DyvilInstaller.setInstallMessage("Fetched " + versions + " versions");
			}
		}
		catch (Exception ex)
		{
			DyvilInstaller.setInstallMessage("Could not fetch version list");
			ex.printStackTrace();
		}
		
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException ex)
		{
		}
		
		DyvilInstaller.setInstallMessage("");
	}
	
	private static int readVersionFile(String content)
	{
		String[] lines = content.split("\n");
		int versions = 0;
		
		Version version = null;
		for (String s : lines)
		{
			if (s.isEmpty())
			{
				continue;
			}
			
			int index = s.indexOf(':');
			if (index < 0)
			{
				if (version != null)
				{
					DyvilInstaller.addVersion(version);
					versions++;
				}
				version = new Version(s);
			}
			else if (version != null)
			{
				version.setURL(s.substring(0, index), s.substring(index + 1));
			}
		}
		
		if (version != null)
		{
			DyvilInstaller.addVersion(version);
			return versions + 1;
		}
		return versions;
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
