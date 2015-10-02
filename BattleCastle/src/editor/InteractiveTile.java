package editor;

import java.awt.Image;
import java.awt.Rectangle;

import game.player.Player;
import utility.Utility;

public abstract class InteractiveTile extends Rectangle
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2723258315202922739L;

	protected Image[] pics;
	
	private static Image check;
	//private int animation;
	
	static
	{
		check=Utility.loadImage("check");
	}

	public InteractiveTile(int x,int y,int width,int height)
	{
		this(x,y,width,height,check);
	}
	
	public InteractiveTile(int x,int y,int width,int height,Image[] pics)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.pics=pics;
	}
	
	public InteractiveTile(int x,int y,int width,int height,Image pic)
	{
		this(x,y,width,height,new Image[]{pic});
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
	
	public abstract void interact(Player p);

}
