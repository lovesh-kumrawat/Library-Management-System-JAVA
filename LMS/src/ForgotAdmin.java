import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class ForgotAdmin {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;

	// User Authentication Components
	JLabel question = new JLabel("<Question>");
	JLabel answer = new JLabel("Answer");
	JTextField answerInp = new JTextField(20);
	JLabel newPassword = new JLabel("New Password");
	JPasswordField newPasswordInp = new JPasswordField(20);
	JButton resetPassword = new JButton("Reset Password");
	JLabel backText = new JLabel("Don't want to Reset Password");
	JButton back = new JButton("Back to Login");

	// Panels
	JPanel forgotPanel = new JPanel(new GridBagLayout());

	public ForgotAdmin(Home root, String username, String securityQuestion) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		
		// Current Panel & Panel Activation Settings
		root.setTitle("Library Management System - Forgot Password");
		forgotPanel.setName("ForgotAdmin");
		root.setLocationRelativeTo(null);
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		root.add(forgotPanel);
		
		// Threading: Adding all Componenets of current Panel
		new Thread() {
			public void run() {
				addComponents();
			}
		}.start();

		question.setText(securityQuestion);
		question.setFont(new Font("Verdana", Font.BOLD, 20));
		answer.setFont(new Font("Verdana", Font.BOLD, 20));
		answerInp.setFont(new Font("Verdana", Font.PLAIN, 20));
		newPassword.setFont(new Font("Verdana", Font.BOLD, 20));
		newPasswordInp.setFont(new Font("Verdana", Font.PLAIN, 20));

		back.setFocusable(false);
		back.setBorderPainted(false);
		back.setBackground(Color.decode("#eeeeee"));
		back.setFont(new Font("Verdana", Font.BOLD, 15));
		back.setPreferredSize(new Dimension(133, 20));
		backText.setPreferredSize(new Dimension(185, 20));
		backText.setFont(new Font("Verdana", Font.PLAIN, 15));

		resetPassword.setFocusable(false);
		resetPassword.setFont(new Font("Verdana", Font.BOLD, 20));
		resetPassword.addActionListener(event -> {
			if (answerInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please answer the Question", "Not Answered", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else if (String.valueOf(newPasswordInp.getPassword()).isEmpty()) {
				JOptionPane.showConfirmDialog(null, "Please Enter the Password", "Empty Password", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			else {
				try {
					PreparedStatement pstmt = conctn.prepareStatement("UPDATE admin_login SET password = ? WHERE username = ? and answer = ?;");
					pstmt.setString(1, String.valueOf(newPasswordInp.getPassword()));
					pstmt.setString(2, username);
					pstmt.setString(3, answerInp.getText());
					
					if (pstmt.executeUpdate() == 1) {

						// Threading: Password Reset Dialoge
						new Thread() {
							public void run() {
								try {
									JOptionPane.showConfirmDialog(null, "Hello " + username + " (Admin) Password Reset Successfull, now you can Login", "Password Reseted", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
								} catch (Exception e) { System.out.println(e); }
							}
						}.start();
						Login.passwordReset = true;
					}
					else {
						JOptionPane.showConfirmDialog(null, "Please answer the question Correctly", "Wrong Answer", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception e) { System.out.println(e); }
			}
		});

		back.addActionListener(event -> { Login.passwordReset = true; });

		forgotPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Reset Password", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 30), new Color(232, 57, 95)));
	}
	
	public void addComponents() {
		
		// MainPanel
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 0, 10, 0);
		constraints.gridwidth = 3;
		forgotPanel.add(question, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(10, 0, 10, 10);
		forgotPanel.add(answer, constraints);
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 10, 10, 0);
		forgotPanel.add(answerInp, constraints);
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(10, 0, 10, 10);
		forgotPanel.add(newPassword, constraints);
		constraints.gridwidth = 2;
		constraints.insets = new Insets(10, 10, 10, 0);
		forgotPanel.add(newPasswordInp, constraints);
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.ipadx = 50;
		constraints.insets = new Insets(10, 0, 10, 0);
		forgotPanel.add(resetPassword, constraints);
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		forgotPanel.add(backText, constraints);
		constraints.gridx = 2;
		constraints.gridy = 4;
		forgotPanel.add(back, constraints);

		// Animated Resizing
		for (int i = 700, j = 415; i >= 665 && j >= 345; i--, j-=2) {
			try {
				Thread.sleep(5);
			} catch (Exception e) {}
			root.setSize(i, j);
			root.setLocationRelativeTo(null);
		}
	}
}