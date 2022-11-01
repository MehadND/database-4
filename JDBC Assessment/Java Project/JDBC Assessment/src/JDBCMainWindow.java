import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class JDBCMainWindow extends JFrame implements ActionListener
	{
		// Create an instance of our class JDBCMainWindowContent
		JDBCMainWindowContent aWindowContent = new JDBCMainWindowContent("Premier League DB");
		private JMenuItem exitItem;
		private JMenuItem data1Item;
		private JMenuItem data2Item;

		public JDBCMainWindow()
		{
			// Sets the Window Title
			super( "A00273758 - JDBC Assessment" );
			setDefaultCloseOperation(EXIT_ON_CLOSE);

			//Setup fileMenu and its elements
			JMenuBar menuBar=new JMenuBar();
			exitItem =new JMenuItem("Exit");

			JMenu viewMenu=new JMenu("View");
			data1Item =new JMenuItem("Open Chart For Get Nationalities Per Club");
			data2Item =new JMenuItem("Open Chart For Get Goals Per Age");

			viewMenu.add(data1Item);
			viewMenu.add(data2Item);
			menuBar.add(viewMenu );
			setJMenuBar(menuBar);

			// Add a listener to the Exit Menu Item
			exitItem.addActionListener(this);
			data1Item.addActionListener(this);
			data2Item.addActionListener(this);

			// Add the instance to the main section of the window
			getContentPane().add( aWindowContent );

			//Content content = new Content("Mehad Nadeem - JDBC Project");
			//getContentPane().add( content );
			
			setSize( 1200, 600 );
			setVisible( true );
		}
		
		// The event handling for the main frame
		public void actionPerformed(ActionEvent e)
		{
//			if(e.getSource().equals(exitItem)){
//				this.dispose();
//			}

			if(e.getSource() == data1Item)
			{
				System.out.println("Open Chart Frame For Data 1 - Get Nationalities Per Club");
				try {
					aWindowContent.openNationalitiesPerClubChart();
				} catch (SQLException | IOException ex) {
					throw new RuntimeException(ex);
				}
			}

			if(e.getSource() == data2Item)
			{
				System.out.println("Open Chart Frame For Data 2 - Get Goals Per Age");
				try {
					aWindowContent.openGoalsPerAgeChart();
				} catch (SQLException | IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		
		
		
	}