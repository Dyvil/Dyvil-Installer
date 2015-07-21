package dyvil.tools.installer;

import java.io.*;
import java.net.URL;

public class InstallThread extends Thread
{
	private final Version	version;
	private final File		installDirectory;
	private final boolean	installDevTools;
	private final boolean	installREPL;
	
	public InstallThread(Version version, File installDirectory, boolean devTools, boolean repl)
	{
		this.version = version;
		this.installDirectory = installDirectory;
		this.installDevTools = devTools;
		this.installREPL = repl;
	}
	
	@Override
	public void run()
	{
		String version = this.version.identifier;
		DyvilInstaller.setInstallMessage("Downloading Required Libraries for Dyvil v" + version);
		File dir = new File(this.installDirectory, version);
		
		downloadBin(dir, "Dyvil Library", this.version.libraryURL);
		if (installDevTools || installREPL)
		{
			downloadBin(dir, "Dyvil Compiler", this.version.compilerURL);
		}
		if (installREPL)
		{
			downloadBin(dir, "Dyvil REPL", this.version.replURL);
		}
		
		DyvilInstaller.setDownloadMessage("");
		DyvilInstaller.setInstallMessage("");
	}
	
	private static String fileName(String url)
	{
		int index = url.lastIndexOf('/');
		return index < 0 ? url : url.substring(index + 1);
	}
	
	private static void downloadBin(File dir, String name, String url)
	{
		File libFile = new File(dir, "bin/" + fileName(url));
		download(name, url, libFile);
	}
	
	private static void download(String name, String url, File dest)
	{
		try
		{
			dest.createNewFile();
		}
		catch (IOException ex)
		{
			DyvilInstaller.setDownloadMessage("Failed to create file for " + name);
			return;
		}
		
		DyvilInstaller.setDownloadMessage("Downloading " + name + "...");
		try (InputStream is = new URL(url).openStream(); OutputStream os = new BufferedOutputStream(new FileOutputStream(dest)))
		{
			byte[] buf = new byte[4096];
			int read = 0;
			while ((read = is.read(buf, 0, 4096)) >= 0)
			{
				os.write(buf, 0, read);
			}
		}
		catch (IOException ex)
		{
			DyvilInstaller.setDownloadMessage("Failed to download " + name);
		}
		
		DyvilInstaller.setDownloadMessage("Successfully Downloaded " + name);
	}
}
