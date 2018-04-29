package Mypackage;

public class Data_Line{

	public int element = 0;
	public int num_user = 0;
	
	public int userId;
	public int itemId;
	public int rating;
	public int Time;

	
	public int []user;
	public int []item;
	public int []rate;
	public int []time;
	
	public Data_Line()
	{
		user = new int [3000];
		item = new int [3000];
		rate = new int [3000];
		time = new int [6040];
		element = 0;
	}
	
	public void setVal(int userId, int itemId, int rating, int time)
	{
		this.userId = userId;
		this.itemId = itemId;
		this.rating = rating;
		this.Time = time;
		
		this.user[element] = userId;
		this.item[element] = itemId;
		this.rate[element] = rating;
		this.time[element] = time;
		
		this.element++;
	}
	
	public int getItem(int index)
	{
		return this.item[index];
	}
	
	public int getUser(int index)
	{
		return this.user[index];
	}
	
	public int getTime(int index)
	{	
		return this.time[index];
	}
	
	public int getRate(int index)
	{
		return this.rate[index];
	}
	
	public int getLength()
	{
		return this.element;
	}
	
	
}
