package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import core.GameState;

public class MenuButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4897853488646672224L;
	private Rectangle bounds;
	private MenuButtonType buttonType;
	private GameState visibleState;
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType, GameState visibleState)
	{
		bounds = new Rectangle(x,y,width,height);
		this.buttonType = buttonType;
		this.visibleState = visibleState;
	}
	
	public void render(Graphics g)
	{
		g.setColor(Color.blue);
		g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
	}
	
	public void tick()
	{
		
	}

	public GameState getVisibleState()
	{
		return visibleState;
	}
	
	public MenuButtonType getButtonType() {
		return buttonType;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
}