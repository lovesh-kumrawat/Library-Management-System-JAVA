import java.sql.Statement;
import java.sql.Connection;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReturnBooks extends IssueBooks {
	Home root;
	Statement stmt;
	Connection conctn;
	
	public ReturnBooks(Home root) {
		super(root, true);
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;

		// Current Panel Settings
		issueBooksPanel.setName("ReturnBooks");
		issue.setText("Return Book");

		ActionListener issueListener = searchStudentResult.getActionListeners()[0];
		searchStudentResult.removeActionListener(issueListener);
		searchStudentResult.addActionListener(event -> {
			if (searchStudentResultGo && ! studentIdInp.getText().isBlank()) {
				searchBook.setEnabled(true);
				searchBookInp.setEditable(false);
				searchBooksPanel.setEnabled(false);
				addBookComponents.setEnabled(false);
				searchBookInp.setText(studentIdInp.getText());
				addBookComponents.addItem("Student ID");
				addBookComponents.setSelectedItem("Student ID");
				searchBook.setEnabled(false);
			}
			else {
				ActionListener listen[] = searchBook.getActionListeners();
				for (ActionListener l : listen) searchBook.removeActionListener(l);
				searchBook.setEnabled(true);
				searchBookInp.setEditable(true);
				searchBooksPanel.setEnabled(true);
				addBookComponents.setEnabled(true);
				searchBookInp.setText("");
				addBookComponents.removeItem("Student ID");
				addBookComponents.setSelectedItem("Book ID");
				for (ActionListener l : listen) searchBook.addActionListener(l);
			}
		});
		searchStudentResult.addActionListener(issueListener);
		
		issue.removeActionListener(issue.getActionListeners()[0]);
		issue.addActionListener(event -> {
			if (bookIdInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Select a Book to Return", "Unkown Book", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			else if (studentIdInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Select a Student to Return a Book", "Unkown Student", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				try {
					PreparedStatement pstmt = conctn.prepareStatement("DELETE FROM books_issued WHERE student_id = ? and book_id = ?;");
					pstmt.setLong(1, Long.valueOf(studentIdInp.getText().trim()));
					pstmt.setLong(2, Long.valueOf(bookIdInp.getText().trim()));

					if (pstmt.executeUpdate() == 1) {
						stmt.executeUpdate(MessageFormat.format("INSERT INTO logs(action, student_id, book_id) VALUES(''Book Returned'', {0}, {1});", studentIdInp.getText().trim(), bookIdInp.getText().trim()));
						JOptionPane.showMessageDialog(null, MessageFormat.format("{0} ({1}) is Successfully returned by {2} ({3})", titleInp.getText(), bookIdInp.getText(), nameInp.getText(), studentIdInp.getText()), "Book Returned", JOptionPane.INFORMATION_MESSAGE);
						resetStudent.doClick();
						resetBook.doClick();
					}
					else {
						JOptionPane.showConfirmDialog(null, MessageFormat.format("{0} ({1}) is already returned by {2} ({3})", titleInp.getText(), bookIdInp.getText(), nameInp.getText(), studentIdInp.getText()), "Already Returned", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
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
		
		root.setTitle("Library Management System - Return Books");
		issueBooksPanel.setVisible(true);
	}
}