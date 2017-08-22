package fileDealer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class Framework extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final JLabel status = new JLabel("Current Folder: Not chosen. Please click on \"Choose Folder\"",SwingConstants.CENTER);
	JButton chooseFolder = new JButton("Choose Folder");
	JButton close = new JButton("Close");
	File rootDirectory = null;
	long tStart = System.currentTimeMillis();
	final int MAX_TO_SHOW = 10;
	final int MAX_LEVEL = 6;
	Chart2 pieChart;
	JTable tableOfDuplicates;

	
	
	public static void main(String args[])
	{
		Framework mainWindow = new Framework();
	}
	
	public Framework()
	{
		chooseFolder.addActionListener(new ChooseFolderListener());
		close.addActionListener(new CloseListener());
		generateView(true);
	}
	
	public void generateView(boolean start)
	{
		this.getContentPane().removeAll();
		status.setPreferredSize(new Dimension(300, 40));
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(chooseFolder);
		controlPanel.add(close);
		
		if(start)
		{
			controlPanel.add(status);
			this.getContentPane().add(controlPanel);
		}
		else
		{
			pieChart = new Chart2(MAX_LEVEL, MAX_TO_SHOW, this.status);
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int chartSize = screenSize.getWidth()<screenSize.getHeight() ? (int) screenSize.getWidth() : (int) screenSize.getHeight();
			pieChart.setPreferredSize(new Dimension(chartSize - 150, chartSize - 150));
			//mainPanel.add(controlPanel);
			mainPanel.add(pieChart);
			
			JPanel fullWindowPanel = new JPanel();
			fullWindowPanel.setLayout(new BoxLayout(fullWindowPanel, BoxLayout.Y_AXIS));
			//fullWindowPanel.add(mainPanel);
			fullWindowPanel.add(pieChart);
			fullWindowPanel.add(status);
			
		    JScrollPane vertical;
		    vertical = new JScrollPane(tableOfDuplicates);
		    vertical.setPreferredSize(fullWindowPanel.getPreferredSize());
		    vertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    vertical.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		    

			
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.addTab("Chart Tab", fullWindowPanel);
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
			
			tabbedPane.addTab("Duplicates Tab", vertical);
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

			this.getContentPane().add(tabbedPane);


			
			
			//this.getContentPane().add(fullWindowPanel);
		}
		this.setTitle("File Dealer");
		//this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack(); 
		this.setVisible(true);
	}

	private class CloseListener implements ActionListener{
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        System.exit(0);
	    }
	}
	
	 private class ChooseFolderListener implements ActionListener{
		 @Override
		    public void actionPerformed(ActionEvent e) {

				JFileChooser directoryChooser = new JFileChooser();
			    directoryChooser.setCurrentDirectory(new java.io.File("."));
			    directoryChooser.setDialogTitle("choosertitle");
			    directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    directoryChooser.setAcceptAllFileFilterUsed(false);

			    if (directoryChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			      rootDirectory = directoryChooser.getSelectedFile();
			      Thread queryThread = new Thread() {
						public void run() {
							runScan();
						}
					};
					queryThread.start();
			    } else {
			      System.out.println("No Selection ");
			    }
		    }
	 }
	
	// Called from non-UI thread
	private void runScan() {
		if(rootDirectory!=null)
		{
			chooseFolder.setEnabled(false); 
			ArrayList<Directory> directoriesByABC = new ArrayList<Directory>();
			ArrayList<Directory> directoriesForDuplicates = new ArrayList<Directory>();
			ArrayList<Directory> directoriesByLevel = new ArrayList<Directory>();
			ArrayList<LevelCounter> levelCounter = new ArrayList<LevelCounter>();
			status.setText("Calculating");
			tStart = System.currentTimeMillis();
			Directory root = new Directory(rootDirectory.getAbsolutePath(), rootDirectory.getName(), 0, null, true);
			scanSystem(root, directoriesByABC, directoriesForDuplicates, directoriesByLevel, 0, MAX_LEVEL, levelCounter);
			Collections.sort(directoriesByLevel, new LevelComparator());
			Collections.sort(directoriesByLevel.subList(0,levelCounter.get(0).getCount()));
			status.setText("Found " + directoriesByABC.size() + " files, directories and sub directories.");
			chooseFolder.setEnabled(true);
			findDuplicates(directoriesForDuplicates);
			generateView(false);
			pieChart.updateChart(directoriesByLevel, levelCounter.get(0).getCount());
		}
	}

	final int NUMBER_OF_COLUMNS = DuplicatesTableRenderer.NUMBER_OF_COLUMNS;
	
	public void findDuplicates(ArrayList<Directory> directories)
	{
		StringBuilder fullPath = new StringBuilder("Full Path");
		String[] columnNames = {"Group", "Name", "Size", "Full Path to File/Directory",  "Directory/File"};
		if(columnNames.length != NUMBER_OF_COLUMNS)
		{
			return ;
		}
		ArrayList<DuplicatesDirs> duplicatesDirs = new ArrayList<DuplicatesDirs>();
		int group = 0;
		Collections.sort(directories);	//sorting by size
		if(directories != null)
		{
			Directory anchor;
			long currentSize = -1;
			boolean match = false;
			ArrayList<Integer> listOfFound = new ArrayList<Integer>();
			while(!directories.isEmpty())
			{
				listOfFound.clear();
				anchor = directories.get(0);
				currentSize = anchor.getSize();
				int i=1;
				match = false;
				listOfFound.add(0);
				while((directories.size()>i)&&(directories.get(i).getSize() == currentSize))
				{
					if(!anchor.isAncestorDirectory(directories.get(i), true))
					{
						if(anchor.getName().equals(directories.get(i).getName()))
						{
							if(match == false)
							{
								match = true;
								group++;
								String isDirectory = anchor.isDirectory() ? "Directory" : "File";
								DuplicatesDirs dd = new DuplicatesDirs(anchor.getName(), anchor.getSize(), anchor.getFullPath(), isDirectory, group);
								duplicatesDirs.add(dd);
							}
							Directory duplicate = directories.get(i);
							String isDirectory = duplicate.isDirectory() ? "Directory" : "File";
							DuplicatesDirs dd = new DuplicatesDirs(duplicate.getName(), duplicate.getSize(), duplicate.getFullPath(), isDirectory, group);
							duplicatesDirs.add(dd);
							listOfFound.add(i);
						}
					}
					i++;
				}
				match = false;
				for(int j=listOfFound.size() - 1; j >= 0; j--)
				{
					int removeLocation = listOfFound.get(j);
					directories.remove(removeLocation);
				}
			}
		}
		Object[][] data = new Object[duplicatesDirs.size()][NUMBER_OF_COLUMNS];
		for(int i=0;i<duplicatesDirs.size();i++)
		{
			data[i][0] = duplicatesDirs.get(i).getGroup();
			data[i][1] = duplicatesDirs.get(i).getName();
			data[i][2] = duplicatesDirs.get(i).getSize();
			data[i][3] = duplicatesDirs.get(i).getFullPath();
			data[i][4] = duplicatesDirs.get(i).isDirectory();
		}
		//tableOfDuplicates = new JTable(data, columnNames);
		
		DuplicatesTableRenderer myRenderer = new DuplicatesTableRenderer();

        DefaultTableModel defModel = new DefaultTableModel(data, columnNames);
        tableOfDuplicates = new JTable(defModel);
        tableOfDuplicates.setDefaultRenderer(Object.class, myRenderer);
        tableOfDuplicates.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	public void scanSystem(Directory currectDirectory, ArrayList<Directory> directoriesByABC, ArrayList<Directory> directoriesForDuplicates, ArrayList<Directory> directoriesByLevel, int level, int maxLevel, ArrayList<LevelCounter> levelCounter) {
		File directory = new File(currectDirectory.getFullPath());
		boolean flagUpdatingUI = false;	    //makes the decision to update UI 
		File[] listOfFiles = directory.listFiles();
		long totalSize = 0;
		if(listOfFiles != null)
		{
			for (File file : listOfFiles) {
				if(levelCounter.size()<=level)
					levelCounter.add(new LevelCounter(level));
				levelCounter.get(level).incrementCount();
				
				if (file.isFile()) {
					totalSize += file.length();
					Directory newFile = new Directory(file.getAbsolutePath(), file.getName(), level, currectDirectory, false);
					currectDirectory.addToSubDirectories(newFile);
					directoriesByABC.add(newFile);
					directoriesForDuplicates.add(newFile);
					directoriesByLevel.add(newFile);
					newFile.setSize(file.length());
				}
				if (file.isDirectory()) {
					Directory newDirectory = new Directory(file.getAbsolutePath(), file.getName(), level, currectDirectory, true);
					currectDirectory.addToSubDirectories(newDirectory);
					directoriesByABC.add(newDirectory);
					directoriesForDuplicates.add(newDirectory);
					directoriesByLevel.add(newDirectory);
					flagUpdatingUI = updateUI(flagUpdatingUI);
					/*if(level<maxLevel){scanSystem(newDirectory, directoriesByABC, directoriesByLevel, level+1, maxLevel, levelCounter);}
					else{newDirectory.setSize(FileUtils.sizeOfDirectory(file));}*/ //if we want to scan only up some level.
					scanSystem(newDirectory, directoriesByABC, directoriesForDuplicates, directoriesByLevel, level+1, maxLevel, levelCounter);
					totalSize += newDirectory.getSize();
				}
			}
			currectDirectory.setSize(totalSize);
		}
	}

	public boolean updateUI(boolean flagUpdatingUI)
	{
		flagUpdatingUI = (System.currentTimeMillis() - tStart > 1000) ? true : false;
		tStart = flagUpdatingUI ? System.currentTimeMillis() : tStart; 
		if(flagUpdatingUI)
		{
			if(status.getText().length()>20)
			{
				status.setText("Calculating");
			}
			else
			{
				status.setText(status.getText() + ".");
			}
		}
		return flagUpdatingUI;
	}
}
