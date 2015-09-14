package editor;

import java.awt.Image;
import java.awt.Rectangle;

import utility.Utility;

public class Tile extends Rectangle
{
	
	private int x,y,width,height;
	private Image[] pics;

	public Tile(int x,int y,int width,int height)
	{
		this(x,y,width,height,null);
	}
	
	public Tile(int x,int y,int width,int height,Image[] pics)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		if(pics!=null)
			this.pics=pics;
		else
		{
			this.pics=new Image[]{Utility.loadImage("resources/check.png")};
		}
	}

}
