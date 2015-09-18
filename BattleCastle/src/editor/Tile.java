package editor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import utility.Utility;

public abstract class Tile extends Rectangle
{
	
	protected Image[] pics;
	
	private static Image check;
	
	static
	{
		check=Utility.loadImage("check");
	}

	public Tile(int x,int y,int width,int height)
	{
		this(x,y,width,height,check);
	}
	
	public Tile(int x,int y,int width,int height,Image[] pics)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.pics=pics;
	}
	
	public Tile(int x,int y,int width,int height,Image pic)
	{
		this(x,y,width,height,new Image[]{pic});
	}
	
	protected void shift(int x,int y)
	{
		this.x+=x;
		this.y+=y;
	}
	
	public abstract void draw(Graphics g);
	
	public abstract void tick();

}
