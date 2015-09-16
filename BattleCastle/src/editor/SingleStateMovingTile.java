package editor;

import java.awt.Graphics;
import java.awt.Image;

import utility.Utility;

public class SingleStateMovingTile extends Tile
{
	
	private static Image staticcheck;
	private int animation;
	
	static
	{
		staticcheck=Utility.loadImage("staticcheck");
	}
	
	public SingleStateMovingTile(int x,int y,int width,int height)
	{
		this(x,y,width,height,staticcheck);
	}
	
	public SingleStateMovingTile(int x,int y,int width,int height,Image[] pic)
	{
		super(x,y,width,height,pic);
	}
	
	public SingleStateMovingTile(int x,int y,int width,int height,Image pic)
	{
		this(x,y,width,height,new Image[]{pic});
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
