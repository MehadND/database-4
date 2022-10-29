import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener, MouseListener {
    String cmd = null;

    // DB Connectivity Attributes
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    private Container content;

    private JPanel crudPanel;

    // CRUD panel components
    private JLabel idLabel = new JLabel("ID:                 ");
    private JLabel firstNameLabel = new JLabel("FirstName:               ");
    private JLabel lastNameLabel = new JLabel("LastName:      ");
    private JLabel ageLabel = new JLabel("Age:        ");
    private JLabel genderLabel = new JLabel("Gender:                 ");
    private JLabel dobLabel = new JLabel("DOB (YYYY-MM-DD):               ");
    private JLabel nationalityLabel = new JLabel("Nationality:      ");
    private JLabel clubLabel = new JLabel("Club:      ");
    private JLabel appearancesLabel = new JLabel("Appearances:        ");
    private JLabel goalsLabel = new JLabel("Goals:        ");
    private JLabel assistsLabel = new JLabel("Assists:        ");
    private JTextField idTextField = new JTextField(20);
    private JTextField firstNameTextField = new JTextField(20);
    private JTextField lastNameTextField = new JTextField(20);
    // private JTextField ageTextField = new JTextField(20);
    SpinnerModel value = new SpinnerNumberModel(18, // initial value
            15, // minimum value
            100, // maximum value
            1); // step
    private JSpinner ageSpinner = new JSpinner(value);

    // private JTextField genderTextField = new JTextField(20);
    private JRadioButton genderMale = new JRadioButton("Male");
    private JRadioButton genderFemale = new JRadioButton("Female");
    private JRadioButton genderOther = new JRadioButton("Other");
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JPanel genderPanel = new JPanel();
    private JTextField dobTextField = new JTextField(20);
    private JTextField nationalityTextField = new JTextField(20);
    private JTextField clubTextField = new JTextField(20);
    private JTextField appearancesTextField = new JTextField(20);
    private JTextField goalsTextField = new JTextField(20);
    private JTextField assistsTextField = new JTextField(20);

    private static QueryTableModel tableModel = new QueryTableModel();
    // Add the models to JTabels
    private JTable tableofDBContents = new JTable(tableModel);
    // Buttons for inserting, and updating members
    // also a clear button to clear details panel
    // caleendar button is used to open calendar for selecting DOB
    private JButton updateButton = new JButton("Update");
    private JButton insertButton = new JButton("Insert");
    private JButton deleteButton = new JButton("Delete");
    private JButton clearButton = new JButton("Clear");
    private JButton dobPicker = new JButton("Open DOB");

    String date = "";

    private JScrollPane dbContentsPanel;

    // Export Panel components
    JPanel exportPanel = new JPanel();
    JLabel headerText = new JLabel("Export Functionality");
    JButton getNationalitiesPerClub = new JButton("Get Nationalities Per Club");
    JButton getGoalsPerAge = new JButton("Get Goals Per Age");
    Border border = new LineBorder(Color.black, 2);
    ArrayList<JTextField> textFieldArrayList = new ArrayList<>();

    public JDBCMainWindowContent(String windowTitle) {
        // setting up the GUI
        super(windowTitle, false, false, false, false);
        setEnabled(true);

        initiate_db_conn();
        // add the 'main' panel to the Internal Frame
        content = getContentPane();
        content.setLayout(null);
        content.setBackground(Color.lightGray);

        textFieldArrayList.add(idTextField);
        textFieldArrayList.add(firstNameTextField);
        textFieldArrayList.add(lastNameTextField);
        textFieldArrayList.add(dobTextField);
        textFieldArrayList.add(nationalityTextField);
        textFieldArrayList.add(appearancesTextField);
        textFieldArrayList.add(clubTextField);
        textFieldArrayList.add(goalsTextField);
        textFieldArrayList.add(assistsTextField);

        // setup crud panel and add the components to it
        settingCrudPanel();

        // setup db table view
        settingDBContentPanel();

        // setup export panel
        settingExportPanel();

        // creates DB control buttons and adds action listener to them and set button sizes
        settingDBControls();

        content.add(exportPanel);
        content.add(dbContentsPanel);
        content.add(crudPanel);

        setSize(1280, 720);
        setVisible(true);

        tableModel.refreshFromDB(stmt);
    }

    public void initiate_db_conn() {
        try {
            // Load the JConnector Driver
            Class.forName("com.mysql.jdbc.Driver");
            // Specify the DB Name
            String url = "jdbc:mysql://127.0.0.1:3307/db_football";
            // Connect to DB using DB URL, Username and password
            con = DriverManager.getConnection(url, "root", "");
            // Create a generic statement which is passed to the TestInternalFrame1
            stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println("Error: Failed to connect to database\n" + e.getMessage());
        }
    }

    private void settingCrudPanel() {
        buttonGroup.add(genderMale);
        buttonGroup.add(genderFemale);
        buttonGroup.add(genderOther);

        dobTextField.setEditable(false);
        dobTextField.setText("fUse <i>DOB</i> Selector Button to select a DOB");

        crudPanel = new JPanel();

        crudPanel.setSize(400, 450);
        crudPanel.setLocation(50, 0);
        crudPanel.setBackground(Color.lightGray);
        crudPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(border, "CRUD", TitledBorder.CENTER, TitledBorder.CENTER, null, Color.red)));
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        crudPanel.setLayout(gridBagLayout);

        genderPanel.add(genderMale);
        genderPanel.add(genderFemale);
        genderPanel.add(genderOther);
        genderFemale.setBackground(null);
        genderMale.setBackground(null);
        genderOther.setBackground(null);
        genderPanel.setBackground(null);

        for (JTextField textField : textFieldArrayList)
        {
            textField.setPreferredSize(new Dimension(0, 25));
        }

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5,0,5,0);

        constraints.gridx = 0;
        constraints.gridy = 0;
        gridBagLayout.setConstraints(idLabel, constraints);
        crudPanel.add(idLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(idTextField, constraints);
        //idTextField.setPreferredSize(new Dimension(0, 50));
        crudPanel.add(idTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(firstNameLabel, constraints);
        crudPanel.add(firstNameLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(firstNameTextField, constraints);
        //firstNameTextField.setPreferredSize(new Dimension(0, 40));
        crudPanel.add(firstNameTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(lastNameLabel, constraints);
        crudPanel.add(lastNameLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(lastNameTextField, constraints);
        //lastNameTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(lastNameTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(dobLabel, constraints);
        crudPanel.add(dobLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(dobTextField, constraints);
        //dobTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(dobTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(ageLabel, constraints);
        crudPanel.add(ageLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(ageSpinner, constraints);
        ageSpinner.setPreferredSize(new Dimension(0, 25));
        crudPanel.add(ageSpinner);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(genderLabel, constraints);
        crudPanel.add(genderLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(genderPanel, constraints);
        crudPanel.add(genderPanel);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(nationalityLabel, constraints);
        crudPanel.add(nationalityLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(nationalityTextField, constraints);
        //nationalityTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(nationalityTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(clubLabel, constraints);
        crudPanel.add(clubLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(clubTextField, constraints);
        //clubTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(clubTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(appearancesLabel, constraints);
        crudPanel.add(appearancesLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(appearancesTextField, constraints);
        //appearancesTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(appearancesTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(goalsLabel, constraints);
        crudPanel.add(goalsLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(goalsTextField, constraints);
        //goalsTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(goalsTextField);

        constraints.gridx = 0;
        constraints.gridy++;
        gridBagLayout.setConstraints(assistsLabel, constraints);
        crudPanel.add(assistsLabel);

        constraints.gridx++;
        gridBagLayout.setConstraints(assistsTextField, constraints);
        //assistsTextField.setPreferredSize(new Dimension(0, 30));
        crudPanel.add(assistsTextField);

        genderFemale.addActionListener(this);
        genderMale.addActionListener(this);
        genderOther.addActionListener(this);
        dobTextField.addActionListener(this);
    }

    private void settingExportPanel() {
        getNationalitiesPerClub.addActionListener(this);
        getGoalsPerAge.addActionListener(this);

        getNationalitiesPerClub.setPreferredSize(new Dimension(200, 50));
        getGoalsPerAge.setPreferredSize(new Dimension(200, 50));

        exportPanel.setBackground(null);
        exportPanel.setSize(300, 150);
        exportPanel.setLocation(750, 10);
        exportPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(border, "Export Data", TitledBorder.CENTER, TitledBorder.CENTER, null, Color.red)));
        //exportPanel.add(headerText);
        exportPanel.add(getNationalitiesPerClub);
        exportPanel.add(getGoalsPerAge);
    }

    private void settingDBContentPanel() {
        tableofDBContents.addMouseListener(this);

        tableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

        dbContentsPanel = new JScrollPane(tableofDBContents, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dbContentsPanel.setBackground(Color.lightGray);

        dbContentsPanel.setSize(600, 150);
        dbContentsPanel.setLocation(550, 300);
    }

    private void settingDBControls() {
        insertButton.setSize(100, 30);
        updateButton.setSize(100, 30);
        deleteButton.setSize(100, 30);
        clearButton.setSize(100, 30);
        dobPicker.setSize(100, 30);

        insertButton.setLocation(550, 10);
        deleteButton.setLocation(550, 60);
        updateButton.setLocation(550, 110);
        clearButton.setLocation(550, 160);
        dobPicker.setLocation(550, 210);

        insertButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        dobPicker.addActionListener(this);

        content.add(insertButton);
        content.add(updateButton);
        content.add(deleteButton);
        content.add(clearButton);
        content.add(dobPicker);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object target = e.getSource();

        // open calendar on 'open DOB' button and sets DOB date in DOB text-field
        if (target == dobPicker) {
            JFrame f = new JFrame();
            JPanel p = new JPanel();
            JCalendar calendar = new JCalendar();
            JLabel label = new JLabel();
            date.replaceAll("\\D", "");
            SimpleDateFormat dcn = new SimpleDateFormat("yyyy");
            SimpleDateFormat actualFormat = new SimpleDateFormat("yyyy-MM-dd");

            calendar.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    // TODO Auto-generated method stub
                    date = dcn.format(calendar.getDate());
                    label.setText(date.toString());

                    dobTextField.setText(actualFormat.format(calendar.getDate()));

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
                    LocalDateTime now = LocalDateTime.now();
                    String nowDate = dtf.format(now);
                    date = date.replaceAll("[^a-zA-Z0-9]", "");
                    System.out.println(date);
                    int age = Integer.parseInt(nowDate) - Integer.parseInt(date);
                    System.out.println(age);
                    ageSpinner.setValue(age);
                }
            });
            p.add(calendar);
            p.add(label);
            f.add(p);
            f.setVisible(true);
            f.setSize(600, 600);
            dobTextField.setText(date);
            f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        if (target == getNationalitiesPerClub) {
            GetNationalitiesPerClub();
        }

        if (target == getGoalsPerAge) {
            GetGoalsPerAge();
        }

        if (target == clearButton) {
            clearFields();
        }

        if (target == insertButton) {
            insertQuery();  // method that runs insert query
            //DisplayDBTable();
        }
        if (target == deleteButton) {
            deleteQuery();  // method that runs delete query
            //DisplayDBTable();
        }
        if (target == updateButton) {
            updateQuery();  // method that runs the update query
        }
    }

    private void updateQuery() {
        String updateTemp = "";
        try {

            if (genderFemale.isSelected() == true) {
                updateTemp = "UPDATE players SET " + "firstName = '" + firstNameTextField.getText()
                        + "', lastName = '" + lastNameTextField.getText() + "', age = " + ageSpinner.getValue()
                        + ", gender ='F'" + ", dob = '" + dobTextField.getText() + "', nationality = '"
                        + nationalityTextField.getText() + "', club = '" + clubTextField.getText()
                        + "', appearances = " + appearancesTextField.getText() + ", goals = "
                        + goalsTextField.getText() + ", assists = " + assistsTextField.getText()
                        + " where player_id = " + idTextField.getText() + ";";
            }

            if (genderMale.isSelected() == true) {
                updateTemp = "UPDATE players SET " + "firstName = '" + firstNameTextField.getText()
                        + "', lastName = '" + lastNameTextField.getText() + "', age = " + ageSpinner.getValue()
                        + ", gender ='M'" + ", dob = '" + dobTextField.getText() + "', nationality = '"
                        + nationalityTextField.getText() + "', club = '" + clubTextField.getText()
                        + "', appearances = " + appearancesTextField.getText() + ", goals = "
                        + goalsTextField.getText() + ", assists = " + assistsTextField.getText()
                        + " where player_id = " + idTextField.getText() + ";";
            }

            if (genderOther.isSelected() == true) {
                updateTemp = "UPDATE players SET " + "firstName = '" + firstNameTextField.getText()
                        + "', lastName = '" + lastNameTextField.getText() + "', age = " + ageSpinner.getValue()
                        + ", gender ='O'" + ", dob = '" + dobTextField.getText() + "', nationality = '"
                        + nationalityTextField.getText() + "', club = '" + clubTextField.getText()
                        + "', appearances = " + appearancesTextField.getText() + ", goals = "
                        + goalsTextField.getText() + ", assists = " + assistsTextField.getText()
                        + " where player_id = " + idTextField.getText() + ";";
            }

            stmt.executeUpdate(updateTemp);
            // these lines do nothing but the table updates when we access the db.
            rs = stmt.executeQuery("SELECT * from players");
            rs.next();
            rs.close();

            printDB();

        } catch (SQLException sqle) {
            System.err.println("Error with  update:\n" + sqle.toString());
        } finally {
            tableModel.refreshFromDB(stmt);
        }
        clearFields();  // clears all input-based components
    }

    private void deleteQuery() {
        try {
            String updateTemp = "DELETE FROM players WHERE player_id = " + idTextField.getText() + ";";
            stmt.executeUpdate(updateTemp);

        } catch (SQLException sqle) {
            System.err.println("Error with delete:\n" + sqle.toString());
        } finally {
            tableModel.refreshFromDB(stmt);
            try {
                printDB();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
        clearFields();  // clears all input-based components
    }

    private void insertQuery() {
        String updateTemp = "";
        try {
            if (genderFemale.isSelected() == true) {
                updateTemp = "INSERT INTO players VALUES(" + null + ", '" + firstNameTextField.getText() + "', "
                        + "'" + lastNameTextField.getText() + "', " + ageSpinner.getValue() + ", " + "'F', " + "'"
                        + dobTextField.getText() + "', " + "'" + nationalityTextField.getText() + "', " + "'"
                        + clubTextField.getText() + "', " + appearancesTextField.getText() + ", "
                        + goalsTextField.getText() + ", " + assistsTextField.getText() + ");";
            }

            if (genderMale.isSelected() == true) {
                updateTemp = "INSERT INTO players VALUES(" + null + ", '" + firstNameTextField.getText() + "', "
                        + "'" + lastNameTextField.getText() + "', " + ageSpinner.getValue() + ", " + "'M', " + "'"
                        + dobTextField.getText() + "', " + "'" + nationalityTextField.getText() + "', " + "'"
                        + clubTextField.getText() + "', " + appearancesTextField.getText() + ", "
                        + goalsTextField.getText() + ", " + assistsTextField.getText() + ");";
            }

            if (genderOther.isSelected() == true) {
                updateTemp = "INSERT INTO players VALUES(" + null + ", '" + firstNameTextField.getText() + "', "
                        + "'" + lastNameTextField.getText() + "', " + ageSpinner.getValue() + ", " + "'O', " + "'"
                        + dobTextField.getText() + "', " + "'" + nationalityTextField.getText() + "', " + "'"
                        + clubTextField.getText() + "', " + appearancesTextField.getText() + ", "
                        + goalsTextField.getText() + ", " + assistsTextField.getText() + ");";
            }

            stmt.executeUpdate(updateTemp);

        } catch (SQLException sqle) {
            System.err.println("Error with  insert:\n" + sqle.toString());
        } finally {
            tableModel.refreshFromDB(stmt);
            try {
                printDB();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        clearFields();  // clears all input-based components
    }

    private void clearFields() {
        idTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        ageSpinner.setValue(18);
        buttonGroup.clearSelection();
        genderMale.setSelected(false);
        genderFemale.setSelected(false);
        genderOther.setSelected(false);
        dobTextField.setText("");
        nationalityTextField.setText("");
        clubTextField.setText("");
        appearancesTextField.setText("");
        goalsTextField.setText("");
        assistsTextField.setText("");

    }

    private void printDB() throws SQLException {
        rs = stmt.executeQuery("SELECT * from players");

        System.out.println(" ");

        System.out.println(
                "| ID ! First Name | Last Name | Age | Gender | DOB | Nationality | Club | Apperances | Goals | Assists |");

        while (rs.next())
            System.out.println("|" + rs.getInt("player_id") + " | " + rs.getString("firstName") + " |  "
                    + rs.getString("lastName") + " | " + rs.getString("age") + " | " + rs.getString("gender") + " | "
                    + rs.getString("dob") + " | " + rs.getString("nationality") + " | " + rs.getString("club") + " | "
                    + rs.getString("appearances") + " | " + rs.getString("goals") + " | " + rs.getString("assists")
                    + " | ");

        rs.close();
    }

    /*
     * TYPE OF EXPORT DATA I CAN GET
     * 1. All nationalities and clubs in 1 csv file, use charts to show diversity in each club.
     * 2. Goals, Assists and Age in 1 csv file, use charts to show total goals scored or assists per age.
     */

    private void GetNationalitiesPerClub() {
        cmd = "select club, count(nationality) from players group by club;";

        try {
            rs = stmt.executeQuery(cmd);
            writeToFile(rs, "ExportData1");
            while (rs.next()) {
                System.out.println(rs.getString("club") + " = " + rs.getString("count(nationality)"));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void GetGoalsPerAge() {
        cmd = "select age, count(goals) from players group by age;";

        try {
            rs = stmt.executeQuery(cmd);
            writeToFile(rs, "ExportData2");
            while (rs.next()) {
                System.out.println(rs.getString("age") + " = " + rs.getString("count(goals)"));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void writeToFile(ResultSet rs, String fileName) {
        try {
            System.out.println("writing to csv file...");
            FileWriter outputFile = new FileWriter("Export Data/" + fileName + ".csv");
            PrintWriter printWriter = new PrintWriter(outputFile);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();

            for (int i = 0; i < numColumns; i++) {
                printWriter.print(rsmd.getColumnLabel(i + 1) + ",");
            }
            printWriter.print("\n");
            while (rs.next()) {
                for (int i = 0; i < numColumns; i++) {
                    printWriter.print(rs.getString(i + 1) + ",");
                }
                printWriter.print("\n");
                printWriter.flush();
            }
            printWriter.close();
            System.out.println("writing to csv file...DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        // fills text-fields when clicked on an id column in db table

        if (tableofDBContents.getSelectedColumn() == 0) {
            idTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 0).toString());
            firstNameTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 1).toString());
            lastNameTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 2).toString());
            ageSpinner.setValue(Integer.valueOf(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 3).toString()));
            if (tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 4).toString().equals("M"))
                genderMale.setSelected(true);
            if (tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 4).toString().equals("F"))
                genderFemale.setSelected(true);
            if (tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 4).toString().equals("O"))
                genderOther.setSelected(true);
            dobTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 5).toString());
            nationalityTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 6).toString());
            clubTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 7).toString());
            appearancesTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 8).toString());
            goalsTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 9).toString());
            assistsTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 10).toString());

        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}