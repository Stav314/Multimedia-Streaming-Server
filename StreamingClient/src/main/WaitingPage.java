package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Label;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import javax.swing.JProgressBar;

public class WaitingPage {

	public JFrame frame;
	public JProgressBar progressBar = new JProgressBar();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WaitingPage waiting = new WaitingPage();
					waiting.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WaitingPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(254, 203, 214));
		frame.getContentPane().setLayout(null);
		
		Label welcomeLabel = new Label("You are using:");
		welcomeLabel.setBounds(-1, 35, 180, 33);
		welcomeLabel.setAlignment(Label.CENTER);
		welcomeLabel.setForeground(new Color(183, 0, 91));
		welcomeLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 21));
		frame.getContentPane().add(welcomeLabel);
		
		JLabel titleLabel = new JLabel("Stav's");
		titleLabel.setFont(new Font("Lucida Calligraphy", Font.BOLD | Font.ITALIC, 58));
		titleLabel.setForeground(new Color(255, 0, 128));
		titleLabel.setBounds(14, 63, 233, 89);
		frame.getContentPane().add(titleLabel);
		
		JLabel titleLabel2 = new JLabel("Streaming Server");
		titleLabel2.setForeground(new Color(255, 0, 128));
		titleLabel2.setFont(new Font("Lucida Calligraphy", Font.BOLD | Font.ITALIC, 58));
		titleLabel2.setBounds(28, 83, 574, 144);
		frame.getContentPane().add(titleLabel2);
		
		JLabel titleLabelShadow = new JLabel("Stav's");
		titleLabelShadow.setForeground(new Color(183, 0, 91));
		titleLabelShadow.setFont(new Font("Lucida Calligraphy", Font.BOLD | Font.ITALIC, 58));
		titleLabelShadow.setBounds(10, 63, 233, 89);
		frame.getContentPane().add(titleLabelShadow);
		
		JLabel titleLabel2Shadow = new JLabel("Streaming Server");
		titleLabel2Shadow.setForeground(new Color(183, 0, 91));
		titleLabel2Shadow.setFont(new Font("Lucida Calligraphy", Font.BOLD | Font.ITALIC, 58));
		titleLabel2Shadow.setBounds(24, 83, 574, 144);
		frame.getContentPane().add(titleLabel2Shadow);
		
		Label speedTestLabel = new Label("Please hang on while we perform a short speed test.");
		speedTestLabel.setForeground(new Color(183, 0, 91));
		speedTestLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 21));
		speedTestLabel.setAlignment(Label.CENTER);
		speedTestLabel.setBounds(167, 229, 447, 33);
		frame.getContentPane().add(speedTestLabel);
		
		Label speedTestLabel2 = new Label("Thank you! :)");
		speedTestLabel2.setForeground(new Color(183, 0, 91));
		speedTestLabel2.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 21));
		speedTestLabel2.setAlignment(Label.RIGHT);
		speedTestLabel2.setBounds(422, 268, 180, 33);
		frame.getContentPane().add(speedTestLabel2);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setIcon(new ImageIcon(WaitingPage.class.getResource("/res/UniWA.png")));

		lblNewLabel.setBounds(-1, 255, 180, 181);
		frame.getContentPane().add(lblNewLabel);
		
		
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(255, 0, 128));
		progressBar.setBounds(255, 359, 315, 23);
		frame.getContentPane().add(progressBar);
		
		frame.setBounds(100, 100, 640, 475);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
