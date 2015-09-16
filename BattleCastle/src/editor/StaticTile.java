package editor;

import java.awt.Image;

import utility.Utility;

public class StaticTile extends Tile
{
	
	private static Image staticcheck;
	
	static
	{
		staticcheck=Utility.loadImage("staticcheck");
	}
	
	public StaticTile(int x,int y,int width,int height)
	{
		this(x,y,width,height,staticcheck);
	}
	
	public StaticTile(int x,int y,int width,int height,Image[] pic)
	{
		super(x,y,width,height,pic);
	}
	
	public StaticTile(int x,int y,int width,int height,Image pic)
	{
		this(x,y,width,height,new Image[]{pic});
	}
	
}
