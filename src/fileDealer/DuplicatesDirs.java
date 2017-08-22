package fileDealer;

public class DuplicatesDirs
{
	private String name = "";
	private long size = 0;
	private String fullPath = "";
	private String isDirectory = "";
	private int group = 0;
	
	public DuplicatesDirs(String name, long size, String fullPath, String isDirectory, int group)
	{
		this.name = name;
		this.size = size;
		this.fullPath = fullPath;
		this.isDirectory = isDirectory;
		this.group = group;
	}

	public String getName()
	{
		return this.name;
	}

	public long getSize()
	{
		return this.size;
	}

	public String getFullPath()
	{
		return this.fullPath;
	}


	public String isDirectory()
	{
		return this.isDirectory;
	}

	public int getGroup()
	{
		return this.group;
	}
	
}
