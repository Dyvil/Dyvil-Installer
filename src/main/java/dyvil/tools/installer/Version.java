package dyvil.tools.installer;

public class Version
{
	public String	identifier;
	public String	libraryURL;
	public String	compilerURL;
	public String	replURL;
	
	public Version(String version)
	{
		this.identifier = version;
	}
	
	public void setURL(String key, String url)
	{
		switch (key)
		{
		case "lib":
		case "library":
			this.libraryURL = url;
			return;
		case "compiler":
			this.compilerURL = url;
			return;
		case "repl":
			this.replURL = url;
			return;
		}
	}
}
