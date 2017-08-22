package fileDealer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

class Slice {
	   /*private*/ double value;
	   /*private*/ Color color;
	   /*private*/ Arc2D arc; 
	   /*private*/ Directory d;
	   
	   public Slice(double value, Color color) {  
	      this.value = value;
	      this.color = color;
	   }
	   public Slice(double value) {  
		      this.value = value;
		      Random rand = new Random();
		      this.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
		   }
	   
	   public void setArc(Arc2D arc)
	   {
		   this.arc = arc;
	   }
	   
	   
	public boolean containsArc(int x, int y)
	   {
		   if(this.arc != null)
			   return this.arc.contains(x, y);
		   return false;
	   }
	public Directory getDirectory()
	{
		return d;
	}
	public void setDirectory(Directory d)
	{
		this.d = d;
	}
	@Override
	public String toString()
	{
		return "Slice [value=" + value + ", color=" + color + "]\n";
	}
	   
	}

public class Chart2 extends JPanel implements MouseListener, ActionListener, MouseMotionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int LEVELS;
	ArrayList<ArrayList<Slice>> slices;
	Rectangle upLevel = new Rectangle(0,0,30,30);
	Point location;
	boolean flagScanComplete = false;
	ArrayList<Directory> dirs;
	long lastMove = System.currentTimeMillis();
	int maxToShow = 1;
	JLabel statusLabel;
	
	public Chart2(int maxLevel, int maxToShow, JLabel statusLabel)
	{
		super();
		this.LEVELS = maxLevel + 1;
		this.statusLabel = statusLabel;
		setMaxToShow(maxToShow);
		addMouseListener(this);
	    addMouseMotionListener(this);
	}
	
	String currentDirectory = "";
	
	public void updateChart(ArrayList<Directory> dirs, int numberOfDirsInLevel)
	{
		//System.out.println("Update: " + dirs.get(0).getParent().getFullPath() + " number: " + dirs.get(0).getParent().getSubDirectories().size());
		if((dirs != null)&&(dirs.size() > 0)&&(dirs.get(0) != null)&&(dirs.get(0).getParent() != null)&&(dirs.get(0).getParent().getFullPath() != null))
			currentDirectory = dirs.get(0).getParent().getFullPath();
		this.dirs = dirs;
		slices = new ArrayList<ArrayList<Slice>>(LEVELS);
		flagScanComplete = true;
		for(int i=0; i< LEVELS; i++)
			slices.add(new ArrayList<Slice>());
		createSlices(dirs, numberOfDirsInLevel, maxToShow, 0);
		repaint();
	}
	
	public void setMaxToShow(int maxToShow)
	{
		this.maxToShow = maxToShow - 1;
	}
	
	public void createSlices(ArrayList<Directory> dirs, int numberOfDirsInLevel, int maxToShow, int level)
	{
		int i=0;
		//long intermidiateSum = 0;
		for(i=0;i<maxToShow && i<numberOfDirsInLevel; i++)
		{
			slices.get(level).add(new Slice(dirs.get(i).getSize()));
			//intermidiateSum += dirs.get(i).getSize();
			slices.get(level).get(slices.get(level).size()-1).setDirectory(dirs.get(i));
		}
		long sumOfAllElse = 0;
		if(numberOfDirsInLevel>maxToShow)
		{			
			for(int j=maxToShow;j<numberOfDirsInLevel;j++)
			{
				sumOfAllElse += dirs.get(j).getSize();
			}
			slices.get(level).add(new Slice(sumOfAllElse));
			slices.get(level).get(slices.get(level).size()-1).setDirectory(new Directory("Other", "", -1, dirs.get(0).getParent(),false));
			slices.get(level).get(slices.get(level).size()-1).getDirectory().setSize(sumOfAllElse);
		}
		if(level<LEVELS)
		{
			for(i=0;i<maxToShow && i<numberOfDirsInLevel; i++)
			{
				if(!dirs.get(i).getSubDirectories().isEmpty())
				{
					Collections.sort(dirs.get(i).getSubDirectories());
					if(level+1 >= slices.size())
					{}
					else
						createSlices(dirs.get(i).getSubDirectories(), dirs.get(i).getSubDirectories().size(), maxToShow, level+1);
				}
				else
				{
					for(int j=level+1; j<LEVELS;j++)
					{
						slices.get(j).add(new Slice(dirs.get(i).getSize(), Color.WHITE));
					}
				}
			}
			for(int j=level+1; j<LEVELS;j++)
			{
				slices.get(j).add(new Slice(sumOfAllElse, Color.WHITE));
			}
		}
	}
		
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(flagScanComplete)
			drawPie((Graphics2D) g, getBounds(), slices);
	}
	boolean flagMouseHover = false;
	int[] whereHover = {0, 0};
	int finesse = 20;
	
	void drawPie(Graphics2D g, Rectangle area, ArrayList<ArrayList<Slice>> slices) {
		
		this.drawUpArrow(g, Color.RED);
		this.statusLabel.setText(currentDirectory);
	      //double[] total = new double[LEVELS];
  	  		double total = 0.0D;
	      //for(int level=0; level<LEVELS; level++)
	      //{
	    	 for(int i=0;i<slices.get(0).size(); i++)
	    	 {
	    		 total += slices.get(0).get(i).value;
	    	 }
	      //}
	      Rectangle[] arcBase = new Rectangle[LEVELS];
	      int difference = 30;
	      for(int i=0;i<LEVELS;i++)
	    	  arcBase[i] = new Rectangle((difference*(LEVELS-i-1)), (difference*(LEVELS-i-1)), area.width - ((difference*(LEVELS-i-1))*2), area.height - ((difference*(LEVELS-i-1))*2));
	      double curValue = 0.0D;
	      double startAngle = 0.0D;
	      for(int level = LEVELS-1; level>=0;level--)
	      {
	    	  double saveStartAngle = 0.0D;
	    	  double saveArcAngle = 0.0D;
		      for (int i = 0; i < slices.get(level).size(); i++) {
		    	  startAngle = (curValue * (360.0 / total));
		    	  double arcAngle = (slices.get(level).get(i).value * (360.0 / total));
		    	  g.setColor(slices.get(level).get(i).color);
		    	  Arc2D arc = new Arc2D.Double(arcBase[level], startAngle, arcAngle, Arc2D.PIE); 
		    	  g.fill(arc);
		    	  
		    	  if(i==whereHover[1])
		    	  {
		    		  saveStartAngle = startAngle;
		    		  saveArcAngle = arcAngle;
		    	  }
		    	  
		    	  slices.get(level).get(i).setArc(arc);
		    	  curValue += slices.get(level).get(i).value;
		      }
		      if((flagMouseHover)&&(level == whereHover[0]))
		      {
		    	  Color sliceColor = slices.get(level).get(whereHover[1]).color;
		    	  int[] rgb = {0, 0, 0};
		    	  rgb[0] = sliceColor.getRed()-20>0 ? sliceColor.getRed()-20 : sliceColor.getRed()+20;
		    	  rgb[1] = sliceColor.getGreen()-20>0 ? sliceColor.getGreen()-20 : sliceColor.getGreen()+20;
		    	  rgb[2] = sliceColor.getBlue()-20>0 ? sliceColor.getBlue()-20 : sliceColor.getBlue()+20;
		    	  g.setColor(new Color(rgb[0], rgb[1], rgb[2]));
		    	  Arc2D arc = new Arc2D.Double(arcBase[level], saveStartAngle, saveArcAngle, Arc2D.PIE); 
		    	  g.fill(arc);
		      }
	      }
	   }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Directory currentDirectory = whereTheMouseIs(e.getX(), e.getY());
		if(currentDirectory != null)
		{
			if(currentDirectory.getSubDirectories().size() != 0)
			{
				this.updateChart(currentDirectory.getSubDirectories(), currentDirectory.getSubDirectories().size());
			}
		}
		else
		{
			if(upLevel.contains(e.getX(), e.getY()))
			{
				if((dirs != null)&&(dirs.size() > 0)&&(dirs.get(0).getParent()!=null)&&(dirs.get(0).getParent().getParent() != null)&&(dirs.get(0).getParent().getParent().getSubDirectories() != null)&&(dirs.get(0).getParent().getParent().getSubDirectories().size() > 0))
				{
					System.out.println("Mouse Clicked: " + dirs.get(0).getParent().getFullPath() + dirs.get(0).getParent().getSubDirectories().size());
					this.updateChart(dirs.get(0).getParent().getParent().getSubDirectories(), dirs.get(0).getParent().getParent().getSubDirectories().size());
				}
			}
		}
		
	}
	
	public void drawUpArrow(Graphics2D g, Color c)
	{
		//g.setColor(Color.BLACK);
		//g.draw(upLevel);
		double heightThickness = (upLevel.getHeight()/4);
		double widthThickness = (upLevel.getWidth()/4);
		Rectangle bottom = new Rectangle((int)(upLevel.getX()), (int)(upLevel.getY() + upLevel.getHeight()-heightThickness), (int)((upLevel.getWidth()/2)+(widthThickness/2)), (int)heightThickness+1); 
		Rectangle up = new Rectangle( (int)(((upLevel.getX()+upLevel.getWidth())/2)-(heightThickness/2)), (int)(upLevel.getY() + (upLevel.getHeight()/2)), (int)widthThickness, (int)(upLevel.getHeight()/2));
		int[] xp = {(int)(upLevel.getX()), (int)(upLevel.getX()+upLevel.getWidth()), (int)((upLevel.getX()+upLevel.getWidth())/2) };
		int[] yp = {(int)(upLevel.getY()+(upLevel.getHeight()/2)), (int)(upLevel.getY()+(upLevel.getHeight()/2)), (int)(upLevel.getY())};
		Polygon arrow = new Polygon(xp, yp, 3);
		//triangle.moveTo(upLevel.getX(), ((upLevel.getX()+upLevel.getWidth())/2)-(heightThickness/2));
		//triangle.pathTo(upLevel.getX()+upLevel.getWidth(), ((upLevel.getX()+upLevel.getWidth())/2)-(heightThickness/2));
		//triangle.pathTo(0, 0);
		//triangle.closePath();
		//g.fill(triangle);
		g.setColor(c);
		g.fill(bottom);
		g.fill(up);
		g.fill(arrow);
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0)	{}

	@Override
	public void mouseExited(MouseEvent arg0)	{}

	@Override
	public void mousePressed(MouseEvent arg0)	{}

	@Override
	public void mouseReleased(MouseEvent arg0)	{}

	@Override
	public void actionPerformed(ActionEvent arg0)	{}

	@Override
	public void mouseDragged(MouseEvent arg0)	{}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		//if(System.currentTimeMillis() - lastMove > 200)
		//{
//			lastMove = System.currentTimeMillis();
			Directory currentDirectory = whereTheMouseIs(e.getX(), e.getY());
			if(currentDirectory != null)
			{
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(1);
				double sizeInKB = (currentDirectory.getSize())/1024.0;
				String text = currentDirectory.getFullPath() + " Size:" + df.format(sizeInKB) + " kb";
				this.statusLabel.setText("<html>Current Directory: " + this.currentDirectory + "<br> Hovering Directory: " + text + "</html>");
				if(System.currentTimeMillis() - lastMove > 200)
				{
					lastMove = System.currentTimeMillis();
					flagMouseHover = true;
					repaint();
				}
			}
			else
			{
				if(upLevel.contains(e.getX(), e.getY()))
				{
					//this.setToolTipText("Up Level");
					this.statusLabel.setText("Up Level");
				}
				else
				{
					//this.setToolTipText(null);
					this.statusLabel.setText("Current Directory: " + this.currentDirectory);
				}
			}
			flagMouseHover = false;
		//}
	}
	
	public Directory whereTheMouseIs(int x, int y)
	{
		int[] location = new int[2];	//location[0] is the level, location[1] is the location in that level
		location[0] = LEVELS;
		location[1] = 0;
		for(int level = LEVELS - 1; level >= 0; level--)
		{
			if((slices != null)&&(slices.size()>level)&&(slices.get(level) != null))
			{		
				for (int i = 0; i < slices.get(level).size(); i++) {
					if(slices.get(level).get(i).containsArc(x, y))
					{
						if(level < location[0])
						{
							location[0] = level;
							location[1] = i;
							whereHover[0] = level;
							whereHover[1] = i;
						}
					}
				}
			}
		}
		if((location[0]<LEVELS)&&(slices.get(location[0]).get(location[1]).getDirectory() != null))
			return slices.get(location[0]).get(location[1]).getDirectory();
		return null;
	}
}
