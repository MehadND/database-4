import com.toedter.calendar.JCalendar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener, MouseListener
{
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
    SpinnerModel value = new SpinnerNumberModel(18, 15, 100, 1);
    private JSpinner ageSpinner = new JSpinner(value);
    private JRadioButton genderMale = new JRadioButton("Male");
    private JRadioButton genderFemale = new JRadioButton("Female");
    private JRadioButton genderOther = new JRadioButton("Other");
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JPanel genderPanel = new JPanel();
    private JTextField dobTextField = new JTextField(20);
    private JTextField nationalityTextField = new JTextField(20);
    String[] clubs = {"AFC Bournemouth", "Arsenal", "Aston Villa", "Brentford", "Brighton & Hove " +
            "Albion", "Chelsea", "Crystal Palace", "Everton", "Fulham", "Leeds United", "Leicester City",
            "Liverpool", "Manchester City",
            "Manchester United",
            "Newcastle United", "Nottingham Forest", "Southampton",
            "Tottenham Hotspur", "West Ham United", "Wolves"};

    JComboBox<String> clubDropdownMenu = new JComboBox<>(clubs);    // dropdown menu for selecting clubs
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

    String dobDate = "";

    private JScrollPane dbContentsPanel;

    // Export Panel components
    JPanel exportPanel = new JPanel();
    JLabel headerText = new JLabel("Export Functionality");
    JButton nationalitiesPerClubButton = new JButton("Get Nationalities Per Club");
    JButton goalsPerAgeButton = new JButton("Get Goals Per Age");
    JLabel infoLabel = new JLabel("Info = ");
    JButton topScorerButton = new JButton("Get Top Scorer");
    private JTextField topScorerTextField = new JTextField(20);

    Border exportPanelBorder;
    JPanel crudButtonPanel = new JPanel();
    ArrayList<JTextField> textFieldArrayList = new ArrayList<>();   // used for setting width + height of all textfields in crudPanel


    public JDBCMainWindowContent(String windowTitle)
    {
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
        textFieldArrayList.add(goalsTextField);
        textFieldArrayList.add(assistsTextField);

        clubDropdownMenu.addActionListener(this);

        // setup crud panel and add the components to it
        settingCrudPanel();

        // setup db table view
        settingDBContentPanel();

        // setup export panel
        settingExportPanel();

        // creates DB control buttons and adds action listener to them and set button sizes
        settingDBControls();

        content.add(crudButtonPanel);
        content.add(exportPanel);
        content.add(dbContentsPanel);
        content.add(crudPanel);

        setSize(1280, 720);
        setVisible(true);

        tableModel.refreshFromDB(stmt);
    }

    public void initiate_db_conn()
    {
        try
        {
            // Load the JConnector Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Specify the DB Name
            String url = "jdbc:mysql://127.0.0.1:3307/db_football";
            // Connect to DB using DB URL, Username and password
            con = DriverManager.getConnection(url, "root", "");
            // Create a generic statement which is passed to the TestInternalFrame1
            stmt = con.createStatement();
        } catch (Exception e)
        {
            System.out.println("Error: Failed to connect to database\n" + e.getMessage());
        }
    }

    private void settingCrudPanel()
    {
        buttonGroup.add(genderMale);
        buttonGroup.add(genderFemale);
        buttonGroup.add(genderOther);

        idTextField.setEditable(false);

        crudPanel = new JPanel();

        crudPanel.setSize(400, 450);
        crudPanel.setLocation(50, 0);
        crudPanel.setBackground(Color.lightGray);
        exportPanelBorder = new LineBorder(new Color(245, 84, 10), 2);
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
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.anchor = GridBagConstraints.CENTER;

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
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, crudPanel.getWidth() / 4, 0, 0);
        constraints.gridy++;
        JLabel l = new JLabel("<html><center>Use <font color=#AC56C3>Open DOB</font color>" + " Button to select a DOB</center></html>");
        //l.setFont(l.getFont().deriveFont(Font.ITALIC, , 12));
        gridBagLayout.setConstraints(l, constraints);
        crudPanel.add(l);

        constraints.insets = new Insets(5, 0, 5, 0);

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
        gridBagLayout.setConstraints(clubDropdownMenu, constraints);
        //clubDropdownMenu.setPreferredSize(new Dimension(200, 130));
        crudPanel.add(clubDropdownMenu);

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

    private void settingExportPanel()
    {
        exportPanelBorder = new LineBorder(new Color(2, 139, 250), 2);
        exportPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(exportPanelBorder, "Export Data"
                , TitledBorder.CENTER, TitledBorder.CENTER, null, new Color(245, 84, 10))));

        topScorerTextField.setEditable(false);
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD, 12));

        nationalitiesPerClubButton.addActionListener(this);
        goalsPerAgeButton.addActionListener(this);
        topScorerButton.addActionListener(this);

        exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.Y_AXIS));
        exportPanel.setBackground(null);
        exportPanel.setSize(200, 250);
        exportPanel.setLocation(800, 10);
        exportPanel.add(nationalitiesPerClubButton);
        exportPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        exportPanel.add(goalsPerAgeButton);
        exportPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        exportPanel.add(topScorerButton);
        exportPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        exportPanel.add(topScorerTextField);
        exportPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        exportPanel.add(infoLabel);

        content.add(exportPanel);
    }

    private void settingDBContentPanel()
    {
        tableofDBContents.addMouseListener(this);

        tableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

        dbContentsPanel = new JScrollPane(tableofDBContents, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dbContentsPanel.setBackground(Color.lightGray);

        dbContentsPanel.setSize(600, 150);
        dbContentsPanel.setLocation(550, 300);
    }

    private void settingDBControls()
    {
        insertButton.setSize(100, 30);
        updateButton.setSize(100, 30);
        deleteButton.setSize(100, 30);
        clearButton.setSize(100, 30);
        dobPicker.setSize(100, 30);

        //dobPicker.setForeground(new Color(172, 86, 195));

        GridLayout experimentLayout = new GridLayout(5, 0);
        experimentLayout.setVgap(5);
        crudButtonPanel.setLayout(new BoxLayout(crudButtonPanel, BoxLayout.Y_AXIS));
        crudButtonPanel.setBackground(null);
        crudButtonPanel.setSize(200, 260);
        crudButtonPanel.setLocation(550, 25);
//        crudButtonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(exportPanelBorder, "Export Data",
//                TitledBorder.CENTER, TitledBorder.CENTER, null, Color.red)));

        insertButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        clearButton.addActionListener(this);
        dobPicker.addActionListener(this);

        crudButtonPanel.add(insertButton);
        crudButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        crudButtonPanel.add(updateButton);
        crudButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        crudButtonPanel.add(deleteButton);
        crudButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        crudButtonPanel.add(clearButton);
        crudButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        crudButtonPanel.add(dobPicker);
        crudButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        crudButtonPanel.add(new JLabel("<html><h3>Note: Select player_id to fill the textfields.</h3></html>"));
    }

    public void openNationalitiesPerClubChart() throws SQLException, IOException
    {
        JFrame frame = new JFrame();

        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select club, count(distinct nationality) as nationality from players group by club;");
        DefaultPieDataset dataset = new DefaultPieDataset();

        while (resultSet.next())
        {
            dataset.setValue(
                    resultSet.getString("club"),
                    Double.parseDouble(resultSet.getString("nationality"))
            );
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Nationalities Per Club",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);
        ChartPanel chartPanel = new ChartPanel(chart);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}"));
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));

        frame.add(chartPanel);
        frame.setSize(700, 500);
        frame.setLocation(content.getWidth(), content.getHeight());
        frame.setVisible(true);



        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpeg"));

        fileChooser.setAcceptAllFileFilterUsed(true);
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = new File(fileChooser.getSelectedFile() + ".jpeg");
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            int width = 560;    /* Width of the image */
            int height = 370;   /* Height of the image */
            ChartUtilities.saveChartAsJPEG(fileToSave, chart, width, height);    // saves chart as jpeg file
        }
    }

    public void openGoalsPerAgeChart() throws SQLException, IOException
    {
        JFrame frame = new JFrame();

        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select age, sum(goals) as goals from players group by age;");
        DefaultPieDataset dataset = new DefaultPieDataset();

        while (resultSet.next())
        {
            dataset.setValue(
                    resultSet.getString("age"),
                    Double.parseDouble(resultSet.getString("goals"))
            );
        }
//        PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0} = {1}");
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Goals Per Age",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);
        ChartPanel chartPanel = new ChartPanel(chart);
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}"));
        plot.setStartAngle(60);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        frame.add(chartPanel);
        frame.setSize(700, 500);
        frame.setLocation(content.getWidth(), content.getHeight());
        frame.setVisible(true);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpeg"));

        fileChooser.setAcceptAllFileFilterUsed(true);
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = new File(fileChooser.getSelectedFile() + ".jpeg");
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            int width = 560;    /* Width of the image */
            int height = 370;   /* Height of the image */
            ChartUtilities.saveChartAsJPEG(fileToSave, chart, width, height);    // saves chart as jpeg file
        }

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object target = e.getSource();

        if (target == topScorerButton)
        {
            getTopScorer();
        }

        if (target == clubDropdownMenu)
        {
            System.out.println(clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex()));
        }

        // open calendar on 'open DOB' button and sets DOB dobDate in DOB text-field
        if (target == dobPicker)
        {
            openDOBPicker();
        }

        if (target == nationalitiesPerClubButton)
        {
            GetNationalitiesPerClub();
        }

        if (target == goalsPerAgeButton)
        {
            GetGoalsPerAge();
        }

        if (target == clearButton)
        {
            clearFields();  // clears all fields to default value
        }

        if (target == insertButton)
        {
            insertQuery();  // method that runs insert query
        }
        if (target == deleteButton)
        {
            deleteQuery();  // method that runs delete query
        }
        if (target == updateButton)
        {
            updateQuery();  // method that runs the update query
        }
    }

    private void openDOBPicker()
    {
        JFrame f = new JFrame();
        JPanel p = new JPanel();
        JCalendar calendar = new JCalendar();
        JLabel label = new JLabel();
        dobDate.replaceAll("\\D", "");
        SimpleDateFormat dcn = new SimpleDateFormat("yyyy");
        SimpleDateFormat actualFormat = new SimpleDateFormat("yyyy-MM-dd");

        calendar.addPropertyChangeListener(new PropertyChangeListener()
        {

            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                // TODO Auto-generated method stub
                dobDate = dcn.format(calendar.getDate());
                label.setText(dobDate.toString());

                dobTextField.setText(actualFormat.format(calendar.getDate()));

                // sets date format from YYYY-MM-DD to YYYY, for easy calculation of age
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy");
                LocalDateTime now = LocalDateTime.now();
                String nowDate = dtf.format(now);
                dobDate = dobDate.replaceAll("[^a-zA-Z0-9]", "");
                //System.out.println(dobDate);
                int age = Integer.parseInt(nowDate) - Integer.parseInt(dobDate);    // calculates player age based on DOB
                //System.out.println(age);
                ageSpinner.setValue(age);
            }
        });
        p.add(calendar);
        p.add(label);
        f.add(p);
        f.setVisible(true);
        f.setSize(600, 600);
        dobTextField.setText(dobDate);
        f.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void updateQuery()
    {
        String updateTemp = "";
        try
        {
            if (genderFemale.isSelected() == true)
            {
                updateTemp = "UPDATE players SET " + "firstName = '" + firstNameTextField.getText()
                        + "', lastName = '" + lastNameTextField.getText() + "', age = " + ageSpinner.getValue()
                        + ", gender ='F'" + ", dob = '" + dobTextField.getText() + "', nationality = '"
                        + nationalityTextField.getText() + "', club = '" + clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex())
                        + "', appearances = " + appearancesTextField.getText() + ", goals = "
                        + goalsTextField.getText() + ", assists = " + assistsTextField.getText()
                        + " where player_id = " + idTextField.getText() + ";";
            }

            if (genderMale.isSelected() == true)
            {
                updateTemp = "UPDATE players SET " + "firstName = '" + firstNameTextField.getText()
                        + "', lastName = '" + lastNameTextField.getText() + "', age = " + ageSpinner.getValue()
                        + ", gender ='M'" + ", dob = '" + dobTextField.getText() + "', nationality = '"
                        + nationalityTextField.getText() + "', club = '" + clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex())
                        + "', appearances = " + appearancesTextField.getText() + ", goals = "
                        + goalsTextField.getText() + ", assists = " + assistsTextField.getText()
                        + " where player_id = " + idTextField.getText() + ";";
            }

            if (genderOther.isSelected() == true)
            {
                updateTemp = "UPDATE players SET " + "firstName = '" + firstNameTextField.getText()
                        + "', lastName = '" + lastNameTextField.getText() + "', age = " + ageSpinner.getValue()
                        + ", gender ='O'" + ", dob = '" + dobTextField.getText() + "', nationality = '"
                        + nationalityTextField.getText() + "', club = '" + clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex())
                        + "', appearances = " + appearancesTextField.getText() + ", goals = "
                        + goalsTextField.getText() + ", assists = " + assistsTextField.getText()
                        + " where player_id = " + idTextField.getText() + ";";
            }

            stmt.executeUpdate(updateTemp);
            rs = stmt.executeQuery("SELECT * from players");
            rs.next();
            rs.close();

            //printDB();

        } catch (SQLException sqle)
        {
            System.err.println("Error with  update:\n" + sqle.toString());
        } finally
        {
            tableModel.refreshFromDB(stmt);
        }
        clearFields();  // clears all input-based components
    }

    private void deleteQuery()
    {
        try
        {
            String updateTemp = "DELETE FROM players WHERE player_id = " + idTextField.getText() + ";";
            stmt.executeUpdate(updateTemp);

        } catch (SQLException sqle)
        {
            System.err.println("Error with delete:\n" + sqle.toString());
        } finally
        {
            tableModel.refreshFromDB(stmt);
            //printDB();

        }
        clearFields();  // clears all input-based components
    }

    private void insertQuery()
    {
        String updateTemp = "";
        try
        {
            if (genderFemale.isSelected() == true)
            {
                updateTemp = "INSERT INTO players VALUES(" + null + ", '" + firstNameTextField.getText() + "', "
                        + "'" + lastNameTextField.getText() + "', " + ageSpinner.getValue() + ", " + "'F', " + "'"
                        + dobTextField.getText() + "', " + "'" + nationalityTextField.getText() + "', " + "'"
                        + clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex()) + "', " + appearancesTextField.getText() + ", "
                        + goalsTextField.getText() + ", " + assistsTextField.getText() + ");";
            }

            if (genderMale.isSelected() == true)
            {
                updateTemp = "INSERT INTO players VALUES(" + null + ", '" + firstNameTextField.getText() + "', "
                        + "'" + lastNameTextField.getText() + "', " + ageSpinner.getValue() + ", " + "'M', " + "'"
                        + dobTextField.getText() + "', " + "'" + nationalityTextField.getText() + "', " + "'"
                        + clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex()) + "', " + appearancesTextField.getText() + ", "
                        + goalsTextField.getText() + ", " + assistsTextField.getText() + ");";
            }

            if (genderOther.isSelected() == true)
            {
                updateTemp = "INSERT INTO players VALUES(" + null + ", '" + firstNameTextField.getText() + "', "
                        + "'" + lastNameTextField.getText() + "', " + ageSpinner.getValue() + ", " + "'O', " + "'"
                        + dobTextField.getText() + "', " + "'" + nationalityTextField.getText() + "', " + "'"
                        + clubDropdownMenu.getItemAt(clubDropdownMenu.getSelectedIndex()) + "', " + appearancesTextField.getText() + ", "
                        + goalsTextField.getText() + ", " + assistsTextField.getText() + ");";
            }

            stmt.executeUpdate(updateTemp);

        } catch (SQLException sqle)
        {
            System.err.println("Error with  insert:\n" + sqle.toString());
        } finally
        {
            tableModel.refreshFromDB(stmt);
            //printDB();
        }
        clearFields();  // clears all input-based components
    }

    private void clearFields()
    {
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
        appearancesTextField.setText("");
        goalsTextField.setText("");
        assistsTextField.setText("");
        infoLabel.setText("Info = ");
        clubDropdownMenu.setSelectedIndex(0);
    }

/*    private void printDB() throws SQLException
    {
        rs = stmt.executeQuery("call spGetTopScorer();");

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
    }*/

    /*
     * TYPE OF EXPORT DATA I CAN GET
     * 1. All nationalities and clubs in 1 csv file, use charts to show diversity in each club.
     * 2. Goals, Assists and Age in 1 csv file, use charts to show total goals scored or assists per age.
     */

    private void GetNationalitiesPerClub()
    {
        cmd = "select club, count(distinct nationality) from players group by club;";

        try
        {
            rs = stmt.executeQuery(cmd);
            writeToFile(rs, "ExportData - Nationalities Per Club");
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    private void GetGoalsPerAge()
    {
        cmd = "select age, sum(goals) as goals from players group by age;";

        try
        {
            rs = stmt.executeQuery(cmd);
            writeToFile(rs, "ExportData - Goals Per Age");
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }


    private void getTopScorer()
    {
        try {
            String updateTemp = "call spGetTopScorer();";
            rs = stmt.executeQuery(updateTemp);
            while(rs.next())
            {
//                System.out.println("FirstName = "+rs.getString("firstName"));
//                System.out.println("LastName = "+rs.getString("lastName"));
//                System.out.println("Goals Scored = "+rs.getString("goals"));
                topScorerTextField.setText(rs.getString("firstName") + " " + rs.getString("lastName") + " (" + rs.getString("goals") + ")");
            }

        } catch (SQLException sqle) {
            System.err.println("Error with delete:\n" + sqle.toString());
        } finally {
            tableModel.refreshFromDB(stmt);
        }
    }

    private void writeToFile(ResultSet rs, String fileName)
    {
        try
        {
            //System.out.println("In writeToFile");
            JFileChooser fileChooser = new JFileChooser();
            FileWriter outputFile = null;



            fileChooser.setDialogTitle("Specify a file to save");
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File", ".csv"));

            fileChooser.setAcceptAllFileFilterUsed(true);
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = new File(fileChooser.getSelectedFile() + ".csv");
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                outputFile = new FileWriter(fileToSave);
            }
            PrintWriter printWriter = new PrintWriter(outputFile);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();

            for (int i = 0; i < numColumns; i++)
            {
                printWriter.print(rsmd.getColumnLabel(i + 1) + ",");
            }
            printWriter.print("\n");
            while (rs.next())
            {
                for (int i = 0; i < numColumns; i++)
                {
                    printWriter.print(rs.getString(i + 1) + ",");
                }
                printWriter.print("\n");
                printWriter.flush();
            }
            printWriter.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

        // fills text-fields when clicked on an id column in db table

        if (tableofDBContents.getSelectedColumn() == 0)
        {
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
            String s = tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 7).toString();

            for (int i = 0; i <= clubs.length - 1; i++)
            {
                if (s.equals(clubs[i]))
                {
                    System.out.println("YES!! SAME");
                    clubDropdownMenu.setSelectedIndex(i);
                }
            }
            appearancesTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 8).toString());
            goalsTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 9).toString());
            assistsTextField.setText(tableofDBContents.getValueAt(tableofDBContents.getSelectedRow(), 10).toString());

        }

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}
