import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.util.HashMap;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;

public class StudentSection {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;
	static boolean roleStudent = false;
	IssueBooks issueBooks = new IssueBooks();

	// Search Students Components
	JButton resetStudentBook = new JButton("Reset");
	JButton searchStudentBook = new JButton("Search");
	JTextField searchStudentBookInp = new JTextField(20);
	JComboBox<String> addBookComponents = issueBooks.addBookComponents;

	String data[][];
	String columns[] = {
		issueBooks.bookId.getText(),
		issueBooks.title.getText(),
		issueBooks.isbn.getText(),
		issueBooks.publisher.getText(),
		issueBooks.edition.getText(),
		issueBooks.price.getText(),
		issueBooks.pages.getText(),
		issueBooks.issueDate.getText()
	};
	HashMap<String, String> dbBookStudentHeader = issueBooks.dbBookStudentHeader;

	JTable studentBookList;
	JScrollPane scrollableStudentsList = new JScrollPane(studentBookList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	// Some Extra Components
	JButton show = new JButton("Show All Books");
	JButton back = new JButton("Back to List");
	
	// Panels
	JPanel searchStudentBookPanel = new JPanel(new GridBagLayout());
	JPanel studentSectionPanel = new JPanel(new GridBagLayout());

	public StudentSection(Home root, JPanel previous, String studentId, String studentName) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;

		if (previous.getName() == null) {
			roleStudent = true;
			back.setVisible(false);
			for (int i = 700, j = 415; i <= 1144 && j <= 674; i+=12, j+=7) {
				try {
					Thread.sleep(5);
				} catch (Exception e) {}
				root.setSize(i, j);			// Animated Resizing (1144, 674)
				root.setLocationRelativeTo(null);
			}
		}

		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		root.setTitle("Library Management System - Student Issued Books");
		studentSectionPanel.setName("Issued");
		root.add(studentSectionPanel);
		addComponents();
		
		// Search Students Components
		addBookComponents.setFont(new Font("", Font.BOLD, 16));
		
		searchStudentBookInp.setFont(new Font("Lucida Console", Font.PLAIN, 26));
		searchStudentBook.setFocusable(false);
		searchStudentBook.setFont(new Font("", Font.BOLD, 15));
		searchStudentBook.addActionListener(event -> {
			try {
				ResultSet rSet;
				String searchQuery, dbSearchSelection = dbBookStudentHeader.get(addBookComponents.getSelectedItem());

				if (searchStudentBookInp.getText().isBlank()) {
					searchQuery = MessageFormat.format("SELECT student_id, book.*, issued_date FROM books_issued JOIN students_info USING(student_id) JOIN books_info as book USING(book_id) WHERE student_id = {0}", studentId);
				}
				else {
					searchQuery = MessageFormat.format("SELECT student_id, book.*, issued_date FROM books_issued JOIN students_info USING(student_id) JOIN books_info as book USING(book_id) WHERE student_id = {0} && {1} like ''%{2}%''", studentId, dbSearchSelection, searchStudentBookInp.getText().trim());
				}
				
				rSet = stmt.executeQuery(searchQuery);

				rSet.last();
				data = new String[rSet.getRow()][8];
				rSet.beforeFirst();

				while (rSet.next()) {
					data[rSet.getRow()-1][0] = rSet.getString("book_id");
					data[rSet.getRow()-1][1] = rSet.getString("title");
					data[rSet.getRow()-1][2] = rSet.getString("isbn");
					data[rSet.getRow()-1][3] = rSet.getString("publisher");
					data[rSet.getRow()-1][4] = rSet.getString("edition");
					data[rSet.getRow()-1][5] = rSet.getString("price");
					data[rSet.getRow()-1][6] = rSet.getString("pages");
					data[rSet.getRow()-1][7] = rSet.getString("issued_date");
				}
				
				Statement stmt2 = conctn.createStatement();
				ResultSet rSetCount = stmt2.executeQuery(MessageFormat.format("SELECT COUNT(*) as totalStudentBook FROM books_issued JOIN students_info USING(student_id) JOIN books_info as book USING(book_id) WHERE student_id = {0}", studentId));
				rSetCount.next();
				Statement stmt3 = conctn.createStatement();
				ResultSet rSetSearchedCount = stmt3.executeQuery(MessageFormat.format("SELECT COUNT(*) as searchedStudentBook FROM ({0}) as new_table;", searchQuery));
				rSetSearchedCount.next();
				
				scrollableStudentsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Books Issued", rSetSearchedCount.getString("searchedStudentBook"), rSetCount.getString("totalStudentBook")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
				scrollableStudentsList.repaint();
				scrollableStudentsList.setViewportView(studentBookList = new JTable(data, columns) {
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				});

				studentBookList.setFocusable(false);
				studentBookList.setRowHeight(20);
				studentBookList.setForeground(Color.WHITE);
				studentBookList.setBackground(new Color(232, 57, 95));
				studentBookList.setFont(new Font("Segoi UI", Font.PLAIN, 16));
				studentBookList.setSelectionBackground(new Color(132, 57, 95));
				studentBookList.setSelectionForeground(new Color(215, 255, 255));
				studentBookList.getTableHeader().setForeground(Color.WHITE);
				studentBookList.getTableHeader().setBackground(new Color(32, 136, 203));
				studentBookList.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 16));
				studentBookList.getSelectionModel().addListSelectionListener(event2 -> {
					if (event2.getValueIsAdjusting()) {
						try {
							rSetCount.first();
							rSetSearchedCount.first();
							scrollableStudentsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Books Issued ({2} Selected)", rSetSearchedCount.getString("searchedStudentBook"), rSetCount.getString("totalStudentBook"), studentBookList.getSelectionModel().getSelectedItemsCount()), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
							scrollableStudentsList.repaint();
						} catch (Exception e) { System.out.println("Error logs: " + e); }
					}
				});
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		resetStudentBook.setFocusable(false);
		resetStudentBook.setFont(new Font("", Font.BOLD, 15));
		resetStudentBook.addActionListener(event -> {
			addBookComponents.setSelectedIndex(0);
			searchStudentBookInp.setText("");
			searchStudentBook.doClick();
		});
		
		searchStudentBookPanel.getComponent(0).setFont(new Font("Lucida Console", Font.PLAIN, 17));
		searchStudentBookPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), studentName + " (" + studentId + ")", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 40), Color.MAGENTA));

		searchStudentBook.doClick();
		scrollableStudentsList.setPreferredSize(new Dimension(0, 400));
		scrollableStudentsList.setBorder(BorderFactory.createLoweredBevelBorder());

		// Some Extra Components
		show.setFocusable(false);
		show.setFont(new Font("Verdana", Font.BOLD, 20));
		show.addActionListener(new BooksInfo(root, studentSectionPanel));

		back.setFocusable(false);
		back.setFont(new Font("Verdana", Font.BOLD, 20));
		back.addActionListener(event -> {
			for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
				root.getContentPane().getComponent(i).setVisible(false);
			}

			root.setTitle("Library Management System - Students Info");
			previous.setVisible(true);
		});
	}
	
	public void addComponents() {

		// Search Students Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(6, 18, 7, 0);
		searchStudentBookPanel.add(new JLabel("By:"), constraints);
		searchStudentBookPanel.add(addBookComponents, constraints);
		constraints.ipadx = 200;
		searchStudentBookPanel.add(searchStudentBookInp, constraints);
		constraints.ipadx = 100;
		constraints.insets = new Insets(6, 0, 7, 0);
		searchStudentBookPanel.add(searchStudentBook, constraints);
		constraints.ipadx = 0;
		constraints.insets = new Insets(6, 10, 7, 18);
		searchStudentBookPanel.add(resetStudentBook, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 5;
		constraints.insets = new Insets(6, 18, 7, 18);
		searchStudentBookPanel.add(scrollableStudentsList, constraints);
		
		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 30;
		constraints.ipady = 25;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(8, 5, 8, 5);
		studentSectionPanel.add(searchStudentBookPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		studentSectionPanel.add(back, constraints);
		constraints.ipadx = 65;
		constraints.anchor = GridBagConstraints.EAST;
		studentSectionPanel.add(show, constraints);
	}
}