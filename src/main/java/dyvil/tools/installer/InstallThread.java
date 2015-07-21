package dyvil.tools.installer;

import java.io.File;

public class InstallThread extends Thread
{
	private final boolean installDevTools;
	private final boolean installREPL;
	private final String version;
	
	public InstallThread(String version, File installDirectory, boolean devTools, boolean repl)
	{
		this.version = version;
		this.installDevTools = devTools;
		this.installREPL = repl;
	}
	
	@Override
	public void run()
	{
		DyvilInstaller.setInstallMessage("Downloading Dyvil Library v" + this.version);
	}
}
