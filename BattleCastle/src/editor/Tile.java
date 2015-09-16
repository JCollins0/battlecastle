package editor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import utility.Utility;

public class Tile extends Rectangle
{
	
	private Image[] pics;
	private int animation;
	
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
	
	public void draw(Graphics g)
	{
		g.drawImage(pics[animation++], x, y, width, height, null);
		if(animation==pics.length)
			animation=0;
	}

}
