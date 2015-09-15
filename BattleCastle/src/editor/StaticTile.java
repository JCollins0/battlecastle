package editor;

import java.awt.Image;

import utility.Utility;

public class StaticTile extends Tile
{
	
	private static Image stile;
	
	static
	{
		stile=Utility.loadImage("check");
	}
	
	public StaticTile(int x,int y,int width,int height)
	{
		this(x,y,width,height,stile);
	}
	
	public StaticTile(int x,int y,int width,int height,Image pic)
	{
		super(x,y,width,height,pic);
	}
	
}
