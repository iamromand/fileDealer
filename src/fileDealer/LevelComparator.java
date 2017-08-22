package fileDealer;

import java.util.Comparator;

public class LevelComparator implements Comparator<Directory>
{
	@Override
	public int compare(Directory arg0, Directory arg1)
	{
		if(arg0.getLevel()>arg1.getLevel())	{
			return 1;
		}
		else if(arg0.getLevel()<arg1.getLevel())	{
			return -1;
		}
		return 0;
	}
}

