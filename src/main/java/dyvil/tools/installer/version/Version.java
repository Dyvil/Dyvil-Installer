package dyvil.tools.installer.version;

import dyvil.collection.Map;
import dyvil.collection.mutable.HashMap;
import dyvil.tools.dpf.converter.flatmapper.FlatMapConverter;
import dyvil.tools.installer.DyvilInstaller;
import dyvil.tools.installer.util.Downloader;
import dyvil.tools.parsing.Name;

import java.io.IOException;

public class Version implements Comparable<Version>
{
	private Distribution distributionData;

	private boolean downloaded;

	private final String distributionName;
	private final String downloadURL;

	public Version(String version, String url)
	{
		this.distributionName = version;
		this.downloadURL = url;
	}

	public String getIdentifier()
	{
		return distributionName;
	}

	public String getUrl()
	{
		return downloadURL;
	}

	@Override
	public String toString()
	{
		return this.distributionName;
	}

	public Distribution getDistributionData()
	{
		return distributionData;
	}

	public void readData()
	{
		if (this.downloaded)
		{
			return;
		}

		this.downloaded = true;

		Distribution baseDistribution = DyvilInstaller.getDistributionData();

		Map<String, Object> baseData = new HashMap<>();
		FlatMapConverter baseConverter = new FlatMapConverter(baseData);
		baseDistribution.accept(baseConverter);

		try
		{
			// Download Version Data
			DyvilInstaller.gui.setDownloadMessage("Downloading Version Data...");
			String versionContent = Downloader.download(this.getUrl());

			// Parse Version Data
			Map<String, Object> versionData = FlatMapConverter.convert(versionContent);

			// Add the Version Data to the Base Data
			baseData.putAll(versionData);

			// Create a Version-specific Distribution
			Distribution versionDistribution = new Distribution(Name.getSpecial(this.getIdentifier()));

			// Expand the Version-specific Distribution
			versionDistribution.expand(versionData, true);

			DyvilInstaller.gui.setDownloadMessage("Version Data download successful");
			this.distributionData = versionDistribution;
		}
		catch (IOException e)
		{
			DyvilInstaller.gui.setInstallMessage("Version Data download failed");
			e.printStackTrace();
		}
	}
	
	@Override
	public int compareTo(Version o)
	{
		if (this.distributionName.contains("Latest"))
		{
			return -1;
		}
		if (o.distributionName.contains("Latest"))
		{
			return 1;
		}
		return -this.distributionName.compareTo(o.distributionName);
	}
}
