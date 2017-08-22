package fileDealer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Directory implements Comparable<Directory>
{

	private String fullPath = "";
	private String name = "";
	private int level = -1;
	private Directory parent = null;
	private long size = 0;
	private ArrayList<Directory> subDirectoriesAL = new ArrayList<Directory>();
	private boolean isDirectory;

	
	Directory(String fullPath, String name, int level, Directory parent, boolean isDirectory)
	{
		this.fullPath = fullPath;
		this.name = name;
		this.level = level;
		this.parent = parent;
		this.isDirectory = isDirectory;
		
	}

	public boolean isAncestorDirectory(Directory suspectedAncestor, boolean flagBothSides)
	{
		if ((suspectedAncestor.isDirectory)&&(this.isDirectory))
			return isAncestor(suspectedAncestor, flagBothSides);
		return false;
	}
	
	private boolean isAncestor(Directory suspectedAncestor, boolean flagBothSides)
	{
		if(suspectedAncestor == null)
			return false;
		Directory upwardsChecker = this;
		boolean found = false;
		while((upwardsChecker.parent != null)&&(!found))
		{
			if(upwardsChecker.parent == suspectedAncestor)
				found = true;
			upwardsChecker = upwardsChecker.parent;
		}
		if((found) || (!flagBothSides))
			return found;
		else
		{
			while((suspectedAncestor.parent != null)&&(!found))
			{
				if(suspectedAncestor.parent == this)
					found = true;
				suspectedAncestor = suspectedAncestor.parent;
			}
		}
		return found;
	}
	public boolean isDirectory()
	{
		return this.isDirectory;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addToSubDirectories(Directory toAdd)
	{
		this.subDirectoriesAL.add(toAdd);
		
	}
	
	public ArrayList<Directory> getSubDirectories()
	{
		return this.subDirectoriesAL;
	}
	
	public String getFullPath()
	{
		return this.fullPath;
	}

	public void setFullPath(String fullPath)
	{
		this.fullPath = fullPath;
	}

	public int getLevel()
	{
		return this.level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public Directory getParent()
	{
		return this.parent;
	}

	public void setParent(Directory parent)
	{
		this.parent = parent;
	}

	public long getSize()
	{
		return this.size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}
	@Override
	public String toString()
	{
		Iterator<Directory> subdirectoriesIterator = subDirectoriesAL.iterator();
		StringBuilder subdirectoriesInfo = new StringBuilder();
		while (subdirectoriesIterator.hasNext())
			subdirectoriesInfo.append("\t" + subdirectoriesIterator.next().getFullPath() + "\n");
		return "Directory [fullPath=" + fullPath + ", level=" + level + ", parent=" + this.getParent().getFullPath() + ", size=" + size + "]\n" + subdirectoriesInfo;
	}

	@Override
	public int compareTo(Directory arg0)
	{
		if(this.getSize() < arg0.getSize())
			return 1;
		else if(this.getSize() > arg0.getSize())
			return -1;
		return 0;
	}
	
}
