package core.menu_object;

import java.awt.image.BufferedImage;

import core.GameState;

public class PlayerSelectObject extends MenuButton{

	private PlayerType type;
	
	public PlayerSelectObject(int x, int y, int width, int height, PlayerType type,
			BufferedImage base_image, GameState... visibleStates) {
		super(x, y, width, height, MenuButtonType.PLAYER_SELECT, base_image, null, visibleStates);
		this.type = type;
	}

	public PlayerType getType() {
		return type;
	}
	
	public boolean isSelected()
	{
		return hoverOver;
	}

	public enum PlayerType
	{
		GREEN,
		RED,
		BLUE,
		YELLOW;
	}
	
	
}

