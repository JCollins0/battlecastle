package editor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import utility.Utility;

public class StaticTile extends Rectangle
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5649265101080536323L;
	protected Image[] pics;
	protected State[] states;
	
	private static Image[] check;
	private static State[] still;
	private int animation;
	
	static
	{
		check= new Image[]{Utility.loadImage("check")};
		still=new State[]{new State()};
	}

	public StaticTile(int x,int y,int width,int height)
	{
		this(x,y,width,height,check,still);
	}
	
	public StaticTile(int x,int y,int width,int height,Image[] pics,State[] states)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.pics=pics;
		this.states=states;
	}
	
	public StaticTile(int x,int y,int width,int height,Image pic,State[] states)
	{
		this(x,y,width,height,new Image[]{pic},states);
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

}
