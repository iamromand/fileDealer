package fileDealer;

import java.util.Comparator;

public class NameComparator implements Comparator<Directory>
{

	@Override
	public int compare(Directory arg0, Directory arg1)
	{
		return arg0.getName().compareTo(arg1.getName());
	}
}
