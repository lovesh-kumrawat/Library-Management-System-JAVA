import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class BooksInfo implements ActionListener {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;
	IssueBooks issueBooks = new IssueBooks();

	// Search Books Components
	JButton resetBook = new JButton("Reset");
	JButton searchBook = new JButton("Search");
	JTextField searchBookInp = new JTextField(20);
	JComboBox<String> addBookComponents = issueBooks.addBookComponents;

	String data[][];
	String columns[] = {
		issueBooks.bookId.getText(),
		issueBooks.title.getText(),
		issueBooks.isbn.getText(),
		issueBooks.publisher.getText(),
		issueBooks.edition.getText(),
		issueBooks.price.getText(),
		issueBooks.pages.getText()
	};
	HashMap<String, String> dbBookStudentHeader = issueBooks.dbBookStudentHeader;

	JTable booksList;
	JScrollPane scrollableBooksList = new JScrollPane(booksList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	// Some Extra Components
	JButton delete = new JButton("Delete Books");
	JButton back = new JButton("Back to Home");
	
	// Panels
	JPanel searchBooksPanel = new JPanel(new GridBagLayout());
	JPanel booksInfoPanel = new JPanel(new GridBagLayout());

	public BooksInfo(Home root, JPanel previous) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		booksInfoPanel.setVisible(false);
		booksInfoPanel.setName("BooksInfo");
		root.add(booksInfoPanel);
		addComponents();
		
		// Search Books Components
		addBookComponents.setFont(new Font("", Font.BOLD, 16));
		
		searchBookInp.setFont(new Font("Lucida Console", Font.PLAIN, 26));
		searchBook.setFocusable(false);
		searchBook.setFont(new Font("", Font.BOLD, 15));
		searchBook.addActionListener(event -> {
			try {
				ResultSet rSet;
				String searchQuery, dbSearchSelection = dbBookStudentHeader.get(addBookComponents.getSelectedItem());

				if (searchBookInp.getText().isBlank()) {
					rSet = stmt.executeQuery(searchQuery = "SELECT * FROM books_info;");
				}
				else {
					searchQuery = MessageFormat.format("SELECT * FROM books_info WHERE {0} like ''%{1}%'';", dbSearchSelection, searchBookInp.getText().trim());
					rSet = stmt.executeQuery(searchQuery);
				}
				
				rSet.last();
				data = new String[rSet.getRow()][7];
				rSet.beforeFirst();

				while (rSet.next()) {
					data[rSet.getRow()-1][0] = rSet.getString("book_id");
					data[rSet.getRow()-1][1] = rSet.getString("title");
					data[rSet.getRow()-1][2] = rSet.getString("isbn");
					data[rSet.getRow()-1][3] = rSet.getString("publisher");
					data[rSet.getRow()-1][4] = rSet.getString("edition");
					data[rSet.getRow()-1][5] = rSet.getString("price");
					data[rSet.getRow()-1][6] = rSet.getString("pages");
				}
				
				Statement stmt2 = conctn.createStatement();
				ResultSet rSetCount = stmt2.executeQuery("SELECT COUNT(*) as totalBooks FROM books_info;");
				rSetCount.next();
				Statement stmt3 = conctn.createStatement();
				ResultSet rSetSearchedCount = stmt3.executeQuery(searchQuery.replaceFirst(" \\* ", " COUNT(*) as searchedBooks "));
				rSetSearchedCount.next();
				
				scrollableBooksList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Books", rSetSearchedCount.getString("searchedBooks"), rSetCount.getString("totalBooks")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
				scrollableBooksList.repaint();
				scrollableBooksList.setViewportView(booksList = new JTable(data, columns) {
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				});

				booksList.setFocusable(false);
				booksList.setRowHeight(20);
				booksList.setForeground(Color.WHITE);
				booksList.setBackground(new Color(232, 57, 95));
				booksList.setFont(new Font("Segoi UI", Font.PLAIN, 16));
				booksList.setSelectionBackground(new Color(132, 57, 95));
				booksList.setSelectionForeground(new Color(215, 255, 255));
				booksList.getTableHeader().setForeground(Color.WHITE);
				booksList.getTableHeader().setBackground(new Color(32, 136, 203));
				booksList.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 16));
				booksList.getSelectionModel().addListSelectionListener(event2 -> {
					if (event2.getValueIsAdjusting()) {
						try {
							rSetCount.first();
							rSetSearchedCount.first();
							scrollableBooksList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Books ({2} Selected)", rSetSearchedCount.getString("searchedBooks"), rSetCount.getString("totalBooks"), booksList.getSelectionModel().getSelectedItemsCount()), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
							scrollableBooksList.repaint();
						} catch (Exception e) { System.out.println("Error logs: " + e); }
					}
				});
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		resetBook.setFocusable(false);
		resetBook.setFont(new Font("", Font.BOLD, 15));
		resetBook.addActionListener(event -> {
			addBookComponents.setSelectedIndex(0);
			searchBookInp.setText("");
			searchBook.doClick();
		});
		
		searchBooksPanel.getComponent(0).setFont(new Font("Lucida Console", Font.PLAIN, 17));
		searchBooksPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Books Info", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 40), Color.MAGENTA));

		searchBook.doClick();
		scrollableBooksList.setPreferredSize(new Dimension(0, 400));
		scrollableBooksList.setBorder(BorderFactory.createLoweredBevelBorder());
		try {
			ResultSet rSetCount = stmt.executeQuery("SELECT COUNT(*) as totalBooks FROM books_info;");
			rSetCount.next();

			scrollableBooksList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {0} Books", rSetCount.getString("totalBooks")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
			scrollableBooksList.repaint();
		} catch (Exception e) { System.out.println("Error logs: " + e); }

		// Some Extra Components
		delete.setVisible(!StudentSection.roleStudent);
		delete.setFocusable(false);
		delete.setFont(new Font("Verdana", Font.BOLD, 20));
		delete.addActionListener(event -> {
			try {
				ListSelectionModel bookSelectionModel = booksList.getSelectionModel();
				int yn = JOptionPane.showConfirmDialog(null, "Are you sure you want to Delete the Selected Books", "Delete Books", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (yn == 0) {
					String deletingList = "";
					for (int i : bookSelectionModel.getSelectedIndices()) {
						if (deletingList.equals("")) deletingList = String.valueOf(booksList.getValueAt(i, 0));
						else deletingList = String.join(", ", deletingList, String.valueOf(booksList.getValueAt(i, 0)));
					}

					int deleteCount = stmt.executeUpdate(MessageFormat.format("DELETE FROM books_info WHERE book_id in ({0})", deletingList));
					if (deleteCount > 0) {
						searchBook.doClick();
						JOptionPane.showConfirmDialog(null, bookSelectionModel.getSelectedItemsCount() + (deleteCount == 1 ? " Book is" : " Books are") +" Deleted Successfully", "Delete Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showConfirmDialog(null, "Please try Again Some Issue Ouccered", "Issue Ouccered", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
				}
			}
			catch (MySQLIntegrityConstraintViolationException e) {
				JOptionPane.showConfirmDialog(null, "Sorry you can't able to Delete these Books because some of them are Issued by Someone", "Some are Issued", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
			}
			catch (Exception e) { System.out.println(e); }
		});

		back.setText("Back to " + previous.getName());
		back.setFocusable(false);
		back.setFont(new Font("Verdana", Font.BOLD, 20));
		back.addActionListener(event -> {
			for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
				root.getContentPane().getComponent(i).setVisible(false);
			}

			root.setTitle("Library Management System - " + (previous.getName() == "Home" ? "Home" : "Student Issued Books"));
			previous.setVisible(true);
		});
	}
	
	public void addComponents() {

		// Search Books Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(6, 18, 7, 0);
		searchBooksPanel.add(new JLabel("By:"), constraints);
		searchBooksPanel.add(addBookComponents, constraints);
		constraints.ipadx = 200;
		searchBooksPanel.add(searchBookInp, constraints);
		constraints.ipadx = 100;
		constraints.insets = new Insets(6, 0, 7, 0);
		searchBooksPanel.add(searchBook, constraints);
		constraints.ipadx = 0;
		constraints.insets = new Insets(6, 10, 7, 18);
		searchBooksPanel.add(resetBook, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 5;
		constraints.insets = new Insets(6, 18, 7, 18);
		searchBooksPanel.add(scrollableBooksList, constraints);
		
		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 30;
		constraints.ipady = 25;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(8, 5, 8, 5);
		booksInfoPanel.add(searchBooksPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		booksInfoPanel.add(back, constraints);
		constraints.ipadx = 65;
		constraints.anchor = GridBagConstraints.EAST;
		booksInfoPanel.add(delete, constraints);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - Books Info");
		searchBook.doClick();
		booksInfoPanel.setVisible(true);
	}
}