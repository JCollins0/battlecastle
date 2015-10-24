package core.menu_object;

import game.object.MapType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utility.Utility;
import core.GameState;

public class MapSelectionObject extends MenuButton {
	
	private MapType map;
	private BufferedImage mapPreview;
	private static final Color hoverColor = new Color(100,100,100,128);
	
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState visibleState) {
		super(x, y, width, height, buttonType, visibleState);
		map = mapType;
		mapPreview = Utility.loadImage(mapType.getBackground());
	}

	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState[] visibleStatesList) {
		super(x, y, width, height, buttonType, visibleStatesList);
		map = mapType;
		mapPreview = Utility.loadImage(mapType.getBackground());
	}
	
	public void render(Graphics g)
	{
		if(mapPreview != null)
		{
			g.drawImage(mapPreview, bounds.x,bounds.y,bounds.width,bounds.height,null);
			if(hoverOver)
			{
				g.setColor(hoverColor);
				g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
			}
			g.setColor(Color.black);
			g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}else
		{
			g.setColor(Color.red);
			g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		
	}

	public MapType getMapType(){
		return map;
	}
}
