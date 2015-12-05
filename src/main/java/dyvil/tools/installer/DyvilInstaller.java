package dyvil.tools.installer;

import dyvil.tools.installer.ui.GUI;

import javax.swing.*;

public class DyvilInstaller
{
	public static GUI	gui	= new GUI();

	private final JFileChooser		fileChooser	= new JFileChooser();

	public static void main(String[] args)
	{
		new StartupThread().start();
		gui.setVisible(true);
	}
}
