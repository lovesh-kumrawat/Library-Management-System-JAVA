import java.sql.Statement;
import java.sql.Connection;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.awt.event.ActionEvent;

public class ReIssueBooks extends ReturnBooks {
	Home root;
	Statement stmt;
	Connection conctn;
	
	public ReIssueBooks(Home root) {
		super(root);
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;

		// Current Panel Settings
		issueBooksPanel.setName("ReIssueBooks");
		issue.setText("ReIssue Book");
		
		issue.removeActionListener(issue.getActionListeners()[0]);
		issue.addActionListener(event -> {
			if (bookIdInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Select a Book to ReIssue", "Unkown Book", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			else if (studentIdInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Select a Student to ReIssue a Book", "Unkown Student", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				try {
					PreparedStatement pstmt = conctn.prepareStatement("UPDATE books_issued SET issued_date = CURDATE() WHERE student_id = ? and book_id = ?;");
					pstmt.setLong(1, Long.valueOf(studentIdInp.getText().trim()));
					pstmt.setLong(2, Long.valueOf(bookIdInp.getText().trim()));

					if (pstmt.executeUpdate() == 1) {
						stmt.executeUpdate(MessageFormat.format("INSERT INTO logs(action, student_id, book_id) VALUES(''Book ReIssued'', {0}, {1});", studentIdInp.getText().trim(), bookIdInp.getText().trim()));
						JOptionPane.showMessageDialog(null, MessageFormat.format("{0} ({1}) is Successfully reissued by {2} ({3})", titleInp.getText(), bookIdInp.getText(), nameInp.getText(), studentIdInp.getText()), "Book ReIssued", JOptionPane.INFORMATION_MESSAGE);
						resetStudent.doClick();
						resetBook.doClick();
					}
					else {
						JOptionPane.showConfirmDialog(null, MessageFormat.format("{0} ({1}) is not issued by {2} ({3})", titleInp.getText(), bookIdInp.getText(), nameInp.getText(), studentIdInp.getText()), "Not Issued", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				}
				catch (Exception e) { System.out.println(e); }
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {

		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - ReIssue Books");
		issueBooksPanel.setVisible(true);
	}
}