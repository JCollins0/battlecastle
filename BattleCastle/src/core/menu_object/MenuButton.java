package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import core.GameState;

public class MenuButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4897853488646672224L;
	private Rectangle bounds;
	private MenuButtonType buttonType;
	private ArrayList<GameState> visibleStates;
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType, GameState visibleState)
	{
		this(x,y,width,height,buttonType,new GameState[]{ visibleState } );
	}
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType, GameState... visibleStatesList)
	{
		bounds = new Rectangle(x,y,width,height);
		this.buttonType = buttonType;
		visibleStates = new ArrayList<GameState>();
		for(int i = 0; i < visibleStatesList.length; i++)
			visibleStates.add(visibleStatesList[i]);
	}
		
	public void render(Graphics g)
	{
		g.setColor(Color.blue);
		g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
	}
	
	public void tick()
	{
		
	}

	public boolean isVisibleAtState(GameState state)
	{
		for(GameState s : visibleStates)
		{
			if (s.equals(state))
				return true;
		}
		return false;
	}
	
	public MenuButtonType getButtonType() {
		return buttonType;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
}