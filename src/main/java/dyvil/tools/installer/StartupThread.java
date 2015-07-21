package dyvil.tools.installer;

public class StartupThread extends Thread
{
	@Override
	public void run()
	{
		DyvilInstaller.setInstallMessage("Determining Installation Directory...");
		DyvilInstaller.setInstallDirectory(getInstallDirectory());
		DyvilInstaller.setInstallMessage("");
	}
	
	private static String getInstallDirectory() {
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
