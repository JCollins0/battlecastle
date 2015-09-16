package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;

import core.GameState;
import game.object.MapType;

public class MapSelectionObject extends MenuButton {
	
	private MapType map;
	
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState visibleState) {
		super(x, y, width, height, buttonType, visibleState);
		map = mapType;
	}

	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			MapType mapType, GameState[] visibleStatesList) {
		super(x, y, width, height, buttonType, visibleStatesList);
		map = mapType;
	}
	
	public void render(Graphics g)
	{
		g.setColor(Color.red);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public MapType getMapType(){
		return map;
	}
}
