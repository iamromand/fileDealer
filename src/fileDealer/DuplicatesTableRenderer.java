package fileDealer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DuplicatesTableRenderer extends DefaultTableCellRenderer
{
	
	static final int NUMBER_OF_COLUMNS = 5;
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{ 
	    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 

	    if(column == 0) //testing group
	    {
	    	int currentGroup = (((Integer)value)%2);
	        if(currentGroup == 0)	//can also use "if ( (x & 1) == 0 )" for performance issues  
	        {
	        	c.setBackground(Color.LIGHT_GRAY);
	        }
	        else
	        {
	        	c.setBackground(Color.YELLOW);
	        }
	    }
	    return c; 
	}
}
