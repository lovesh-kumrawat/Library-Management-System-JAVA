import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.time.LocalDate;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class IssueBooks implements ActionListener {
	Home root;
	Statement stmt;
	Connection conctn;
	boolean returning;
	GridBagConstraints constraints;
	AddBooks addBooks = new AddBooks();
	AddStudents addStudents = new AddStudents();

	// Book Details Components
	JLabel bookId = addBooks.bookId;
	JTextField bookIdInp = new JTextField(20);

	JLabel title = addBooks.title;
	JTextField titleInp = new JTextField(20);

	JLabel isbn = addBooks.isbn;
	JTextField isbnInp = new JTextField(20);

	JLabel publisher = addBooks.publisher;
	JTextField publisherInp = new JTextField(20);

	JLabel edition = addBooks.edition;
	JTextField editionInp = new JTextField(20);

	JLabel price = addBooks.price;
	JTextField priceInp = new JTextField(20);

	JLabel pages = addBooks.pages;
	JTextField pagesInp = new JTextField(20);

	JLabel issueDate = new JLabel("Date of Issue");
	JTextField issueDateInp = new JTextField(20);

	// Search Books Components
	JComboBox<String> searchBookResult = new JComboBox<String>(new String[] {});
	boolean searchBookResultGo = true;
	JComboBox<String> addBookComponents = new JComboBox<String>(new String[] {
		bookId.getText(),
		title.getText(),
		isbn.getText(),
		publisher.getText(),
		edition.getText(),
		price.getText(),
		pages.getText()
	});
	JButton resetBook = new JButton("Reset");
	JButton searchBook = new JButton("Search");
	JTextField searchBookInp = new JTextField(20);
	
	// Student Details Components
	JLabel studentId = addStudents.studentId;
	JTextField studentIdInp = new JTextField(20);

	JLabel name = addStudents.name;
	JTextField nameInp = new JTextField(20);

	JLabel fathersName = addStudents.fathersName;
	JTextField fathersNameInp = new JTextField(20);

	JLabel course = addStudents.course;
	JTextField courseInp = new JTextField(20);

	JLabel branch = addStudents.branch;
	JTextField branchInp = new JTextField(20);

	JLabel year = addStudents.year;
	JTextField yearInp = new JTextField(20);

	JLabel semester = addStudents.semester;
	JTextField semesterInp = new JTextField(20);

	JLabel totalIssued = new JLabel("Books Issued");
	JComboBox<String> totalIssuedInp = new JComboBox<String>(new String[] {"Select Student"});

	// Search Students Components
	JComboBox<String> searchStudentResult = new JComboBox<String>(new String[] {});
	boolean searchStudentResultGo = true;
	JComboBox<String> addStudentComponents = new JComboBox<String>(new String[] {
		studentId.getText(),
		name.getText(),
		fathersName.getText(),
		course.getText(),
		branch.getText(),
		year.getText(),
		semester.getText(),
		totalIssued.getText()
	});
	JButton resetStudent = new JButton("Reset");
	JButton searchStudent = new JButton("Search");
	JTextField searchStudentInp = new JTextField(20);
	
	// Usefull Variables
	HashMap<String, String> dbBookStudentHeader = new HashMap<String, String>() {{
		// db books_info
		put(bookId.getText(), "book_id");
		put(title.getText(), "title");
		put(isbn.getText(), "isbn");
		put(publisher.getText(), "publisher");
		put(edition.getText(), "edition");
		put(price.getText(), "price");
		put(pages.getText(), "pages");
		put(issueDate.getText(), "issued_date");

		// db students_info
		put(studentId.getText(), "student_id");
		put(name.getText(), "name");
		put(fathersName.getText(), "fathers_name");
		put(course.getText(), "course");
		put(branch.getText(), "branch");
		put(year.getText(), "year");
		put(semester.getText(), "semester");
		put(totalIssued.getText(), "booksIssued");
	}};
	
	// Some Extra Components
	JButton issue = new JButton("Issue Book");
	JButton back = new JButton("Back to Home");

	// Panels
	JPanel resultBooksPanel = new JPanel(new GridBagLayout());
	JPanel searchBooksPanel = new JPanel(new GridBagLayout());
	JPanel bookDetailsPanel = new JPanel(new GridBagLayout());

	JPanel resultStudentsPanel = new JPanel(new GridBagLayout());
	JPanel searchStudentsPanel = new JPanel(new GridBagLayout());
	JPanel studentDetailsPanel = new JPanel(new GridBagLayout());
	
	JPanel issueBooksPanel = new JPanel(new GridBagLayout());

	public IssueBooks() {}
	public IssueBooks(Home root, boolean returning) {

		// Current Panel Settings
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		this.returning = returning;
		issueBooksPanel.setVisible(false);
		issueBooksPanel.setName("IssueBooks");
		root.add(issueBooksPanel);
		addComponents();
		
		// Book Details Components
		bookDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Book Details", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 25), new Color(232, 57, 95)));
		for (int i=1; i<bookDetailsPanel.getComponentCount(); i++) {
			if (i % 2 != 0) bookDetailsPanel.getComponent(i).setFont(new Font("", Font.BOLD, 18));
			else {
				bookDetailsPanel.getComponent(i).setFont(new Font("Lucida Console", Font.PLAIN, 15));
				((JTextField) bookDetailsPanel.getComponent(i)).setEditable(false);
			}
		}
		
		// Result Books Components
		searchBookResult.setPrototypeDisplayValue("");
		searchBookResult.setFont(new Font("Lucida Console", Font.PLAIN, 15));
		
		// Search Books Components
		searchBook.addActionListener(event -> {
			try {
				ResultSet rSet;
				String searchQuery, dbSearchSelection = dbBookStudentHeader.get(addBookComponents.getSelectedItem());
				
				if (dbSearchSelection.equals("student_id")) {
					rSet = stmt.executeQuery(searchQuery = MessageFormat.format("SELECT * FROM books_issued JOIN students_info USING(student_id) JOIN books_info USING(book_id) WHERE student_id = {0};", searchBookInp.getText()));
				}
				else {
					if (searchBookInp.getText().isBlank()) {
						rSet = stmt.executeQuery(searchQuery = "SELECT * FROM books_info;");
					}
					else {
						searchQuery = MessageFormat.format("SELECT * FROM books_info WHERE {0} like ''%{1}%'';", dbSearchSelection, searchBookInp.getText().trim());
						rSet = stmt.executeQuery(searchQuery);
					}
				}
				
				Statement stmt2 = conctn.createStatement();
				ResultSet rSetCount = stmt2.executeQuery("SELECT COUNT(*) as totalBooks FROM books_info;");
				rSetCount.next();
				Statement stmt3 = conctn.createStatement();
				ResultSet rSetSearchedCount = stmt3.executeQuery(searchQuery.replaceFirst(" \\* ", " COUNT(*) as searchedBooks "));
				rSetSearchedCount.next();
				
				searchBookResultGo = false;
				searchBookResult.removeAllItems();
				if (searchBookInp.getText().isBlank()) {
					searchBookResult.addItem(addBookComponents.getSelectedItem() + ": All Books");
				}
				else {
					searchBookResult.addItem(MessageFormat.format(rSetSearchedCount.getLong("searchedBooks") != 0 ? "{0} / {1} Books FOUND" : "NO Match FOUND", rSetSearchedCount.getString("searchedBooks"), rSetCount.getString("totalBooks")));
				}

				while (rSet.next()) {
					if (dbSearchSelection.equals("student_id")) {
						searchBookResult.addItem("Title: " + rSet.getString("title") + " (" + rSet.getString("book_id") + ")");
					}
					else {
						searchBookResult.addItem(addBookComponents.getSelectedItem() + ": " + rSet.getString(dbSearchSelection) + (dbSearchSelection != "book_id" ? " (" + rSet.getString("book_id") + ")" : ""));
					}
				}
			} catch (SQLException e) {
				System.out.println(e);
				JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
			} finally { searchBookResultGo = true; }
		});
		searchBook.doClick();
		
		searchBookResult.addActionListener(event2 -> {
			String selectedItem = String.valueOf(searchBookResult.getSelectedItem());
			if (selectedItem.endsWith("FOUND") || selectedItem.endsWith(": All Books")) {
				bookIdInp.setText("");
				titleInp.setText("");
				isbnInp.setText("");
				publisherInp.setText("");
				editionInp.setText("");
				priceInp.setText("");
				pagesInp.setText("");
				issueDateInp.setText("");
			}
			else if (searchBookResultGo) {
				try {
					String bookIdd = "";
					if (selectedItem.startsWith("Book ID: ")) {
						bookIdd = selectedItem.substring(selectedItem.indexOf(": ") + 2);
					}
					else if (selectedItem.endsWith(")")) {
						bookIdd = selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.length() - 1);
					}

					ResultSet rSet2 = stmt.executeQuery(MessageFormat.format("SELECT * FROM books_info LEFT JOIN books_issued USING(book_id) WHERE book_id = {0};", bookIdd));
					rSet2.next();
					
					bookIdInp.setText(rSet2.getString("book_id"));
					titleInp.setText(rSet2.getString("title"));
					isbnInp.setText(rSet2.getString("isbn"));
					publisherInp.setText(rSet2.getString("publisher"));
					editionInp.setText(rSet2.getString("edition"));
					priceInp.setText(rSet2.getString("price"));
					pagesInp.setText(rSet2.getString("pages"));
					if (returning) issueDateInp.setText(rSet2.getString("issued_date"));
					else issueDateInp.setText(String.valueOf(LocalDate.now()));
				} catch (Exception e) { System.out.println(e); }
			}
		});
		
		resetBook.setFocusable(false);
		resetBook.addActionListener(event -> {
			searchBookInp.setText("");
			addBookComponents.setSelectedIndex(0);
		});
		addBookComponents.setFont(new Font("", Font.BOLD, 18));
		addBookComponents.addActionListener(event -> { searchBook.doClick(); });
		searchBook.setFont(new Font("", Font.BOLD, 18));
		searchBook.setFocusable(false);
		searchBookInp.setFont(new Font("Lucida Console", Font.PLAIN, 15));
		searchBooksPanel.getComponent(0).setFont(new Font("Lucida Console", Font.PLAIN, 17));
		searchBooksPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Search Book", TitledBorder.RIGHT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 18), Color.MAGENTA));
	
		// Student Details Components
		totalIssuedInp.setPrototypeDisplayValue("");
		studentDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Student Details", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 25), new Color(232, 57, 95)));
		for (int i=1; i<studentDetailsPanel.getComponentCount(); i++) {
			if (i % 2 != 0) studentDetailsPanel.getComponent(i).setFont(new Font("", Font.BOLD, 18));
			else {
				studentDetailsPanel.getComponent(i).setFont(new Font("Lucida Console", Font.PLAIN, 15));
				if (i != 16) ((JTextField) studentDetailsPanel.getComponent(i)).setEditable(false);
			}
		}
		
		// Result Students Components
		searchStudentResult.setPrototypeDisplayValue("");
		searchStudentResult.setFont(new Font("Lucida Console", Font.PLAIN, 15));
		
		// Search Students Components
		searchStudent.setFocusable(false);
		searchStudent.addActionListener(event -> {
			try {
				ResultSet rSet;
				String searchQuery, dbSearchSelection = dbBookStudentHeader.get(addStudentComponents.getSelectedItem());

				// if (dbSearchSelection.equals("book_id")) {
				// 	rSet = stmt.executeQuery(searchQuery = MessageFormat.format("SELECT * FROM books_issued JOIN students_info USING(student_id) JOIN books_info USING(book_id) WHERE book_id = {0};", searchStudentInp.getText()));
				// }
				// else {
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
				// }
				
				Statement stmt2 = conctn.createStatement();
				ResultSet rSetCount = stmt2.executeQuery("SELECT COUNT(*) as totalStudents FROM students_info;");
				rSetCount.next();
				Statement stmt3 = conctn.createStatement();
				ResultSet rSetSearchedCount = stmt3.executeQuery(searchQuery.replaceFirst(" \\* ", " COUNT(*) as searchedStudents "));
				rSetSearchedCount.next();
				
				searchStudentResultGo = false;
				searchStudentResult.removeAllItems();
				if (searchStudentInp.getText().isBlank()) {
					searchStudentResult.addItem(addStudentComponents.getSelectedItem() + ": All Students");
				}
				else {
					searchStudentResult.addItem(MessageFormat.format(rSetSearchedCount.getLong("searchedStudents") != 0 ? "{0} / {1} Students FOUND" : "NO Match FOUND", rSetSearchedCount.getString("searchedStudents"), rSetCount.getString("totalStudents")));
				}

				PreparedStatement pstmt = conctn.prepareStatement("SELECT COUNT(*) as booksIssued FROM books_issued JOIN students_info USING(student_id) JOIN books_info USING(book_id) WHERE student_id = ?;");

				while (rSet.next()) {
					pstmt.setLong(1, Long.valueOf(rSet.getString("student_id")));
					ResultSet rSet2 = pstmt.executeQuery();
					rSet2.next();
					
					searchStudentResult.addItem(addStudentComponents.getSelectedItem() + ": " + (dbSearchSelection != "booksIssued" ? rSet : rSet2).getString(dbSearchSelection) + (dbSearchSelection != "student_id" ? " (" + rSet.getString("student_id") + ")" : ""));
				}
			} catch (SQLException e) {
				System.out.println(e);
				JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
			} finally { searchStudentResultGo = true; }
		});
		searchStudent.doClick();
		
		searchStudentResult.addActionListener(event2 -> {
			String selectedItem = String.valueOf(searchStudentResult.getSelectedItem());
			if (selectedItem.endsWith("FOUND") || selectedItem.endsWith(": All Students")) {
				studentIdInp.setText("");
				nameInp.setText("");
				fathersNameInp.setText("");
				courseInp.setText("");
				branchInp.setText("");
				yearInp.setText("");
				semesterInp.setText("");
				totalIssuedInp.removeAllItems();
				totalIssuedInp.addItem("Select Student");
			}
			else if (searchStudentResultGo) {
				try {
					String studentIdd = "";
					if (selectedItem.startsWith("Student ID: ")) {
						studentIdd = selectedItem.substring(selectedItem.indexOf(": ") + 2);
					}
					else if (selectedItem.endsWith(")")) {
						studentIdd = selectedItem.substring(selectedItem.indexOf("(") + 1, selectedItem.length() - 1);
					}
					
					PreparedStatement pstmt = conctn.prepareStatement("SELECT COUNT(*) as booksIssued FROM books_issued JOIN students_info USING(student_id) JOIN books_info USING(book_id) WHERE student_id = ?;");
					pstmt.setLong(1, Long.valueOf(studentIdd));
					ResultSet rSet3 = pstmt.executeQuery();
					rSet3.next();
					totalIssuedInp.removeAllItems();
					totalIssuedInp.addItem(rSet3.getString("booksIssued") + " Books Issued");

					ResultSet rSet2 = stmt.executeQuery(MessageFormat.format("SELECT student.*, book_id, title FROM books_issued RIGHT JOIN students_info as student USING(student_id) LEFT JOIN books_info USING(book_id) WHERE student_id = {0};", studentIdd));
					rSet2.next();
					
					studentIdInp.setText(rSet2.getString("student_id"));
					nameInp.setText(rSet2.getString("name"));
					fathersNameInp.setText(rSet2.getString("fathers_name"));
					courseInp.setText(rSet2.getString("course"));
					branchInp.setText(rSet2.getString("branch"));
					yearInp.setText(rSet2.getString("year"));
					semesterInp.setText(rSet2.getString("semester"));

					do {
						if (! "null".equals(String.valueOf(rSet2.getString("title")))) totalIssuedInp.addItem(rSet2.getString("title"));
					} while (rSet2.next());
				} catch (Exception e) { System.out.println(e); }
			}
		});
		
		resetStudent.setFocusable(false);
		resetStudent.addActionListener(event -> {
			searchStudentInp.setText("");
			addStudentComponents.setSelectedIndex(0);
		});
		addStudentComponents.setFont(new Font("", Font.BOLD, 18));
		addStudentComponents.addActionListener(event -> { searchStudent.doClick(); });
		searchStudent.setFont(new Font("", Font.BOLD, 18));
		searchStudentInp.setFont(new Font("Lucida Console", Font.PLAIN, 15));
		searchStudentsPanel.getComponent(0).setFont(new Font("Lucida Console", Font.PLAIN, 17));
		searchStudentsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Search Student", TitledBorder.RIGHT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 18), Color.MAGENTA));

		// Some Extra Components
		issue.setFocusable(false);
		issue.setFont(new Font("Verdana", Font.BOLD, 20));
		issue.addActionListener(event -> {
			if (bookIdInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Select a Book to Issue", "Unkown Book", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			else if (studentIdInp.getText().isBlank()) {
				JOptionPane.showConfirmDialog(null, "Please Select a Student to Issue a Book", "Unkown Student", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				try {
					PreparedStatement pstmt = conctn.prepareStatement("INSERT INTO books_issued(student_id, book_id, issued_date) VALUES(?, ?, ?);");
					pstmt.setLong(1, Long.valueOf(studentIdInp.getText().trim()));
					pstmt.setLong(2, Long.valueOf(bookIdInp.getText().trim()));
					pstmt.setString(3, issueDateInp.getText());

					if (pstmt.executeUpdate() == 1) {
						stmt.executeUpdate(MessageFormat.format("INSERT INTO logs(action, student_id, book_id) VALUES(''Book Issued'', {0}, {1});", studentIdInp.getText().trim(), bookIdInp.getText().trim()));
						JOptionPane.showMessageDialog(null, MessageFormat.format("{0} ({1}) is Successfully issued to {2} ({3})", titleInp.getText(), bookIdInp.getText(), nameInp.getText(), studentIdInp.getText()), "Book Issued", JOptionPane.INFORMATION_MESSAGE);
						resetStudent.doClick();
						resetBook.doClick();
					}
					else {
						JOptionPane.showMessageDialog(null, "Some Database issue Occured, Please try again!", "Database Issue", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (MySQLIntegrityConstraintViolationException e) {
					JOptionPane.showConfirmDialog(null, MessageFormat.format("{0} ({1}) is already issued by {2} ({3})", titleInp.getText(), bookIdInp.getText(), nameInp.getText(), studentIdInp.getText()), "Duplicate ID", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				catch (Exception e) { System.out.println(e); }
			}
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
		
		// Result Books Components
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 0, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		resultBooksPanel.add(searchBookResult, constraints);
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 0;
		resultBooksPanel.add(resetBook, constraints);
		
		// Search Books Components
		constraints = new GridBagConstraints();
		searchBooksPanel.add(new JLabel("By:"), constraints);
		constraints.insets = new Insets(6, 18, 7, 18);
		searchBooksPanel.add(addBookComponents, constraints);
		searchBooksPanel.add(searchBookInp, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.ipadx = 100;
		searchBooksPanel.add(searchBook, constraints);

		// Book Details Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(6, 18, 7, 18);

		constraints.gridwidth = 2;
		bookDetailsPanel.add(resultBooksPanel, constraints);
		
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		bookDetailsPanel.add(bookId, constraints);
		bookDetailsPanel.add(bookIdInp, constraints);
		constraints.gridy = 2;
		bookDetailsPanel.add(title, constraints);
		bookDetailsPanel.add(titleInp, constraints);
		constraints.gridy = 3;
		bookDetailsPanel.add(isbn, constraints);
		bookDetailsPanel.add(isbnInp, constraints);
		constraints.gridy = 4;
		bookDetailsPanel.add(publisher, constraints);
		bookDetailsPanel.add(publisherInp, constraints);
		constraints.gridy = 5;
		bookDetailsPanel.add(edition, constraints);
		bookDetailsPanel.add(editionInp, constraints);
		constraints.gridy = 6;
		bookDetailsPanel.add(price, constraints);
		bookDetailsPanel.add(priceInp, constraints);
		constraints.gridy = 7;
		bookDetailsPanel.add(pages, constraints);
		bookDetailsPanel.add(pagesInp, constraints);
		constraints.gridy = 8;
		bookDetailsPanel.add(issueDate, constraints);
		bookDetailsPanel.add(issueDateInp, constraints);
		constraints.gridy = 9;
		constraints.gridwidth = 2;
		bookDetailsPanel.add(searchBooksPanel, constraints);
		
		// Result Books Components
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 0, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		resultStudentsPanel.add(searchStudentResult, constraints);
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 0;
		resultStudentsPanel.add(resetStudent, constraints);
		
		// Search Students Components
		constraints = new GridBagConstraints();
		searchStudentsPanel.add(new JLabel("By:"), constraints);
		constraints.insets = new Insets(6, 18, 7, 18);
		searchStudentsPanel.add(addStudentComponents, constraints);
		searchStudentsPanel.add(searchStudentInp, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 3;
		constraints.ipadx = 100;
		searchStudentsPanel.add(searchStudent, constraints);
		
		// Student Details Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(6, 18, 7, 18);

		constraints.gridwidth = 2;
		studentDetailsPanel.add(resultStudentsPanel, constraints);
		
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		studentDetailsPanel.add(studentId, constraints);
		studentDetailsPanel.add(studentIdInp, constraints);
		constraints.gridy = 2;
		studentDetailsPanel.add(name, constraints);
		studentDetailsPanel.add(nameInp, constraints);
		constraints.gridy = 3;
		studentDetailsPanel.add(fathersName, constraints);
		studentDetailsPanel.add(fathersNameInp, constraints);
		constraints.gridy = 4;
		studentDetailsPanel.add(course, constraints);
		studentDetailsPanel.add(courseInp, constraints);
		constraints.gridy = 5;
		studentDetailsPanel.add(branch, constraints);
		studentDetailsPanel.add(branchInp, constraints);
		constraints.gridy = 6;
		studentDetailsPanel.add(year, constraints);
		studentDetailsPanel.add(yearInp, constraints);
		constraints.gridy = 7;
		studentDetailsPanel.add(semester, constraints);
		studentDetailsPanel.add(semesterInp, constraints);
		constraints.gridy = 8;
		studentDetailsPanel.add(totalIssued, constraints);
		studentDetailsPanel.add(totalIssuedInp, constraints);
		constraints.gridy = 9;
		constraints.gridwidth = 2;
		studentDetailsPanel.add(searchStudentsPanel, constraints);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 50;
		constraints.ipady = 25;
		constraints.insets = new Insets(8, 5, 8, 5);
		issueBooksPanel.add(bookDetailsPanel, constraints);
		issueBooksPanel.add(studentDetailsPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.anchor = GridBagConstraints.WEST;
		issueBooksPanel.add(back, constraints);
		constraints.ipadx = 95;
		constraints.anchor = GridBagConstraints.EAST;
		issueBooksPanel.add(issue, constraints);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {

		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - Issue Books");
		issueBooksPanel.setVisible(true);
	}
}