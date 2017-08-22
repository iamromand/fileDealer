package fileDealer;

public class LevelCounter implements Comparable<LevelCounter>
{

	private int level = 0;
	private int count = 0;
	
	LevelCounter(int level)
	{
		this.level = level;
	}

	public int getLevel()
	{
		return this.level;
	}

	public int getCount()
	{
		return this.count;
	}
	
	public void incrementCount()
	{
		count++;
	}

	@Override
	public int compareTo(LevelCounter o)
	{
		if(this.getLevel() > o.getLevel())
			return 1;
		else if(this.getLevel() < o.getLevel())
			return -1;
		return 0;
	}

	@Override
	public String toString()
	{
		return "LevelCounter [level=" + level + ", count=" + count + "]";
	}
	
	
}
