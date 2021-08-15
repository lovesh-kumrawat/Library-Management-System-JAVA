import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;

public class Login {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;
	static Boolean adminAvailable = false;
	static Boolean passwordReset = false;

	// User Authentication Components
	JLabel username = new JLabel("User Name");
	JTextField usernameInp = new JTextField(20);
	JLabel password = new JLabel("Password");
	JPasswordField passwordInp = new JPasswordField(20);
	JButton login = new JButton("Login");
	JLabel forgotText = new JLabel("Don't remember the Password");
	JButton forgot = new JButton("Forgot Password");
	JRadioButton[] role = new JRadioButton[] {new JRadioButton("Student"), new JRadioButton("Admin", true)};
	ButtonGroup roleGrp = new ButtonGroup();

	// Panels
	JPanel rolePanel = new JPanel(new GridLayout(1, 2));
	JPanel authenticatePanel = new JPanel(new GridBagLayout());
	JPanel loginPanel = new JPanel(new GridBagLayout());

	public Login(Home root) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;

		// Admin Availability Checkup
		try {
			ResultSet rSet = stmt.executeQuery("SELECT COUNT(*) as count FROM admin_login;");
			rSet.next();
			if (rSet.getInt("count") == 0) new RegisterAdmin(root);
			else adminAvailable = true;
		} catch (Exception e) { System.out.println(e); }
		
		while (!adminAvailable) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) { System.out.println(e); }
		}
		
		// Current Panel & Panel Activation Settings
		root.setTitle("Library Management System - Login");
		loginPanel.setName("Login");
		loginPanel.setName("Login");
		for (int i = 500, j = 15; i <= 700 && j <= 415; i+=5, j+=10) {
			try {
				Thread.sleep(3);
			} catch (Exception e) {}
			root.setSize(i, j);			// Animated Resizing
			root.setLocationRelativeTo(null);
		}
		root.setLocationRelativeTo(null);
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		root.add(loginPanel);
		
		// Threading: Adding all Componenets of current Panel
		new Thread() {
			public void run() {
				addComponents();
			}
		}.start();

		// Role Components
		role[0].setFont(new Font("Verdana", Font.BOLD, 20));
		role[0].setActionCommand("Student");
		role[0].setFocusable(false);
		role[1].setFont(new Font("Verdana", Font.BOLD, 20));
		role[1].setActionCommand("Admin");
		role[1].setFocusable(false);

		role[0].addActionListener(event -> {
			username.setText("Student ID");
			forgot.setText("Change Password");
			forgotText.setText("Default Password is 'password'");
			forgot.setPreferredSize(new Dimension(140, 20));
			forgotText.setPreferredSize(new Dimension(187, 20));
		});
		role[1].addActionListener(event -> {
			username.setText("User Name");
			forgot.setText("Forgot Password");
			forgotText.setText("Don't remember the Password");
			forgot.setPreferredSize(new Dimension(133, 20));
			forgotText.setPreferredSize(new Dimension(185, 20));
		});
		
		// User Authentication Components
		username.setFont(new Font("Verdana", Font.BOLD, 20));
		usernameInp.setFont(new Font("Verdana", Font.PLAIN, 20));
		password.setFont(new Font("Verdana", Font.BOLD, 20));
		passwordInp.setFont(new Font("Verdana", Font.PLAIN, 20));

		forgot.setFocusable(false);
		forgot.setBorderPainted(false);
		forgot.setBackground(Color.decode("#eeeeee"));
		forgot.setFont(new Font("Verdana", Font.BOLD, 15));
		forgot.setPreferredSize(new Dimension(133, 20));
		forgotText.setPreferredSize(new Dimension(185, 20));
		forgotText.setFont(new Font("Verdana", Font.PLAIN, 15));

		login.setFocusable(false);
		login.setFont(new Font("Verdana", Font.BOLD, 20));
		login.addActionListener(event -> {
			if (usernameInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Enter the Username", "Empty Username", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else if (String.valueOf(passwordInp.getPassword()).isEmpty()) {
				JOptionPane.showConfirmDialog(null, "Please Enter the Password", "Empty Password", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else {
				try {
					PreparedStatement pstmt = conctn.prepareStatement(roleGrp.getSelection().getActionCommand() == "Admin"
						? "SELECT * FROM admin_login WHERE username = ? and password = ?;"
						: "SELECT * FROM students_info WHERE student_id = ? and password = ?;");
					pstmt.setString(1, usernameInp.getText());
					pstmt.setString(2, String.valueOf(passwordInp.getPassword()));
					
					ResultSet rSet = pstmt.executeQuery();
					if (rSet.next()) {

						// Threading: Welcome Dialoge
						new Thread() {
							public void run() {
								try {
									if (roleGrp.getSelection().getActionCommand() == "Student") {
										stmt.executeUpdate(MessageFormat.format("INSERT INTO logs(action, student_id) VALUES(''Login'', {0});", rSet.getString("student_id")));
									}
									JOptionPane.showConfirmDialog(null, "Hello " + rSet.getString("name") + "!", "Login Successfull", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) { System.out.println(e); }
							}
						}.start();

						if (roleGrp.getSelection().getActionCommand() == "Admin") {
							loginPanel.setVisible(false);
							Home.validLogin = true;
						}
						else new StudentSection(root, new JPanel(), rSet.getString("student_id"), rSet.getString("name"));
					}
					else {
						JOptionPane.showConfirmDialog(null, "Wrong Username or Password", "Invalid Credentials", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception e) { System.out.println(e); }
			}
		});

		forgot.addActionListener(event -> {
			if (roleGrp.getSelection().getActionCommand() == "Admin") {
				
				// Searching User
				Object username = "";
				while (username.equals("")) {
					username = JOptionPane.showInputDialog(null, "Enter Your Username", "Admin", JOptionPane.QUESTION_MESSAGE);
					if (username == null) break;
				}
				
				if (username != null) {
					try {
						ResultSet rSet = stmt.executeQuery(MessageFormat.format("SELECT * FROM admin_login WHERE username = ''{0}'';", username));
						if (rSet.next()) {
							passwordReset = false;
							new ForgotAdmin(root, String.valueOf(username), rSet.getString("security_question"));
							new Thread() {
								public void run() {
									while (!passwordReset) {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) { System.out.println(e); }
									}

									root.setTitle("Library Management System - Login");
									for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
										root.getContentPane().getComponent(i).setVisible(false);
									}
									loginPanel.setVisible(true);
									
									// Animated Resizing
									for (int i = 665, j = 345; i <= 700 && j <= 415; i++, j+=2) {
										try {
											Thread.sleep(5);
										} catch (Exception e) {}
										root.setSize(i, j);
										root.setLocationRelativeTo(null);
									}
								}
							}.start();
						}
						else JOptionPane.showConfirmDialog(null, "Please enter the Valid Username", "Invalid Username", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					} catch (Exception e) { System.out.println(e); }
				}
			}
		});

		loginPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Login", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 30), new Color(232, 57, 95)));
	}
	
	public void addComponents() {
		
		// Role Components
		roleGrp.add(role[0]);
		roleGrp.add(role[1]);
		rolePanel.add(role[0]);
		rolePanel.add(role[1]);
		
		// User Authentication Components
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 0, 10, 10);
		authenticatePanel.add(username, constraints);
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 10, 10, 0);
		authenticatePanel.add(usernameInp, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(10, 0, 10, 10);
		authenticatePanel.add(password, constraints);
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 10, 10, 0);
		authenticatePanel.add(passwordInp, constraints);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.ipadx = 50;
		constraints.insets = new Insets(10, 0, 10, 0);
		authenticatePanel.add(login, constraints);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		authenticatePanel.add(forgotText, constraints);
		constraints.gridx = 2;
		constraints.gridy = 3;
		authenticatePanel.add(forgot, constraints);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 70;
		constraints.insets = new Insets(20, 0, 20, 0);
		loginPanel.add(rolePanel, constraints);
		constraints.gridy = 1;
		loginPanel.add(authenticatePanel, constraints);
	}
}