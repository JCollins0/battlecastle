package core.menu_object;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class MenuButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4897853488646672224L;
	private Rectangle bounds;
	private MenuButtonType buttonType;
	
	public MenuButton(int x, int y, int width, int height, MenuButtonType buttonType)
	{
		bounds = new Rectangle(x,y,width,height);
		this.buttonType = buttonType;
	}
	
	public void render(Graphics g)
	{
		g.setColor(Color.blue);
		g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
	}
	
	public void tick()
	{
		
	}

	public MenuButtonType getButtonType() {
		return buttonType;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
}