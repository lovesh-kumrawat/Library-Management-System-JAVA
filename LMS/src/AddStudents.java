import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Locale;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class AddStudents implements ActionListener {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;
	RuleBasedNumberFormat spell = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);

	// Student Details Components
	JLabel studentId = new JLabel("Student ID");
	JTextField studentIdInp = new JTextField(30);

	JLabel name = new JLabel("Name");
	JTextField nameInp = new JTextField(30);

	JLabel fathersName = new JLabel("Father's Name");
	JTextField fathersNameInp = new JTextField(30);

	JLabel course = new JLabel("Course");
	JComboBox<String> courseInp = new JComboBox<String>(new String[] {"----------", "B.Tech", "B.Sc", "B.Arch", "Pharma", "BDS"});

	JLabel branch = new JLabel("Branch");
	JComboBox<String> branchInp = new JComboBox<String>(new String[] {"Unselected Course"});

	JLabel year = new JLabel("Year");
	JComboBox<String> yearInp = new JComboBox<String>(new String[] {"----------"});

	JLabel semester = new JLabel("Semester");
	JComboBox<String> semesterInp = new JComboBox<String>(new String[] {"Unselected Year"});
	
	// Some Extra Components
	JButton add = new JButton("Add to Database");
	JButton back = new JButton("Back to Home");

	// Panels
	JPanel studentDetailsPanel = new JPanel(new GridBagLayout());
	JPanel addStudentsPanel = new JPanel(new GridBagLayout());
	
	public AddStudents() {}
	public AddStudents(Home root) {

		// Current Panel Settings
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		addStudentsPanel.setVisible(false);
		addStudentsPanel.setName("AddStudents");
		root.add(addStudentsPanel);
		addComponents();

		// Student Details Components
		branchInp.setPrototypeDisplayValue("");
		courseInp.setPrototypeDisplayValue("");
		courseInp.addActionListener(event -> {
			branchInp.removeAllItems();
			branchInp.addItem("----------");
			
			String items[];
			switch ((String) courseInp.getSelectedItem()) {
				case "B.Tech":
					items = new String[] {"Computer Science", "Information Technology", "Mechanical", "Electrical", "Automobile"};
					break;
			
				case "B.Sc":
					items = new String[] {"B.Sc Agriculture", "B.Sc Biotechnology", "B.Sc Nursing", "B.Sc. Physiotherapy"};
					break;
			
				case "B.Arch":
					items = new String[] {"Landscape Architecture", "Restoration Architecture", "Research Architecture"};
					break;
			
				case "Pharma":
					items = new String[] {"Pharmaceutical Formulation", "Pharmaceutical Manufacturing", "Dispensing Pharmacy"};
					break;
			
				case "BDS":
					items = new String[] {"Public Health Dentistry", "Oral Pathology and Microbiology", "Prosthodontics"};
					break;
			
				default:
					branchInp.removeAllItems();
					items = new String[] {"Unselected Course"};
			}
			
			for (String item : items) {
				branchInp.addItem(item);
			}
		});
		
		for (int i = 1; i < 5; i++) {
			yearInp.addItem(spell.format(i, "%spellout-ordinal"));
		}
		yearInp.addItem("Other");
		semesterInp.setPrototypeDisplayValue("");
		yearInp.setPrototypeDisplayValue("");
		
		yearInp.addActionListener(event -> {
			Object otherInp = "";
			if (yearInp.getSelectedItem().equals("Other")) {
				while (otherInp.equals("")) {
					otherInp = JOptionPane.showInputDialog(null, "Enter the Number:", "Year", JOptionPane.QUESTION_MESSAGE);
					
					// On "Cancel"
					if (otherInp == null) {
						yearInp.setSelectedItem("----------");
						break;
					}
				}
				
				// On "Ok"
				if (otherInp != null) {
					try {
						String item = spell.format(Double.valueOf(otherInp.toString()), "%spellout-ordinal");
						yearInp.removeItem(item);
						yearInp.insertItemAt(item, yearInp.getItemCount()-1);
						yearInp.setSelectedIndex(yearInp.getItemCount()-2);
					} catch (Exception e) {
						JOptionPane.showConfirmDialog(null, "Enter the Valid Number", "Invalid Input", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
						yearInp.setSelectedItem("----------");
					}
				}
			}
			
			if (yearInp.getSelectedItem().equals("----------")) {
				semesterInp.removeAllItems();
				semesterInp.addItem("Unselected Year");
			}
			else {
				semesterInp.removeAllItems();
				semesterInp.addItem("----------");

				try {
					Double Y = spell.parse((String) yearInp.getSelectedItem()).doubleValue();
					semesterInp.addItem(spell.format(Y * 2 - 1, "%spellout-ordinal"));
					semesterInp.addItem(spell.format(Y * 2, "%spellout-ordinal"));
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		studentDetailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Enter Student Details", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 40), new Color(232, 57, 95)));
		for (int i=0; i<studentDetailsPanel.getComponentCount(); i++) {
			if (i%2==0) {
				studentDetailsPanel.getComponent(i).setFont(new Font("", Font.BOLD, 30));
				studentDetailsPanel.getComponent(i).setPreferredSize(new Dimension(250, 35));
			}
			else studentDetailsPanel.getComponent(i).setFont(new Font("Lucida Console", Font.PLAIN, 25));
		}

		// Some Extra Components
		add.setFont(new Font("Verdana", Font.BOLD, 20));
		add.setFocusable(false);
		add.addActionListener(event -> {
			boolean empty = false;
			for (int i = 1; i < studentDetailsPanel.getComponentCount(); i=i+2) {
				if ((i <= 5 && (empty = ((JTextField) studentDetailsPanel.getComponent(i)).getText().isBlank()))
					|| (i > 5 && (empty = ((JComboBox<?>) studentDetailsPanel.getComponent(i)).getSelectedItem().equals("----------")))) {
					JOptionPane.showConfirmDialog(null, "Please fill the '" + ((JLabel) studentDetailsPanel.getComponent(i - 1)).getText() + "' Field", "Empty Field", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				if ((i == 3 || i==5) && (empty = ! ((JTextField) studentDetailsPanel.getComponent(i)).getText().matches("[a-zA-Z\s]+"))) {
					JOptionPane.showConfirmDialog(null, "Please confirm that '" + ((JLabel) studentDetailsPanel.getComponent(i - 1)).getText() + "' Field contains only Alphabets & Spaces", "Invalid Text", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
					break;
				}
			}

			if (!empty) {
				try {
					PreparedStatement pstmt = conctn.prepareStatement("INSERT INTO students_info(student_id, name, fathers_name, course, branch, year, semester) VALUES(?, ?, ?, ?, ?, ?, ?);");
					pstmt.setLong(1, Long.valueOf(studentIdInp.getText().trim()));
					pstmt.setString(2, nameInp.getText().trim());
					pstmt.setString(3, fathersNameInp.getText().trim());
					pstmt.setString(4, String.valueOf(courseInp.getSelectedItem()));
					pstmt.setString(5, String.valueOf(branchInp.getSelectedItem()));
					pstmt.setString(6, String.valueOf(yearInp.getSelectedItem()));
					pstmt.setString(7, String.valueOf(semesterInp.getSelectedItem()));
					if (pstmt.executeUpdate() == 1) {
						stmt.executeUpdate(MessageFormat.format("INSERT INTO logs(action, student_id) VALUES(''Student Added'', {0});", studentIdInp.getText().trim()));

						for (int i = 1; i < 7; i=i+2) {
							((JTextField) studentDetailsPanel.getComponent(i)).setText("");
						}
						for (int i = 7; i < studentDetailsPanel.getComponentCount(); i=i+2) {
							((JComboBox<?>) studentDetailsPanel.getComponent(i)).setSelectedItem("----------");
						}
						JOptionPane.showMessageDialog(null, "Student has been registered Successfully", "Student Added", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null, "Some Database issue Occured, Please try again!", "Database Issue", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (MySQLIntegrityConstraintViolationException e) {
					JOptionPane.showConfirmDialog(null, "Given Student ID is already registered to another Student", "Duplicate ID", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				catch (NumberFormatException e) {
					JOptionPane.showConfirmDialog(null, "Student ID must be limited and a Number", "Faulty Format", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				catch (Exception e) {
					System.out.println("Error logs: " + e);
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
		
		// Student Details Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(12, 25, 12, 25);

		studentDetailsPanel.add(studentId, constraints);
		studentDetailsPanel.add(studentIdInp, constraints);
		constraints.gridy = 1;
		studentDetailsPanel.add(name, constraints);
		studentDetailsPanel.add(nameInp, constraints);
		constraints.gridy = 2;
		studentDetailsPanel.add(fathersName, constraints);
		studentDetailsPanel.add(fathersNameInp, constraints);
		constraints.gridy = 3;
		studentDetailsPanel.add(course, constraints);
		studentDetailsPanel.add(courseInp, constraints);
		constraints.gridy = 4;
		studentDetailsPanel.add(branch, constraints);
		studentDetailsPanel.add(branchInp, constraints);
		constraints.gridy = 5;
		studentDetailsPanel.add(year, constraints);
		studentDetailsPanel.add(yearInp, constraints);
		constraints.gridy = 6;
		studentDetailsPanel.add(semester, constraints);
		studentDetailsPanel.add(semesterInp, constraints);

		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 150;
		constraints.ipady = 50;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(12, 25, 12, 25);
		addStudentsPanel.add(studentDetailsPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		addStudentsPanel.add(back, constraints);
		constraints.ipadx = 80;
		constraints.anchor = GridBagConstraints.EAST;
		addStudentsPanel.add(add, constraints);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {

		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - Add Students");
		addStudentsPanel.setVisible(true);
	}
}