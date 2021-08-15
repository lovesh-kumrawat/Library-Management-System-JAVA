import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class StudentsInfo implements ActionListener {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;
	IssueBooks issueBooks = new IssueBooks();

	// Search Students Components
	JButton resetStudent = new JButton("Reset");
	JButton searchStudent = new JButton("Search");
	JTextField searchStudentInp = new JTextField(20);
	JComboBox<String> addStudentComponents = issueBooks.addStudentComponents;

	String data[][];
	String columns[] = {
		issueBooks.studentId.getText(),
		issueBooks.name.getText(),
		issueBooks.fathersName.getText(),
		issueBooks.course.getText(),
		issueBooks.branch.getText(),
		issueBooks.year.getText(),
		issueBooks.semester.getText(),
		issueBooks.totalIssued.getText()
	};
	HashMap<String, String> dbBookStudentHeader = issueBooks.dbBookStudentHeader;

	JTable studentsList;
	JScrollPane scrollableStudentsList = new JScrollPane(studentsList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	// Some Extra Components
	JButton delete = new JButton("Delete Students");
	JButton back = new JButton("Back to Home");
	
	// Panels
	JPanel searchStudentsPanel = new JPanel(new GridBagLayout());
	JPanel studentsInfoPanel = new JPanel(new GridBagLayout());

	public StudentsInfo(Home root) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		studentsInfoPanel.setVisible(false);
		studentsInfoPanel.setName("StudentsInfo");
		root.add(studentsInfoPanel);
		addComponents();
		
		// Search Students Components
		addStudentComponents.setFont(new Font("", Font.BOLD, 16));
		
		searchStudentInp.setFont(new Font("Lucida Console", Font.PLAIN, 26));
		searchStudent.setFocusable(false);
		searchStudent.setFont(new Font("", Font.BOLD, 15));
		searchStudent.addActionListener(event -> {
			try {
				ResultSet rSet;
				String searchQuery, dbSearchSelection = dbBookStudentHeader.get(addStudentComponents.getSelectedItem());

				if (searchStudentInp.getText().isBlank()) {
					rSet = stmt.executeQuery(searchQuery = "SELECT * FROM students_info;");
				}
				else {
					String table = "students_info";
					if (dbSearchSelection == "booksIssued") {
						table = "(SELECT student.*, COUNT(*) as booksIssued, book_id FROM books_issued RIGHT JOIN students_info as student USING(student_id) LEFT JOIN books_info USING(book_id) GROUP BY(student_id) ORDER BY(student_id)) as newTable";
					}

					searchQuery = MessageFormat.format("SELECT * FROM {0} WHERE {1} like ''%{2}%'' {3};", table, dbSearchSelection, searchStudentInp.getText().trim(), dbSearchSelection == "booksIssued" && searchStudentInp.getText().trim().equals("0") ? "OR book_id is NULL" : "");
					rSet = stmt.executeQuery(searchQuery);
				}
				
				PreparedStatement pstmt = conctn.prepareStatement("SELECT COUNT(*) as booksIssued FROM books_issued JOIN students_info USING(student_id) JOIN books_info USING(book_id) WHERE student_id = ?;");

				rSet.last();
				data = new String[rSet.getRow()][8];
				rSet.beforeFirst();

				while (rSet.next()) {
					pstmt.setLong(1, Long.valueOf(rSet.getString("student_id")));
					ResultSet rSet2 = pstmt.executeQuery();
					rSet2.next();
					
					data[rSet.getRow()-1][0] = rSet.getString("student_id");
					data[rSet.getRow()-1][1] = rSet.getString("name");
					data[rSet.getRow()-1][2] = rSet.getString("fathers_name");
					data[rSet.getRow()-1][3] = rSet.getString("course");
					data[rSet.getRow()-1][4] = rSet.getString("branch");
					data[rSet.getRow()-1][5] = rSet.getString("year");
					data[rSet.getRow()-1][6] = rSet.getString("semester");
					data[rSet.getRow()-1][7] = rSet2.getString("booksIssued");
				}
				
				Statement stmt2 = conctn.createStatement();
				ResultSet rSetCount = stmt2.executeQuery("SELECT COUNT(*) as totalStudents FROM students_info;");
				rSetCount.next();
				Statement stmt3 = conctn.createStatement();
				ResultSet rSetSearchedCount = stmt3.executeQuery(searchQuery.replaceFirst(" \\* ", " COUNT(*) as searchedStudents "));
				rSetSearchedCount.next();
				
				scrollableStudentsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Students", rSetSearchedCount.getString("searchedStudents"), rSetCount.getString("totalStudents")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
				scrollableStudentsList.repaint();
				scrollableStudentsList.setViewportView(studentsList = new JTable(data, columns) {
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				});

				studentsList.setFocusable(false);
				studentsList.setRowHeight(20);
				studentsList.setForeground(Color.WHITE);
				studentsList.setBackground(new Color(232, 57, 95));
				studentsList.setFont(new Font("Segoi UI", Font.PLAIN, 16));
				studentsList.setSelectionBackground(new Color(132, 57, 95));
				studentsList.setSelectionForeground(new Color(215, 255, 255));
				studentsList.getTableHeader().setForeground(Color.WHITE);
				studentsList.getTableHeader().setBackground(new Color(32, 136, 203));
				studentsList.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 16));
				studentsList.getSelectionModel().addListSelectionListener(event2 -> {
					if (event2.getValueIsAdjusting()) {
						try {
							rSetCount.first();
							rSetSearchedCount.first();
							scrollableStudentsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Students ({2} Selected)", rSetSearchedCount.getString("searchedStudents"), rSetCount.getString("totalStudents"), studentsList.getSelectionModel().getSelectedItemsCount()), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
							scrollableStudentsList.repaint();
						} catch (Exception e) { System.out.println("Error logs: " + e); }
					}
				});
				studentsList.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent mouseEvent) {
						if (mouseEvent.getClickCount() == 2) {
							new StudentSection(root, studentsInfoPanel, String.valueOf(studentsList.getValueAt(studentsList.getSelectedRow(), 0)), String.valueOf(studentsList.getValueAt(studentsList.getSelectedRow(), 1)));
						}
					}
				});
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		resetStudent.setFocusable(false);
		resetStudent.setFont(new Font("", Font.BOLD, 15));
		resetStudent.addActionListener(event -> {
			addStudentComponents.setSelectedIndex(0);
			searchStudentInp.setText("");
			searchStudent.doClick();
		});
		
		searchStudentsPanel.getComponent(0).setFont(new Font("Lucida Console", Font.PLAIN, 17));
		searchStudentsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Students Info", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 40), Color.MAGENTA));

		searchStudent.doClick();
		scrollableStudentsList.setPreferredSize(new Dimension(0, 400));
		scrollableStudentsList.setBorder(BorderFactory.createLoweredBevelBorder());
		try {
			ResultSet rSetCount = stmt.executeQuery("SELECT COUNT(*) as totalStudents FROM students_info;");
			rSetCount.next();

			scrollableStudentsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {0} Students", rSetCount.getString("totalStudents")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
			scrollableStudentsList.repaint();
		} catch (Exception e) { System.out.println("Error logs: " + e); }

		// Some Extra Components
		delete.setFocusable(false);
		delete.setFont(new Font("Verdana", Font.BOLD, 20));
		delete.addActionListener(event -> {
			try {
				ListSelectionModel studentSelectionModel = studentsList.getSelectionModel();
				int yn = JOptionPane.showConfirmDialog(null, "Are you sure you want to Delete the Selected Students", "Delete Students", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (yn == 0) {
					String deletingList = "";
					for (int i : studentSelectionModel.getSelectedIndices()) {
						if (deletingList.equals("")) deletingList = String.valueOf(studentsList.getValueAt(i, 0));
						else deletingList = String.join(", ", deletingList, String.valueOf(studentsList.getValueAt(i, 0)));
					}

					int deleteCount = stmt.executeUpdate(MessageFormat.format("DELETE FROM students_info WHERE student_id in ({0})", deletingList));
					if (deleteCount > 0) {
						searchStudent.doClick();
						JOptionPane.showConfirmDialog(null, studentSelectionModel.getSelectedItemsCount() + (deleteCount == 1 ? " Student is" : " Students are") + " Deleted Successfully", "Delete Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showConfirmDialog(null, "Please try Again Some Issue Ouccered", "Issue Ouccered", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				}
			}
			catch (MySQLIntegrityConstraintViolationException e) {
				JOptionPane.showConfirmDialog(null, "Sorry you can't able to Delete these Students because some of them are Issued some Books", "Some Issued Books", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			catch (Exception e) { System.out.println(e); }
		});

		back.setFocusable(false);
		back.setFont(new Font("Verdana", Font.BOLD, 20));
		back.addActionListener(event -> {
			for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
				root.getContentPane().getComponent(i).setVisible(false);
			}

			root.setTitle("Library Management System - Home");
			root.homePanel.setVisible(true);
		});
	}
	
	public void addComponents() {

		// Search Students Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(6, 18, 7, 0);
		searchStudentsPanel.add(new JLabel("By:"), constraints);
		searchStudentsPanel.add(addStudentComponents, constraints);
		constraints.ipadx = 200;
		searchStudentsPanel.add(searchStudentInp, constraints);
		constraints.ipadx = 100;
		constraints.insets = new Insets(6, 0, 7, 0);
		searchStudentsPanel.add(searchStudent, constraints);
		constraints.ipadx = 0;
		constraints.insets = new Insets(6, 10, 7, 18);
		searchStudentsPanel.add(resetStudent, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 5;
		constraints.insets = new Insets(6, 18, 7, 18);
		searchStudentsPanel.add(scrollableStudentsList, constraints);
		
		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 30;
		constraints.ipady = 25;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(8, 5, 8, 5);
		studentsInfoPanel.add(searchStudentsPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		studentsInfoPanel.add(back, constraints);
		constraints.ipadx = 65;
		constraints.anchor = GridBagConstraints.EAST;
		studentsInfoPanel.add(delete, constraints);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - Students Info");
		searchStudent.doClick();
		studentsInfoPanel.setVisible(true);
	}
}