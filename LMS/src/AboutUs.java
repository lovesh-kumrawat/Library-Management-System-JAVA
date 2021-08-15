import java.awt.*;
import java.net.URI;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.border.TitledBorder;

public class AboutUs implements ActionListener {
	Home root;
	GridBagConstraints constraints;
	
	// Abut Us Components
	JTextArea appInfo = new JTextArea(11, 35);
	JScrollPane scrollableAppInfo = new JScrollPane(appInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	JButton linkedIn = new JButton(new ImageIcon(getClass().getResource("icons/LinkedIn.png")));
	JButton gitHub = new JButton(new ImageIcon(getClass().getResource("icons/GitHub.png")));
	JButton instaGram = new JButton(new ImageIcon(getClass().getResource("icons/InstaGram.png")));
	JLabel imgLabel = new JLabel(new ImageIcon(getClass().getResource("icons/LoveshKumrawat.png")));
	
	// Some Extra Components
	JButton back = new JButton("Back to Home");

	// Panels
	JPanel contactsPanel = new JPanel(new GridBagLayout());
	JPanel infoPanel = new JPanel(new GridBagLayout());
	JPanel aboutUsPanel = new JPanel(new GridBagLayout());

	public AboutUs(Home root) {
		this.root = root;
		aboutUsPanel.setVisible(false);
		aboutUsPanel.setName("AboutUs");
		root.add(aboutUsPanel);
		addComponents();

		linkedIn.setFocusable(false);
		linkedIn.setBorderPainted(false);
		linkedIn.setBackground(Color.decode("#eeeeee"));
		linkedIn.addActionListener(event -> {
			Desktop browser = Desktop.getDesktop();
			try {
				browser.browse(new URI("https://www.linkedin.com/in/lovesh-kumrawat/"));
			} catch (IOException | URISyntaxException e) {
				JOptionPane.showConfirmDialog(null, "Sorry for the Inconvenience, some browser issue occured", "Not Rechable", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		});

		gitHub.setFocusable(false);
		gitHub.setBorderPainted(false);
		gitHub.setBackground(Color.decode("#eeeeee"));
		gitHub.addActionListener(event -> {
			Desktop browser = Desktop.getDesktop();
			try {
				browser.browse(new URI("https://github.com/lovesh-kumrawat"));
			} catch (IOException | URISyntaxException e) {
				JOptionPane.showConfirmDialog(null, "Sorry for the Inconvenience, some browser issue occured", "Not Rechable", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
		});

		instaGram.setFocusable(false);
		instaGram.setBorderPainted(false);
		instaGram.setBackground(Color.decode("#eeeeee"));
		instaGram.addActionListener(event -> {
			JOptionPane.showConfirmDialog(null, "Oops! Currently, I don't have an Instagram Account", "No Account", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		});

		appInfo.setEditable(false);
		scrollableAppInfo.setBorder(null);
		appInfo.setBackground(Color.decode("#eeeeee"));
		appInfo.setFont(new Font("Segoi UI", Font.PLAIN, 20));
		appInfo.setText(String.join("\n",
			"Hello!",
			"Myself Lovesh Kumrawat pursuing 2nd Year at Medicaps University.",
			"And currently Intern at TCR Innovation for Core Java - SQL June Batch.",
			"I have successfully completed the 2 Months Training at TCR.",
			"This is my Final Project for the same.",
			"",
			"I would like to thank Rutuja Doiphode, Saheel Ramji, and Yuvraj Pandey,",
			"who gave me such a great opportunity and experience.",
			"And special thanks to respected Abhijeet Salvi Sir,",
			"who work hard to make me qualified."
		));
		
		appInfo.setLineWrap(true);
		appInfo.setWrapStyleWord(true);

		// Some Extra Components
		back.setFont(new Font("Verdana", Font.BOLD, 20));
		back.setFocusable(false);
		back.addActionListener(event -> {
			for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
				root.getContentPane().getComponent(i).setVisible(false);
			}
			
			root.setTitle("Library Management System - Home");
			root.homePanel.setVisible(true);
		});

		// Panels
		contactsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Contact Us", TitledBorder.RIGHT, TitledBorder.CENTER, new Font("Segoi UI", Font.BOLD, 40), Color.PINK));
		infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "About Us", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 40), Color.MAGENTA));
	}
	
	public void addComponents() {

		// Contacts Components
		constraints = new GridBagConstraints();
		constraints.ipadx = 15;
		constraints.insets = new Insets(20, 10, 20, 10);
		contactsPanel.add(linkedIn, constraints);
		constraints.gridy = 1;
		contactsPanel.add(gitHub, constraints);
		constraints.gridy = 2;
		contactsPanel.add(instaGram, constraints);
		
		// Info Components
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 40, 10, 20);
		infoPanel.add(imgLabel, constraints);
		constraints.ipadx = 30;
		constraints.gridy = 1;
		constraints.insets = new Insets(20, 40, 20, 20);
		infoPanel.add(scrollableAppInfo, constraints);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.insets = new Insets(20, 20, 20, 40);
		constraints.fill = GridBagConstraints.VERTICAL;
		infoPanel.add(contactsPanel, constraints);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.gridwidth = 2;
		aboutUsPanel.add(infoPanel, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(20, 0, 20, 0);
		aboutUsPanel.add(back, constraints);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - About Us");
		aboutUsPanel.setVisible(true);
	}
}