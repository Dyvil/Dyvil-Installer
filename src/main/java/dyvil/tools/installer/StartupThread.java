package dyvil.tools.installer;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class StartupThread extends Thread
{
	@Override
	public void run()
	{
		DyvilInstaller.setInstallMessage("Determining Installation Directory...");
		DyvilInstaller.setInstallDirectory(getInstallDirectory());
		
		try
		{
			URL url = new URL("https://raw.githubusercontent.com/Dyvil/Dyvil-Installer/master/versions.txt");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			ByteBuffer bytebuf = ByteBuffer.allocate(1024);
			rbc.read(bytebuf);
			String content = new String(bytebuf.array(), StandardCharsets.UTF_8);
			
			int versions = readVersionFile(content);
			DyvilInstaller.setInstallMessage("Fetched " + versions + " versions");
		}
		catch (Exception ex)
		{
			DyvilInstaller.setInstallMessage("Could not fetch version list");
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
			return System.getenv("ProgramFiles") + "/Dyvil";
		}
		else if (os.contains("MAC"))
		{
			return "/Library/Dyvil";
		}
		else if (os.contains("NUX"))
		{
			return System.getProperty("user.home") + "/Dyvil";
		}
		return System.getProperty("user.dir") + "/Dyvil";
	}
}
