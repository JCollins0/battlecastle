package core.menu_object;

import core.GameState;
import game.object.MapType;

public class MapSelectionObject extends MenuButton {
	
	private MapType map;
	
	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			GameState visibleState) {
		super(x, y, width, height, buttonType, visibleState);
	}

	public MapSelectionObject(int x, int y, int width, int height, MenuButtonType buttonType,
			GameState[] visibleStatesList) {
		super(x, y, width, height, buttonType, visibleStatesList);
	}

}
