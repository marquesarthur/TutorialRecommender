package annotationtoool;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import tutorial.SectionElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


public class Viewer extends JFrame
implements ActionListener, WindowListener{

	private JScrollPane scrPane;
	private JPanel questionPane;
	private JTextPane sectionText;
	private JButton nextBtn;	
	private JButton prevBtn;	
	private JProgressBar progressBar;
	private JCheckBox chRelevant;
	private JLabel lblSection;


	private String sectionInfoPath;


	//private List<TutorialSection> Labels = new ArrayList<TutorialSection>();
	private int currentIndex = 0;
	private static Tutorial tutorial;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Viewer frame = new Viewer();					
					frame.setVisible(true);
					tutorial = new Tutorial();
					tutorial.LoadTutorial("tutorials/jodatime.csv", false, true); 
										
					frame.drawSection();
					frame.initProgressBar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void initProgressBar() {
		progressBar.setMaximum(tutorial.sectionElementsPerSection.size());
		progressBar.setValue(currentIndex);
	}


	/**
	 * Create the frame.
	 */
	public Viewer() {
						
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1400, 1000);

		sectionText = new JTextPane();
		scrPane = new JScrollPane(sectionText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		questionPane = new JPanel();
		questionPane.setLayout(new GridLayout(2,1));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrPane, questionPane);
		split.setDividerLocation(800);		

		//Provide minimum sizes for the two components in the split pane.
		Dimension minimumSize = new Dimension(100, 50);
		questionPane.setMinimumSize(minimumSize);
		scrPane.setMinimumSize(minimumSize);

		//Provide a preferred size for the split pane.
		split.setPreferredSize(new Dimension(800, 200));

		sectionText.setAutoscrolls(true);
		sectionText.setContentType("text/html");
		//sectionText.setText("test");
		
		drawQuestions();

		setContentPane(split);
		setTitle("Viewer");
	}



	public void drawSection()
	{	
		if (currentIndex == tutorial.sectionElementsPerSection.size())
		{
			nextBtn.setEnabled(false);
			prevBtn.setEnabled(true);
		}
		else if (currentIndex == -1)
		{
			prevBtn.setEnabled(false);
			nextBtn.setEnabled(true);
		}
		else
		{
			nextBtn.setEnabled(true);
			prevBtn.setEnabled(true);
			sectionText.setText(tutorial.highlightwords(tutorial.highlghtSection(currentIndex), "tutorials/seedwords.csv"));	
			resetQuestions();
		}
	}

	public void resetQuestions()
	{
		chRelevant.setSelected(false);
	}
	
	public void drawQuestions()
	{
		JLabel lblDefinition =  new JLabel();		
		lblDefinition.setText("<html><u>Definition</u> <br> A SECTION is relevant to an ELEMENT if: <br>"+
						"It would help a reader unfamiliar with the corresponding API to decide when and how to use the ELEMENT to complete a programming task; <br><br>" +
						"A SECTION is not relevant to an ELEMENT in all other cases, and in particular:<br>" +
						"- When the element is mentioned for completeness in a list of elements, without being otherwise referenced. <br>"+
						"- When the element is mentioned just to give more information for relevant API element by comparison or by other ways. <br>" +
						"- when the element is mentioned as an example which is not showing the use of the element. <br>" +
						"- the information is structural which can be derived from javadoc or source code. <br>" +
						"- when the information shows static relationship. <br>" +
						"- when information given is too little and can be considered as non-information. <br>"+
						"- if you are not sure whether it is relevant then its cannot be relevant.<html>");
		questionPane.add(lblDefinition);
		
		JPanel buttonPane = new JPanel();
		
		prevBtn =  new JButton("Previous");
		prevBtn.addActionListener(this);
		buttonPane.add(prevBtn);
		
		chRelevant  =  new JCheckBox();
		chRelevant.setText("RELEVANT");
		buttonPane.add(chRelevant);
		
		progressBar = new JProgressBar();	
		buttonPane.add(progressBar);
		
		nextBtn =  new JButton("Next");
		nextBtn.addActionListener(this);
		buttonPane.add(nextBtn);		
		questionPane.add(buttonPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(nextBtn))
		{
			currentIndex++;
			drawSection();
			progressBar.setValue(progressBar.getValue()+1);
		}
		else if(e.getSource().equals(prevBtn))
		{
			currentIndex--;
			drawSection();
			progressBar.setValue(progressBar.getValue()-1);
		}
	}



	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {

	}

	private void saveState() {
		File original =  new File(sectionInfoPath);		
		File renamed = new File("old" + sectionInfoPath);		
		original.renameTo(renamed);

		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(sectionInfoPath), ',');
			for(SectionElement slabel:tutorial.sectionElements)
			{	
				writer.writeNext(new String[]{slabel.getIsLabeled().toString(), slabel.getSection().getTitle(), slabel.getSection().getSubTitle(), 
												slabel.getSection().getHTML(), slabel.getApiElement().getAPIElementName(), slabel.getApiElement().getFqn()});
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println(sectionInfoPath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {

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
}

