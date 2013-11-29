package TermExtraction;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DependencyAnnotation extends JFrame
implements ActionListener, WindowListener{ 

	private JScrollPane scrPane;
	private JPanel questionPane;
	private JTextPane sectionText;
	private JButton nextBtn;	
	private JButton previousBtn;
	
	private JCheckBox chUseLess;
	
	private JCheckBox chPositive;
	private JCheckBox chNegative;
	private JCheckBox chPNDecide;
	
	private JCheckBox chFunctional;
	private JCheckBox chStructural;
	private JCheckBox chDisciptive;
	private JCheckBox chCommand;
	private JCheckBox chInterCLT;
	private JCheckBox chOther;
	private JCheckBox chTypeDecide;
	
	List<CLTDependency> dependencies = new ArrayList<CLTDependency>();

	int current = 0;
	public static void main(String[] args) {
		
		Properties properties = new Properties();
        try {
			properties.load(new FileInputStream("settings"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        final String depdir = properties.getProperty("dep_dir");
        
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DependencyAnnotation frame = new DependencyAnnotation();					
					frame.setVisible(true);
					frame.LoadDependencies(depdir + "nsubjpass.txt"); 
									
					frame.drawSection();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void drawSection() {
		
		if (current == dependencies.size())
		{
			nextBtn.setText("DONE!");
			nextBtn.setEnabled(false);
		}
		else
		{
			sectionText.setText(highlight());
			resetQuestions();
			
			CLTDependency currentDep = dependencies.get(current);
			
			chUseLess.setSelected(currentDep.isUseless());
			
			chPositive.setSelected(currentDep.isPositive());
			chNegative.setSelected(currentDep.isNegative());
			chPNDecide.setSelected(currentDep.isPnDecide());
			
			chFunctional.setSelected(currentDep.isFunctional());
			chStructural.setSelected(currentDep.isStructural());
			chDisciptive.setSelected(currentDep.isDiscriptive());		
			chCommand.setSelected(currentDep.isCommand());
			chInterCLT.setSelected(currentDep.isInterCLT());
			chOther.setSelected(currentDep.isOther());
			chTypeDecide.setSelected(currentDep.isTypeDecide());
			
			sectionText.setToolTipText(dependencies.get(current).getRel());
		}
	}

	protected String highlight()
	{
		String color = "#ffff00";
		String highlightBeginning = "<SPAN style=\"BACKGROUND-COLOR: " + color + "\">";
		String highlightingEnding = "</span>";

		CLTDependency currentDep = dependencies.get(current);
		String sentence = currentDep.getSentence();
		
		String governor = currentDep.getGovernor();
		if(governor.endsWith("y"))
				governor = governor.substring(0, currentDep.getGovernor().length()-1);
		String dependent = currentDep.getDependant();
		if(dependent.endsWith("y"))
			dependent = dependent.substring(0, currentDep.getDependant().length()-1);
		
		sentence = sentence.replaceAll("(?i)" + dependent, highlightBeginning + currentDep.getDependant() + highlightingEnding);
		sentence = sentence.replaceAll("(?i)" + governor, highlightBeginning + currentDep.getGovernor() + highlightingEnding);

		return sentence;
	}

	protected void LoadDependencies(String path) {

		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(path));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if(nextLine.length == 4)
				{
					CLTDependency dep = new CLTDependency(nextLine[0], nextLine[2], nextLine[1], nextLine[3]);
					dependencies.add(dep);
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println(path + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DependencyAnnotation() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 300);

		sectionText = new JTextPane();
		scrPane = new JScrollPane(sectionText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		questionPane = new JPanel();
		questionPane.setLayout(new GridLayout(9,3));
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrPane, questionPane);
		split.setDividerLocation(600);		

		//Provide minimum sizes for the two components in the split pane.
		Dimension minimumSize = new Dimension(100, 50);
		questionPane.setMinimumSize(minimumSize);
		scrPane.setMinimumSize(minimumSize);

		//Provide a preferred size for the split pane.
		split.setPreferredSize(new Dimension(800, 200));

		sectionText.setAutoscrolls(true);
		sectionText.setContentType("text/html");
		sectionText.setText("test");

		drawQuestions();

		setContentPane(split);
		setTitle("Question mapping V2.1");

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}

	protected void saveSection()
	{
		CLTDependency currentDep = dependencies.get(current);
		currentDep.setUseless(chUseLess.isSelected());
		
		currentDep.setPositive(chPositive.isSelected());
		currentDep.setNegative(chNegative.isSelected());
		currentDep.setPnDecide(chPNDecide.isSelected());
		
		currentDep.setFunctional(chFunctional.isSelected());
		currentDep.setStructural(chStructural.isSelected());
		currentDep.setDiscriptive(chDisciptive.isSelected());
		currentDep.setCommand(chCommand.isSelected());
		currentDep.setInterCLT(chInterCLT.isSelected());
		currentDep.setOther(chOther.isSelected());
		currentDep.setTypeDecide(chTypeDecide.isSelected());
	}

	protected void nextSection()
	{
		saveSection();
		current++;
		drawSection();
	}
	
	protected void previousSection()
	{
		saveSection();
		current--;
		drawSection();
	}
	
	public void resetQuestions()
	{
//		chPositive.setSelected(false);
//		chNegative.setSelected(false);
//		chFunctional.setSelected(false);
//		chStructural.setSelected(false);
	}

	public void drawQuestions()
	{	
		// 1st row
		questionPane.add(new JLabel());
		
		chUseLess  =  new JCheckBox();
		chUseLess.setText("Useless example");
		questionPane.add(chUseLess);
		
		questionPane.add(new JLabel());
		
		
		// 2nd row
		chPositive  =  new JCheckBox();
		chPositive.setText("Positive");
		questionPane.add(chPositive);
		
		questionPane.add(new JLabel());
		
		chFunctional  =  new JCheckBox();
		chFunctional.setText("Functional");
		questionPane.add(chFunctional);
	
		
		// 3rd row
		chNegative  =  new JCheckBox();
		chNegative.setText("Negative");
		questionPane.add(chNegative);
		
		questionPane.add(new JLabel());
		
		chStructural  =  new JCheckBox();
		chStructural.setText("Structural");
		questionPane.add(chStructural);
		
		
		// 4th row
		chPNDecide  =  new JCheckBox();
		chPNDecide.setText("Cannot decide");
		questionPane.add(chPNDecide);
		
		questionPane.add(new JLabel());
		
		chDisciptive  =  new JCheckBox();
		chDisciptive.setText("Desciptive");
		questionPane.add(chDisciptive);
		
		// 5th row
		questionPane.add(new JLabel());
		questionPane.add(new JLabel());
		chCommand  =  new JCheckBox();
		chCommand.setText("Command");
		questionPane.add(chCommand);
		
		// 6th row
		questionPane.add(new JLabel());
		questionPane.add(new JLabel());
		chInterCLT  =  new JCheckBox();
		chInterCLT.setText("Inter CLT");
		questionPane.add(chInterCLT);
		
		// 7th row
		questionPane.add(new JLabel());
		questionPane.add(new JLabel());
		chOther  =  new JCheckBox();
		chOther.setText("Other");
		questionPane.add(chOther);
		
		// 8th row
		questionPane.add(new JLabel());
		questionPane.add(new JLabel());
		chTypeDecide  =  new JCheckBox();
		chTypeDecide.setText("Cannot decide");
		questionPane.add(chTypeDecide);
		
		// 9th row
		previousBtn =  new JButton("Previous");
		previousBtn.addActionListener(this);
		questionPane.add(previousBtn);
		
		questionPane.add(new JLabel());
		
		nextBtn =  new JButton("Next");
		nextBtn.addActionListener(this);
		questionPane.add(nextBtn);	
		
			
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		int feedback = JOptionPane.showConfirmDialog((Component)this.getContentPane(), "Do you want to save current progress?");

		if (feedback == 0 || feedback == 1) 			
		{
			if (feedback == 0)
				saveState();
			dispose();
			System.exit(0);
		}

		System.out.println(feedback);

	}

	private void saveState() {
		CSVWriter writer;
		try {
			Properties properties = new Properties();
	        try {
				properties.load(new FileInputStream("settings"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String depdir = properties.getProperty("dep_dir");
	        
			writer = new CSVWriter(new FileWriter(depdir + "dependencydone.csv"), ',');
			for(CLTDependency dep:dependencies)
			{	
				writer.writeNext(new String[]{dep.getGovernor(), dep.getRel(),dep.getDependant(), 
						Boolean.toString(dep.isUseless()),
						Boolean.toString(dep.isPositive()), Boolean.toString(dep.isNegative()), Boolean.toString(dep.isPnDecide()), 
						Boolean.toString(dep.isFunctional()), Boolean.toString(dep.isStructural()), 
						Boolean.toString(dep.isDiscriptive()), Boolean.toString(dep.isCommand()), 
						Boolean.toString(dep.isInterCLT()), Boolean.toString(dep.isOther()), Boolean.toString(dep.isTypeDecide())});
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(nextBtn))
		{
			nextSection();			
		}
		else if (e.getSource().equals(previousBtn))
		{
			previousSection();			
		}
	}
	
}
