package dyvil.tools.installer.task;

import dyvil.tools.installer.DyvilInstaller;
import dyvil.tools.installer.version.Distribution;
import dyvil.tools.installer.version.Version;

import javax.swing.tree.DefaultMutableTreeNode;

public class VersionSelectThread extends Thread
{
	protected final Version version;

	public VersionSelectThread(Version version)
	{
		this.version = version;
	}

	@Override
	public void run()
	{
		this.version.readData();

		Distribution distribution = this.version.getDistributionData();
		if (distribution == null)
		{
			return;
		}

		distribution.resolve();
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(distribution.getDistributionName());
		DyvilInstaller.gui.setTreeNode(treeNode);
	}
}
