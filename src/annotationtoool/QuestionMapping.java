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


public class QuestionMapping extends JFrame
implements ActionListener, WindowListener{

	private JScrollPane scrPane;
	private JPanel questionPane;
	private JTextPane sectionText;
	private JButton nextBtn;	
	private JProgressBar progressBar;
	private JCheckBox chRelevant;
	private JLabel lblSection;
	private JLabel lblTest;


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
					String tutorialPath = "tut_annotation.csv";
					QuestionMapping frame = new QuestionMapping();					
					frame.setVisible(true);
					tutorial = new Tutorial();					
					tutorial.LoadTutorial(tutorialPath, false, false); 
					
					frame.sectionInfoPath = tutorialPath; 
//					frame.LoadSectionInfo();										
					frame.drawSection();
					frame.initProgressBar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void initProgressBar() {
		progressBar.setMaximum(tutorial.sectionElements.size());
		progressBar.setValue(currentIndex);
	}

//	protected void LoadSectionInfo() {
//		CSVReader reader;
//		try {
//			reader = new CSVReader(new FileReader(sectionInfoPath), ',');
//
//			String[] nextLine;
//			while ((nextLine = reader.readNext()) != null) {	
//				TutorialSection  s = new TutorialSection(nextLine[0].equals("labeled"), nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5], nextLine[6]);					
//				Labels.add(s);
//			}
//			reader.close();
//		} catch (FileNotFoundException e) {
//			System.out.println(sectionInfoPath + " is not a valid path.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	/**
	 * Create the frame.
	 */
	public QuestionMapping() {
						
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
		sectionText.setText("test");
		
		drawQuestions();

		setContentPane(split);
		setTitle("Question mapping V2.1");

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}


	protected void saveSection()
	{
		SectionElement current = tutorial.sectionElements.get(currentIndex);
		current.setIsLabeled(true);
		current.setLabel(chRelevant.isSelected());		
		current.saveToFile("result.csv");
	}

	protected void nextSection()
	{
		saveSection();
		currentIndex++;
		drawSection();
	}

	public void drawSection()
	{	
		while (currentIndex<tutorial.sectionElements.size() && tutorial.sectionElements.get(currentIndex).getIsLabeled())		
		{
			currentIndex++;			
		}

		if (currentIndex == tutorial.sectionElements.size())
		{
			nextBtn.setText("DONE!");
			nextBtn.setEnabled(false);
		}
		else
		{
			sectionText.setText(tutorial.highlightElement(currentIndex));	
			sectionText.setToolTipText(tutorial.sectionElements.get(currentIndex).getApiElement().getFqn());
			resetQuestions();
		}
		
		if (currentIndex >=10)
			lblTest.setText("");
	}

	public void resetQuestions()
	{
		chRelevant.setSelected(false);
	}
	public void drawQuestions()
	{
		JPanel infoPane = new JPanel();
		infoPane.setLayout(new GridLayout(2,1));
		
		lblTest = new JLabel();
		lblTest.setText("<html><head><style type=\"text/css\">h1 {color:red;} h1 {text-align:center}</style></head><body><h1 align=\"center\">TEST</h1></body></html>");
		
		infoPane.add(lblTest);
		
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
		infoPane.add(lblDefinition);
		
		questionPane.add(infoPane);
		
		JPanel buttonPane = new JPanel();
		
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
			nextSection();
			progressBar.setValue(progressBar.getValue()+1);
		}
	}



	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		//SaveProgressDialogBox.showDialog((JFrame)this);
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
		File original =  new File(sectionInfoPath);		
		File renamed = new File("old" + sectionInfoPath);		
		original.renameTo(renamed);

		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(sectionInfoPath), ',');
			for(SectionElement slabel:tutorial.sectionElements)
			{	
				writer.writeNext(new String[]{slabel.getIsLabeled().toString(), slabel.getSection().getTitle(), slabel.getSection().getSubTitle(), 
												slabel.getSection().getHTML(), slabel.getApiElement().getAPIElementName(), slabel.getApiElement().getFqn(), 
												slabel.getApiElement().getKind(), slabel.getLabel().toString()});
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

