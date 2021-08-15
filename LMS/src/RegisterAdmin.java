import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class RegisterAdmin {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;

	// RegisterAdmin Components
	JLabel name = new JLabel("Admin Name");
	JTextField nameInp = new JTextField(20);
	JLabel username = new JLabel("User Name");
	JTextField usernameInp = new JTextField(20);
	JLabel question = new JLabel("Security Question");
	JComboBox<String> questionInp = new JComboBox<String>(new String[] {
		"What is your Nick Name?",
		"What is your Birth Place?",
		"What is your Favorite Color?",
		"What is your Favorite Food?",
		"What is your Mobile Number?"
	});

	JLabel answer = new JLabel("Answer");
	JTextField answerInp = new JTextField(20);
	JLabel password = new JLabel("Password");
	JPasswordField passwordInp = new JPasswordField(20);
	JButton sinup = new JButton("Register Admin");
	
	// Panels
	JPanel infoEntryPanel = new JPanel(new GridBagLayout());
	JPanel sinupPanel = new JPanel(new GridBagLayout());

	public RegisterAdmin(Home root) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
				
		// Current Panel & Panel Activation Settings
		root.setTitle("Library Management System - Admin Registration");
		sinupPanel.setName("AdminRegistration");
		root.setSize(700, 460);
		root.setLocationRelativeTo(null);
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		root.add(sinupPanel);
		
		// Threading: Adding all Componenets of current Panel
		new Thread() {
			public void run() {
				addComponents();
			}
		}.start();

		name.setFont(new Font("Verdana", Font.BOLD, 20));
		nameInp.setFont(new Font("Segoi UI", Font.PLAIN, 20));

		username.setFont(new Font("Verdana", Font.BOLD, 20));
		usernameInp.setFont(new Font("Segoi UI", Font.PLAIN, 20));
		
		password.setFont(new Font("Verdana", Font.BOLD, 20));
		passwordInp.setFont(new Font("Segoi UI", Font.PLAIN, 20));
		
		question.setFont(new Font("Verdana", Font.BOLD, 20));
		questionInp.setFont(new Font("Segoi UI", Font.PLAIN, 20));
		
		answer.setFont(new Font("Verdana", Font.BOLD, 20));
		answerInp.setFont(new Font("Segoi UI", Font.PLAIN, 20));

		sinup.setFocusable(false);
		sinup.setFont(new Font("Verdana", Font.BOLD, 20));
		sinup.addActionListener(event -> {
			if (nameInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Enter the Name", "Empty Name", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else if (usernameInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Enter the Username", "Empty Username", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else if (String.valueOf(passwordInp.getPassword()).isEmpty()) {
				JOptionPane.showConfirmDialog(null, "Please Enter the Password", "Empty Password", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else if (answerInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Answer the Security Question", "Empty Answer", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else if (! nameInp.getText().matches("[a-zA-Z\s]+")) {
				JOptionPane.showConfirmDialog(null, "Please Confirm that 'Name' must contains Alphabets and Spaces", "Invalid Name", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else {
				try {
					PreparedStatement pstmt = conctn.prepareStatement("INSERT INTO admin_login(username, name, password, security_question, answer) VALUES(?, ?, ?, ?, ?);");
					pstmt.setString(1, usernameInp.getText().trim());
					pstmt.setString(2, nameInp.getText().trim());
					pstmt.setString(3, String.valueOf(passwordInp.getPassword()).trim());
					pstmt.setString(4, String.valueOf(questionInp.getSelectedItem()).trim());
					pstmt.setString(5, answerInp.getText().trim());
					
					if (pstmt.executeUpdate() == 1) {

						// Threading: Welcome Dialoge
						new Thread() {
							public void run() {
								try {
									JOptionPane.showConfirmDialog(null, nameInp.getText() + " (Admin) was Successfully registerd as Admin, now you can Login", "Admin Registration Successfull", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) { System.out.println(e); }
							}
						}.start();
						
						sinupPanel.setVisible(false);
						Login.adminAvailable = true;
					}
					else {
						JOptionPane.showConfirmDialog(null, "Some Database issue Occured. Please try Again!", "Database Issue", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				}
				catch (MySQLIntegrityConstraintViolationException e) {
					JOptionPane.showConfirmDialog(null, "This Username was already registerd, Please try with another Username", "Unavalaible Username", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				catch (Exception e) { System.out.println(e); }
			}
		});

		sinupPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Register Admin", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 30), new Color(232, 57, 95)));
	}
	
	public void addComponents() {
		
		// User Authentication Components
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		infoEntryPanel.add(name, constraints);
		infoEntryPanel.add(nameInp, constraints);
		constraints.gridy = 1;
		infoEntryPanel.add(username, constraints);
		infoEntryPanel.add(usernameInp, constraints);
		constraints.gridy = 2;
		infoEntryPanel.add(password, constraints);
		infoEntryPanel.add(passwordInp, constraints);
		constraints.gridy = 3;
		infoEntryPanel.add(question, constraints);
		infoEntryPanel.add(questionInp, constraints);
		constraints.gridy = 4;
		infoEntryPanel.add(answer, constraints);
		infoEntryPanel.add(answerInp, constraints);
		constraints.gridy = 5;
		constraints.ipadx = 100;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.CENTER;
		infoEntryPanel.add(sinup, constraints);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 40;
		constraints.insets = new Insets(20, 0, 20, 0);
		sinupPanel.add(infoEntryPanel, constraints);
	}
}