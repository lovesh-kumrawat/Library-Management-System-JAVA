import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class AddBooks implements ActionListener {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;

	// Book Details Components
	JLabel bookId = new JLabel("Book ID");
	JTextField bookIdInp = new JTextField(30);

	JLabel title = new JLabel("Title");
	JTextField titleInp = new JTextField(30);

	JLabel isbn = new JLabel("ISBN");
	JTextField isbnInp = new JTextField(30);

	JLabel publisher = new JLabel("Publisher");
	JTextField publisherInp = new JTextField(30);

	JLabel edition = new JLabel("Edition");
	JTextField editionInp = new JTextField(30);

	JLabel price = new JLabel("Price");
	JTextField priceInp = new JTextField(30);

	JLabel pages = new JLabel("Pages");
	JTextField pagesInp = new JTextField(30);
	
	// Some Extra Components
	JButton add = new JButton("Add to Library");
	JButton back = new JButton("Back to Home");

	// Panels
	JPanel bookDetailsPanel = new JPanel(new GridBagLayout());
	JPanel addBooksPanel = new JPanel(new GridBagLayout());
	
	public AddBooks() {}
	public AddBooks(Home root) {
		
		// Current Panel Settings
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		addBooksPanel.setVisible(false);
		addBooksPanel.setName("AddBooks");
		root.add(addBooksPanel);
		addComponents();
		
		// Book Details Components
		bookDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Enter Book Details", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 40), new Color(232, 57, 95)));
		for (int i=0; i<bookDetailsPanel.getComponentCount(); i++) {
			if (i%2==0) {
				bookDetailsPanel.getComponent(i).setFont(new Font("", Font.BOLD, 30));
				bookDetailsPanel.getComponent(i).setPreferredSize(new Dimension(250, 35));
			}
			else bookDetailsPanel.getComponent(i).setFont(new Font("Lucida Console", Font.PLAIN, 25));
		}

		// Some Extra Components
		add.setFont(new Font("Verdana", Font.BOLD, 20));
		add.setFocusable(false);
		add.addActionListener(event -> {
			boolean empty = false;
			for (int i = 1; i < bookDetailsPanel.getComponentCount(); i=i+2) {
				if (empty = ((JTextField) bookDetailsPanel.getComponent(i)).getText().isBlank()) {
					JOptionPane.showConfirmDialog(null, "Please fill the '" + ((JLabel) bookDetailsPanel.getComponent(i - 1)).getText() + "' Field", "Empty Field", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				if ((i == 3 || i == 7) && (empty = ! ((JTextField) bookDetailsPanel.getComponent(i)).getText().matches("[a-zA-Z\s]+"))) {
					JOptionPane.showConfirmDialog(null, "Please confirm that '" + ((JLabel) bookDetailsPanel.getComponent(i - 1)).getText() + "' Field contains only Alphabets & Spaces", "Invalid Text", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					break;
				}
			}

			if (!empty) {
				int isbnLen = isbnInp.getText().length();
				if (isbnLen != 10 && isbnLen != 13) {
					JOptionPane.showConfirmDialog(null, "ISBN must be a 10 or 13 digit Number", "Wrong ISBN Number", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				else {
					try {
						PreparedStatement pstmt = conctn.prepareStatement("INSERT INTO books_info(book_id, title , isbn , publisher , edition , price , pages) VALUES(?, ?, ?, ?, ?, ?, ?);");
						pstmt.setLong(1, Long.valueOf(bookIdInp.getText().trim()));
						pstmt.setString(2, titleInp.getText().trim());
						pstmt.setLong(3, Long.valueOf(isbnInp.getText().trim()));
						pstmt.setString(4, publisherInp.getText().trim());
						pstmt.setInt(5, Integer.valueOf(editionInp.getText().trim()));
						pstmt.setLong(6, Long.valueOf(priceInp.getText().trim()));
						pstmt.setInt(7, Integer.valueOf(pagesInp.getText().trim()));
						if (pstmt.executeUpdate() == 1) {
							stmt.executeUpdate(MessageFormat.format("INSERT INTO logs(action, book_id) VALUES(''Book Added'', {0});", bookIdInp.getText().trim()));

							for (int i = 1; i < bookDetailsPanel.getComponentCount(); i=i+2) {
								((JTextField) bookDetailsPanel.getComponent(i)).setText("");
							}
							JOptionPane.showMessageDialog(null, "Book has been added Successfully", "Book Added", JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							JOptionPane.showMessageDialog(null, "Some Database issue Occured, Please try again!", "Database Issue", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (MySQLIntegrityConstraintViolationException e) {
						JOptionPane.showConfirmDialog(null, "Given Book ID or ISBN Number is already registered to another Book", "Duplicate ID", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
					catch (NumberFormatException e) {
						JOptionPane.showConfirmDialog(null, "Book ID, ISBN, Edition, Price, Pages are must be limited and a Number", "Faulty Format", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					}
					catch (Exception e) {
						System.out.println("Error logs: " + e);
					}
				}
			}
		});

		back.setFont(new Font("Verdana", Font.BOLD, 20));
		back.setFocusable(false);
		back.addActionListener(event -> {
			for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
				root.getContentPane().getComponent(i).setVisible(false);
			}
			
			root.setTitle("Library Management System - Home");
			root.homePanel.setVisible(true);
		});
	}
	
	public void addComponents() {
		
		// Book Details Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(12, 25, 12, 25);

		bookDetailsPanel.add(bookId, constraints);
		bookDetailsPanel.add(bookIdInp, constraints);
		constraints.gridy = 1;
		bookDetailsPanel.add(title, constraints);
		bookDetailsPanel.add(titleInp, constraints);
		constraints.gridy = 2;
		bookDetailsPanel.add(isbn, constraints);
		bookDetailsPanel.add(isbnInp, constraints);
		constraints.gridy = 3;
		bookDetailsPanel.add(publisher, constraints);
		bookDetailsPanel.add(publisherInp, constraints);
		constraints.gridy = 4;
		bookDetailsPanel.add(edition, constraints);
		bookDetailsPanel.add(editionInp, constraints);
		constraints.gridy = 5;
		bookDetailsPanel.add(price, constraints);
		bookDetailsPanel.add(priceInp, constraints);
		constraints.gridy = 6;
		bookDetailsPanel.add(pages, constraints);
		bookDetailsPanel.add(pagesInp, constraints);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 150;
		constraints.ipady = 50;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(12, 25, 12, 25);
		addBooksPanel.add(bookDetailsPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		addBooksPanel.add(back, constraints);
		constraints.ipadx = 95;
		constraints.anchor = GridBagConstraints.EAST;
		addBooksPanel.add(add, constraints);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {

		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - Add Books");
		addBooksPanel.setVisible(true);
	}
}