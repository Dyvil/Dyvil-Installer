package dyvil.tools.installer;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

public class DyvilInstaller
{
	public static DyvilInstaller instance = new DyvilInstaller();
	
	private JFrame		frame;
	public JLabel		labelVersion;
	public JComboBox	comboBoxVersion;
	public JCheckBox	checkBoxInstallDevTools;
	public JCheckBox	checkBoxInstallREPL;
	public JButton		buttonInstall;
	public JButton		buttonCancel;
	public JProgressBar	progressBarInstall;
	public JProgressBar	progressBarDownload;
	public JLabel		labelInstallDir;
	public JTextField	textFieldInstallDir;
	public JButton		buttonChooseDir;
	
	/**
	 * @wbp.nonvisual location=182,301
	 */
	private final JFileChooser fileChooser = new JFileChooser();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		new StartupThread().start();
		instance.frame.setVisible(true);
	}
	
	/**
	 * Create the application.
	 */
	public DyvilInstaller()
	{
		this.initialize();
	}
	
	public static void setInstallDirectory(String directory)
	{
		instance.textFieldInstallDir.setText(directory);
	}
	
	public static void setDownloadMessage(String message)
	{
		instance.progressBarDownload.setString(message);
	}
	
	public static void setInstallMessage(String message)
	{
		instance.progressBarInstall.setString(message);
	}
	
	public static void setDownloadPercentage(float percentage)
	{
		instance.progressBarDownload.setValue((int) (percentage * 100));
	}
	
	public static void setInstallPercentage(float percentage)
	{
		instance.progressBarInstall.setValue((int) (percentage * 100));
	}
	
	/**
	 * Initialize the contents of the frame.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private void initialize()
	{
		this.fileChooser.setAcceptAllFileFilterUsed(false);
		this.fileChooser.setDialogTitle("Select Installation Directory");
		this.frame = new JFrame();
		this.frame.setResizable(false);
		this.frame.setTitle("Dyvil Installer");
		this.frame.setBounds(16, -22, 400, 234);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getContentPane().setLayout(null);
		
		this.labelVersion = new JLabel("Dyvil Version:");
		this.labelVersion.setBounds(6, 44, 100, 16);
		this.frame.getContentPane().add(this.labelVersion);
		
		this.comboBoxVersion = new JComboBox();
		this.comboBoxVersion.setBounds(224, 40, 170, 27);
		this.frame.getContentPane().add(this.comboBoxVersion);
		
		this.checkBoxInstallDevTools = new JCheckBox("Install the Dyvil Interactive REPL");
		this.checkBoxInstallDevTools.setBounds(6, 100, 288, 23);
		this.frame.getContentPane().add(this.checkBoxInstallDevTools);
		
		this.checkBoxInstallREPL = new JCheckBox("Install Dyvil Development Tools");
		this.checkBoxInstallREPL.setBounds(6, 72, 288, 23);
		this.frame.getContentPane().add(this.checkBoxInstallREPL);
		
		this.buttonInstall = new JButton("Install");
		this.buttonInstall.addActionListener(this::install);
		this.buttonInstall.setBounds(6, 177, 146, 29);
		this.frame.getContentPane().add(this.buttonInstall);
		
		this.buttonCancel = new JButton("Cancel");
		this.buttonCancel.addActionListener(e -> System.exit(1));
		this.buttonCancel.setBounds(248, 177, 146, 29);
		this.frame.getContentPane().add(this.buttonCancel);
		
		this.progressBarInstall = new JProgressBar();
		this.progressBarInstall.setMaximum(100);
		this.progressBarInstall.setStringPainted(true);
		this.progressBarInstall.setBounds(6, 150, 388, 20);
		this.frame.getContentPane().add(this.progressBarInstall);
		
		this.progressBarDownload = new JProgressBar();
		this.progressBarDownload.setMaximum(100);
		this.progressBarDownload.setBounds(6, 127, 388, 20);
		this.frame.getContentPane().add(this.progressBarDownload);
		
		this.labelInstallDir = new JLabel("Installation Path");
		this.labelInstallDir.setBounds(6, 12, 111, 16);
		this.frame.getContentPane().add(this.labelInstallDir);
		
		this.textFieldInstallDir = new JTextField();
		this.textFieldInstallDir.setBounds(117, 6, 232, 28);
		this.frame.getContentPane().add(this.textFieldInstallDir);
		this.textFieldInstallDir.setColumns(10);
		
		this.buttonChooseDir = new JButton("...");
		this.buttonChooseDir.addActionListener(e -> {
			File file = new File(this.textFieldInstallDir.getText());
			this.fileChooser.setCurrentDirectory(file);
			int ret = this.fileChooser.showOpenDialog(this.frame);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				file = this.fileChooser.getSelectedFile();
				this.textFieldInstallDir.setText(file.getAbsolutePath());
			}
		});
		this.buttonChooseDir.setBounds(349, 7, 45, 29);
		this.frame.getContentPane().add(this.buttonChooseDir);
		
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
	
	private void install(ActionEvent event)
	{
		Object version = this.comboBoxVersion.getSelectedItem();
		if (version == null)
		{
			this.progressBarInstall.setString("Installation Failed - No Version selected");
			return;
		}
		
		this.progressBarDownload.setStringPainted(true);
		
		boolean devTools = this.checkBoxInstallDevTools.isSelected();
		boolean repl = this.checkBoxInstallREPL.isSelected();
		File directory = new File(this.textFieldInstallDir.getText());
		new InstallThread(version.toString(), directory, devTools, repl).start();
	}
}
