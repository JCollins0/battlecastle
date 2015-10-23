package editor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import utility.Utility;

public class Tile extends Rectangle
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5649265101080536323L;
	protected Image[] pics;
	protected State[] states;
	protected int animation;
	
	private static Image[] check;
	private static State[] still;
	
	static
	{
		check= new Image[]{Utility.loadImage("check")};
		still=new State[]{new State()};
	}

	public Tile(int x,int y,int width,int height)
	{
		this(x,y,width,height,check,still);
	}
	
	public Tile(int x,int y,int width,int height,Image[] pics,State[] states)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.pics=pics;
		this.states=states;
	}
	
	public Tile(int x,int y,int width,int height,Image pic,State[] states)
	{
		this(x,y,width,height,new Image[]{pic},states);
	}
	
	public void setStates(State[] s)
	{
		states=s;
	}
	
	protected void shift(int x,int y)
	{
		this.x+=x;
		this.y+=y;
		if(this.x>1024)
			this.x=-this.width;
		else if(this.x<-this.width)
			this.x=1024;
		if(this.y>768)
			this.y=-this.height;
		else if(this.y<-this.height)
			this.y=768;
	}
	
	public void draw(Graphics g)
	{
		g.drawImage(pics[animation], x, y, width, height, null);
	}
	
	public void tick()
	{
		if(animation++==pics.length-1)
			animation=0;
	}
	
	public String stringify()
	{
		return String.format("X:%d,Y:%d,W:%d,H:%d,A:%d",
							 x,y,width,height,animation);
	}
	
	public void execute(String s)
	{
		String[] items = s.split(",");
		for(String item : items)
		{
			String[] key_value = item.split(":");
			switch(key_value[0])
			{
			case "X": this.x = Integer.parseInt(key_value[1]);
				break;
			case "Y": this.y = Integer.parseInt(key_value[1]);
				break;
			case "W": this.width = Integer.parseInt(key_value[1]);
				break;
			case "H": this.height = Integer.parseInt(key_value[1]);
				break;
			case "A": this.animation=Integer.parseInt(key_value[1]);
				break;
				
			}
		}
	}
	
	

}
