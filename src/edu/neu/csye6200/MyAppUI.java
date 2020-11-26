package edu.neu.csye6200;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.*;

import static edu.neu.csye6200.ABSimulation.OCEAN_LENGTH;
import static edu.neu.csye6200.ABSimulation.OCEAN_WIDTH;


/**
 * A simple application example that demonstrates inheritance from an abstract class
 * @author mgmunson, Zongduo Li
 */
public class MyAppUI extends ABApp  {

	private Logger log = Logger.getLogger(MyAppUI.class.getName());
	
	//private JFrame frame; // This is now in ABApp
	private JPanel northPanel;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton pauseBtn;
	
	private JComboBox<String> comboBox;
	private MyCanvas canvas;
	
	private ABSimulation mySim;
	
	/**
	 * Constructor
	 */
	public MyAppUI() {
		log.info("MyAppUI started");

	 	frame.setSize(900, 950);
		frame.setTitle("MyAppUI");
		
		menuMgr.createDefaultActions(); // Set up default menu items
		
		initSim(); // Initialize the sim

		showUI(); // Cause the Swing Dispatch thread to display the JFrame
		// make the subscription
		mySim.addObserver(canvas); // Allow the panel to hear about simulation events
	}

	/*
	 * Initialize the simulation
	 */
	private void initSim() {
		mySim = new ABSimulation();
		mySim.ocean[19][19].setOilCoverage(100);
		mySim.ocean[19][20].setOilCoverage(100);
		mySim.ocean[20][19].setOilCoverage(100);
		mySim.ocean[20][20].setOilCoverage(100);
		mySim.ocean[8][8].setOilCoverage(100);
		mySim.ocean[35][35].setOilCoverage(100);

	}
	
	
	// Create a north panel with buttons
	public JPanel getNorthPanel() {
		northPanel = new JPanel(); // Create a small canvas
		northPanel.setLayout(new FlowLayout()); // Flow controls
		
		startBtn = new JButton("Start");
		//startBtn.addActionListener(this); // Make my application listen to the button
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("start pressed");
				mySim.startSim();

			}
		});
		
		stopBtn = new JButton("Stop");
		//stopBtn.addActionListener(this);
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("stop pressed");
				mySim.stopSim();
			}
		});
		
		pauseBtn = new JButton("Pause");
		pauseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("pause pressed");
				mySim.pauseSim();
			}
		});
		
		
		comboBox = new JComboBox();
		comboBox.addItem("Simple");
		comboBox.addItem("Medium");
		comboBox.addItem("Complex");
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String item = e.getItem().toString();
				switch(item){
					case "Simple":
						//In simple mode, the ship can only move at 45°
						mySim.simpleSim();
						break;
					case "Medium":
						//In medium mode, the ship can move 45 ° and left and right
						mySim.mediumSim();
						break;
					case "Complex":
						//In complex mode, the ship can move at 45°, left and right, up and down
						mySim.complexSim();
						break;
				}
			}
		});

		JRadioButton radioButton = new JRadioButton("show coverage");
		radioButton.setSelected(true);
		radioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int state = e.getStateChange();
				switch (state){
					case ItemEvent.SELECTED:
						//Show oil coverage of each grid
						mySim.coverageShowFlag = true;
						break;
					case ItemEvent.DESELECTED:
							//Do not show
						mySim.coverageShowFlag = false;
						break;

				}
			}
		});
		
	
	// Lay out the panel	
		northPanel.add(startBtn);
		northPanel.add(pauseBtn);
		northPanel.add(stopBtn);
		
		northPanel.add(new JLabel("Rule:"));
		northPanel.add(comboBox);
		northPanel.add(radioButton);
		
		//mainPanel.setBackground(Color.Blue);
		
		return northPanel;
	}
	
	/**
	 * Create a center panel that has a drawable JPanel canvas
	 */
	@Override
	public JPanel getCenterPanel() {
		canvas = new MyCanvas(); // Build the drawable panel
		return canvas;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
         ///MyAppUI myApp = new MyAppUI();
	  new MyAppUI();
      System.out.println("MyAppIO is exiting !!!!!!!!!!!!!!");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}




}
