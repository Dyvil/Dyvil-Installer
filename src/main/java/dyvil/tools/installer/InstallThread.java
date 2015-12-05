package dyvil.tools.installer;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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
		
		DyvilInstaller.gui.setDownloadMessage("Downloading " + name + "...");
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
					DyvilInstaller.gui.setDownloadPercentage((float) totalRead / (float) length);
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
		
		DyvilInstaller.gui.setDownloadMessage("Successfully Downloaded " + name);
	}
}
