package dyvil.tools.installer.task;

import dyvil.collection.Entry;
import dyvil.collection.List;
import dyvil.collection.Map;
import dyvil.collection.mutable.ArrayList;
import dyvil.tools.installer.DyvilInstaller;
import dyvil.tools.installer.version.Version;
import dyvil.tools.installer.util.Downloader;
import dyvil.tools.dpf.converter.Expandable;
import dyvil.tools.dpf.converter.flatmapper.FlatMapConverter;

import java.io.File;

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
			String content = Downloader
					.download(DyvilInstaller.VERSION_DATA_URL);
			
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


		DyvilInstaller.getDistributionData();
		
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException ignored)
		{
		}
		
		DyvilInstaller.gui.setInstallMessage("");
	}

	private static int readVersionFile(String content)
	{
		Map<String, Object> versionData = FlatMapConverter.convert(content);
		Map<String, Object> versionFiles = ((Map<String, Object>) versionData.get("versionFiles"));
		List<Version> versions = new ArrayList<>();
		
		for (Entry<String, Object> entry : versionFiles)
		{
			Object value = Expandable.expand(entry.getValue(), versionData, false);
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
