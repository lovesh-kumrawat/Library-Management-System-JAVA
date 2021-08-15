import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.text.MessageFormat;
import javax.swing.border.TitledBorder;

public class Logs implements ActionListener {
	Home root;
	Statement stmt;
	Connection conctn;
	GridBagConstraints constraints;

	// Search Logs Components
	String data[][];
	String columns[] = {"Action", "Student ID", "Book ID", "Date", "Time"};
	
	JButton resetLogs = new JButton("Reset");
	JButton searchLogs = new JButton("Search");
	JTextField searchLogsInp = new JTextField(20);
	JComboBox<String> searchLogComponents = new JComboBox<String>(columns);

	HashMap<String, String> dbLogHeader = new HashMap<String, String>() {{
		put("Action", "action");
		put("Student ID", "student_id");
		put("Book ID", "book_id");
		put("Date", "date");
		put("Time", "time");
	}};

	JTable logsList;
	JScrollPane scrollableLogsList = new JScrollPane(logsList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	// Some Extra Components
	JButton clear = new JButton("Clear All Logs");
	JButton back = new JButton("Back to Home");
	
	// Panels
	JPanel searchLogsPanel = new JPanel(new GridBagLayout());
	JPanel logsPanel = new JPanel(new GridBagLayout());

	public Logs(Home root) {
		this.root = root;
		this.stmt = root.stmt;
		this.conctn = root.conctn;
		logsPanel.setVisible(false);
		logsPanel.setName("Logs");
		root.add(logsPanel);
		addComponents();
		
		// Search Logs Components
		searchLogComponents.setFont(new Font("", Font.BOLD, 16));
		searchLogsInp.setFont(new Font("Lucida Console", Font.PLAIN, 26));
		searchLogs.setFocusable(false);
		searchLogs.setFont(new Font("", Font.BOLD, 15));
		searchLogs.addActionListener(event -> {
			try {
				ResultSet rSet;
				String searchQuery, dbSearchSelection = dbLogHeader.get(searchLogComponents.getSelectedItem());

				if (searchLogsInp.getText().isBlank()) {
					rSet = stmt.executeQuery(searchQuery = "SELECT * FROM logs;");
				}
				else {
					searchQuery = MessageFormat.format("SELECT * FROM logs WHERE {0} like ''%{1}%'';", dbSearchSelection, searchLogsInp.getText().trim());
					rSet = stmt.executeQuery(searchQuery);
				}

				rSet.last();
				data = new String[rSet.getRow()][5];
				rSet.beforeFirst();

				while (rSet.next()) {
					
					data[rSet.getRow()-1][0] = rSet.getString("action");
					data[rSet.getRow()-1][1] = rSet.getString("student_id");
					data[rSet.getRow()-1][2] = rSet.getString("book_id");
					data[rSet.getRow()-1][3] = rSet.getString("date");
					data[rSet.getRow()-1][4] = rSet.getString("time");
				}
				
				Statement stmt2 = conctn.createStatement();
				ResultSet rSetCount = stmt2.executeQuery("SELECT COUNT(*) as totalLogs FROM logs;");
				rSetCount.next();
				Statement stmt3 = conctn.createStatement();
				ResultSet rSetSearchedCount = stmt3.executeQuery(searchQuery.replaceFirst(" \\* ", " COUNT(*) as searchedLogs "));
				rSetSearchedCount.next();
				
				scrollableLogsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Logs", rSetSearchedCount.getString("searchedLogs"), rSetCount.getString("totalLogs")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
				scrollableLogsList.repaint();
				scrollableLogsList.setViewportView(logsList = new JTable(data, columns) {
					@Override
					public boolean isCellEditable(int row, int column) {
						return false;
					}
				});

				logsList.setFocusable(false);
				logsList.setRowHeight(20);
				logsList.setForeground(Color.WHITE);
				logsList.setBackground(new Color(232, 57, 95));
				logsList.setFont(new Font("Segoi UI", Font.PLAIN, 16));
				logsList.setSelectionBackground(new Color(132, 57, 95));
				logsList.setSelectionForeground(new Color(215, 255, 255));
				logsList.getTableHeader().setForeground(Color.WHITE);
				logsList.getTableHeader().setBackground(new Color(32, 136, 203));
				logsList.getTableHeader().setFont(new Font("Verdana", Font.PLAIN, 16));
				logsList.getSelectionModel().addListSelectionListener(event2 -> {
					if (event2.getValueIsAdjusting()) {
						try {
							rSetCount.first();
							rSetSearchedCount.first();
							scrollableLogsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {1} Logs ({2} Selected)", rSetSearchedCount.getString("searchedLogs"), rSetCount.getString("totalLogs"), logsList.getSelectionModel().getSelectedItemsCount()), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
							scrollableLogsList.repaint();
						} catch (Exception e) { System.out.println("Error logs: " + e); }
					}
				});
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Some issue occured, please Restart the App", "Restart App", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		resetLogs.setFocusable(false);
		resetLogs.setFont(new Font("", Font.BOLD, 15));
		resetLogs.addActionListener(event -> {
			searchLogComponents.setSelectedIndex(0);
			searchLogsInp.setText("");
			searchLogs.doClick();
		});
		
		searchLogsPanel.getComponent(0).setFont(new Font("Lucida Console", Font.PLAIN, 17));
		searchLogsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "All Logs", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Verdana", Font.BOLD, 40), Color.MAGENTA));

		searchLogs.doClick();
		scrollableLogsList.setPreferredSize(new Dimension(0, 400));
		scrollableLogsList.setBorder(BorderFactory.createLoweredBevelBorder());
		try {
			ResultSet rSetCount = stmt.executeQuery("SELECT COUNT(*) as totalLogs FROM logs;");
			rSetCount.next();

			scrollableLogsList.setViewportBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), MessageFormat.format("{0} of {0} Logs", rSetCount.getString("totalLogs")), TitledBorder.RIGHT, TitledBorder.BELOW_BOTTOM, new Font("Comic Sans MS", Font.BOLD, 18), Color.DARK_GRAY));
			scrollableLogsList.repaint();
		} catch (Exception e) { System.out.println("Error logs: " + e); }

		// Some Extra Components
		clear.setFocusable(false);
		clear.setFont(new Font("Verdana", Font.BOLD, 20));
		clear.addActionListener(event -> {
			try {
				int yn = JOptionPane.showConfirmDialog(null, "You want to Proceed, All Logs will be Deleted Permanently", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if (yn == 0) {
					stmt.executeUpdate("DELETE FROM logs;");
					searchLogs.doClick();
					JOptionPane.showConfirmDialog(null, "All logs is Successfully Deleted", "Logs Cleared", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (Exception e) { System.out.println(e); }
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

		// Search Logs Components
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(6, 18, 7, 0);
		searchLogsPanel.add(new JLabel("By:"), constraints);
		searchLogsPanel.add(searchLogComponents, constraints);
		constraints.ipadx = 200;
		searchLogsPanel.add(searchLogsInp, constraints);
		constraints.ipadx = 100;
		constraints.insets = new Insets(6, 0, 7, 0);
		searchLogsPanel.add(searchLogs, constraints);
		constraints.ipadx = 0;
		constraints.insets = new Insets(6, 10, 7, 18);
		searchLogsPanel.add(resetLogs, constraints);
		constraints.gridy = 1;
		constraints.gridwidth = 5;
		constraints.insets = new Insets(6, 18, 7, 18);
		searchLogsPanel.add(scrollableLogsList, constraints);
		
		// MainPanel
		constraints = new GridBagConstraints();
		constraints.ipadx = 30;
		constraints.ipady = 25;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(8, 5, 8, 5);
		logsPanel.add(searchLogsPanel, constraints);
		constraints.gridy = 1;
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		logsPanel.add(back, constraints);
		constraints.ipadx = 65;
		constraints.anchor = GridBagConstraints.EAST;
		logsPanel.add(clear, constraints);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		// Panel Activation Settings
		for (int i = 0; i < root.getContentPane().getComponentCount(); i++) {
			root.getContentPane().getComponent(i).setVisible(false);
		}
		
		root.setTitle("Library Management System - All Logs");
		searchLogs.doClick();
		logsPanel.setVisible(true);
	}
}