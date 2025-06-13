package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Label;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SelectionPage {

	static Logger log = LogManager.getLogger(SelectionPage.class);
	
	static ArrayList<String> availableVideos;
	static float speed;
	public JFrame frame;

	
	JButton streamButton = new JButton("Start stream");
	JComboBox<String> titleChoice = new JComboBox<>();
	JComboBox<String> qualityChoice = new JComboBox<>();
	JComboBox<String> filetypeChoice = new JComboBox<>();
	JComboBox<String> protocolChoice = new JComboBox<>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SelectionPage window = new SelectionPage(availableVideos,speed);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SelectionPage(ArrayList<String> videos, float clientSpeed) {
		SelectionPage.availableVideos=videos;
		SelectionPage.speed=clientSpeed;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 640, 475);
		frame.getContentPane().setBackground(new Color(254, 203, 214));
		frame.getContentPane().setLayout(null);
		
		
		titleChoice.setForeground(new Color(255, 0, 128));
		titleChoice.setFont(new Font("Serif", Font.BOLD, 15));
		titleChoice.setBounds(33, 240, 211, 26);
		frame.getContentPane().add(titleChoice);
		titleChoice.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
		
		
		qualityChoice.setForeground(new Color(255, 0, 128));
		qualityChoice.setFont(new Font("Serif", Font.BOLD, 15));
		qualityChoice.setBounds(283, 240, 139, 26);
		qualityChoice.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
		frame.getContentPane().add(qualityChoice);
		
		
		
		ArrayList<String> titles = new ArrayList<String>();
		for (int i=0;i<availableVideos.size();i++) {
			if(!titles.contains(availableVideos.get(i).substring(0,availableVideos.get(i).lastIndexOf("-")))) {
				titles.add(availableVideos.get(i).substring(0,availableVideos.get(i).lastIndexOf("-")));
			}
		}
		
		for (int i=0;i<titles.size();i++) {
			titleChoice.addItem(titles.get(i));
		}
		
		setQualityOptions(titleChoice,qualityChoice);
		
		
		streamButton.setForeground(new Color(183, 0, 91));
		streamButton.setBackground(new Color(255, 213, 234));
		streamButton.setFont(new Font("Serif", Font.BOLD, 25));
		streamButton.setBounds(359, 350, 227, 46);
		UIManager.put("Button.select", (new Color(183, 0, 91)));
		frame.getContentPane().add(streamButton);
		
		
		
		
		protocolChoice.setForeground(new Color(255, 0, 128));
		protocolChoice.setFont(new Font("Serif", Font.BOLD, 15));
		protocolChoice.setBounds(33, 338, 170, 26);
		frame.getContentPane().add(protocolChoice);
		protocolChoice.addItem("Auto-choice");
		protocolChoice.addItem("TCP");
		protocolChoice.addItem("UDP");
		protocolChoice.addItem("RTP/UDP");
		protocolChoice.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
		
		
	
		
		
		filetypeChoice.setForeground(new Color(255, 0, 128));
		filetypeChoice.setFont(new Font("Serif", Font.BOLD, 15));
		filetypeChoice.setBounds(467, 240, 129, 26);
		filetypeChoice.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
		frame.getContentPane().add(filetypeChoice);
		
		Label welcomeLabel = new Label("Your bandwidth is:");
		welcomeLabel.setForeground(new Color(183, 0, 91));
		welcomeLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 21));
		welcomeLabel.setAlignment(Label.CENTER);
		welcomeLabel.setBounds(10, 10, 268, 53);
		frame.getContentPane().add(welcomeLabel);
		
		String formattedSpeed = String.format( "%.02f", speed );
		JLabel speedLabel = new JLabel(formattedSpeed+" Mbps");
		speedLabel.setForeground(new Color(255, 0, 128));
		speedLabel.setFont(new Font("Lucida Calligraphy", Font.BOLD, 40));
		speedLabel.setHorizontalAlignment(SwingConstants.LEFT);
		speedLabel.setIcon(new ImageIcon(SelectionPage.class.getResource("/res/SpeedTest.png")));
		speedLabel.setBounds(33, 35, 535, 156);
		frame.getContentPane().add(speedLabel);
		
		JLabel speedLabel_1 = new JLabel(formattedSpeed+" Mbps");
		speedLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		speedLabel_1.setForeground(new Color(183, 0, 91));
		speedLabel_1.setFont(new Font("Lucida Calligraphy", Font.BOLD, 40));
		speedLabel_1.setBounds(163, 35, 384, 156);
		frame.getContentPane().add(speedLabel_1);
		
		Label welcomeLabel_1 = new Label("1. Select your file title");
		welcomeLabel_1.setForeground(new Color(183, 0, 91));
		welcomeLabel_1.setFont(new Font("Serif", Font.PLAIN, 18));
		welcomeLabel_1.setBounds(31, 210, 172, 26);
		frame.getContentPane().add(welcomeLabel_1);
		
		Label welcomeLabel_1_1 = new Label("2. Select resolution");
		welcomeLabel_1_1.setForeground(new Color(183, 0, 91));
		welcomeLabel_1_1.setFont(new Font("Serif", Font.PLAIN, 18));
		welcomeLabel_1_1.setBounds(283, 210, 151, 26);
		frame.getContentPane().add(welcomeLabel_1_1);
		
		Label welcomeLabel_1_1_1 = new Label("3. Select file type");
		welcomeLabel_1_1_1.setForeground(new Color(183, 0, 91));
		welcomeLabel_1_1_1.setFont(new Font("Serif", Font.PLAIN, 18));
		welcomeLabel_1_1_1.setBounds(467, 208, 139, 26);
		frame.getContentPane().add(welcomeLabel_1_1_1);
		
		Label welcomeLabel_1_2 = new Label("4. Select streaming protocol");
		welcomeLabel_1_2.setForeground(new Color(183, 0, 91));
		welcomeLabel_1_2.setFont(new Font("Serif", Font.PLAIN, 18));
		welcomeLabel_1_2.setBounds(33, 306, 211, 26);
		frame.getContentPane().add(welcomeLabel_1_2);
		filetypeChoice.addItem("mp4");
		filetypeChoice.addItem("avi");
		filetypeChoice.addItem("mkv");
		
		titleChoice.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				qualityChoice.removeAllItems();
				setQualityOptions(titleChoice,qualityChoice);
			}
		});
		
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	void setQualityOptions(JComboBox<String>  tc, JComboBox<String> qualityChoice){
		//By default, the files are sorted alphabetically. That makes the number order ugly.
		//This helper array parses them as integers, so they can be sorted normally.
		ArrayList<Integer> qualities = new ArrayList<Integer>();
		for (int i=0;i<availableVideos.size();i=i+3) {
			if((availableVideos.get(i).substring(0,availableVideos.get(i).lastIndexOf("-"))).equals(tc.getSelectedItem())) {
				qualities.add(Integer.parseInt(availableVideos.get(i).substring(availableVideos.get(i).lastIndexOf("-")+1,availableVideos.get(i).lastIndexOf("p."))));
			}
		}
		
		//sort descending so that highest possible quality (the recommended one) is first
		Collections.sort(qualities, Collections.reverseOrder());
		for (int i=0;i<qualities.size();i++) {
			qualityChoice.addItem(qualities.get(i)+"p");
		}
	}
	
	public String getTitle(String t) {
		return t;
	}
}

