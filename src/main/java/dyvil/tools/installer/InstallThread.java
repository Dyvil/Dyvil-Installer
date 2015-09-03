package dyvil.tools.installer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

public class InstallThread extends Thread
{
	public static final byte	DEV_TOOLS	= 1;
	public static final byte	REPL		= 2;
	
	private final Version	version;
	private final File		installDirectory;
	private final int		components;
	
	public InstallThread(Version version, File installDirectory, int components)
	{
		super("Install-" + version.identifier);
		this.version = version;
		this.installDirectory = installDirectory;
		this.components = components;
	}
	
	@Override
	public void run()
	{
		String version = this.version.identifier;
		DyvilInstaller.setInstallPercentage(1F / 4F);
		DyvilInstaller.setInstallMessage("Downloading Required Libraries for Dyvil v" + version);
		
		File dir = new File(this.installDirectory, version);
		File lib = new File(dir, "lib");
		File bin = new File(dir, "bin");
		File license = new File(dir, "license");
		
		download(new File(lib, "dyvil-library.jar"), "Dyvil Library", this.version.libraryURL);
		switch (this.components)
		{
		case REPL | DEV_TOOLS:
		case REPL:
			download(new File(lib, "dyvil-repl.jar"), "Dyvil REPL Library", this.version.replURL);
		case DEV_TOOLS:
			download(new File(lib, "dyvil-compiler.jar"), "Dyvil Compiler Library", this.version.compilerURL);
		}
		
		DyvilInstaller.setInstallPercentage(2F / 4F);
		DyvilInstaller.setInstallMessage("Downloading Scripts");
		switch (this.components)
		{
		case REPL | DEV_TOOLS:
		case REPL:
		{
			File f = new File(bin, "dyvil");
			download(f, "REPL Launcher", "https://raw.githubusercontent.com/Dyvil/Dyvil/master/scripts/dyvil");
			f.setExecutable(true);
		}
		case DEV_TOOLS:
		{
			File f = new File(bin, "dyvilc");
			download(f, "Compiler Launcher", "https://raw.githubusercontent.com/Dyvil/Dyvil/master/scripts/dyvilc");
			f.setExecutable(true);
		}
		}
		
		DyvilInstaller.setInstallPercentage(3F / 4F);
		DyvilInstaller.setInstallMessage("Downloading Text resources");
		download(new File(dir, "Changelog.txt"), "Changelog", "https://raw.githubusercontent.com/Dyvil/Dyvil/master/Changelog.txt");
		download(new File(license, "ASM-LICENSE.txt"), "ASM License", "https://raw.githubusercontent.com/Dyvil/Dyvil/master/ASM-LICENSE.txt");
		download(new File(license, "LICENSE.txt"), "ASM License", "https://raw.githubusercontent.com/Dyvil/Dyvil/master/LICENSE.txt");
		
		DyvilInstaller.setDownloadMessage("");
		DyvilInstaller.setInstallPercentage(1F);
		DyvilInstaller.setInstallMessage("Installation Successful");
	}
	
	private static void error(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Error during Installation", JOptionPane.ERROR_MESSAGE);
	}
	
	private static boolean createFile(File file)
	{
		try
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
			return true;
		}
		catch (IOException ex)
		{
			error("Failed to create " + file.getAbsolutePath() + ": " + ex.getMessage());
		}
		return false;
	}
	
	private static void download(File dest, String name, String url)
	{
		if (!createFile(dest))
		{
			return;
		}
		
		DyvilInstaller.setDownloadMessage("Downloading " + name + "...");
		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			int length = connection.getContentLength();
			
			try (InputStream is = connection.getInputStream(); OutputStream os = new BufferedOutputStream(new FileOutputStream(dest)))
			{
				byte[] buf = new byte[1024];
				int read = 0;
				int totalRead = 0;
				while ((read = is.read(buf, 0, 1024)) >= 0)
				{
					os.write(buf, 0, read);
					
					totalRead += read;
					DyvilInstaller.setDownloadPercentage((float) totalRead / (float) length);
				}
			}
			catch (IOException ex)
			{
				error("Failed to download " + name + ": " + ex.getMessage());
			}
		}
		catch (Exception ex)
		{
			error("Failed to download " + name + ": " + ex.getMessage());
		}
		
		DyvilInstaller.setDownloadMessage("Successfully Downloaded " + name);
	}
}
