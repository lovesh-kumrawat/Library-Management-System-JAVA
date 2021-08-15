import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Home extends JFrame {
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;
	static Boolean validLogin = false;

	// Home Components
	JButton addBooks = new JButton("Add Books", new ImageIcon(getClass().getResource("icons/AddBooks.png")));
	JButton addStudents = new JButton("Add Students", new ImageIcon(getClass().getResource("icons/AddStudents.png")));
	JButton logs = new JButton("Logs", new ImageIcon(getClass().getResource("icons/Statistics.png")));
	
	JButton issueBooks = new JButton("Issue Books", new ImageIcon(getClass().getResource("icons/IssueBooks.png")));
	JButton returnBooks = new JButton("Return Books", new ImageIcon(getClass().getResource("icons/ReturnBooks.png")));
	JButton reissueBooks = new JButton("Reissue Books", new ImageIcon(getClass().getResource("icons/ReissueBooks.png")));
	
	JButton studentsInfo = new JButton("Students Info", new ImageIcon(getClass().getResource("icons/StudentsInfo.png")));
	JButton showBooks = new JButton("Show Books", new ImageIcon(getClass().getResource("icons/ShowBooks.png")));
	JButton aboutUs = new JButton("About Us", new ImageIcon(getClass().getResource("icons/AboutUs.png")));
	
	// Panels
	JPanel operationsPanel = new JPanel(new GridLayout(1, 3));
	JPanel actionsPanel = new JPanel(new GridLayout(1, 3));
	JPanel infoPanel = new JPanel(new GridLayout(3, 1));
	JPanel homePanel = new JPanel(new GridBagLayout());

	public Home() {

		// SQL Connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conctn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?characterEncoding=latin1", "root", "root@mysql");
			stmt = conctn.createStatement();
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS lms;");
			stmt.executeUpdate("USE lms;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS books_info(book_id BIGINT PRIMARY KEY, title VARCHAR(25), isbn BIGINT UNIQUE, publisher VARCHAR(25), edition INT, price BIGINT, pages INT);");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS students_info(student_id BIGINT PRIMARY KEY, name VARCHAR(25), fathers_name VARCHAR(25), course VARCHAR(50), branch VARCHAR(50), year VARCHAR(50), semester VARCHAR(50), password VARCHAR(30) DEFAULT 'password');");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS books_issued(student_id BIGINT, book_id BIGINT, issued_date DATE, FOREIGN KEY(student_id) REFERENCES students_info(student_id), FOREIGN KEY(book_id) REFERENCES books_info(book_id), PRIMARY KEY(student_id, book_id));");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin_login(username VARCHAR(15) PRIMARY KEY, name VARCHAR(30), password VARCHAR(30), security_question VARCHAR(50), answer VARCHAR(50));");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS logs(action VARCHAR(15), student_id BIGINT, book_id BIGINT, date DATE DEFAULT (CURRENT_DATE), time TIME DEFAULT (CURRENT_TIME));");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Database not connected, Please restart your App", "Database Issue", JOptionPane.ERROR_MESSAGE);
		}
		
		// Project Settings
		setVisible(true);
		homePanel.setName("Home");
		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage("src/icons/LMSIcon.png"));

		new Login(this);
		// validLogin = true;
		
		// Operation Components
		addBooks.setHorizontalTextPosition(SwingConstants.CENTER);
		addBooks.setVerticalTextPosition(SwingConstants.BOTTOM);
		addBooks.setBackground(Color.decode("#eeeeee"));
		addBooks.setBorderPainted(false);
		addBooks.setFocusable(false);
		addBooks.addActionListener(new AddBooks(this));

		addStudents.setHorizontalTextPosition(SwingConstants.CENTER);
		addStudents.setVerticalTextPosition(SwingConstants.BOTTOM);
		addStudents.setBackground(Color.decode("#eeeeee"));
		addStudents.setBorderPainted(false);
		addStudents.setFocusable(false);
		addStudents.addActionListener(new AddStudents(this));
		
		logs.setHorizontalTextPosition(SwingConstants.CENTER);
		logs.setVerticalTextPosition(SwingConstants.BOTTOM);
		logs.setBackground(Color.decode("#eeeeee"));
		logs.setBorderPainted(false);
		logs.setFocusable(false);
		logs.addActionListener(new Logs(this));
		
		operationsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Operations", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 30), Color.MAGENTA));
		
		// Action Components
		issueBooks.setHorizontalTextPosition(SwingConstants.CENTER);
		issueBooks.setVerticalTextPosition(SwingConstants.BOTTOM);
		issueBooks.setBackground(Color.decode("#eeeeee"));
		issueBooks.setBorderPainted(false);
		issueBooks.setFocusable(false);
		issueBooks.addActionListener(new IssueBooks(this, false));

		returnBooks.setHorizontalTextPosition(SwingConstants.CENTER);
		returnBooks.setVerticalTextPosition(SwingConstants.BOTTOM);
		returnBooks.setBackground(Color.decode("#eeeeee"));
		returnBooks.setBorderPainted(false);
		returnBooks.setFocusable(false);
		returnBooks.addActionListener(new ReturnBooks(this));
		
		reissueBooks.setHorizontalTextPosition(SwingConstants.CENTER);
		reissueBooks.setVerticalTextPosition(SwingConstants.BOTTOM);
		reissueBooks.setBackground(Color.decode("#eeeeee"));
		reissueBooks.setBorderPainted(false);
		reissueBooks.setFocusable(false);
		reissueBooks.addActionListener(new ReIssueBooks(this));
		
		actionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Actions", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 30), Color.MAGENTA));
		
		// Info Components
		studentsInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		studentsInfo.setVerticalTextPosition(SwingConstants.BOTTOM);
		studentsInfo.setBackground(Color.decode("#eeeeee"));
		studentsInfo.setBorderPainted(false);
		studentsInfo.setFocusable(false);
		studentsInfo.addActionListener(new StudentsInfo(this));

		showBooks.setHorizontalTextPosition(SwingConstants.CENTER);
		showBooks.setVerticalTextPosition(SwingConstants.BOTTOM);
		showBooks.setBackground(Color.decode("#eeeeee"));
		showBooks.setBorderPainted(false);
		showBooks.setFocusable(false);
		showBooks.addActionListener(new BooksInfo(this, homePanel));
		
		aboutUs.setHorizontalTextPosition(SwingConstants.CENTER);
		aboutUs.setVerticalTextPosition(SwingConstants.BOTTOM);
		aboutUs.setBackground(Color.decode("#eeeeee"));
		aboutUs.setBorderPainted(false);
		aboutUs.setFocusable(false);
		aboutUs.addActionListener(new AboutUs(this));

		infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Info", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Comic Sans MS", Font.BOLD, 30), Color.MAGENTA));

		// MainPanel
		homePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Library Management System", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 45), new Color(232, 57, 95)));
	}
	
	public void addComponents() {
		
		// Home Components
		operationsPanel.add(addBooks);
		operationsPanel.add(addStudents);
		operationsPanel.add(logs);
		
		actionsPanel.add(issueBooks);
		actionsPanel.add(returnBooks);
		actionsPanel.add(reissueBooks);
		
		infoPanel.add(studentsInfo);
		infoPanel.add(showBooks);
		infoPanel.add(aboutUs);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 200;
		constraints.ipady = 15;
		constraints.insets = new Insets(10, 20, 10, 20);
		homePanel.add(operationsPanel, constraints);
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.SOUTH;
		homePanel.add(actionsPanel, constraints);
		constraints.gridy = 0;
		constraints.ipadx = 50;
		constraints.gridheight = 2;
		homePanel.add(infoPanel, constraints);
	}
	
	public static void main(String args[]) {
		
		// Project Settings
		Home root = new Home();
		root.setResizable(false);

		// Current Panel Settings
		root.homePanel.setVisible(false);
		root.add(root.homePanel);
		root.addComponents();

		// Login Validation
		while (!validLogin) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) { System.out.println(e); }
		}

		// Panel Activation Settings
		root.setTitle("Library Management System - Home");
		for (int i = 700, j = 415; i <= 1144 && j <= 674; i+=12, j+=7) {
			try {
				Thread.sleep(5);
			} catch (Exception e) {}
			root.setSize(i, j);			// Animated Resizing (1144, 674)
			root.setLocationRelativeTo(null);
		}
		root.homePanel.setVisible(true);
		
		// root.studentsInfo.doClick();			//@ Temporary
	}
}