package dyvil.tools.installer;

public class Version implements Comparable<Version>
{
	public String	identifier;
	public String	url;
	
	public Version(String version, String url)
	{
		this.identifier = version;
		this.url = url;
	}
	
	@Override
	public String toString()
	{
		return this.identifier;
	}
	
	@Override
	public int compareTo(Version o)
	{
		if (this.identifier.contains("Latest"))
		{
			return -1;
		}
		if (o.identifier.contains("Latest"))
		{
			return 1;
		}
		return -this.identifier.compareTo(o.identifier);
	}
}
