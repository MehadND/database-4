import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JDBCMainWindow extends JFrame implements ActionListener
	{
		//private JMenuItem exitItem;

		public JDBCMainWindow()
		{
			// Sets the Window Title
			super( "A00273758 - JDBC Assessment" );
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			//Setup fileMenu and its elements
//			JMenuBar menuBar=new JMenuBar();
//			JMenu fileMenu=new JMenu("File");
//			exitItem =new JMenuItem("Exit");
	
//			fileMenu.add(exitItem);
//			menuBar.add(fileMenu );
//			setJMenuBar(menuBar);
			
			// Add a listener to the Exit Menu Item
//			exitItem.addActionListener(this);

			// Create an instance of our class JDBCMainWindowContent 
			JDBCMainWindowContent aWindowContent = new JDBCMainWindowContent("A00273758 - JDBC Assessment");
			// Add the instance to the main section of the window
			getContentPane().add( aWindowContent );

			//Content content = new Content("Mehad Nadeem - JDBC Project");
			//getContentPane().add( content );
			
			setSize( 1200, 550 );
			setVisible( true );
		}
		
		// The event handling for the main frame
		public void actionPerformed(ActionEvent e)
		{
//			if(e.getSource().equals(exitItem)){
//				this.dispose();
//			}
		}
		
		
		
	}