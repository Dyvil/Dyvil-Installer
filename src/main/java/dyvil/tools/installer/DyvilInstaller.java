package dyvil.tools.installer;

import dyvil.tools.dpf.Parser;
import dyvil.tools.installer.task.StartupThread;
import dyvil.tools.installer.ui.GUI;
import dyvil.tools.installer.util.Downloader;
import dyvil.tools.installer.version.Distribution;
import dyvil.tools.parsing.Name;
import dyvil.tools.parsing.marker.MarkerList;

import java.io.IOException;

public class DyvilInstaller
{
	public static final String VERSION_DATA_URL      = "https://raw.githubusercontent.com/Dyvil/Dyvil/master/versions/versions.dyp";
	public static final String DISTRIBUTION_DATA_URL = "https://raw.githubusercontent.com/Dyvil/Dyvil/master/versions/distributions.dyp";

	public static GUI gui = new GUI();

	private static Distribution distributionData;

	public static void main(String[] args)
	{
		new StartupThread().start();
		gui.setVisible(true);
	}

	public static Distribution getDistributionData()
	{
		if (distributionData != null)
		{
			return distributionData;
		}

		Distribution distribution = distributionData = new Distribution(Name.getQualified("distributions"));
		try
		{
			gui.setDownloadMessage("Downloading Distribution Data...");

			String content = Downloader.download(DISTRIBUTION_DATA_URL);
			new Parser(new MarkerList(), content).accept(distribution);
		}
		catch (IOException e)
		{
			gui.setInstallMessage("Failed to fetch or parse Distribution Data");
		}
		return distribution;
	}
}
