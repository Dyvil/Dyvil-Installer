package dyvil.tools.installer.ui;

import dyvil.tools.installer.task.InstallThread;
import dyvil.tools.installer.version.Version;
import dyvil.tools.installer.task.VersionSelectThread;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;

public class GUI extends JFrame
{
	public static final Color TRANSPARENT = new Color(0x0, true);

	private JPanel mainPanel;

	private JButton            buttonPathSelect;
	private JComboBox<Version> comboBoxVersion;
	private JCheckBoxTree      tree;
	private JProgressBar       progressBarDownload;
	private JProgressBar       progressBarInstallation;
	private JButton            buttonInstall;
	private JButton            buttonCancel;
	private JTextField         textFieldInstallationPath;

	private JFileChooser fileChooser = new JFileChooser();
	private DefaultTreeModel       treeModel;
	private DefaultMutableTreeNode rootNode;

	static
	{
		try
		{
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}

	public GUI()
	{
		this.setContentPane(mainPanel);
		this.setTitle("Dyvil Installer");
		this.setBounds(100, 100, 500, 400);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.buttonInstall.addActionListener(this::install);
		this.buttonCancel.addActionListener(this::exit);
		this.buttonPathSelect.addActionListener(this::selectInstallDirectory);
		this.comboBoxVersion.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				displayVersion((Version) e.getItem());
			}
		});

		this.rootNode = new DefaultMutableTreeNode("Select a Version");
		this.treeModel = new DefaultTreeModel(this.rootNode);
		this.tree.setModel(this.treeModel);
	}

	private void displayVersion(Version item)
	{
		new VersionSelectThread(item).start();
	}

	private void selectInstallDirectory(ActionEvent actionEvent)
	{
		File file = new File(this.textFieldInstallationPath.getText());
		this.fileChooser.setCurrentDirectory(file);
		int ret = this.fileChooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			file = this.fileChooser.getSelectedFile();
			this.textFieldInstallationPath.setText(file.getAbsolutePath());
		}
	}

	private void exit(ActionEvent e)
	{
		System.exit(1);
	}

	public void addVersion(Version version)
	{
		this.comboBoxVersion.addItem(version);
	}

	public void setInstallDirectory(String path)
	{
		this.textFieldInstallationPath.setText(path);
	}

	public void setDownloadMessage(String message)
	{
		this.progressBarDownload.setString(message);
	}

	public void setInstallMessage(String message)
	{
		this.progressBarInstallation.setString(message);
	}

	public void setDownloadPercentage(float percentage)
	{
		this.progressBarDownload.setValue((int) (percentage * 100));
	}

	public void setInstallPercentage(float percentage)
	{
		this.progressBarInstallation.setValue((int) (percentage * 100));
	}

	public void install(ActionEvent event)
	{
		Object version = this.comboBoxVersion.getSelectedItem();
		if (version == null)
		{
			this.progressBarInstallation.setString("Installation Failed - No Version selected");
			return;
		}

		this.progressBarDownload.setStringPainted(true);

		File directory = new File(this.textFieldInstallationPath.getText());

		int components = 0;
		new InstallThread((Version) version, directory, components).start();
	}

	public void setTreeNode(DefaultMutableTreeNode treeNode)
	{
		this.rootNode = treeNode;
		this.treeModel.setRoot(treeNode);

		this.tree.repaint();
	}
}
